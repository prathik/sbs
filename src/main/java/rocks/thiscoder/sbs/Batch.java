package rocks.thiscoder.sbs;


import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import rocks.thiscoder.sbs.models.JobResult;

import java.util.List;

/**
 * @author prathik.raj
 */
@RequiredArgsConstructor
public class Batch {
    @Getter
    final String csv;
    @Getter
    final Salesforce salesforce;

    @Getter
    String jobId;

    @Getter
    String batchId;

    public void setJobId(@NonNull String jobId) {
        this.jobId = jobId;
    }

    public void setBatchId(@NonNull String batchId) {
        this.batchId = batchId;
    }

    public String checkStatus() {
        return null;
    }

    public List<JobResult> jobResults() {
        return null;
    }
}
