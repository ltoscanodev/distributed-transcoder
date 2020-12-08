package dev.ltoscano.transcoder.lib.configuration;

import dev.ltoscano.transcoder.lib.serialization.Serializable;

/**
 *
 * @author ltosc
 */
public class Configuration extends Serializable
{
    private final String serviceIP;
    private final int servicePort;
    
    private final String multicastIP;
    private final int multicastPort;
    
    public Configuration()
    {
        this.serviceIP = "127.0.0.1";
        this.servicePort = 8888;
        
        this.multicastIP = "230.0.0.0";
        this.multicastPort = 8888;
    }
    
    public Configuration(String serviceIP, int servicePort)
    {
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        
        this.multicastIP = "230.0.0.0";
        this.multicastPort = 8888;
    }

    /**
     * @return the serviceIP
     */
    public String getServiceIP() {
        return serviceIP;
    }

    /**
     * @return the servicePort
     */
    public int getServicePort() {
        return servicePort;
    }

    /**
     * @return the multicastIP
     */
    public String getMulticastIP() {
        return multicastIP;
    }

    /**
     * @return the multicastPort
     */
    public int getMulticastPort() {
        return multicastPort;
    }

}
