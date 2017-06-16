package rocks.thiscoder.sbs;


import lombok.Getter;
import lombok.NonNull;
import rocks.thiscoder.sbs.models.JobResult;

import java.io.File;
import java.util.List;

/**
 * @author prathik.raj
 */
public class Batch {
    @Getter
    final File csv;
    @Getter
    final Salesforce salesforce;

    public Batch(@NonNull File csv, @NonNull Salesforce salesforce) throws SalesforceException {
        this.csv = csv;
        this.salesforce = salesforce;
        if(!csv.isFile()) {
            throw new SalesforceException("Invalid file");
        }

        if(salesforce.getSessionId() == null || salesforce.getInstance() == null) {
            throw new SalesforceException("Uninitialized salesforce object passed");
        }
    }

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
