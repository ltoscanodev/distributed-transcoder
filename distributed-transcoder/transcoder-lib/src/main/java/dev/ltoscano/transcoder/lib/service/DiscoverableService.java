package dev.ltoscano.transcoder.lib.service;

import dev.ltoscano.transcoder.lib.configuration.Configuration;
import dev.ltoscano.transcoder.lib.discovering.DiscoveryService;
import dev.ltoscano.transcoder.lib.discovering.model.AdvertisementInfo;
import dev.ltoscano.transcoder.lib.discovering.model.MulticastGroupInfo;
import dev.ltoscano.transcoder.lib.service.model.ServiceInfo;
import dev.ltoscano.transcoder.lib.service.model.ServiceType;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;

/**
 *
 * @author ltosc
 */
public abstract class DiscoverableService implements IDiscoverableService
{
    private final DiscoveryService discoveryService;
    
    public DiscoverableService(Configuration serviceConfig, String servicePath, ServiceType serviceType) throws UnknownHostException, SocketException
    {
        discoveryService = new DiscoveryService(
                new MulticastGroupInfo(
                        serviceConfig.getServiceIP(),
                        serviceConfig.getMulticastIP(), 
                        serviceConfig.getMulticastPort()),
                new AdvertisementInfo(
                        new ServiceInfo(
                            "http://" + serviceConfig.getServiceIP() + ":" + serviceConfig.getServicePort() + servicePath,
                            serviceType
                        )));
        
        discoveryService.startService();
    }
    
    @Override
    public String getServiceInfo() 
    {
        StringBuilder builder = new StringBuilder();
        String lineSeparator = System.lineSeparator();
        
        builder.append("Distributed Transcoder Services:").append(lineSeparator);
        
        try
        {
            Set<String> distributedTranscoderServices = discoveryService.getServiceTable().getServices(ServiceType.DISTRIBUTED_TRANSCODER_SERVICE);
        
            for(String service : distributedTranscoderServices)
            {
                builder.append(service).append(lineSeparator);
            }
        }
        catch(RuntimeException ex)
        {
            builder.append("Empty list").append(lineSeparator);
        }
        
        builder.append(lineSeparator);
        
         builder.append("Transcoder Services:").append(lineSeparator);
        
        try
        {
            Set<String> transcoderServices = discoveryService.getServiceTable().getServices(ServiceType.TRANSCODER_SERVICE);
        
            for(String service : transcoderServices)
            {
                builder.append(service).append(lineSeparator);
            }
        }
        catch(RuntimeException ex)
        {
            builder.append("Empty list").append(lineSeparator);
        }
        
        builder.append(lineSeparator);
        
         builder.append("Stream Services:").append(lineSeparator);
        
        try
        {
            Set<String> streamServices = discoveryService.getServiceTable().getServices(ServiceType.STREAM_SERVICE);
        
            for(String service : streamServices)
            {
                builder.append(service).append(lineSeparator);
            }
        }
        catch(RuntimeException ex)
        {
            builder.append("Empty list").append(lineSeparator);
        }
        
        builder.append(lineSeparator);
        
         builder.append("Unknown Services:").append(lineSeparator);
        
        try
        {
            Set<String> unknownServices = discoveryService.getServiceTable().getServices(ServiceType.UNKNOWN);
        
            for(String service : unknownServices)
            {
                builder.append(service).append(lineSeparator);
            }
        }
        catch(RuntimeException ex)
        {
            builder.append("Empty list").append(lineSeparator);
        }
        
        builder.append(lineSeparator);
        
        return builder.toString();
    }
    
    protected DiscoveryService getDiscoveryService()
    {
        return discoveryService;
    }
}
