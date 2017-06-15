package rocks.thiscoder.http;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author prathik.raj
 */
@RequiredArgsConstructor
@Data
public class FileUploadRequest {
    @NonNull final String csv;
    @NonNull final String sessionId;
    @NonNull final String jobId;
    @NonNull final String url;
    @NonNull final String contentType;
}
