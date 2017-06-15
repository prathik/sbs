package rocks.thiscoder.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author prathik.raj
 */
public class XMLClient {
    /**
     * Makes a text/xml based request to the given end-point with the given content
     * @param argUrl Url to make the request on
     * @param content XML content
     * @return Response from the endpoint
     * @throws IOException Error when unable to make the POST request
     */
    public String makeRequest(String argUrl, String content, String contentType, String sessionId) throws IOException {
        URL url = new URL( argUrl );
        URLConnection con = url.openConnection();
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setConnectTimeout( 20000 );  // long timeout, but not infinite
        con.setReadTimeout( 20000 );
        con.setUseCaches (false);
        con.setDefaultUseCaches (false);
        con.setRequestProperty ( "Content-Type", contentType );
        if(sessionId != null) {
            con.setRequestProperty ( "X-SFDC-Session", sessionId );
        }
        OutputStreamWriter writer = null;
        writer = new OutputStreamWriter( con.getOutputStream() );
        writer.write( content );
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
        return buf.toString();
    }
}
