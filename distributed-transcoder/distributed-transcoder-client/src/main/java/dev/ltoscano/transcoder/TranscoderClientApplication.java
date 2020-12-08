package dev.ltoscano.transcoder;

import dev.ltoscano.transcoder.lib.service.client.TranscoderClient;
import dev.ltoscano.transcoder.lib.service.model.JobInfo;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ltosc
 */
public class TranscoderClientApplication 
{
    public static void main(String[] args)
    {
        try
        {
            try(TranscoderClient client = new TranscoderClient("http://192.168.1.120:8082/distributed-transcoder"))
            {
                JobInfo jobInfo = client.createJob("240p", "Data/input.mp4");
                
                while(!client.getJobResultStatus(jobInfo.getJobId()))
                {
                    Thread.sleep(100);
                }
                
                try(FileOutputStream fileOutputStream = new FileOutputStream("Data/output.mp4"))
                {
                    InputStream inputStream = client.getJobResult(jobInfo.getJobId());
                    
                    byte[] buffer = new byte[4096];
                    int read;
                    
                    while((read = inputStream.read(buffer)) != -1)
                    {
                        fileOutputStream.write(buffer, 0, read);
                    }
                }
                
                Process ffplay = Runtime.getRuntime().exec("D:/ffmpeg/ffplay.exe -x 800 -y 600 Data/output.mp4");
            }
        }
        catch(Exception ex)
        {
            Logger.getLogger(TranscoderClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
