package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

//import java.sql.Connection;

public interface RtlogClientMd5Dao {
	
	String ENVIADO ="E";
	
//	Connection getConnection(String dbUrl, String userName, String password);
	
	public Connection getConnection(Rtlog rtlog);
	
	public boolean updateRtlog(Rtlog rtlog);

	
	

}
