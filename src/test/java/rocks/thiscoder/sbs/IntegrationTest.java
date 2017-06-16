package rocks.thiscoder.sbs;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.thiscoder.xml.XMLClient;

/**
 * @author prathik.raj
 */
public class IntegrationTest {
    @Test(enabled = false)
    void loginToSalesforce() throws SalesforceException {
        XMLClient xmlClient = new XMLClient();
        Salesforce salesforce = new Salesforce("rrrrrrrrrrrrrrrrrrrrrrrrrrr",
                "rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr",
                Type.SANDBOX,
                xmlClient);
        salesforce.login();
        Assert.assertNotNull(salesforce.getSessionId());
        Assert.assertNotNull(salesforce.getInstance());
        System.out.println("salesforce.getSessionId() = " + salesforce.getSessionId());
    }
}
