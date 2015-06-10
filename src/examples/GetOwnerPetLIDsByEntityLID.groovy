package examples

import com.sun.mdm.index.master.ProcessingException;
import com.sun.mdm.index.master.UserException;
import com.sun.mdm.index.script.CustomLogic;
import com.sun.mdm.index.script.CustomLogicContext;
import com.sun.mdm.index.script.CustomLogicParameter;
import com.sun.mdm.index.script.CustomLogicResponse

/**
 *
 * @author David
 */
class GetOwnerPetLIDsByEntityLID implements CustomLogic {

    @Override
    public CustomLogicResponse execute(CustomLogicContext clc) throws ProcessingException, UserException {
        def systemCode
        def localID
        def lidType;

        def params = clc.getParameters()
        for (CustomLogicParameter param : params) {
            if(param.getName().equals("systemCode")) systemCode = param.getValue()
            if(param.getName().equals("localID")) localID = param.getValue()
            if(param.getName().equals("lidType")) lidType = param.getValue()
        }

        if(systemCode == null || localID == null || lidType == null ){
            throw new UserException("GetOwnerPetLIDsByEntityLID: Null parameter encountered:\n" +
                    "\tValues: {systemCode: ${systemCode}, localID: ${localID}, lidType: ${lidType}}")
        }

        def localIDColumn = getLocalIDColumn(lidType)
        //Blow up, can't form query without valid lid type
        if(localIDColumn == null) throw new UserException("Unrecognized LID Type: ${lidType}")

        def sql = "select OWNERPETLID from mcpi_ownerpet_owner_pet where " +
                "SYSTEMCODE='${systemCode}' and ${localIDColumn}='${localID}' and STATUS='active';"

        def ownerPetLIDs = executeSingleColumnStringQuery(clc, sql)

        def response = new CustomLogicResponse()
        response.setResponseText(ownerPetLIDs.join(","))
        response.setResponseCode(0) //not important for this application
        response
    }

    private def getLocalIDColumn(String lidType) {
        lidType.equals("OWNER") ? "OWNERLID":
                lidType.equals("PET") ? "PETLID":
                        null
    }

    private def executeSingleColumnStringQuery(CustomLogicContext clc, String sql) {
        def stringResults = []
        def con = clc.getConnection()
        def rs = null
        def statement = null
        try {
            statement = con.createStatement()
            rs = statement.executeQuery(sql)

            while (rs.next()) {
                stringResults << rs.getString(1)
            }
        } finally {
            if (rs != null) rs.close()
            if (statement != null) statement.close()
        }
        stringResults
    }

}
