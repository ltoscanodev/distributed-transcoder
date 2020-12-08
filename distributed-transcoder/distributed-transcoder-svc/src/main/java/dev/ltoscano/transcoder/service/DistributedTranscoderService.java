package dev.ltoscano.transcoder.service;

import dev.ltoscano.transcoder.DistributedTranscoderServiceApplication;
import dev.ltoscano.transcoder.lib.logging.Logging;
import dev.ltoscano.transcoder.lib.service.DiscoverableService;
import dev.ltoscano.transcoder.lib.service.model.ServiceType;
import dev.ltoscano.transcoder.configuration.DistributedTranscoderServiceConfiguration;
import dev.ltoscano.transcoder.lib.service.client.TranscoderClient;
import dev.ltoscano.transcoder.service.model.JobInfo;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.StreamingOutput;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

/**
 *
 * @author ltosc
 */
public class DistributedTranscoderService extends DiscoverableService implements IDistributedTranscoderService
{
    private final Logging logging = new Logging(DistributedTranscoderServiceApplication.class.getName());
    
    private final FFmpeg ffmpeg;
    private final FFprobe ffprobe;
    
    private final ExecutorService taskExecutor;

    public DistributedTranscoderService(DistributedTranscoderServiceConfiguration serviceConfig) throws UnknownHostException, IOException
    {
        super(serviceConfig, "/distributed-transcoder", ServiceType.DISTRIBUTED_TRANSCODER_SERVICE);
        
        this.ffmpeg = new FFmpeg(serviceConfig.getFFMpegFilePath());
        this.ffprobe = new FFprobe(serviceConfig.getFFProbeFilePath());
        
        this.taskExecutor = Executors.newFixedThreadPool(8);
    }

    @Override
    public String createJob(String quality, Attachment mediaFile)
    {
        Set<String> transcoderServices = getDiscoveryService().getServiceTable().getServices(ServiceType.TRANSCODER_SERVICE);
        
        File jobsDir = new File("Data/Jobs");
        
        if(!jobsDir.exists())
        {
            jobsDir.mkdirs();
            logging.logInfo("'" + jobsDir.getAbsolutePath() + "' created");
        }
        
        JobInfo jobInfo = new JobInfo();
        
        logging.logInfo("New job: (id = '" + jobInfo.getJobId() + "')");
        
        File jobDir = Paths.get(jobsDir.getAbsolutePath(), jobInfo.getJobId()).toFile();

        if (!jobDir.exists())
        {
            jobDir.mkdirs();
            logging.logInfo("'" + jobDir.getAbsolutePath() + "' created");
        }

        File jobMediaFile = Paths.get(jobDir.getAbsolutePath(), "input.mp4").toFile();

        logging.logInfo("Writing job media file");

        try (FileOutputStream mediaOutputStream = new FileOutputStream(jobMediaFile))
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

            logging.logInfo("Media file writed: (id = '" + jobInfo.getJobId() + "' | file = '" + jobMediaFile + "')");

            File segmentDir = Paths.get(jobDir.getAbsolutePath(), "Segment").toFile();

            if (!segmentDir.exists()) 
            {
                segmentDir.mkdirs();
                logging.logInfo("'" + segmentDir.getAbsolutePath() + "' created");
            }
            
            logging.logInfo("Segmenting media file");

            String outputSegmentPath = segmentDir.getAbsolutePath() + "/segment_%03d.mp4";

            FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
                    .setInput(jobMediaFile.getAbsolutePath())
                    .addOutput(outputSegmentPath)
                    .setVideoCodec("copy")
                    .setFormat("segment")
                    .addExtraArgs("-segment_list", segmentDir.getAbsolutePath() + "/Split.ffcat")
                    .done();

            FFmpegExecutor ffmpegExecutor = new FFmpegExecutor(ffmpeg, ffprobe);
            ffmpegExecutor.createJob(ffmpegBuilder).run();
            
            logging.logInfo("Media file segmented");

            File[] segmentFiles = segmentDir.listFiles(new FilenameFilter() 
            {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mp4");
                }
            });
            
            File resultDir = Paths.get(jobDir.getAbsolutePath(), "Result").toFile();

            if (!resultDir.exists())
            {
                resultDir.mkdirs();
                logging.logInfo("'" + resultDir.getAbsolutePath() + "' created");
            }
            
            Iterator<String> transcoderIt = transcoderServices.iterator();
            
            for (File segmentFile : segmentFiles) 
            {
                String serviceURL = transcoderIt.next();
                String resultFilePath = Paths.get(resultDir.getAbsolutePath(), segmentFile.getName()).toString();
                
                taskExecutor.submit(new Runnable() 
                {
                    @Override
                    public void run() 
                    {
                        try(TranscoderClient client = new TranscoderClient(serviceURL))
                        {
                            client.createTask(quality, segmentFile.getAbsolutePath(), resultFilePath);
                        }
                        catch (IOException ex) 
                        {
                            logging.logError(ex.getMessage(), ex);
                        }
                    }
                });
                
                if(!transcoderIt.hasNext())
                {
                    transcoderIt = transcoderServices.iterator();
                }
                
                jobInfo.getPartList().add(segmentFile.getName());
            }
            
            int segmentFileCount = segmentDir.list().length;
            
            Files.move(
                    Paths.get(segmentDir.getAbsolutePath() + "/Split.ffcat"),
                    Paths.get(resultDir.getAbsolutePath() + "/Split.ffcat"),
                    StandardCopyOption.REPLACE_EXISTING);
            
            taskExecutor.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    int resultFileCount; 
                    
                    do
                    {
                        resultFileCount = resultDir.list().length;
                    }
                    while(resultFileCount < segmentFileCount);
                    
                    FFmpegBuilder ffmpegBuilder = new FFmpegBuilder()
                            .setInput(resultDir.getAbsolutePath() + "/Split.ffcat")
                            .setFormat("concat")
                            .addExtraArgs("-safe", "0")
                            .addOutput(jobDir.getAbsolutePath() + "/output.mp4")
                            .setVideoCodec("copy")
                            .done();
                    
                    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
                    executor.createJob(ffmpegBuilder).run();
                }
            });
        } 
        catch (IOException ex) 
        {
            logging.logError(ex.getMessage(), ex);
        }
        
        return jobInfo.toJson();
    }
    
    @Override
    public String getJobResultStatus(String jobId)
    {
        return (Paths.get("Data/Jobs/" + jobId, "/output.mp4").toFile().exists() ? "true" : "false");
    }

    @Override
    public StreamingOutput getJobResult(String jobId)
    {
        File jobDir = Paths.get("Data/Jobs", jobId).toFile();

        if (!jobDir.exists())
        {
            throw new RuntimeException("No job found");
        }
        
        File resultFile = Paths.get(jobDir.getAbsolutePath(), "/output.mp4").toFile();
        
        while(!resultFile.exists())
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
        
        try 
        {
            //TODO
            Thread.sleep(10000);
        } 
        catch (InterruptedException ex) 
        {
            logging.logError(ex.getMessage(), ex);
        }
        
        return new StreamingOutput() 
        {
            @Override
            public void write(final OutputStream outputStream) 
            {
                try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(resultFile)))
                {
                    byte[] buffer = new byte[4096];
                    int read;
                    
                    while((read = inputStream.read(buffer)) != -1)
                    {
                        outputStream.write(buffer, 0, read);
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
