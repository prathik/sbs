package rocks.thiscoder.xml;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author prathik.raj
 */
@Slf4j
public class XMLClient {
    /**
     * Makes a text/xml based request to the given end-point with the given content
     * @param request XML request object
     * @return Response from the endpoint
     * @throws IOException Error when unable to make the POST request
     */
    public String makeRequest(XMLRequest request) throws IOException {
        log.debug(request.toString());
        URL url = new URL( request.getArgUrl() );
        URLConnection con = url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setConnectTimeout( 20000 );  // long timeout, but not infinite
        con.setReadTimeout( 20000 );
        con.setUseCaches (false);
        con.setDefaultUseCaches (false);
        con.setRequestProperty ( "Content-Type", request.getContentType() );
        if(request.getSessionId() != null) {
            con.setRequestProperty ( "X-SFDC-Session", request.getSessionId() );
        }

        if(request.getLogin()) {
            con.setRequestProperty("SOAPAction", "login");
        }

        OutputStreamWriter writer = null;
        writer = new OutputStreamWriter( con.getOutputStream() );
        writer.write( request.getContent() );
        writer.flush();
        writer.close();
        InputStreamReader reader = new InputStreamReader( con.getInputStream() );
        StringBuilder buf = new StringBuilder();
        char[] cbuf = new char[ 2048 ];
        int num;
        while ( -1 != (num=reader.read( cbuf )))
        {
            buf.append( cbuf, 0, num );
        }
        log.debug("Response for XML Request: " + buf.toString());
        return buf.toString();
    }
}
