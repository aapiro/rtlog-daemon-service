package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

@Service
public class RtlogClientDaoImpl implements RtlogClientDao{
	
	protected Logger log = Logger.getLogger(RtlogClientDaoImpl.class.getName());

	public Connection getConnection(Rtlog rtlog) {
		Connection conn = null;
		log.info("getDbUrl "+rtlog.getDbUrl());
		log.info("getDbUserName "+rtlog.getDbUserName());
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException exception) {
			log.info("Oracle Driver Class Not found Exception: " + exception.toString());
		}

		DriverManager.setLoginTimeout(60);
		log.info("Oracle JDBC Driver Successfully Registered! Let's make connection now");

		try {
			// Attempts to establish a connection
			// here DB name: localhost, sid:
			conn = DriverManager.getConnection(rtlog.getDbUrl(), rtlog.getDbUserName(), rtlog.getDbPassword());
		}catch (SQLException e) {
			log.info("Conexion principal fallida, intentando conectar a contingencia");
			e.printStackTrace();
//			try {
//				conn = DriverManager.getConnection(rtlog.getDbUrlBackup(), rtlog.getDbUserName(), rtlog.getDbPassword());
//			} catch (SQLException e1) {
//				log.info("NO PUDO CONECTAR CON BASE DE DATOS DE CONTINGENCIA");
//				e1.printStackTrace();
//			}
		}catch(Exception e){
			
			e.printStackTrace();
			
		}

		return conn;
	}
	
	@Override
	public Connection getConnection(String dbUrl, String dbUserName, String dbPassword) {
		Connection conn = null;
		log.info("getDbUrl "+dbUrl);
		log.info("getDbUserName "+dbUserName);
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException exception) {
			log.info("Oracle Driver Class Not found Exception: " + exception.toString());
		}

		DriverManager.setLoginTimeout(60);
		log.info("Oracle JDBC Driver Successfully Registered! Let's make connection now");

		try {
			// Attempts to establish a connection
			// here DB name: localhost, sid:
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		} catch (SQLException e) {
			e.printStackTrace();
			log.info("Conexion principal fallida, intentando conectar a contingencia");
//			try {
//				conn = DriverManager.getConnection(dbUrlBackup, dbUserName, dbPassword);
//			} catch (SQLException e1) {
//
//				log.info("NO PUDO CONECTAR CON BASE DE DATOS DE CONTINGENCIA");
//				e1.printStackTrace();
//			}
		}

		return conn;
	}
	
	/**
	 * Insert in the table for this fields:
	 * <p>
	 * RTLOG_NAME STORE_ID STATUS MD5_ORIGIN MD5_DESTINATION SENDED_TIME
	 * RECEIVED_TIME QTY_PROCESSED
	 */
	 public boolean sendToDataBase(Rtlog rtlog) {
		 
			log.info("PARAMETER TO DB 1 -> "+rtlog.getRtlogName());
			log.info("PARAMETER TO DB 2 -> "+Integer.toString(rtlog.getStoreIdByName()));
			log.info("PARAMETER TO DB 3 -> "+rtlog.getStatus());
			log.info("PARAMETER TO DB 4 -> "+rtlog.getMd5Origin());//
			log.info("PARAMETER TO DB 5 -> "+rtlog.getMd5Destination());//
			log.info("PARAMETER TO DB 6 -> "+rtlog.getSendedTime());
			log.info("PARAMETER TO DB 7 -> "+rtlog.getReceivedTime());//
			log.info("PARAMETER TO DB 8 -> "+rtlog.getQtyProcessed());//
			log.info("PARAMETER TO DB 9 -> "+rtlog.getDbUrl());
			log.info("PARAMETER TO DB 10 -> "+rtlog.getDbUserName());
			log.info("PARAMETER TO DB 11-> "+rtlog.getDbPassword());
			
		Connection conn = getConnection(rtlog.getDbUrl(), rtlog.getDbUserName(), rtlog.getDbPassword());
		PreparedStatement pstmt = null;
		String sql = "INSERT INTO RTLOG_TRANSACTION (RTLOG_NAME," + " STORE_ID," + " STATUS," + " MD5_ORIGIN,"
				+ " MD5_DESTINATION," + " SENDED_TIME," + " RECEIVED_TIME,"
				+ " QTY_PROCESSED) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		boolean success = false;
		log.info("SQL QUERY -> "+sql);
		try {
			log.info("DB SCHEMA -> "+conn.getSchema());
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rtlog.getRtlogName());
			pstmt.setInt(2, rtlog.getStoreIdByName());
			pstmt.setString(3, rtlog.getStatus());
			pstmt.setString(4, rtlog.getMd5Origin());
			pstmt.setString(5, rtlog.getMd5Destination());
			pstmt.setString(6, rtlog.getSendedTime());
			pstmt.setString(7, rtlog.getReceivedTime());
			pstmt.setString(8, rtlog.getQtyProcessed());
			log.info("ANTES DEL EXECUTE");
			 success = pstmt.execute();
			 log.info("DESPUES DEL EXECUTE");
			if (!success) {
				success = true;
				log.info("An existing RTLOG was sended successfully!");
			} else {
				log.info("ALGO PASA!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
log.info("EN EL FINALLY");
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			log.info("EN EL FINALLY AL FINAL");
		}
		return success;

	}
	 
	 public boolean updateDateInPosbo(Rtlog rtlog){
		 	
		 	log.info("METODO -> updateDateInPosbo");
		    Connection conn = getConnection(rtlog.getDbUrlPosbo(), rtlog.getDbUserNamePosbo(), rtlog.getDbPasswordPosbo());
		    PreparedStatement pstmt = null;
		    boolean success = false;
		    String sql = "UPDATE TR_TRN SET ID_RTLOG_JMS_BTCH = ? WHERE ID_STR_RT = ? AND ID_RTLOG_BTCH = ?";
		    try
		    {
		    	
		      pstmt = conn.prepareStatement(sql);
		      pstmt.setString(1, rtlog.getSendedTimeToPosbo());
		      pstmt.setString(2, rtlog.getRtlogName().substring(6, 11)); //Ejemplo de un nombre de RTLOG RTLOG_00121_20100426000125.DAT 
		      pstmt.setString(3, rtlog.getRtlogName().substring(12, 26));
		      
		      int rowsUpdated = pstmt.executeUpdate();
		      if (rowsUpdated > 0)
		      {
		        success = true;
		        log.info("An existing RTLOG was updated successfully in POSBO!");
		      }
//		      return success;
		    } catch (SQLException e) {
				e.printStackTrace();
			} finally {

				if (pstmt != null)
					try {
						pstmt.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

				if (conn != null)
					try {
						conn.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
			return success;
		 
	 }


}
