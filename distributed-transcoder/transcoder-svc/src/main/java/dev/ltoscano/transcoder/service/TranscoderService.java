package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.configuration.TranscoderServiceConfiguration;
import dev.ltoscano.transcoder.lib.logging.Logging;
import dev.ltoscano.transcoder.lib.service.DiscoverableService;
import dev.ltoscano.transcoder.lib.service.model.ServiceType;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.UUID;
import javax.ws.rs.core.StreamingOutput;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

/**
 *
 * @author ltosc
 */
public class TranscoderService extends DiscoverableService implements ITranscoderService
{
    private final Logging logging = new Logging(TranscoderService.class.getName());
    
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    
    public TranscoderService(TranscoderServiceConfiguration serviceConfig) throws UnknownHostException, IOException
    {
        super(serviceConfig, "/transcoder", ServiceType.TRANSCODER_SERVICE);
        
        this.ffmpeg = new FFmpeg(serviceConfig.getFFMpegFilePath());
        this.ffprobe = new FFprobe(serviceConfig.getFFProbeFilePath());
    }

    @Override
    public StreamingOutput createTask(String quality, Attachment mediaFile) 
    {
        File tasksDir = new File("Data/Tasks");
        
        if(!tasksDir.exists())
        {
            tasksDir.mkdirs();
            logging.logInfo("'" + tasksDir.getAbsolutePath() + "' created");
        }
        
        String jobId = UUID.randomUUID().toString();
        
        File taskDir = Paths.get(tasksDir.getAbsolutePath(), jobId).toFile();

        if (!taskDir.exists())
        {
            taskDir.mkdirs();
            logging.logInfo("'" + taskDir.getAbsolutePath() + "' created");
        }

        File taskMediaFile = Paths.get(taskDir.getAbsolutePath(), "input.mp4").toFile();

        logging.logInfo("Writing task media file");

        try (FileOutputStream mediaOutputStream = new FileOutputStream(taskMediaFile))
        {
            try (InputStream mediaInputStream = mediaFile.getDataHandler().getInputStream()) 
            {
                byte[] dataBuffer = new byte[4096];
                int read;

                while ((read = mediaInputStream.read(dataBuffer)) != -1) 
                {
                    mediaOutputStream.write(dataBuffer, 0, read);
                }
            }

            logging.logInfo("Media file writed: (file = '" + taskMediaFile + "')");
            
            String outputPath = taskDir.getAbsolutePath() + "/output.mp4";

            FFmpegOutputBuilder ffmpegOutputBuilder = new FFmpegBuilder()
                    .setInput(taskMediaFile.getAbsolutePath())
                    .addOutput(outputPath);
            
            switch(quality)
            {
                case "240p":
                {
                    ffmpegOutputBuilder.setVideoResolution(320, 240);
                    break;
                }
                case "480p":
                {
                    ffmpegOutputBuilder.setVideoResolution(640, 480);
                    break;
                }
                case "720p":
                {
                    ffmpegOutputBuilder.setVideoResolution(1280, 720);
                    break;
                }
                case "1080p":
                {
                    ffmpegOutputBuilder.setVideoResolution(1920, 1080);
                    break;
                }
                case "2160p":
                {
                    ffmpegOutputBuilder.setVideoResolution(3840, 2160);
                    break;
                }
                case "4320p":
                {
                    ffmpegOutputBuilder.setVideoResolution(7680, 4320);
                    break;
                }
                default:
                {
                    throw new RuntimeException("Invalid quality option");
                }
            }
            
            FFmpegBuilder ffmpegBuilder = ffmpegOutputBuilder.done();

            FFmpegExecutor ffmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
            ffmpegExecutor.createJob(ffmpegBuilder).run();
            
            logging.logInfo("Media task completed");
        } 
        catch (IOException ex) 
        {
            logging.logError(ex.getMessage(), ex);
        }
        
        return new StreamingOutput() 
        {
            @Override
            public void write(final OutputStream outputStream) 
            {
                String outputPath = taskDir.getAbsolutePath() + "/output.mp4";
                
                try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(outputPath)))
                {
                    byte[] buffer = new byte[4096];
                    
                    while(inputStream.read(buffer) > 0)
                    {
                        outputStream.write(buffer);
                    }
                } 
                catch (FileNotFoundException ex)
                {
                    logging.logError(ex.getMessage(), ex);
                }
                catch (IOException ex) 
                {
                    logging.logError(ex.getMessage(), ex);
                }
            }
        };
    }
}