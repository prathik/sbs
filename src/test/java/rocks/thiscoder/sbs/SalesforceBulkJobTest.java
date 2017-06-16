package rocks.thiscoder.sbs;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.thiscoder.http.FileClient;
import rocks.thiscoder.sbs.models.UploadRequest;
import rocks.thiscoder.xml.XMLClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author prathik.raj
 */
public class SalesforceBulkJobTest {
    final FileClient mockFileClient;

    SalesforceBulkJobTest() {
        mockFileClient = mock(FileClient.class);
    }

    @Test
    void generateJobXMLTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.getXMLTemplate(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">\n" +
                "    <operation>insert</operation>\n" +
                "    <object>Contact</object>\n" +
                "    <contentType>CSV</contentType>\n" +
                "</jobInfo>");
    }

    @Test(expectedExceptions = SalesforceException.class)
    void errorOnNullSessionTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn(null).when(salesforce).getSessionId();
        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);

    }

    @Test
    void urlBuildTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.buildURL(), "https://sfb.thiscoder.rocks/services/async/40.0/job");
    }

    @Test
    void createJobTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/jobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertNull(salesforceBulkJob.getJobId());
        salesforceBulkJob.createJob();
        Assert.assertEquals(salesforceBulkJob.getJobId(), "750S0000002bl3xIAA");
    }

    @Test
    void closeJobTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        salesforceBulkJob.setJobId("abc");
        Assert.assertEquals(salesforceBulkJob.closeJob(), "Closed");
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Job ID is null.*")
    void noJobIdTest() throws SalesforceException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        salesforceBulkJob.closeJob();
    }

    @Test
    void buildJobURLTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        salesforceBulkJob.setJobId("abc");
        Assert.assertEquals(salesforceBulkJob.buildJobURL(), "https://sfb.thiscoder.rocks/services/async/4" +
                "0.0/job/abc");
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Job id is null.*")
    void noJobIDURLTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.buildJobURL(), "https://sfb.thiscoder.rocks/services/async/4" +
                "0.0/job/abc");
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Job ID is null.*")
    void addBatchJobIDNullTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch("abc.csv", salesforce);
        salesforceBulkJob.addBatch(batch);

    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "SF Client .*")
    void differentSFInstanceTest() throws SalesforceException, IOException {
         byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        Salesforce salesforce1 = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch("abc.csv", salesforce1);
        salesforceBulkJob.setJobId("123");
        salesforceBulkJob.addBatch(batch);
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Batch has a job id " +
            "already!")
    void batchHasAJobIdTest() throws SalesforceException, IOException {
         byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch("abc.csv", salesforce);
        batch.setJobId("abc");
        salesforceBulkJob.setJobId("123");
        salesforceBulkJob.addBatch(batch);
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Batch already has a " +
            "batch id, this batch has already been added.")
    void batchHasABatchIdTest() throws SalesforceException, IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch("abc.csv", salesforce);
        batch.setBatchId("abc");
        salesforceBulkJob.setJobId("123");
        salesforceBulkJob.addBatch(batch);
    }

    @Test(enabled = false)
    void addBatchTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(anyString(), anyString(), anyString(), any());

        UploadRequest uploadRequest = new UploadRequest("dummy.csv", "Contact", "insert");

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        salesforceBulkJob.setJobId("abc");
        Batch batch = new Batch("abc.csv", salesforce);
        salesforceBulkJob.addBatch(batch);

    }
}
