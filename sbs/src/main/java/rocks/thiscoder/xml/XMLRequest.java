package rocks.thiscoder.xml;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author prathik.raj
 */
@ToString
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
