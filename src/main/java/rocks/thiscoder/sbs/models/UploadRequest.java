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
    @NonNull
    @Getter
    @Setter
    final String csv;
    @Getter
    @Setter
    @NonNull
    final String sfObject;
    @Getter
    @Setter
    @NonNull
    final String type;
}
