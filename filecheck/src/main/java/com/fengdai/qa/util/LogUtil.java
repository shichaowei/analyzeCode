package com.fengdai.qa.util;



import org.slf4j.LoggerFactory;
import org.slf4j.Logger;




public class LogUtil {
    private static Logger log;


	public  LogUtil(final Class<?> loggerName) {
		log=LoggerFactory.getLogger(loggerName);
	}

    /**
     * 记录Info级别日志。
     *
     * @param message the message object.
     */
    public static void logInfo(Object message) {
        log.info("[INFO] " + message);
    }
    
    /**
     * 记录Info级别日志。
     *
     * @param message the message object.
     */
    public static void logInfo(Object message,Class<Object> class1) {
    	log.info("[INFO] " + message);
    }


}
