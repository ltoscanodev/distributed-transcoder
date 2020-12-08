package dev.ltoscano.transcoder.lib.discovering.model;

import dev.ltoscano.transcoder.lib.serialization.Serializable;
import dev.ltoscano.transcoder.lib.service.model.ServiceInfo;

/**
 *
 * @author ltosc
 */
public class AdvertisementInfo extends Serializable
{
    private final ServiceInfo serviceInfo;
    
    public AdvertisementInfo(ServiceInfo serviceInfo)
    {
        this.serviceInfo = serviceInfo;
    }

    /**
     * @return the serviceInfo
     */
    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }
}
