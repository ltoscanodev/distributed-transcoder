package dev.ltoscano.transcoder.lib.service.model;

import dev.ltoscano.transcoder.lib.serialization.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ltosc
 */
public class JobInfo extends Serializable
{
    private final String jobId;
    
    private final List<String> partList;
    
    public JobInfo()
    {
        this.jobId = UUID.randomUUID().toString();
        this.partList = new ArrayList<>();
    }

    /**
     * @return the jobId
     */
    public String getJobId()
    {
        return jobId;
    }

    /**
     * @return the partList
     */
    public List<String> getPartList() {
        return partList;
    }
}
