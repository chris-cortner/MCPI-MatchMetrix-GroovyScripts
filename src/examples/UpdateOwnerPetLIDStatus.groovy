package examples

import com.sun.mdm.index.master.ProcessingException
import com.sun.mdm.index.master.UserException
import com.sun.mdm.index.script.CustomLogic
import com.sun.mdm.index.script.CustomLogicContext
import com.sun.mdm.index.script.CustomLogicParameter
import com.sun.mdm.index.script.CustomLogicResponse
import java.sql.Connection
import java.sql.SQLException

/**
 * Created by c_cortner on 6/8/2015.
 */
class UpdateOwnerPetLIDStatus implements CustomLogic {
    @Override
    CustomLogicResponse execute(CustomLogicContext customLogicContext) throws ProcessingException, UserException {
        def systemCode
        def localID

        def params = customLogicContext.getParameters()
        for (CustomLogicParameter param : params) {
            if(param.getName().equals("systemCode")) systemCode = param.getValue()
            if(param.getName().equals("localID")) localID = param.getValue()
        }

        if(systemCode == null || localID == null){
            throw new UserException("UpdateOwnerPetLIDStatus: Null parameter encountered:\n" +
                    "\tValues: {systemCode: ${systemCode}, localID: ${localID}}")
        }

        def status = getOwnerPetLIDStatus(systemCode, localID, customLogicContext.getConnection())
        updateStatusInLIDLookupTable(systemCode, localID, status, customLogicContext.getConnection())
        null
    }

    def updateStatusInLIDLookupTable(String systemCode, String ownerPetLID, String status, Connection conn) {
        def sql = "update mcpi_ownerpet_owner_pet set status = '${status}' where systemcode='${systemCode}' and ownerpetlid='${ownerPetLID}'"

        def statement = null
        try {
            statement = conn.createStatement()
            statement.execute(sql)
        } catch (SQLException e){
            throw new UserException("UpdateOwnerPetLIDStatus: an error occurred updating the record's status", e)
        } finally {
            if (statement != null) statement.close()
        }
    }

    def getOwnerPetLIDStatus(String systemCode, String ownerPetLID, Connection conn) {
        def sql = "select status from mm_systemobject where systemcode='${systemCode}' and lid='${ownerPetLID}'";

        def status
        def rs = null
        def statement = null
        try {
            statement = conn.createStatement()
            rs = statement.executeQuery(sql)

            if(rs.next()) {
                status = rs.getString(1)
            }
        } finally {
            if (rs != null) rs.close()
            if (statement != null) statement.close()
        }

        //if we weren't able to obtain a status for the ownerpet lid from mm_systemobject, default to active
        status == null ? 'active': status
    }

}
