package com.fengdai.qa.util;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class LogUtil {
	private  Logger log;


	public  LogUtil(final Class<?> loggerName) {
		log=LoggerFactory.getLogger(loggerName);
	}

	/**
	 * 记录Info级别日志。
	 *
	 * @param message the message object.
	 */
	public  void logInfo(Object message) {
		log.info("[INFO] " + message);
	}
	/**
	 * 记录Info级别日志。
	 *
	 * @param message the message object.
	 */
	public  void logDebug(Object message) {
		log.debug("[DEBUG] " + message);
	}

	/**
	 * 记录Info级别日志。
	 *
	 * @param message the message object.
	 */
	public  void logInfo(Object message,Class<Object> class1) {
		log.info("[INFO] " + message);
	}


}
