package rocks.thiscoder.sbs.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author prathik.raj
 */
@RequiredArgsConstructor
public class UploadRequest {
    @Getter
    @Setter
    final String sfObject;
    @Getter
    @Setter
    final String type;
    @Getter
    @Setter
    final String externalIdFieldName;
}
