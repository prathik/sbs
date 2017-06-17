package rocks.thiscoder.sbs;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.thiscoder.xml.XMLClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author prathik.raj
 */
@Test
public class SalesforceTest {
    @Test
    void generateLoginXMLTest() throws IOException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = new Salesforce("prathik.raj",
                "password",
                Type.SANDBOX, xmlClient);
        Assert.assertEquals(salesforce.getXMLTemplate(), "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "              xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "              xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "    <env:Body>\n" +
                "        <n1:login xmlns:n1=\"urn:partner.soap.sforce.com\">\n" +
                "            <n1:username>prathik.raj</n1:username>\n" +
                "            <n1:password>password</n1:password>\n" +
                "        </n1:login>\n" +
                "    </env:Body>\n" +
                "</env:Envelope>");
    }

    @Test
    void buildURLTest() throws IOException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        Assert.assertEquals(salesforce.buildURL(), "https://test.salesforce.com/services/Soap/u/40.0");
        Salesforce salesforce1 = new Salesforce("prathik.raj", "password", Type.PRODUCTION,
                xmlClient);
        Assert.assertEquals(salesforce1.buildURL(), "https://login.salesforce.com/services/Soap/u/40.0");
    }

    @Test
    void loginTest() throws SalesforceException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/loginResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        salesforce.login();
        Assert.assertEquals(salesforce.getSessionId(), "00DS0000003E9ZE!ASAAQEpS5ELRQc2O1eb5mgCypof7g74sLD4jwa" +
                "zxAwE1ikxpflUgwI6T_jEoyEoZCKVo1e1hqWW8WLsuMGLhGZbvIu3CVAq2");
        Assert.assertEquals(salesforce.getInstance(), "https://cs1.salesforce.com");

    }

    @Test(expectedExceptions = SalesforceException.class)
    void errorOnLoginTest() throws SalesforceException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/loginResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doThrow(new IOException("Error parsing XML")).when(xmlClient).makeRequest(any());
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        salesforce.login();

    }

    @Test(expectedExceptions = SalesforceException.class)
    void errorOnInvalidTest() throws SalesforceException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/invalid.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        salesforce.login();
    }

    @Test
    void getBaseUrlTest() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/loginResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        salesforce.setInstance("http://sfb.thiscoder.rocks:9090/abc");
        Assert.assertEquals(salesforce.getInstance(), "http://sfb.thiscoder.rocks:9090");
        salesforce.setInstance("http://sfb.thiscoder.rocks/abc");
        Assert.assertEquals(salesforce.getInstance(), "http://sfb.thiscoder.rocks");
        salesforce.setInstance("https://sfb.thiscoder.rocks/abc");
        Assert.assertEquals(salesforce.getInstance(), "https://sfb.thiscoder.rocks");
    }

    @Test(expectedExceptions = IOException.class)
    void gracefullyHandleInvalidUrlTest() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/loginResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());
        Salesforce salesforce = new Salesforce("prathik.raj", "password", Type.SANDBOX, xmlClient);
        salesforce.setInstance("abc");
    }
}
