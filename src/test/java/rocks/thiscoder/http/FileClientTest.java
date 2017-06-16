package rocks.thiscoder.http;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;

/**
 * @author prathik.raj
 */
@Slf4j
public class FileClientTest {
    @Test
    void fileClientIntegrationTest() throws IOException {
        FileUploadRequest fileUploadRequest = new FileUploadRequest("src/test/resources/data.csv",
               "testSession",
               "testJob",
                "http://httpbin.org/post",
                "text/csv");
        FileClient fileClient = new FileClient();
        String fileContent = fileClient.uploadFile(fileUploadRequest);
        log.debug(fileContent);
        Assert.assertTrue(fileContent.contains("Test__c\\nTest\\nTest1\\nTest2\\nTest3\\n\""));
    }

    @Test(expectedExceptions = NoSuchFileException.class)
    void invalidFileTest() throws IOException {
        FileUploadRequest fileUploadRequest = new FileUploadRequest("src/test/resources/abc.csv",
               "testSession",
               "testJob",
                "http://httpbin.org/post",
                "text/csv");
        FileClient fileClient = new FileClient();
        fileClient.uploadFile(fileUploadRequest);
    }

    @Test(expectedExceptions = MalformedURLException.class)
    void invalidUrlTest() throws IOException {
        FileUploadRequest fileUploadRequest = new FileUploadRequest("src/test/resources/abc.csv",
               "testSession",
               "testJob",
                "httpzz://httpbin.org/post",
                "text/csv");
        FileClient fileClient = new FileClient();
        fileClient.uploadFile(fileUploadRequest);
    }
}
