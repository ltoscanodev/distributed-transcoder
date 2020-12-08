package dev.ltoscano.transcoder;

import dev.ltoscano.transcoder.configuration.StreamServiceConfiguration;
import dev.ltoscano.transcoder.lib.configuration.Configuration;
import dev.ltoscano.transcoder.service.StreamService;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class StreamServiceApplication 
{
    @Autowired
    private Bus bus;
    
    private static String configFilePath;
    
    private StreamServiceConfiguration loadConfiguration() throws IOException
    {
        File serviceDir = new File("Data");
        
        if(!serviceDir.exists())
        {
            serviceDir.mkdirs();
        }
        
        String cfgPath = Paths.get(serviceDir.getAbsolutePath(), configFilePath).toAbsolutePath().toString();
        
        return Configuration.load(cfgPath, StreamServiceConfiguration.class);
    }
    
    @Bean
    public Server rsServer() 
    {
        StreamServiceConfiguration serviceConfig;
        
        try 
        {
            serviceConfig = loadConfiguration();
            
        } 
        catch (IOException ex0) 
        {
            serviceConfig = new StreamServiceConfiguration();
            
            try 
            {
                serviceConfig.save("Data/config.json");
            } 
            catch (IOException ex1) 
            {
                Logger.getLogger(StreamServiceApplication.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        
        try 
        {
            endpoint.setServiceBeans(Arrays.<Object>asList(new StreamService(serviceConfig)));
            endpoint.setFeatures(Arrays.asList(createOpenApiFeature(), createSwaggerFeature()));
            endpoint.setAddress("/");
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(StreamServiceApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return endpoint.create();
    }

    private OpenApiFeature createOpenApiFeature() 
    {
        final OpenApiFeature openApiFeature = new OpenApiFeature();
        
        openApiFeature.setPrettyPrint(true);
        
        openApiFeature.setTitle("Video Stream Service");
        openApiFeature.setDescription("Serviço de streaming de vídeo");
        openApiFeature.setVersion("1.0.0");
        
        openApiFeature.setContactName("Luis Augusto Toscano Guimarães");
        openApiFeature.setContactEmail("contato@ltoscano.dev");
        openApiFeature.setContactUrl("https://ltoscano.dev/");
        
        return openApiFeature;
    }
    
    private Swagger2Feature createSwaggerFeature() 
    {
        final Swagger2Feature swaggerFeature = new Swagger2Feature();
        swaggerFeature.setSwaggerUiConfig(new SwaggerUiConfig().url("/openapi.json"));
        
        return swaggerFeature;
    }
    
    public static void main(String[] args) 
    {
        switch(args.length)
        {
            case 1:
            {
                configFilePath = args[0];
                break;
            }
            default:
            {
                configFilePath = "/config.json";
            }
        }
        
        SpringApplication.run(StreamServiceApplication.class, args);
    }
}
