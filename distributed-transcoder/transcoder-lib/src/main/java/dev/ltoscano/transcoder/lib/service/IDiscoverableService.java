package dev.ltoscano.transcoder.lib.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author ltosc
 */
public interface IDiscoverableService
{
    @GET
    @Path("/getServiceInfo")
    @Produces(MediaType.TEXT_PLAIN)
    public String getServiceInfo();
}
