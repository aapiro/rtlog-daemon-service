package com.farmatodo.rtlogsenderdaemon;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.stereotype.Repository;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

import oracle.AQ.AQQueueTable;
import oracle.AQ.AQQueueTableProperty;
import oracle.jms.AQjmsDestination;
import oracle.jms.AQjmsDestinationProperty;
import oracle.jms.AQjmsFactory;
import oracle.jms.AQjmsSession;

@Repository
public class OracleAQClientImpl implements OracleAQClient {

	protected Logger log = Logger.getLogger(OracleAQClientImpl.class.getName());
	/**
	 * Waiting for DI way
	 * @param string5 
	 * @param string4 
	 * @param string3 
	 * @param oracle_sid 
	 * @param queueHostname 
	 * @return QueueConnection
	 * @throws JMSException
	 */
	public QueueConnection getConnection(Rtlog rtlog) throws JMSException {
		QueueConnectionFactory QFac = null;
		QueueConnection QCon = null;
		log.info("OracleAQClientImpl getQueueHostname  "+rtlog.getQueueHostname());
		log.info("OracleAQClientImpl getOracle_sid  "+rtlog.getOracleSid());
		log.info("OracleAQClientImpl getOracle_sid2  "+rtlog.getOracleSid2());
		log.info("OracleAQClientImpl getQueuePort  "+rtlog.getQueuePort());
		log.info("OracleAQClientImpl getQueueDriver  "+rtlog.getQueueDriver());
		try {
			QFac = AQjmsFactory.getQueueConnectionFactory(
					rtlog.getQueueHostname(),
					rtlog.getOracleSid(),
//					"simqa1",
					Integer.parseInt(rtlog.getQueuePort()),
					rtlog.getQueueDriver());
			QCon = QFac.createQueueConnection(
					rtlog.getQueueUserName(),
					rtlog.getQueuePassword());
		} catch (Exception e) {
			log.info("CONNECTION SID 1 TO THE QUEUE NOT FOUND "+rtlog.getOracleSid());
//			e.printStackTrace();
			try {
				log.info("CONNECTING TO SID 2 "+rtlog.getOracleSid2());
				QFac = AQjmsFactory.getQueueConnectionFactory(
						rtlog.getQueueHostname(),
						rtlog.getOracleSid2(),
						Integer.parseInt(rtlog.getQueuePort()),
						rtlog.getQueueDriver());
				QCon = QFac.createQueueConnection(
						rtlog.getQueueUserName(),
						rtlog.getQueuePassword());
			} catch (Exception e2) {
				log.info("CONNECTION SID 2 TO THE QUEUE NOT FOUND "+rtlog.getOracleSid2());
				e2.printStackTrace();
			}
		}
		return QCon;
	}
//	public static QueueConnection getConnection() throws JMSException {
//		
//		// String hostname = "dsved-test22-scan.farmatodo.com"; // T2
//		// String oracle_sid = "simqa1";//T2
//		String hostname = "dsved-cont2-scan.farmatodo.com"; // T3
//		String oracle_sid = "simpd1";// T3
//		String oracle_sid2 = "simpd2";// T3
//		int portno = 1521;
//		String userName = "pos";
//		String password = "pos12qa";
//		String driver = "thin";
//		
//		QueueConnectionFactory QFac = null;
//		QueueConnection QCon = null;
//		try {
//			QFac = AQjmsFactory.getQueueConnectionFactory(hostname, oracle_sid, portno, driver);
//			QCon = QFac.createQueueConnection(userName, password);
//		} catch (Exception e) {
//			e.printStackTrace();
//			try {
//				QFac = AQjmsFactory.getQueueConnectionFactory(hostname, oracle_sid2, portno, driver);
//				QCon = QFac.createQueueConnection(userName, password);
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//		}
//		return QCon;
//	}

	public void createQueue(String user, String qTable, String queueName) {
		try {
			/* Create Queue Tables */
			log.info("Creating Queue Table...");
			QueueConnection QCon = getConnection();
			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

			AQQueueTableProperty qt_prop;
			AQQueueTable q_table = null;
			AQjmsDestinationProperty dest_prop;
			Queue queue = null;
			qt_prop = new AQQueueTableProperty("SYS.AQ$_JMS_TEXT_MESSAGE");

			q_table = ((AQjmsSession) session).createQueueTable(user, qTable, qt_prop);

			log.info("Qtable created");
			dest_prop = new AQjmsDestinationProperty();
			/* create a queue */
			queue = ((AQjmsSession) session).createQueue(q_table, queueName, dest_prop);
			log.info("Queue created");
			/* start the queue */
			((AQjmsDestination) queue).start(session, true, true);

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public void sendMessage(Rtlog rtlog) {

		try {
			QueueConnection QCon = getConnection(rtlog);
			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
			QCon.start();
			
			log.info("getQueueUserName "+rtlog.getQueueUserName());
			log.info("getQueueName "+rtlog.getQueueName());
			Queue queue = ((AQjmsSession) session).getQueue(rtlog.getQueueUserName(), rtlog.getQueueName());
			MessageProducer producer = session.createProducer(queue);
			TextMessage tMsg = session.createTextMessage(rtlog.getMessage());
			// set properties to msg since axis2 needs this parameters to find
			// the operation
			tMsg.setStringProperty("RTLOGFILENAME", rtlog.getRtlogName().trim());
			producer.send(tMsg);
			// log.info("Sent message = " + tMsg.getText());
			session.close();
			producer.close();
			QCon.close();

		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}
	}

//	public void sendMessage(String rtlogMessage, String rtlogName) {
//
//		try {
//			QueueConnection QCon = getConnection();
//			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
//			QCon.start();
//			Queue queue = ((AQjmsSession) session).getQueue(rtlogMessage, rtlogProducerController.getQueueName());
//			MessageProducer producer = session.createProducer(queue);
//			TextMessage tMsg = session.createTextMessage(rtlogMessage);
//
//			// set properties to msg since axis2 needs this parameters to find
//			// the operation
//			tMsg.setStringProperty("RTLOGFILENAME", rtlogName.trim());
//			producer.send(tMsg);
//			// log.info("Sent message = " + tMsg.getText());
//
//			session.close();
//			producer.close();
//			QCon.close();
//
//		} catch (JMSException e) {
//			e.printStackTrace();
//			return;
//		}
//	}

	public void sendMessage(String user, String queueName, String rtlogMessage) {

		try {
			QueueConnection QCon = getConnection();
			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
			QCon.start();
			Queue queue = ((AQjmsSession) session).getQueue(user, queueName);
			MessageProducer producer = session.createProducer(queue);
			TextMessage tMsg = session.createTextMessage(rtlogMessage);

			// set properties to msg since axis2 needs this parameters to find
			// the operation
			tMsg.setJMSDeliveryMode(1);
			tMsg.setJMSExpiration(2678400);
			tMsg.setStringProperty("RTLOGFILENAME", "TEST-NAME-RTLOG");
			producer.send(tMsg);
			log.info("Sent message = " + tMsg.getText());

			session.close();
			producer.close();
			QCon.close();

		} catch (JMSException e) {
			e.printStackTrace();
			return;
		}
	}

	public void browseMessage(String user, String queueName) {
		Queue queue;
		try {
			QueueConnection QCon = getConnection();
			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);

			QCon.start();
			queue = ((AQjmsSession) session).getQueue(user, queueName);
			QueueBrowser browser = session.createBrowser(queue);
			Enumeration<?> enu = browser.getEnumeration();
			List<String> list = new ArrayList<String>();
			while (enu.hasMoreElements()) {
				TextMessage message = (TextMessage) enu.nextElement();
				list.add(message.getText());
			}
			for (int i = 0; i < list.size(); i++) {
				log.info("Browsed msg " + list.get(i));
			}
			browser.close();
			session.close();
			QCon.close();

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	public void consumeMessage(String user, String queueName) {
		Queue queue;
		try {
			QueueConnection QCon = getConnection();
			Session session = QCon.createQueueSession(false, Session.CLIENT_ACKNOWLEDGE);
			QCon.start();
			queue = ((AQjmsSession) session).getQueue(user, queueName);
			MessageConsumer consumer = session.createConsumer(queue);
			TextMessage msg = (TextMessage) consumer.receive();
			log.info("MESSAGE RECEIVED " + msg.getText());
			consumer.close();
			session.close();
			QCon.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public QueueConnection getConnection() throws JMSException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendMessage(String rtlogMessage, String rtlogName) {
		// TODO Auto-generated method stub
		
	}


	// public static void main(String args[]) {
	// String userName = "pos";
	// String queue = "ftd_rtlog_q";
	//// String qTable = "ftd_rtlog_qtbl";
	// //createQueue(userName, qTable, queue);
	// sendMessage(userName, queue," ");
	// sendMessage(userName, queue,"----------------MESSAGE TO THE
	// QUEUE-------------");
	// sendMessage(userName, queue," ");
	//// browseMessage(userName, queue);
	//// consumeMessage(userName, queue);
	//
	// }
}