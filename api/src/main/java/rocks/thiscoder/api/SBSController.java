package rocks.thiscoder.api;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import rocks.thiscoder.http.FileClient;
import rocks.thiscoder.sbs.*;
import rocks.thiscoder.sbs.models.UploadRequest;
import rocks.thiscoder.xml.XMLClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Properties;

/**
 * @author prathik.raj
 */
@Path("/")
@Slf4j
public class SBSController {
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(@FormDataParam("file") InputStream uploadedFile,
                             @FormDataParam("file") FormDataContentDisposition meta,
                             @FormDataParam("type") String type,
                             @FormDataParam("object") String object
                             ) {
        String fileLocation = "/tmp/" + meta.getFileName();
        File data = new File(fileLocation);

        try {

            FileOutputStream out;
            int read = 0;
            byte[] bytes = new byte[1024];
            out = new FileOutputStream(data);
            while ((read = uploadedFile.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();

            XMLClient xmlClient = new XMLClient();

            File file = new File("src/main/resources/salesforce.properties");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();

            Type instanceType = Type.PRODUCTION;

            if(properties.getProperty("type").equals("sandbox")) {
                instanceType = Type.SANDBOX;
            }

            Salesforce salesforce = new Salesforce(properties.getProperty("username"),
                    properties.getProperty("password"),
                    instanceType,
                    xmlClient);
            salesforce.login();
            UploadRequest uploadRequest = new UploadRequest( object, type);
            FileClient fileClient = new FileClient();

            if(properties.getProperty("proxyhost", null) != null
                    && properties.getProperty("proxyport", null) != null) {
                fileClient.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                        properties.getProperty("proxyhost"),
                        Integer.valueOf(properties.getProperty("proxyport")))));
            }

            SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, fileClient);
            salesforceBulkJob.createJob();
            Batch b = new Batch(data, salesforce);
            salesforceBulkJob.addBatch(b);
            return b.getBatchId();

        } catch (IOException | SalesforceException e) {
            return e.getMessage();
        }

    }
}
