package dev.ltoscano.transcoder.configuration;

import dev.ltoscano.transcoder.lib.configuration.Configuration;

/**
 *
 * @author ltosc
 */
public class DistributedTranscoderServiceConfiguration extends Configuration
{
    private final String ffmpegFilePath;
    private final String ffprobeFilePath;
    
    public DistributedTranscoderServiceConfiguration()
    {
        super("127.0.0.1", 8082);
        
        this.ffmpegFilePath = "ffmpeg";
        this.ffprobeFilePath = "ffprobe";
    }

    /**
     * @return the ffmpegFilePath
     */
    public String getFFMpegFilePath() {
        return ffmpegFilePath;
    }

    /**
     * @return the ffprobeFilePath
     */
    public String getFFProbeFilePath() {
        return ffprobeFilePath;
    }
}
