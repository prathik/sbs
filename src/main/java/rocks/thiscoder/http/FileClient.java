package rocks.thiscoder.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author prathik.raj
 */
@RequiredArgsConstructor
public class FileClient {
    @Setter
    @Getter
    Proxy proxy;

    public String uploadFile(FileUploadRequest fileUploadRequest) throws IOException {
        URL url = new URL( fileUploadRequest.getUrl() );
        URLConnection con;

        if(getProxy() != null) {
            con = url.openConnection(getProxy());
        } else {
            con = url.openConnection();
        }

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setConnectTimeout( 20000 );  // long timeout, but not infinite
        con.setReadTimeout( 20000 );
        con.setUseCaches (false);
        con.setDefaultUseCaches (false);
        con.setRequestProperty ( "Content-Type", fileUploadRequest.getContentType() );
        con.setRequestProperty ( "X-SFDC-Session",  fileUploadRequest.getSessionId());
        OutputStream outputStream = con.getOutputStream();
        outputStream.write(Files.readAllBytes(Paths.get(fileUploadRequest.getCsv())) );
        outputStream.flush();
        outputStream.close();
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
