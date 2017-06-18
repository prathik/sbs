package rocks.thiscoder.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
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
@Slf4j
@Path("/upload")
@Api( value = "/upload", description = "Upload file to Salesforce")
public class SBSController {
    @Path("{object}/{type}")
    @ApiOperation(
        value = "Upload documents to salesforce",
        response = String.class
    )
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public String uploadFile(
            @ApiParam( value = "CSV Meta", required = true, name = "file" )
            @FormDataParam("file") InputStream uploadedFile,
            @FormDataParam("file") FormDataContentDisposition meta,
            @ApiParam( value = "Operation type (currently only insert is supported)", required = true, name = "type" )
            @PathParam("type") String type,
            @ApiParam( value = "Salesforce object to which data is inserted", required = true, name = "object" )
            @PathParam("object") String object
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

            Parameters params = new Parameters();
            FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.properties()
                                    .setFileName("salesforce.properties"));

            Configuration config = builder.getConfiguration();

            Type instanceType = Type.PRODUCTION;

            if(config.getString("type").equals("sandbox")) {
                instanceType = Type.SANDBOX;
            }

            Salesforce salesforce = new Salesforce(config.getString("username"),
                    config.getString("password"),
                    instanceType,
                    xmlClient);
            salesforce.login();
            UploadRequest uploadRequest = new UploadRequest( object, type);
            FileClient fileClient = new FileClient();

            if(config.getString("proxyhost") != null
                    && config.getString("proxyport") != null) {
                fileClient.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
                        config.getString("proxyhost"),
                        config.getInt("proxyport"))));
            }

            SalesforceBulkJob salesforceBulkJob = new SalesforceBulkJob(uploadRequest, salesforce, xmlClient, fileClient);
            salesforceBulkJob.createJob();
            Batch b = new Batch(data, salesforce);
            salesforceBulkJob.addBatch(b);
            return b.getBatchId();

        } catch (IOException | SalesforceException | ConfigurationException e) {
            return e.getMessage();
        }

    }
}
