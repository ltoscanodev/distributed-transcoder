package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.lib.service.IDiscoverableService;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
@Path("/transcoder")
public interface ITranscoderService extends IDiscoverableService
{
    @POST
    @Path("/createTask")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput createTask(
            @Multipart("quality") @NotNull String quality, 
            @Multipart("mediaFile") @NotNull Attachment mediaFile);
}
