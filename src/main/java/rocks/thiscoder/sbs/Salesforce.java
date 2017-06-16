package rocks.thiscoder.sbs;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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
@RequiredArgsConstructor
@Slf4j
public class Salesforce {
    @Getter
    @Setter
    private String sessionId;

    @Getter
    private String instance;

    @NonNull final String username;
    @NonNull final String password;
    @NonNull final Type type;
    @NonNull final XMLClient xmlClient;

    String getXMLTemplate() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("src/main/resources/templates/login.xml"));
        String xml = new String(encoded);
        xml = String.format(xml, username, password);
        return xml;
    }

    String buildURL() {
        String argUrl = "https://login.salesforce.com/services/Soap/u/40.0";
        if(type.equals(Type.SANDBOX)) {
            argUrl = "https://test.salesforce.com/services/Soap/u/40.0";
        }
        log.debug("URL: " + argUrl);
        return argUrl;
    }

    void setInstance(String instance) throws MalformedURLException {
        URL url = new URL(instance);
        this.instance = url.getProtocol() + "://" + url.getHost();
        if(url.getPort() != -1) {
            this.instance += ":" + String.valueOf(url.getPort());
        }
    }

    /**
     * Login to SF and store session id
     * @throws SalesforceException Thrown in case of any error
     */
    void login() throws SalesforceException {
        try {
            XMLRequest xmlRequest = new XMLRequest();
            xmlRequest.setArgUrl(buildURL());
            xmlRequest.setContent(getXMLTemplate());
            xmlRequest.setContentType("text/xml");
            xmlRequest.setLogin(true);
            String response = xmlClient.makeRequest(xmlRequest);
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input =  new ByteArrayInputStream(response.getBytes());
            Document doc = builder.parse(input);
            NodeList nodes = doc.getElementsByTagName("sessionId");
            setSessionId(nodes.item(0).getTextContent());

            nodes = doc.getElementsByTagName("serverUrl");
            setInstance(nodes.item(0).getTextContent());
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new SalesforceException(e);
        }
    }

}
