package rocks.thiscoder.xml;

import lombok.Getter;
import lombok.Setter;

/**
 * @author prathik.raj
 */
public class XMLRequest {
    @Setter
    @Getter
    String argUrl;
    @Setter
    @Getter
    String content;
    @Setter
    @Getter
    String contentType;
    @Setter
    @Getter
    String sessionId;
    @Setter
    @Getter
    Boolean login;
}
