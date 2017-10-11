package com.farmatodo.rtlogsenderdaemon.model;

/**
 * 
 * <br/>
 * Copyright: (c) 2016 Farmatodo Venezuela<br/>
 * 
 * @author Farmatodo C.A. (FTD) <br/>
 *         Anthony Piñero (AP) anthony.pinero@farmatodo.com<br/>
 *         Changes:<br/>
 *         <ul>
 *         <li>29/03/2017 (AP) Creaci&oacute;n de la Clase RtlogDbMessage</li>
 *         </ul>
 * @version 1.0
 * @since rtlog-p2p - 1.0<br/>
 */
public class Rtlog {

	private String rtlogName;
	private int storeId;

	private String status;
	private String md5Origin;
	private String md5Destination;

	private String sendedTime;
	private String sendedTimeToPosbo;
	private String receivedTime;
	private String qtyProcessed;

	private String dbUrl;
	private String dbUrlBackup;
	private String dbUserName;
	private String dbPassword;
	private String dbPort;
	
	private String dbUrlPosbo;
	private String dbUrlBackupPosbo;
	private String dbUserNamePosbo;
	private String dbPasswordPosbo;
	private String dbPortPosbo;
	
	private String queueUserName;
	private String queueName;
	private String queuePassword;
	private String queueHostname; // T3
	private String oracleSid;// T3
	private String oracleSid2;// T3
	private String queueDriver;
	private String queuePort;
	private String message;
	
	public String getDbUrlPosbo() {
		return dbUrlPosbo;
	}

	public void setDbUrlPosbo(String dbUrlPosbo) {
		this.dbUrlPosbo = dbUrlPosbo;
	}

	public String getDbUrlBackupPosbo() {
		return dbUrlBackupPosbo;
	}

	public void setDbUrlBackupPosbo(String dbUrlBackupPosbo) {
		this.dbUrlBackupPosbo = dbUrlBackupPosbo;
	}

	public String getDbUserNamePosbo() {
		return dbUserNamePosbo;
	}

	public void setDbUserNamePosbo(String dbUserNamePosbo) {
		this.dbUserNamePosbo = dbUserNamePosbo;
	}

	public String getDbPasswordPosbo() {
		return dbPasswordPosbo;
	}

	public void setDbPasswordPosbo(String dbPasswordPosbo) {
		this.dbPasswordPosbo = dbPasswordPosbo;
	}

	public String getDbPortPosbo() {
		return dbPortPosbo;
	}

	public void setDbPortPosbo(String dbPortPosbo) {
		this.dbPortPosbo = dbPortPosbo;
	}




		/**
		 * @return the rtlogName
		 */
		public String getRtlogName() {
			return this.rtlogName;
		}

		/**
		 * @param rtlogName the rtlogName to set
		 */
		public void setRtlogName(String rtlogName) {
			this.rtlogName = rtlogName;
		}

		/**
		 * @return the storeId
		 */
		public int getStoreIdByName() {
			return Integer.parseInt(this.rtlogName.substring(8, 11)); //Get the StoreId from the Rtlog name
		}

//		/**
//		 * @param storeId the storeId to set
//		 */
//		public void setStoreId(int storeId) {
//			this.storeId = storeId;
//		}

		/**
		 * @return the status
		 */
		public String getStatus() {
			return status;
		}

		/**
		 * @param status the status to set
		 */
		public void setStatus(String status) {
			this.status = status;
		}

		/**
		 * @return the md5Origin
		 */
		public String getMd5Origin() {
			return md5Origin;
		}

		/**
		 * @param md5Origin the md5Origin to set
		 */
		public void setMd5Origin(String md5Origin) {
			this.md5Origin = md5Origin;
		}

		/**
		 * @return the md5Destination
		 */
		public String getMd5Destination() {
			return md5Destination;
		}

		/**
		 * @param md5Destination the md5Destination to set
		 */
		public void setMd5Destination(String md5Destination) {
			this.md5Destination = md5Destination;
		}

		/**
		 * @return the sendedTime
		 */
		public String getSendedTime() {
			return sendedTime;
		}

		/**
		 * @param sendedTime the sendedTime to set
		 */
		public void setSendedTime(String sendedTime) {
			this.sendedTime = sendedTime;
		}

		/**
		 * @return the receivedTime
		 */
		public String getReceivedTime() {
			return receivedTime;
		}

		/**
		 * @param receivedTime the receivedTime to set
		 */
		public void setReceivedTime(String receivedTime) {
			this.receivedTime = receivedTime;
		}

		/**
		 * @return the qtyProcessed
		 */
		public String getQtyProcessed() {
			return qtyProcessed;
		}

		/**
		 * @param qtyProcessed the qtyProcessed to set
		 */
		public void setQtyProcessed(String qtyProcessed) {
			this.qtyProcessed = qtyProcessed;
		}

		/**
		 * @return the dbUrl
		 */
		public String getDbUrl() {
			return dbUrl;
		}

		/**
		 * @param dbUrl the dbUrl to set
		 */
		public void setDbUrl(String dbUrl) {
			this.dbUrl = dbUrl;
		}

		/**
		 * @return the dbUrlBackup
		 */
		public String getDbUrlBackup() {
			return dbUrlBackup;
		}

		/**
		 * @param dbUrlBackup the dbUrlBackup to set
		 */
		public void setDbUrlBackup(String dbUrlBackup) {
			this.dbUrlBackup = dbUrlBackup;
		}

		/**
		 * @return the dbUserName
		 */
		public String getDbUserName() {
			return dbUserName;
		}

		/**
		 * @param dbUserName the dbUserName to set
		 */
		public void setDbUserName(String dbUserName) {
			this.dbUserName = dbUserName;
		}

		/**
		 * @return the dbPassword
		 */
		public String getDbPassword() {
			return dbPassword;
		}

		/**
		 * @param dbPassword the dbPassword to set
		 */
		public void setDbPassword(String dbPassword) {
			this.dbPassword = dbPassword;
		}

		/**
		 * @return the dbPort
		 */
		public String getDbPort() {
			return dbPort;
		}

		/**
		 * @param dbPort the dbPort to set
		 */
		public void setDbPort(String dbPort) {
			this.dbPort = dbPort;
		}

		/**
		 * @return the queueUserName
		 */
		public String getQueueUserName() {
			return queueUserName;
		}

		/**
		 * @param queueUserName the queueUserName to set
		 */
		public void setQueueUserName(String queueUserName) {
			this.queueUserName = queueUserName;
		}

		/**
		 * @return the queueName
		 */
		public String getQueueName() {
			return queueName;
		}

		/**
		 * @param queueName the queueName to set
		 */
		public void setQueueName(String queueName) {
			this.queueName = queueName;
		}

		/**
		 * @return the queuePassword
		 */
		public String getQueuePassword() {
			return queuePassword;
		}

		/**
		 * @param queuePassword the queuePassword to set
		 */
		public void setQueuePassword(String queuePassword) {
			this.queuePassword = queuePassword;
		}

			/**
		 * @return the queueDriver
		 */
		public String getQueueDriver() {
			return queueDriver;
		}

		/**
		 * @param queueDriver the queueDriver to set
		 */
		public void setQueueDriver(String queueDriver) {
			this.queueDriver = queueDriver;
		}

		/**
		 * @return the queuePort
		 */
		public String getQueuePort() {
			return queuePort;
		}

		/**
		 * @param queuePort the queuePort to set
		 */
		public void setQueuePort(String queuePort) {
			this.queuePort = queuePort;
		}

		/**
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMessage(String message) {
			this.message = message;
		}

		/**
		 * @return the queueHostname
		 */
		public String getQueueHostname() {
			return queueHostname;
		}

		/**
		 * @param queueHostname the queueHostname to set
		 */
		public void setQueueHostname(String queueHostname) {
			this.queueHostname = queueHostname;
		}

		public int getStoreId() {
			return storeId;
		}

		public void setStoreId(int storeId) {
			this.storeId = storeId;
		}

		/**
		 * @return the oracleSid
		 */
		public String getOracleSid() {
			return oracleSid;
		}

		/**
		 * @param oracleSid the oracleSid to set
		 */
		public void setOracleSid(String oracleSid) {
			this.oracleSid = oracleSid;
		}

		/**
		 * @return the oracleSid2
		 */
		public String getOracleSid2() {
			return oracleSid2;
		}

		/**
		 * @param oracleSid2 the oracleSid2 to set
		 */
		public void setOracleSid2(String oracleSid2) {
			this.oracleSid2 = oracleSid2;
		}

		public String getSendedTimeToPosbo() {
			return sendedTimeToPosbo;
		}

		public void setSendedTimeToPosbo(String sendedTimeToPosbo) {
			this.sendedTimeToPosbo = sendedTimeToPosbo;
		}
		
		

	
}
