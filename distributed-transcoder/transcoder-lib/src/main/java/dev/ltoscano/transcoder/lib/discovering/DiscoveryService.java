package dev.ltoscano.transcoder.lib.discovering;

import dev.ltoscano.transcoder.lib.discovering.model.AdvertisementInfo;
import dev.ltoscano.transcoder.lib.discovering.model.MulticastGroupInfo;
import dev.ltoscano.transcoder.lib.logging.Logging;
import dev.ltoscano.transcoder.lib.service.model.ServiceInfoTable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class DiscoveryService implements Runnable
{
    private final Logging logging = new Logging(DiscoveryService.class.getName());
    
    private final MulticastGroupInfo multicastGroupInfo;
    private final InetSocketAddress multicastSocketAddress;
    private MulticastSocket multicastSocket;
    
    private final AdvertisementInfo advertisementInfo;
    
    private boolean running;
    private Thread discoveryThread;
    
    private Timer advertisingTimer;
    
    private final ServiceInfoTable serviceTable;
    
    private class AdvertisingTask extends TimerTask
    {
        private final AdvertisementInfo advertisementInfo;
        
        public AdvertisingTask(AdvertisementInfo advertisementInfo)
        {
            this.advertisementInfo = advertisementInfo;
        }
        
        @Override
        public void run() 
        {
            byte[] sendBuffer = advertisementInfo.toJson().getBytes();
            
            try(DatagramSocket udpSocket = new DatagramSocket())
            {
                udpSocket.send(
                        new DatagramPacket(
                                sendBuffer, 
                                sendBuffer.length, 
                                multicastGroupInfo.getMulticastIP(), 
                                multicastGroupInfo.getMulticastPort()));
                
                logging.logInfo("Advertisement package sent to " + multicastGroupInfo.getStringAddress());
            } 
            catch (SocketException ex)
            {
                logging.logError(ex.getMessage(), ex);
            } 
            catch (UnknownHostException ex) 
            {
                logging.logError(ex.getMessage(), ex);
            } 
            catch (IOException ex)
            {
                logging.logError(ex.getMessage(), ex);
            }
        }
    }
    
    public DiscoveryService(MulticastGroupInfo multicastGroupInfo, AdvertisementInfo advertisementInfo)
    {
        this.multicastGroupInfo = multicastGroupInfo;
        this.multicastSocketAddress = new InetSocketAddress(
                multicastGroupInfo.getMulticastIP(), 
                multicastGroupInfo.getMulticastPort());
        
        this.advertisementInfo = advertisementInfo;
        
        this.serviceTable = new ServiceInfoTable();
    }
    
    public void startService()
    {
        startService(10000);
    }
    
    public void startService(long advertisementPeriod)
    {
        running = true;
        
        discoveryThread = new Thread(this);
        discoveryThread.start();
        
        advertisingTimer = new Timer();
        advertisingTimer.schedule(new AdvertisingTask(advertisementInfo), 0, advertisementPeriod);
        
        logging.logInfo("Discovery service started");
    }
    
    public void stopService() throws InterruptedException
    {
        running = false;
        
        advertisingTimer.cancel();
        
        multicastSocket.close();
        discoveryThread.join(10000);
        
        logging.logInfo("Discovery service stopped");
    }

    @Override
    public void run() 
    {
        try
        {
            multicastSocket = new MulticastSocket(multicastGroupInfo.getMulticastPort());
            
            multicastSocket.joinGroup(
                    multicastSocketAddress,
                    multicastGroupInfo.getNetworkInterface());
            
            logging.logInfo("Joined the multicast group: " + multicastGroupInfo.getStringAddress());
            
            byte[] receiveBuffer = new byte[4096];
            
            while (running) 
            {
                try
                {
                    DatagramPacket udpPackage = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    multicastSocket.receive(udpPackage);

                    AdvertisementInfo receivedAdvertisement = AdvertisementInfo.fromJson(
                            new String(udpPackage.getData(), 0, udpPackage.getLength()), 
                            AdvertisementInfo.class);
                    
                    getServiceTable().addService(receivedAdvertisement.getServiceInfo());
                    
                    logging.logInfo("New advertisement received: " + receivedAdvertisement.getServiceInfo().toString());
                }
                catch(SocketException ex)
                {
                    Logger.getLogger(DiscoveryService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } 
        catch (IOException ex) 
        {
            logging.logError(ex.getMessage(), ex);
        }
        finally
        {
            if(multicastSocket != null)
            {
                try 
                {
                    multicastSocket.leaveGroup(
                            multicastSocketAddress,
                            multicastGroupInfo.getNetworkInterface());
                    
                    multicastSocket.close();
                    
                    logging.logInfo("Leaved the multicast group: %s" + multicastGroupInfo.getStringAddress());
                } 
                catch (IOException ex) 
                {
                    logging.logError(ex.getMessage(), ex);
                }
            }
        }
    }
    
    /**
     * @return the serviceTable
     */
    public ServiceInfoTable getServiceTable() 
    {
        return serviceTable;
    }
}
