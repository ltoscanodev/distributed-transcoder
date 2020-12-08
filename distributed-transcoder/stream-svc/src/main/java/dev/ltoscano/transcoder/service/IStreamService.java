package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.lib.service.IDiscoverableService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;
import org.springframework.stereotype.Service;

/**
 *
 * @author ltosc
 */
@Service
@Path("/stream")
public interface IStreamService extends IDiscoverableService
{
    @GET
    @Path("/getStream")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public StreamingOutput getStream(@QueryParam("quality") String quality);
}
