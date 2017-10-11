package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

@Service
public class RtlogClientValidatorDaoImpl implements RtlogClientValidatorDao {
	
	protected Logger log = Logger.getLogger(RtlogClientValidatorDaoImpl.class.getName());

	public Connection getConnection(Rtlog rtlog) {
		Connection conn = null;
		log.info("getDbUrl "+rtlog.getDbUrl());
		log.info("getDbUserName "+rtlog.getDbUserName());
		log.info("rtlog.getDbPassword() "+rtlog.getDbPassword());
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
		}

		return conn;
	}
	
	 public List<Rtlog> selectRtlogNameByStatusF(Rtlog rtlog) {
		Connection conn = getConnection(rtlog);
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<Rtlog> ll = new LinkedList<Rtlog>();
		// Creates a Statement object for sending SQL statements to the database
//		String sql = "SELECT RTLOG_NAME FROM RTLOG_TRANSACTION WHERE STATUS = '"+ RtlogClientValidatorDao.FALLO +"' AND STORE_ID=?";
		String sql = "SELECT RTLOG_NAME FROM RTLOG_TRANSACTION WHERE STATUS = 'F' AND STORE_ID=?";
		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, rtlog.getStoreId());
			rs = pstmt.executeQuery();
			// Fetch each row from the result set
			while (rs.next()) {
//				Rtlog rtlog = null;
				rtlog.setRtlogName(rs.getString("RTLOG_NAME"));
//			  Rtlog user = new Rtlog().setRtlogName(rs.getString("RTLOG_NAME"));
//				new Rtlog().setRtlogName(rs.getString("RTLOG_NAME"))
			  ll.add(rtlog);
			}
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

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

		log.info("selectRtlogNameByStatusF Oracle JDBC connect and query completed. ");
		return ll;
	}
	
	 public boolean updateRtlogStatusFail(Rtlog rtlog) {

		Connection conn = getConnection(rtlog);
		PreparedStatement pstmt = null;
		boolean success = false;
		String sql = "UPDATE RTLOG_TRANSACTION SET STATUS=?, RTLOG_NAME=? WHERE RTLOG_NAME=?";
		try {

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rtlog.getStatus());
			pstmt.setString(2, rtlog.getRtlogName() + RtlogClientValidatorDao.MARCA_DESCARTE);//TO SHOW AN DIFERENCE IN THE NAME RESULT FROM THE REPROCESS
			pstmt.setString(3, rtlog.getRtlogName());

			int rowsUpdated = pstmt.executeUpdate();
			if (rowsUpdated > 0) {
				success = true;
				log.info("An existing RTLOG was updated successfully!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {

			if (pstmt != null)
				try {
					pstmt.close();
				} catch (Exception e) {
					// TODO: handle exception
				}

			if (conn != null)
				try {
					conn.close();
				} catch (Exception e) {
					// TODO: handle exception
				}
		}
		return success;
	}
	 

//	 public boolean updateRtlogStatusProcess(Rtlog rtlog) {
//		
//		Connection conn = getConnection(rtlog);
//		PreparedStatement pstmt = null;
//		boolean success = false;
//		String sql = "UPDATE RTLOG_TRANSACTION SET STATUS=? WHERE RTLOG_NAME=?";
//		try {
//			
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, rtlog.getStatus());
//			pstmt.setString(2, rtlog.getRtlogName());//TO SHOW AN DIFERENCE IN THE REPROCESS
//			
//			int rowsUpdated = pstmt.executeUpdate();
//			if (rowsUpdated > 0) {
//				success = true;
//				log.info("An existing RTLOG was updated successfully!");
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}finally {
//			
//			if (pstmt != null)
//				try {
//					pstmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//		return success;
//	}
	 


	/**
	 * Insert in the table for this fields:
	 * <p>
	 * RTLOG_NAME STORE_ID STATUS MD5_ORIGIN MD5_DESTINATION SENDED_TIME
	 * RECEIVED_TIME QTY_PROCESSED
	 */
//	 public boolean insertRtlog(Rtlog rtlog) {
//		Connection conn = getConnection(rtlog);
//		String sql = "INSERT INTO RTLOG_TRANSACTION (RTLOG_NAME," + " STORE_ID," + " STATUS," + " MD5_ORIGIN,"
//				+ " MD5_DESTINATION," + " SENDED_TIME," + " RECEIVED_TIME,"
//				+ " QTY_PROCESSED) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
//		boolean success = false;
//		PreparedStatement pstmt = null;
//		try {
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, rtlog.getRtlogName());
//			pstmt.setInt(2, rtlog.getStoreId());
//			pstmt.setString(3, rtlog.getStatus());
//			pstmt.setString(4, rtlog.getMd5Origin());
//			pstmt.setString(5, rtlog.getMd5Destination());
//			pstmt.setString(6, rtlog.getSendedTime());
//			pstmt.setString(7, rtlog.getReceivedTime());
//			pstmt.setString(8, rtlog.getQtyProcessed());
//
//			// pstmt.setString(1, "RTLOG_00112_20170215191111.DAT");
//			// pstmt.setString(2, "112");
//			// pstmt.setString(3, "E");
//			// pstmt.setString(4, "3d0ee9820bc0827151e703ddee354cce ");
//			// pstmt.setString(5, null);
//			// pstmt.setString(6, "20170328_101753");
//			// pstmt.setString(7, null);
//			// pstmt.setString(8, "1");
//
//			int rowsInserted = pstmt.executeUpdate();
//			if (rowsInserted > 0) {
//				success = true;
//				log.info("A new user was inserted successfully!");
//			}
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//
//			if (pstmt != null)
//				try {
//					pstmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//		return success;
//
//	}

//	 public void selectRtlog(Rtlog rtlog) {
//		Connection conn = getConnection(rtlog);
//		Statement stmt = null;
//		ResultSet resultset = null;
//		// Creates a Statement object for sending SQL statements to the database
//
//		try {
//
//			stmt = conn.createStatement();
//			resultset = stmt.executeQuery("SELECT RTLOG_NAME from RTLOG_TRANSACTION");
//			if (resultset.next())
//				log.info("Data Details: " + resultset.getString(1));
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		} finally {
//
//			if (resultset != null)
//				try {
//					resultset.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (stmt != null)
//				try {
//					stmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//
//		log.info("Oracle JDBC connect and query test completed.");
//	}

//	 public boolean updateRtlog(Rtlog rtlog) {
//
//		Connection conn = getConnection(rtlog);
//		PreparedStatement pstmt = null;
//		boolean success = false;
//		String sql = "UPDATE RTLOG_TRANSACTION SET MD5_ORIGIN=? WHERE RTLOG_NAME=? AND STORE_ID=?";
//		// UPDATE POS.RTLOG_TRANSACTION SET
//		// MD5_ORIGIN='11dc5d89bffe4f0d6d26340b3c91dc03' WHERE RTLOG_NAME =
//		// 'RTLOG_00112_20171111100000.DAT' AND STORE_ID = '112';
//		try {
//
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, rtlog.getMd5Origin());
//			pstmt.setString(2, rtlog.getRtlogName());
//			pstmt.setInt(3, rtlog.getStoreId());
//
//			int rowsUpdated = pstmt.executeUpdate();
//			if (rowsUpdated > 0) {
//				success = true;
//				log.info("An existing user was updated successfully!");
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally {
//
//			if (pstmt != null)
//				try {
//					pstmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//		return success;
//	}
	
//	 public List<Rtlog> selectRtlogNameByStatusV(String storeId) {
//		Connection conn = getConnection();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		
//		List<Rtlog> ll = new LinkedList<Rtlog>();
//		// Creates a Statement object for sending SQL statements to the database
//		String sql = "SELECT RTLOG_NAME FROM RTLOG_TRANSACTION WHERE STATUS = 'V' AND STORE_ID=? ";
//		try {
//
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, storeId);
//			rs = pstmt.executeQuery( );
//			// Fetch each row from the result set
//			while (rs.next()) {
//				Rtlog rtlog = null;
//				rtlog.setRtlogName(rs.getString("RTLOG_NAME"));
//			  ll.add(rtlog);
//			}
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			if (rs != null)
//				try {
//					rs.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (pstmt != null)
//				try {
//					pstmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//
//		log.info("Oracle JDBC connect and query completed.");
//		return ll;
//	}
	

	
	
//	 public List<Rtlog> selectRtlogNameBySameMD5() {
//		Connection conn = getConnection();
//		Statement stmt = null;
//		ResultSet rs = null;
//		List<Rtlog> ll = new LinkedList<Rtlog>();
//		// Creates a Statement object for sending SQL statements to the database
//
//		try {
//
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery("SELECT RTLOG_NAME FROM RTLOG_TRANSACTION WHERE MD5_ORIGIN = MD5_DESTINATION");
//			// Fetch each row from the result set
//			while (rs.next()) {
//				
////			  Rtlog user = new Rtlog(rs.getString("RTLOG_NAME"));
//
//			  ll.add(new Rtlog(rs.getString("RTLOG_NAME")));
//			}
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//
//			if (rs != null)
//				try {
//					rs.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (stmt != null)
//				try {
//					stmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//
//		log.info("Oracle JDBC connect and query completed.");
//		return ll;
//	}
	
//	 public List<Rtlog> selectRtlogNameByDiffMD5() {
//		Connection conn = getConnection();
//		Statement stmt = null;
//		ResultSet rs = null;
//		List<Rtlog> ll = new LinkedList<Rtlog>();
//		// Creates a Statement object for sending SQL statements to the database
//		
//		try {
//			
//			stmt = conn.createStatement();
//			rs = stmt.executeQuery("SELECT RTLOG_NAME FROM RTLOG_TRANSACTION WHERE MD5_ORIGIN != MD5_DESTINATION");
//			// Fetch each row from the result set
//			while (rs.next()) {
//				
////			  Rtlog user = new Rtlog(rs.getString("RTLOG_NAME"));
//				
//				ll.add(new Rtlog(rs.getString("RTLOG_NAME")));
//			}
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			
//			if (rs != null)
//				try {
//					rs.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			
//			if (stmt != null)
//				try {
//					stmt.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			
//			if (conn != null)
//				try {
//					conn.close();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//		}
//		
//		log.info("Oracle JDBC connect and query completed.");
//		return ll;
//	}

	@Override
	public List<Rtlog> selectRtlogNameByStatusV(String storeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateRtlogStatusProcess(Rtlog rtlog) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean insertRtlog(Rtlog rtlog) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selectRtlog(Rtlog rtlog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean updateRtlog(Rtlog rtlog) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Rtlog> selectRtlogNameBySameMD5() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Rtlog> selectRtlogNameByDiffMD5() {
		// TODO Auto-generated method stub
		return null;
	}

	// public  void main(String[] args) {
	//
	// log.info("INICIE PETICION");
	//
	// // select();
	// RtlogDbMessage rtlogDbMessage = null;
	// insertRtlog(rtlogDbMessage);
	//
	// log.info("TERMINANDO PETICION");
	//
	// }

}
