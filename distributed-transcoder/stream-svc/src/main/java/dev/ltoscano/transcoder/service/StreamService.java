package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.configuration.StreamServiceConfiguration;
import dev.ltoscano.transcoder.lib.logging.Logging;
import dev.ltoscano.transcoder.lib.service.DiscoverableService;
import dev.ltoscano.transcoder.lib.service.client.TranscoderClient;
import dev.ltoscano.transcoder.lib.service.model.JobInfo;
import dev.ltoscano.transcoder.lib.service.model.ServiceType;
import dev.ltoscano.transcoder.service.model.VideoOptions;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.StreamingOutput;

/**
 *
 * @author ltosc
 */
public class StreamService extends DiscoverableService implements IStreamService
{
    private final Logging logging = new Logging(StreamService.class.getName());
    
    private final VideoOptions videoOptions;

    public StreamService(StreamServiceConfiguration serviceConfig) throws UnknownHostException
    {
        super(serviceConfig, "/stream", ServiceType.STREAM_SERVICE);
        
        this.videoOptions = new VideoOptions(
                serviceConfig.getQvgaStreamFilePath(),
                serviceConfig.getVgaStreamFilePath(),
                serviceConfig.getHdStreamFilePath(),
                serviceConfig.getFullHDFilePath(),
                serviceConfig.getUltraHDFilePath(),
                serviceConfig.getDoubleUHDStreamFilePath()
        );
    }
    
    @Override
    public StreamingOutput getStream(String quality) 
    {
        Set<String> distributedTranscoderServices = getDiscoveryService().getServiceTable().getServices(ServiceType.DISTRIBUTED_TRANSCODER_SERVICE);
        String serviceURL = distributedTranscoderServices.iterator().next();
        
        try(TranscoderClient client = new TranscoderClient(serviceURL))
        {
            JobInfo jobInfo = client.createJob(quality, videoOptions.getQvgaStreamFilePath());
            
            while (!client.getJobResultStatus(jobInfo.getJobId()))
            {
                try 
                {
                    Thread.sleep(100);
                } 
                catch (InterruptedException ex) 
                {
                    logging.logError(ex.getMessage(), ex);
                }
            }
            
            return new StreamingOutput()
            {
                @Override
                public void write(final OutputStream outputStream) 
                {
                    InputStream inputStream = null;
                    
                    try 
                    {
                        inputStream = client.getJobResult(jobInfo.getJobId());
                        
                        byte[] buffer = new byte[4096];
                        int read;
                        
                        while((read = inputStream.read(buffer)) != -1)
                        {
                            outputStream.write(buffer, 0, read);
                        }
                    } 
                    catch (IOException ex)
                    {
                        logging.logError(ex.getMessage(), ex);
                    } 
                    finally 
                    {
                        if(inputStream != null)
                        {
                            try 
                            {
                                inputStream.close();
                            }
                            catch (IOException ex) 
                            {
                                logging.logError(ex.getMessage(), ex);
                            }
                        }
                    }
                }
            };
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
//    @Override
//    public StreamingOutput getStream(String quality) 
//    {
//        String streamFilePath;
//        
//        switch(quality)
//        {
//            case "240p":
//            {
//                streamFilePath = videoOptions.getQvgaStreamFilePath();
//                break;
//            }
//            case "480p":
//            {
//                streamFilePath = videoOptions.getVgaStreamFilePath();
//                break;
//            }
//            case "720p":
//            {
//                streamFilePath = videoOptions.getHdStreamFilePath();
//                break;
//            }
//            case "1080p":
//            {
//                streamFilePath = videoOptions.getFullHDStreamFilePath();
//                break;
//            }
//            case "2160p":
//            {
//                streamFilePath = videoOptions.getUltraHDStreamFilePath();
//                break;
//            }
//            case "4320p":
//            {
//                streamFilePath = videoOptions.getDoubleUHDStreamFilePath();
//                break;
//            }
//            default:
//            {
//                streamFilePath = videoOptions.getQvgaStreamFilePath();
//            }
//        }
//        
//        return new StreamingOutput() 
//        {
//            @Override
//            public void write(final OutputStream outputStream) 
//            {
//                try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(streamFilePath)))
//                {
//                    byte[] buffer = new byte[4096];
//                    
//                    while(inputStream.read(buffer) > 0)
//                    {
//                        outputStream.write(buffer);
//                    }
//                } 
//                catch (FileNotFoundException ex)
//                {
//                    logging.logError(ex.getMessage(), ex);
//                }
//                catch (IOException ex) 
//                {
//                    logging.logError(ex.getMessage(), ex);
//                }
//            }
//        };
//    }
}