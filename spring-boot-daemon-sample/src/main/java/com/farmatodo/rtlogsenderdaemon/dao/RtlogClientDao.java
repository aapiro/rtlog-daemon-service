/**
 * 
 */
package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

/**
 * @author APinero
 *
 */
public interface RtlogClientDao {
	
	String ENVIADO ="E";

	Connection getConnection(String dbUrl, String dbUserName, String dbPassword);

//	Connection getConnection(String dbUrl, String dbUrlBackup, String dbUserName, String dbPassword);

//	Connection getConnection(String dbUrl, String userName, String password);
	
	boolean sendToDataBase(Rtlog rtlog);

	boolean updateDateInPosbo(Rtlog rtlog);


}
