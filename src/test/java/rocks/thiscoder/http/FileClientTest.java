package rocks.thiscoder.http;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;

/**
 * @author prathik.raj
 */
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
        Assert.assertTrue(fileContent.contains("header\\n1\\n2\\n3\""));
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
