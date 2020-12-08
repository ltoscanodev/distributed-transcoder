package dev.ltoscano.transcoder.lib.serialization;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author ltosc
 */
public class Serializable 
{
    private static final Gson PARSER = new Gson();
    
    public static <T> T fromJson(String json, Class<T> classOfT)
    {
        return PARSER.fromJson(json, classOfT);
    }
    
    public String toJson()
    {
        return PARSER.toJson(this);
    }
    
    public static <T> T load(String fileName, Class<T> classOfT) throws IOException
    {
        StringBuilder jsonBuilder = new StringBuilder();
        
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            jsonBuilder.append(reader.readLine());
        }
        
        return fromJson(jsonBuilder.toString(), classOfT);
    }
    
    public void save(String fileName) throws IOException
    {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            writer.write(toJson());
        }
    }
}
