package dev.ltoscano.transcoder.configuration;

import dev.ltoscano.transcoder.lib.configuration.Configuration;

/**
 *
 * @author ltosc
 */
public class StreamServiceConfiguration extends Configuration
{
    private final String qvgaStreamFilePath;
    private final String vgaStreamFilePath;
    private final String hdStreamFilePath;
    private final String fullHDFilePath;
    private final String ultraHDFilePath;
    private final String doubleUHDStreamFilePath;
    
    public StreamServiceConfiguration()
    {
        super("127.0.0.1", 8081);
        
        this.qvgaStreamFilePath = "Data/Video/qvga.mp4";
        this.vgaStreamFilePath = "Data/Video/vga.mp4";
        this.hdStreamFilePath = "Data/Video/hd.mp4";
        this.fullHDFilePath = "Data/Video/fullHD.mp4";
        this.ultraHDFilePath = "Data/Video/ultraHD.mp4";
        this.doubleUHDStreamFilePath = "Data/Video/doubleUHD.mp4";
    }

    /**
     * @return the qvgaStreamFilePath
     */
    public String getQvgaStreamFilePath() {
        return qvgaStreamFilePath;
    }

    /**
     * @return the vgaStreamFilePath
     */
    public String getVgaStreamFilePath() {
        return vgaStreamFilePath;
    }

    /**
     * @return the hdStreamFilePath
     */
    public String getHdStreamFilePath() {
        return hdStreamFilePath;
    }

    /**
     * @return the fullHDFilePath
     */
    public String getFullHDFilePath() {
        return fullHDFilePath;
    }

    /**
     * @return the ultraHDFilePath
     */
    public String getUltraHDFilePath() {
        return ultraHDFilePath;
    }

    /**
     * @return the doubleUHDStreamFilePath
     */
    public String getDoubleUHDStreamFilePath() {
        return doubleUHDStreamFilePath;
    }

}
