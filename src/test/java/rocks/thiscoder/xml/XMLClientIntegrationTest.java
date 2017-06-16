package rocks.thiscoder.xml;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author prathik.raj
 */
@Test
@Slf4j
public class XMLClientIntegrationTest {

    @Test
    void postXMLTest() throws IOException {
        XMLClient xmlClient = new XMLClient();
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        String response = xmlClient.makeRequest("http://httpbin.org/post", xml, "text/xml", null);
        log.debug(response);
        Assert.assertTrue(response.contains("750S0000002bl3xIAA"));
    }

    @Test
    void postXMLWithSessionTest() throws IOException {
        XMLClient xmlClient = new XMLClient();
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        String response = xmlClient.makeRequest("http://httpbin.org/post", xml, "text/xml", "someSessionId!");
        log.debug(response);
        Assert.assertTrue(response.contains("750S0000002bl3xIAA"));
        Assert.assertTrue(response.contains("X-Sfdc-Session"));
        Assert.assertTrue(response.contains("someSessionId!"));
    }
}
