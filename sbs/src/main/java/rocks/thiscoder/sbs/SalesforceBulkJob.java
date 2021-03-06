package rocks.thiscoder.sbs;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import rocks.thiscoder.http.FileClient;
import rocks.thiscoder.http.FileUploadRequest;
import rocks.thiscoder.sbs.models.UploadRequest;
import rocks.thiscoder.xml.XMLClient;
import rocks.thiscoder.xml.XMLRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author prathik.raj
 */

@Slf4j
public class SalesforceBulkJob {
    @NonNull final UploadRequest request;
    @Getter
    @NonNull final Salesforce salesforce;

    @NonNull final XMLClient xmlClient;
    @Getter
    @NonNull final FileClient fileClient;
    @Getter
    @Setter
    String jobId;

    public SalesforceBulkJob(UploadRequest request, Salesforce salesforce, XMLClient xmlClient, FileClient fileClient)
            throws SalesforceException {
        this.request = request;
        this.salesforce = salesforce;
        this.xmlClient = xmlClient;
        this.fileClient = fileClient;

        if(salesforce.getSessionId() == null) {
            throw new SalesforceException("SessionID is null, has the login() method on Salesforce been called?");
        }

        if(salesforce.getInstance() == null) {
            throw new SalesforceException("Instance ID null when SessionID is not null should not occur");
        }
    }

    String getXMLTemplate() throws IOException, SalesforceException {
        switch (request.getType()) {
            case "insert": {
                URL url = getClass().getClassLoader().getResource("templates/job.xml");
                log.debug("Template location: " + url.getPath());
                byte[] encoded = Files.readAllBytes(Paths.get(url.getPath()));
                String xml = new String(encoded);
                xml = String.format(xml, request.getType(), request.getSfObject());
                return xml;
            }
            case "upsert": {
                URL url = getClass().getClassLoader().getResource("templates/upsertJob.xml");
                log.debug("Template location: " + url.getPath());
                byte[] encoded = Files.readAllBytes(Paths.get(url.getPath()));
                String xml = new String(encoded);
                xml = String.format(xml, request.getType(), request.getSfObject(), request.getExternalIdFieldName());
                return xml;
            }
            default:
                throw new SalesforceException("Invalid type");
        }
    }

    String getCloseXMLTemplate() throws IOException {
        URL url = getClass().getClassLoader().getResource("templates/closeJob.xml");
        log.debug("Template location: " + url.getPath());
        byte[] encoded = Files.readAllBytes(Paths.get(url.getPath()));
        String xml = new String(encoded);
        xml = String.format(xml, request.getType(), request.getSfObject());
        return xml;
    }

    String buildURL() throws MalformedURLException {
        URL url = new URL(new URL(salesforce.getInstance()), "services/async/40.0/job");
        return url.toString();
    }

    String buildJobURL() throws MalformedURLException, SalesforceException {
        if(getJobId() == null) {
            throw new SalesforceException("Job id is null, job hasn't been created.");
        }
        return buildURL() + "/" + getJobId();
    }


    public void createJob() throws SalesforceException {
        try {
            XMLRequest xmlRequest = new XMLRequest();
            xmlRequest.setLogin(false);
            xmlRequest.setContentType("application/xml");
            xmlRequest.setContent(getXMLTemplate());
            xmlRequest.setArgUrl(buildURL());
            xmlRequest.setSessionId(salesforce.getSessionId());
            String response = xmlClient.makeRequest(xmlRequest);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes());
            Document doc = builder.parse(input);
            NodeList nodes = doc.getElementsByTagName("id");
            setJobId(nodes.item(0).getTextContent());
            log.debug("Job ID: " + getJobId());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SalesforceException(e);
        }
    }

    public void addBatch(@NonNull Batch batch) throws SalesforceException {
        if(getJobId() == null) {
            throw new SalesforceException("Job ID is null, this means that Job hasn't been created.");
        }

        if(batch.getSalesforce() != getSalesforce()) {
            throw new SalesforceException("SF Client of batch and this instance are different.");
        }

        if(batch.getJobId() != null) {
            throw new SalesforceException("Batch has a job id already!");
        }

        if(batch.getBatchId() != null) {
            throw new SalesforceException("Batch already has a batch id, this batch has already been added.");
        }

        try {
            FileUploadRequest fileUploadRequest = new FileUploadRequest(batch.getCsv().getAbsolutePath(),
                    salesforce.getSessionId(),
                    getJobId(),
                    buildJobURL() + "/batch",
                    "text/csv");
            String response = getFileClient().uploadFile(fileUploadRequest);
            batch.setJobId(getJobId());
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes());
            Document doc = builder.parse(input);
            NodeList nodes = doc.getElementsByTagName("id");
            batch.setBatchId(nodes.item(0).getTextContent());
            log.debug("Batch ID: " + batch.getBatchId());
        } catch (MalformedURLException e) {
            throw new SalesforceException("Invalid URL");
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SalesforceException(e);
        } catch (NullPointerException e) {
            throw new SalesforceException("File client responded with a null");
        }
    }

    String closeJob() throws SalesforceException {
        if(getJobId() == null) {
            throw new SalesforceException("Job ID is null, this means that Job hasn't been created.");
        }

        try {
            XMLRequest xmlRequest = new XMLRequest();
            xmlRequest.setLogin(false);
            xmlRequest.setContentType("application/xml");
            xmlRequest.setContent(getCloseXMLTemplate());
            xmlRequest.setArgUrl(buildJobURL());
            xmlRequest.setSessionId(salesforce.getSessionId());

            String response = xmlClient.makeRequest(xmlRequest);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes());
            Document doc = builder.parse(input);
            NodeList nodes = doc.getElementsByTagName("state");
            return nodes.item(0).getTextContent();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SalesforceException(e);
        }
    }
}
