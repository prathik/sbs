package rocks.thiscoder.sbs;

import org.testng.Assert;
import org.testng.annotations.Test;
import rocks.thiscoder.http.FileClient;
import rocks.thiscoder.sbs.models.UploadRequest;
import rocks.thiscoder.xml.XMLClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * @author prathik.raj
 */
public class SalesforceBulkJobTest {
    final FileClient mockFileClient;
    final File mockedFile;

    SalesforceBulkJobTest() {
        mockFileClient = mock(FileClient.class);
        mockedFile = mock(File.class);
        doReturn(true).when(mockedFile).isFile();
        doReturn(true).when(mockedFile).exists();
        doReturn("/tmp/abc.csv").when(mockedFile).getAbsolutePath();
    }

    @Test
    void generateJobXMLTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.getXMLTemplate(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">\n" +
                "    <operation>insert</operation>\n" +
                "    <object>Contact</object>\n" +
                "    <contentType>CSV</contentType>\n" +
                "</jobInfo>");
    }

    @Test
    void generateJobXMLUpsertTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest( "Contact", "upsert", "EXT__c");
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.getXMLTemplate(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">\n" +
                "    <operation>upsert</operation>\n" +
                "    <object>Contact</object>\n" +
                "    <contentType>CSV</contentType>\n" +
                "    <externalIdFieldName>EXT__c</externalIdFieldName>\n" +
                "</jobInfo>");
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Invalid type")
    void generateJobXMLInvalidTypeTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest( "Contact", "abc", null);
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        salesforceBulkJob.getXMLTemplate();
    }

    @Test(expectedExceptions = SalesforceException.class)
    void errorOnNullSessionTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn(null).when(salesforce).getSessionId();
        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);

    }

    @Test
    void urlBuildTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);
        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);
        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Assert.assertEquals(salesforceBulkJob.buildURL(), "https://sfb.thiscoder.rocks/services/async/40.0/job");
    }

    @Test
    void createJobTest() throws IOException, SalesforceException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/jobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch(mockedFile, salesforce);
        salesforceBulkJob.addBatch(batch);

    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "SF Client .*")
    void differentSFInstanceTest() throws SalesforceException, IOException {
         byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

        Salesforce salesforce = mock(Salesforce.class);
        Salesforce salesforce1 = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();
        doReturn("abc").when(salesforce1).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce1).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch(mockedFile, salesforce1);
        salesforceBulkJob.setJobId("123");
        salesforceBulkJob.addBatch(batch);
    }

    @Test(expectedExceptions = SalesforceException.class, expectedExceptionsMessageRegExp = "Batch has a job id " +
            "already!")
    void batchHasAJobIdTest() throws SalesforceException, IOException {
         byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/closeJobResponse.xml"));
        String xml = new String(encoded);
        XMLClient xmlClient = mock(XMLClient.class);
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest( "Contact", "insert", null);

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch(mockedFile, salesforce);
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
        doReturn(xml).when(xmlClient).makeRequest(any());

        UploadRequest uploadRequest = new UploadRequest("Contact", "insert", null);

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, mockFileClient);
        Batch batch = new Batch(mockedFile, salesforce);
        batch.setBatchId("abc");
        salesforceBulkJob.setJobId("123");
        salesforceBulkJob.addBatch(batch);
    }

    @Test
    void addBatchTest() throws IOException, SalesforceException {
        XMLClient xmlClient = mock(XMLClient.class);

        byte[] encoded = Files.readAllBytes(Paths.get("src/test/resources/createBatchResponse.xml"));
        String xml = new String(encoded);
        FileClient fileClient = mock(FileClient.class);
        doReturn(xml).when(fileClient).uploadFile(any());

        UploadRequest uploadRequest = new UploadRequest("Contact", "insert", null);

        Salesforce salesforce = mock(Salesforce.class);
        doReturn("abc").when(salesforce).getSessionId();
        doReturn("https://sfb.thiscoder.rocks").when(salesforce).getInstance();

        SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, fileClient);
        salesforceBulkJob.setJobId("abc");
        Batch batch = new Batch(mockedFile, salesforce);
        salesforceBulkJob.addBatch(batch);
        Assert.assertEquals(batch.getJobId(), "abc");
        Assert.assertEquals(batch.getBatchId(), "751S0000002P1fXIAS");

    }
}
