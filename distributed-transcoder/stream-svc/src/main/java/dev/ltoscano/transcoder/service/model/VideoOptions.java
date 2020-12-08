package dev.ltoscano.transcoder.service.model;

/**
 *
 * @author ltosc
 */
public class VideoOptions
{
    private final String qvgaStreamFilePath;
    private final String vgaStreamFilePath;
    private final String hdStreamFilePath;
    private final String fullHDStreamFilePath;
    private final String ultraHDStreamFilePath;
    private final String doubleUHDStreamFilePath;
    
    public VideoOptions(String qvgaStreamFilePath, String vgaStreamFilePath, String hdStreamFilePath,
            String fullHDFilePath, String ultraHDFilePath, String doubleUHDStreamFilePath)
    {
        this.qvgaStreamFilePath = qvgaStreamFilePath;
        this.vgaStreamFilePath = vgaStreamFilePath;
        this.hdStreamFilePath = hdStreamFilePath;
        this.fullHDStreamFilePath = fullHDFilePath;
        this.ultraHDStreamFilePath = ultraHDFilePath;
        this.doubleUHDStreamFilePath = doubleUHDStreamFilePath;
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
     * @return the fullHDStreamFilePath
     */
    public String getFullHDStreamFilePath() {
        return fullHDStreamFilePath;
    }

    /**
     * @return the ultraHDStreamFilePath
     */
    public String getUltraHDStreamFilePath() {
        return ultraHDStreamFilePath;
    }

    /**
     * @return the doubleUHDStreamFilePath
     */
    public String getDoubleUHDStreamFilePath() {
        return doubleUHDStreamFilePath;
    }
}
