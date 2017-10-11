package com.farmatodo.rtlogsenderdaemon;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

public interface OracleAQClient {
	
	QueueConnection getConnection() throws JMSException;
	
	QueueConnection getConnection(Rtlog rtlog) throws JMSException;
	
	void createQueue(String user, String qTable, String queueName);
	
	void sendMessage(String rtlogMessage, String rtlogName);
	
	void sendMessage(String user, String queueName, String rtlogMessage);
	
	void browseMessage(String user, String queueName);
	
	void consumeMessage(String user, String queueName);

	void sendMessage(Rtlog rtlog);

}
