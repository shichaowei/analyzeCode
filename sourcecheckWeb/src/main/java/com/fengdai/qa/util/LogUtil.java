package com.fengdai.qa.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

public class LogUtil {
	private Logger log;
	//用來解決多个logback 不知道取那个logbcack文件
	static {
		load("F:\\test\\代码分析\\analyzeCode\\sourcecheckWeb\\src\\main\\resources\\logback.xml");
	}
	public LogUtil(final Class<?> loggerName) {

		log = LoggerFactory.getLogger(loggerName);
	}

	/**
	 * 记录Info级别日志。
	 *
	 * @param message
	 *            the message object.
	 */
	public void logInfo(Object message) {
		log.info("[INFO] " + message);
	}

	/**
	 * 记录Info级别日志。
	 *
	 * @param message
	 *            the message object.
	 */
	public void logDebug(Object message) {
		log.debug("[DEBUG] " + message);
	}

	/**
	 * 记录Info级别日志。
	 *
	 * @param message
	 *            the message object.
	 */
	public void logInfo(Object message, Class<Object> class1) {
		log.info("[INFO] " + message);
	}

	/**
	 * 加载外部的logback配置文件
	 *
	 * @param externalConfigFileLocation
	 *            配置文件路径
	 * @throws IOException
	 * @throws JoranException
	 */
	public static void load(String externalConfigFileLocation) {

		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		File externalConfigFile = new File(externalConfigFileLocation);

		try {
			if (!externalConfigFile.exists()) {

				throw new IOException("Logback External Config File Parameter does not reference a file that exists");

			} else {

				if (!externalConfigFile.isFile()) {
					throw new IOException("Logback External Config File Parameter exists, but does not reference a file");

				} else {

					if (!externalConfigFile.canRead()) {
						throw new IOException("Logback External Config File exists and is a file, but cannot be read.");

					} else {

						JoranConfigurator configurator = new JoranConfigurator();
						configurator.setContext(lc);
						lc.reset();
						configurator.doConfigure(externalConfigFileLocation);

						StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
					}

				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JoranException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
