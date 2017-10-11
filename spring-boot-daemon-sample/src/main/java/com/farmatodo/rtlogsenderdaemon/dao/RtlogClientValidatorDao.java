package com.farmatodo.rtlogsenderdaemon.dao;

import java.sql.Connection;
import java.util.List;

import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

//import java.sql.Connection;

public interface RtlogClientValidatorDao {
	
	String ENVIADO ="E";
	
	String MARCA_DESCARTE = "X";
	
	String FALLO = "F";
	
	String MD5 = "MD5";
	
	public Connection getConnection(Rtlog rtlog);
	
	public boolean updateRtlogStatusFail(Rtlog rtlog);
	
	public boolean updateRtlogStatusProcess(Rtlog rtlog);
	
	public boolean insertRtlog(Rtlog rtlog);
	
	public void selectRtlog(Rtlog rtlog);
	
	public boolean updateRtlog(Rtlog rtlog);
	
	public List<Rtlog> selectRtlogNameByStatusV(String storeId);
	
	public List<Rtlog> selectRtlogNameByStatusF(Rtlog rtlog);
	
	public List<Rtlog> selectRtlogNameBySameMD5();
	
	public List<Rtlog> selectRtlogNameByDiffMD5();
	
	
//	Connection getConnection(String dbUrl, String userName, String password);
	
	
	

}
