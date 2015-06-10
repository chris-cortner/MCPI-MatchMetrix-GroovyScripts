package examples

import com.sun.mdm.index.master.CallerInfo
import com.sun.mdm.index.script.CustomLogicContext
import com.sun.mdm.index.script.CustomLogicParameter
import examples.UpdateOwnerPetLIDStatus

import java.sql.DriverManager

/**
 * Created by c_cortner on 6/9/2015.
 */
class UpdateOwnerPetLIDStatusTest extends GroovyTestCase {
    void testExecute() {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        def conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ownerpet?user=user&password=password");

        UpdateOwnerPetLIDStatus updateOwnerPetLIDStatus = new UpdateOwnerPetLIDStatus()
        List<CustomLogicParameter> params = new ArrayList<>()

        def systemCodeParam = new CustomLogicParameter()
        systemCodeParam.setName("systemCode")
        systemCodeParam.setValue("DW")
        params.add(systemCodeParam)

        def localIDParam = new CustomLogicParameter()
        localIDParam.setName("localID")
        localIDParam.setValue("9034:1:001_9034:1:001-2")
        params.add(localIDParam)

        updateOwnerPetLIDStatus.execute(new CustomLogicContext(null, params, new CallerInfo(), conn))

        conn.close();
    }
}
