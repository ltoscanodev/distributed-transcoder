package dev.ltoscano.transcoder.lib.service.model;

import dev.ltoscano.transcoder.lib.serialization.Serializable;

/**
 *
 * @author ltosc
 */
public class ServiceInfo extends Serializable
{
    private final String serviceUrl;
    private final ServiceType serviceType;
    
    public ServiceInfo(String serviceUrl, String serviceType)
    {
        this.serviceUrl = serviceUrl;
        
        switch(serviceType.toUpperCase())
        {
            case "DISTRIBUTED_TRANSCODER_SERVICE":
            {
                this.serviceType = ServiceType.DISTRIBUTED_TRANSCODER_SERVICE;
                break;
            }
            case "TRANSCODER_SERVICE":
            {
                this.serviceType = ServiceType.TRANSCODER_SERVICE;
                break;
            }
            case "STREAM_SERVICE":
            {
                this.serviceType = ServiceType.STREAM_SERVICE;
                break;
            }
            default:
            {
                this.serviceType = ServiceType.UNKNOWN;
            }
        }
    }
    
    public ServiceInfo(String serviceUrl, ServiceType serviceType)
    {
        this.serviceUrl = serviceUrl;
        this.serviceType = serviceType;
    }

    /**
     * @return the serviceUrl
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * @return the serviceType
     */
    public ServiceType getServiceType() {
        return serviceType;
    }
    
    @Override
    public String toString()
    {
        return String.format("%s (%s)", serviceUrl, serviceType.name());
    }
}
