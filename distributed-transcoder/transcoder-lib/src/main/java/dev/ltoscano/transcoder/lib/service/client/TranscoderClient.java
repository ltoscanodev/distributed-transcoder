package dev.ltoscano.transcoder.lib.service.client;

import dev.ltoscano.transcoder.lib.service.model.JobInfo;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author ltosc
 */
public class TranscoderClient implements Closeable
{
    private final CloseableHttpClient httpClient;
    private final String serviceBaseURL;
    
    public TranscoderClient(String serviceBaseURL)
    {
        this.serviceBaseURL = serviceBaseURL;
        this.httpClient = HttpClients.createDefault();
    }
    
    public JobInfo createJob(String quality, String mediaFilePath) throws FileNotFoundException, IOException
    {
        HttpPost request = new HttpPost(serviceBaseURL + "/createJob");
        
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        
        entityBuilder.addTextBody("quality", quality);
        
        File mediaFile = new File(mediaFilePath);
        
        entityBuilder.addBinaryBody(
                "mediaFile",
                new FileInputStream(mediaFile),
                ContentType.APPLICATION_OCTET_STREAM,
                mediaFile.getName()
        );
        
        HttpEntity multipartEntity = entityBuilder.build();
        request.setEntity(multipartEntity);
        
        CloseableHttpResponse response = httpClient.execute(request);
        
        try(InputStream responseInputStream = response.getEntity().getContent())
        {
            byte[] dataBuffer = responseInputStream.readAllBytes();
            String jsonResponse = new String(dataBuffer, 0, dataBuffer.length);
            
            return JobInfo.fromJson(jsonResponse, JobInfo.class);
        }
    }
    
    public void createTask(String quality, String mediaFilePath, String outputFilePath) throws FileNotFoundException, IOException
    {
        HttpPost request = new HttpPost(serviceBaseURL + "/createTask");
        
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        
        entityBuilder.addTextBody("quality", quality);
        
        File mediaFile = new File(mediaFilePath);
        
        entityBuilder.addBinaryBody(
                "mediaFile",
                new FileInputStream(mediaFile),
                ContentType.APPLICATION_OCTET_STREAM,
                mediaFile.getName()
        );
        
        HttpEntity multipartEntity = entityBuilder.build();
        request.setEntity(multipartEntity);
        
        CloseableHttpResponse response = httpClient.execute(request);
        
        try(InputStream responseInputStream = response.getEntity().getContent())
        {
            try(FileOutputStream fileOutputStream = new FileOutputStream(outputFilePath))
            {
                byte[] dataBuffer = new byte[4096];
                int read;
                
                while((read = responseInputStream.read(dataBuffer)) != -1)
                {
                    fileOutputStream.write(dataBuffer, 0, read);
                }
            }
        }
    }
    
    public boolean getJobResultStatus(String jobId) throws IOException
    {
        HttpGet request = new HttpGet(serviceBaseURL + "/getJobResultStatus?jobId=" + jobId);
        CloseableHttpResponse response = httpClient.execute(request);
        
        String status = new String(response.getEntity().getContent().readAllBytes());
        
        return status.equalsIgnoreCase("true");
    }
    
    public InputStream getJobResult(String jobId) throws IOException
    {
        HttpGet request = new HttpGet(serviceBaseURL + "/getJobResult?jobId=" + jobId);
        CloseableHttpResponse response = httpClient.execute(request);
        
        return response.getEntity().getContent();
    }

    @Override
    public void close() throws IOException 
    {
        httpClient.close();
    }
}
