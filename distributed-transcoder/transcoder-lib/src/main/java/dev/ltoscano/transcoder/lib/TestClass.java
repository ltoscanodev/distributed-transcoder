package dev.ltoscano.transcoder.lib;

import dev.ltoscano.transcoder.lib.logging.Logging;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class TestClass 
{
    public static void main(String[] args)
    {
        try 
        {
            Logging logging = new Logging(TestClass.class.getName());
            logging.logInfo("Logging test!");
            
            
            
//            Configuration config = Serializable.load("config.json", Configuration.class);
//            DiscoveryService discoveryService = new DiscoveryService(config);
//            discoveryService.start();
//            
//            Thread.sleep(30000);
//            
//            discoveryService.stop();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}