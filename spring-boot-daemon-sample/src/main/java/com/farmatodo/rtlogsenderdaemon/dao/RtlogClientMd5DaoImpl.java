package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

@Service
public class RtlogClientMd5DaoImpl implements RtlogClientMd5Dao {

	protected Logger log = Logger.getLogger(RtlogClientMd5DaoImpl.class.getName());

	public Connection getConnection(Rtlog rtlog) {
		Connection conn = null;
		log.info("getDbUrl " + rtlog.getDbUrl());
		log.info("getDbUserName " + rtlog.getDbUserName());
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
		} catch (SQLException e) {
			log.info("Connection Failed! Check output console");
			e.printStackTrace();
			try {
				conn = DriverManager.getConnection(rtlog.getDbUrlBackup(), rtlog.getDbUserName(), rtlog.getDbPassword());
			} catch (SQLException e1) {
				log.info("NO PUDO CONECTAR CON BASE DE DATOS DE CONTINGENCIA");
				e1.printStackTrace();
			}
		}

		return conn;
	}

	public boolean updateRtlog(Rtlog rtlog) {

		log.info("ENTRANDO AL METODO UPDATE");

		Connection conn = getConnection(rtlog);
		PreparedStatement pstmt = null;
		boolean success = false;
		String sql = "UPDATE RTLOG_TRANSACTION SET MD5_ORIGIN=? WHERE RTLOG_NAME=? ";
		// UPDATE POS.RTLOG_TRANSACTION SET
		// MD5_ORIGIN='11dc5d89bffe4f0d6d26340b3c91dc03' WHERE RTLOG_NAME =
		// 'RTLOG_00112_20171111100000.DAT' AND STORE_ID = '112';
		try {
			log.info("updateRtlog PARAMETER 1 > "+sql);
			log.info("updateRtlog PARAMETER 2 > "+rtlog.getRtlogName());
			log.info("updateRtlog PARAMETER 3 > "+rtlog.getMd5Origin());
			log.info("updateRtlog PARAMETER 4 > "+rtlog.getDbUrl());
			log.info("updateRtlog PARAMETER 5 > "+rtlog.getDbUserName());
			log.info("updateRtlog PARAMETER 6 > "+rtlog.getDbUrl());
			log.info("updateRtlog PARAMETER 7 > "+rtlog.getDbUserName());
			log.info("updateRtlog PARAMETER 8 > "+rtlog.getDbPassword());
			log.info("ENTRANDO AL TRY METODO UPDATE");
			log.info("SQL QUERY IS -> "+sql);
			log.info("CLIENT INFO -> "+conn.getClientInfo().toString());
			pstmt = conn.prepareStatement(sql);
			log.info("CLIENT INFO -> "+conn.getClientInfo().toString());
			pstmt.setString(1, rtlog.getMd5Origin());
			pstmt.setString(2, rtlog.getRtlogName());
			log.info("ANTES DE EJECUTAR");
			// int rowsUpdated = pstmt.execute();
			success = pstmt.execute();
			log.info("DESPUES DE EJECUTAR");
			// if (rowsUpdated > 0) {
			if (!success) {
				success = true;
				log.info("An existing RTLOG was updated successfully!");
			} else {
				log.info("SOMETHING ITS WRONG!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (pstmt != null)
				try {
					pstmt.close();
					log.info("CERRANDO STATEMENT");
				} catch (Exception e) {
					e.printStackTrace();
				}

			if (conn != null)
				try {
					conn.close();
					log.info("CERRANDO CONEXION");
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		return success;
	}

	// public void selectRtlog(Rtlog rtlog) {
	// Connection conn = getConnection(rtlog);
	// Statement stmt = null;
	// ResultSet resultset = null;
	// // Creates a Statement object for sending SQL statements to the database
	//
	// try {
	//
	// stmt = conn.createStatement();
	// resultset = stmt.executeQuery("SELECT RTLOG_NAME from
	// RTLOG_TRANSACTION");
	// if (resultset.next())
	// log.info("Data Details: " + resultset.getString(1));
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// } finally {
	//
	// if (resultset != null)
	// try {
	// resultset.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// if (stmt != null)
	// try {
	// stmt.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// if (conn != null)
	// try {
	// conn.close();
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	//
	// log.info("Oracle JDBC connect and query test completed.");
	// }

}
