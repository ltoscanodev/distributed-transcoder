package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.lib.service.IDiscoverableService;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.springframework.stereotype.Service;

/**
 *
 * @author ltosc
 */
@Service
@Path("/distributed-transcoder")
public interface IDistributedTranscoderService extends IDiscoverableService
{
    @POST
    @Path("/createJob")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String createJob(
            @Multipart("quality") @NotNull String quality,
            @Multipart("mediaFile") @NotNull Attachment mediaFile);
    
    @GET
    @Path("/getJobResultStatus")
    @Produces(MediaType.TEXT_PLAIN)
    public String getJobResultStatus(@QueryParam("jobId") String jobId);
    
    @GET
    @Path("/getJobResult")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getJobResult(@QueryParam("jobId") String jobId);
}
