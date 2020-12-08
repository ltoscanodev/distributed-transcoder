package dev.ltoscano.transcoder.lib.discovering.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author ltosc
 */
public class MulticastGroupInfo
{
    private final InetAddress multicastIP;
    private final int multicastPort;
    private final NetworkInterface networkInterface;
    
    public MulticastGroupInfo(String serverIP, String multicastIP, int multicastPort) throws UnknownHostException, SocketException
    {
        this.multicastIP = InetAddress.getByName(multicastIP);
        this.multicastPort = multicastPort;
        this.networkInterface = NetworkInterface.getByInetAddress(InetAddress.getByName(serverIP));
    }
    
    /**
     * @return the multicastIP
     */
    public InetAddress getMulticastIP()
    {
        return multicastIP;
    }

    /**
     * @return the multicastPort
     */
    public int getMulticastPort() 
    {
        return multicastPort;
    }
    
    public String getStringAddress()
    {
        return String.format("%s:%s", multicastIP.getHostAddress(), multicastPort);
    }

    /**
     * @return the networkInterface
     */
    public NetworkInterface getNetworkInterface() {
        return networkInterface;
    }
}
