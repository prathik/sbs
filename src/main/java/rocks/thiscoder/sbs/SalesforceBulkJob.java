package rocks.thiscoder.sbs;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import rocks.thiscoder.sbs.models.JobResult;
import rocks.thiscoder.sbs.models.UploadRequest;
import rocks.thiscoder.xml.XMLClient;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author prathik.raj
 */

@Slf4j
public class SalesforceBulkJob {
    @NonNull final UploadRequest request;
    @NonNull final Salesforce salesforce;
    @NonNull final XMLClient xmlClient;
    @Getter
    @Setter
    String jobId;

    public SalesforceBulkJob(UploadRequest request, Salesforce salesforce, XMLClient xmlClient)
            throws SalesforceException {
        this.request = request;
        this.salesforce = salesforce;
        this.xmlClient = xmlClient;

        if(salesforce.getSessionId() == null) {
            throw new SalesforceException("SessionID is null, has the login() method on Salesforce been called?");
        }

        if(salesforce.getInstance() == null) {
            throw new SalesforceException("Instance ID null when SessionID is not null should not occur");
        }
    }

    String getXMLTemplate() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/templates/job.xml"));
        String xml = new String(encoded);
        xml = String.format(xml, request.getType(), request.getSfObject());
        return xml;
    }

    String getCloseXMLTemplate() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/templates/closeJob.xml"));
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


    void createJob() throws SalesforceException {
        try {
            String response = xmlClient.makeRequest(buildURL(), getXMLTemplate(), "application/xml",
                    salesforce.getSessionId());
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes());
            Document doc = builder.parse(input);
            NodeList nodes = doc.getElementsByTagName("id");
            setJobId(nodes.item(0).getTextContent());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SalesforceException(e);
        }
    }

    void addBatch() throws SalesforceException {
        if(getJobId() == null) {
            throw new SalesforceException("Job ID is null, this means that Job hasn't been created.");
        }
    }

    void checkStatus() {

    }

    String closeJob() throws SalesforceException {
        if(getJobId() == null) {
            throw new SalesforceException("Job ID is null, this means that Job hasn't been created.");
        }

        try {
            String response = xmlClient.makeRequest(buildJobURL(), getCloseXMLTemplate(), "application/xml",
                    salesforce.getSessionId());
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

    List<JobResult> retriveResult() {
        return null;
    }
}
