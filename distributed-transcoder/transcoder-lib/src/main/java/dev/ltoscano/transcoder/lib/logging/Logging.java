package dev.ltoscano.transcoder.lib.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author ltosc
 */
public class Logging 
{
    private final Logger logger;
    
    public Logging(String loggerName)
    {
        this.logger = LogManager.getLogger(loggerName);
    }
    
    public void logTrace(String msg)
    {
        logger.trace(msg);
    }
    
    public void logDebug(String msg)
    {
        logger.debug(msg);
    }
    
    public void logInfo(String msg)
    {
        logger.info(msg);
    }
    
    public void logWarn(String msg)
    {
        logger.warn(msg);
    }
    
    public void logWarn(String msg, Throwable ex)
    {
        logger.warn(msg, ex);
    }
    
    public void logError(String msg)
    {
        logger.error(msg);
    }
    
    public void logError(String msg, Throwable ex)
    {
        logger.error(msg, ex);
    }
    
    public void logFatal(String msg)
    {
        logger.fatal(msg);
    }
    
    public void logFatal(String msg, Throwable ex)
    {
        logger.fatal(msg, ex);
    }
}
