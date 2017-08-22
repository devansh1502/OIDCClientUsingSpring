package com.gslab.oidc.client;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
/**
 * 
 * @author GS-1547
 * Class for Creating Logs to store the response.
 */
public class ClientLogging {
	class MyFormatter extends Formatter {
		// Create a DateFormat to format the logger timestamp.
		private final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder(1000);
			builder.append(df.format(new Date(record.getMillis()))).append(" - ");
			builder.append("[").append(record.getSourceClassName()).append(".");
			builder.append(record.getSourceMethodName()).append("] - ");
			builder.append("[").append(record.getLevel()).append("] - ");
			builder.append(formatMessage(record));
			builder.append("\n").append(System.getProperty("line.separator"));
			return builder.toString();
		}

		public String getHead(Handler h) {
			return super.getHead(h);
		}

		public String getTail(Handler h) {
			return super.getTail(h);
		}
	}

	public Logger logger = Logger.getLogger(ClientLogging.class.getSimpleName());

	private FileHandler fh = null;

	public ClientLogging() {
		// just to make our log file nicer
		logger.setUseParentHandlers(false);
		MyFormatter formatter = new MyFormatter();
		try {
			java.util.Date today = new java.util.Date();
			fh = new FileHandler("C:/Program Files (x86)/Java/MyLogFile" + today.getTime() + ".log");
		} catch (Exception e) {
			e.printStackTrace();
		}

		fh.setFormatter(formatter);
		logger.addHandler(fh);
	}
}
