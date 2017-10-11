package com.farmatodo.rtlogsenderdaemon;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.farmatodo.rtlogsenderdaemon.dao.RtlogClientDao;
import com.farmatodo.rtlogsenderdaemon.dao.RtlogClientMd5Dao;
import com.farmatodo.rtlogsenderdaemon.dao.RtlogClientValidatorDao;
import com.farmatodo.rtlogsenderdaemon.model.Rtlog;

@Component
public class RtlogDaemon {

    private static final Logger log = LoggerFactory.getLogger(RtlogDaemon.class);
    
    
 	@Value("${fileRoot:\\OracleRetailStore\\Server\\pos\\bin\\POSLog\\}")
 	private String fileRoot;

 	@Value("${fileToSend:\\OracleRetailStore\\RTLOG-P2P-CLOUD\\RTLOG-TO-SEND\\}")
 	private String fileToSend;

 	@Value("${fileToChecksum:\\OracleRetailStore\\RTLOG-P2P-CLOUD\\RTLOG-TO-CHECKSUM\\}")
 	private String fileToChecksum;

 	@Value("${fileToValidate:\\OracleRetailStore\\RTLOG-P2P-CLOUD\\RTLOG-TO-VALIDATE\\}")
 	private String fileToValidate;

 	@Value("${logProcess:\\OracleRetailStore\\RTLOG-P2P-CLOUD\\LOG-PROCESS\\}")
 	private String logProcess;

 	@Value("${pathLetter:C}")
 	private String pathLetter;

 	@Value("${pathSystemLetter:W}") // W = windows ; U = Unix
 	private String pathSystemLetter;

 	@Value("${timeToWait:30}")
 	private String timeToWait;

 	@Value("${fileExtension:DAT}")
 	private String fileExtension;

 	@Value("${logFileName:rtlog-transactions}")
 	private String logFileName;

 	@Value("${logFileExtension:txt}")
 	private String logFileExtension;

 	// QUEUE
 	@Value("${queueUserName:pos}")
 	private String queueUserName;

 	@Value("${queuePassword:pos12qa}")
 	private String queuePassword;

 	@Value("${queueName:ftd_rtlog_q}")
 	private String queueName;

 	// @Value("${queueHostname:dsved-cont2-scan.farmatodo.com}")//T3
 	@Value("${queueHostname:dsved-test22-scan.farmatodo.com}") // T2
 	private String queueHostname; // T2

 	// @Value("${oracle_sid:simpd1}")//T3
 	@Value("${oracleSid:simqa1}") // T2
 	private String oracleSid;// T2

 	// @Value("${oracle_sid2:simpd2}") //T3
 	@Value("${oracleSid2:simqa2}") // T2
 	private String oracleSid2;// T2

 	@Value("${queuePort:1521}")
 	private String queuePort;
 	// DATA BASE PORT

 	@Value("${driver:thin}")
 	private String queueDriver;
 	// POS DATA BASE
 	@Value("${dbHostname:dsved-test22-scan.farmatodo.com}") // T2
 	private String dbHostname;

 	@Value("${portno:1521}")
 	private String dbPortNo;

 	@Value("${oracleServiceName:simqa}")
 	private String oracleServiceName;// T2

 	@Value("${dbHostnameBup:dsved-test22-scan.farmatodo.com}") // T2
 	private String dbHostnameBup;

 	@Value("${dbPortNoBup:1521}")
 	private String dbPortNoBup;

 	@Value("${oracleServiceNameBup:simqa}")
 	private String oracleServiceNameBup;// T2

 	@Value("${dbUserName:pos}")
 	private String dbUserName;

 	@Value("${dbPassword:pos12qa}")
 	private String dbPassword;
 	// POSBO DATA BASE ----
 	@Value("${dbHostname:st114ve01}")
 	private String dbHostnamePosbo; // T3

 	@Value("${dbUserNamePosbo:posbo}")
 	private String dbUserNamePosbo;

 	@Value("${dbPasswordPosbo:farmaad01}")
 	private String dbPasswordPosbo;

 	@Value("${dbPortPosbo:1521}")
 	private String dbPortPosbo;
 	// POSBO DATA BASE URL BACKUP
 	@Value("${dbHostname:st114ve01}")
 	private String dbHostnamePosboBup; // T3

 	@Value("${oracleServiceNamePosbo:bodb}")
 	private String oracleServiceNamePosbo;// T2

 	@Value("${dbPortPosboBup:1521}")
 	private String dbPortPosboBup;

 	@Value("${oracleServiceNamePosboBup:bodb}")
 	private String oracleServiceNamePosboBup;// T2
 	// ------------------------------
 	@Value("${rtlogToReprocess:rtlog-to-reprocess}")
 	private String rtlogToReprocess;
 	// ------------------------------
 	@Value("${statusReprocess:X}")
 	private String statusReprocess;

 	@Value("${timeToWaitToDb:1}")
 	private int timeToWaitToDb;

 	private String driver = "jdbc:oracle:thin:@";

 	private String format = "yyyy/MM/dd HH:mm:ss";

 	private static HashMap<String, String> hmap = new HashMap<>();
 	private final AtomicLong counter = new AtomicLong();

 	@Autowired
 	private OracleAQClient oracleAQClient;

 	@Autowired
 	private RtlogClientDao rtlogClientDao;

 	@Autowired
 	private RtlogClientMd5Dao rtlogClientMd5Dao;

 	@Autowired
 	private RtlogClientValidatorDao rtlogClientValidatorDao;

 	@Scheduled(fixedRate = 5000)
 	public void rtlog() {

 		log.info("fileRoot-> " + fileRoot);
 		log.info("fileToSend-> " + fileToSend);
 		log.info("fileToChecksum-> " + fileToChecksum);
 		log.info("fileToValidate-> " + fileToValidate);
 		log.info("logProcess-> " + logProcess);
 		log.info("pathLetter-> " + pathLetter);
 		log.info("pathSystemLetter-> " + pathSystemLetter);
 		log.info("timeToWait-> " + timeToWait);
 		log.info("fileExtension-> " + fileExtension);
 		log.info("logFileName-> " + logFileName);
 		log.info("logFileExtension-> " + logFileExtension);
 		log.info("queueUserName-> " + queueUserName);
 		log.info("queueName-> " + queueName);
 		log.info("dbHostname-> " + dbHostname); // T3
 		log.info("queueHostname-> " + queueHostname); // T3
 		log.info("queuePort-> " + queuePort); // T3
 		log.info("oracleSid-> " + oracleSid);// T3
 		log.info("oracleSid2-> " + oracleSid2);// T3
 		log.info("dbPortNo-> " + dbPortNo);
 		log.info("dbUserName-> " + dbUserName);
 		log.info("dbPassword-> " + dbPassword);
 		log.info("driver-> " + driver);
 		log.info("oracleServiceName-> " + oracleServiceName);// T2

// 		if (input.equals("TURN-ON-SERVICE")) {
// 			this.onApp = true;
 			log.info("SERVICIO ACTIVO");
// 		} else if (input.equals("TURN-OFF-SERVICE")) {
// 			this.onApp = false;
// 			log.info("SERVICIO INACTIVO");
// 		}
// 		while (this.onApp) {
 			log.info("SERVICIO INICIADO");
 			hmap.clear();
 			long startTime = System.currentTimeMillis();
 			try {
 				log.info("INICIANDO SERVICIO EN 5 SEGS...");
				TimeUnit.SECONDS.sleep(5);
			} catch (NumberFormatException | InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
 			try {
 				log.info("PRIMERA FASE");
 				log.info("hmap " + hmap);
 				findAndLoadRtlogs(fileToSend, pathSystemLetter, pathLetter, fileExtension);//
 				log.info("hmap " + hmap);
 				if (!hmap.isEmpty()) {
 					log.info("TAMANIO DE LA LISTA DE ARCHIVOS " + hmap.size());
 					sendToQueue(fileToSend, fileToChecksum, pathSystemLetter, pathLetter);
 				} else {
 					log.info("BUSCANDO RTLOGS A TO SEND");
 					findAndMoveToSend(fileRoot, fileToSend, pathSystemLetter, pathLetter, fileExtension);
 				}
 			} catch (Exception e) {
 				log.info(e.toString());
 			}
 			log.info("BUSCAMOS LOS RTLOGS PARA CHECKSUM");
 			/// ----------------CHECKSUM--------
 			try {
 				log.info("SEGUNDA FASE");
 				log.info("FILE TO CHECKSUM ES " + route(fileToChecksum, pathSystemLetter, pathLetter));
 				File[] files = null;
 				File file = new File(route(fileToChecksum, pathSystemLetter, pathLetter));
 				files = file.listFiles();
 				for (File f : files) {
 					if (f.getName().endsWith("." + fileExtension)) {
 						log.info("COMENZANDO CHECKSUM");
 						String md5 = getMD5Checksum(f.getPath());
 						log.info(md5);
 						boolean dbSuccess = sendMD5toDb(f.getName(), md5);
 						if (dbSuccess) {
 							md5LogFile(f.getName(), md5);
 							log.info("GUARDADO EN LOG");
 							log.info("SE ENVIO A LA BASE DE DATOS");
 							moveToValidated(route(fileToChecksum, pathSystemLetter, pathLetter),
 									route(fileToValidate, pathSystemLetter, pathLetter), f.getName());
 							log.info("MOVIDO A MD5 READY");
 							log.info("The RTLOGS in the folder to MD5 are -> " + f.getName() + " MD5 ES: " + md5);
 						}
 					}
 				}

 			} catch (Exception e) {
 				e.printStackTrace();
 			}
 			// ---------------VALIDATION-----------
// 			File[] files = null;
// 			File file = new File(route(fileToValidate, pathSystemLetter, pathLetter));
// 			// log.info("FILE PATH ES "+FILE_TO_VALIDATE.toString());
// 			files = file.listFiles();
// 			// log.info("LIST FILES "+file.listFiles());
 //
// 			if (files.length > 0)
// 				try {
// 					log.info("TERCERA FASE");
// 					// for (File f : files) {
// 					// if (f.getName().endsWith("." + fileExtension)) {
 //
// 					List<Rtlog> rtlListDiff = getRtlogByStatusF();
// 					if (rtlListDiff.size() > 0)
// 						for (Iterator<Rtlog> i = rtlListDiff.iterator(); i.hasNext();) {
// 							Rtlog rtlog = (Rtlog) i.next();
// 							if (moveAndConfirm(route(fileToValidate, pathSystemLetter, pathLetter),
// 									route(fileToSend, pathSystemLetter, pathLetter), rtlog.getRtlogName())) {
// 								rtlogToReprocess(rtlog.getRtlogName());
// 								log.info("MOVIDO A TO SEND PARA REPROCESAR");
// 								rtlog.setStatus(statusReprocess);
// 								boolean dbSuccess = UpdateStatusToFail(rtlog);
// 								if (dbSuccess) {
// 									log.info("SE ENVIO ESTATUS A LA BASE DE DATOS");
// 								}
// 							}
// 						}
// 					// if (rtlListDiff.size() <= 0){
// 					// TimeUnit.MINUTES.sleep(timeToWaitToDb);// WAIT
// 					// // IF WE DON'T HAVE
// 					// }
// 					// FOR
// 					// ONE
// 					// MINUTE
// 					// }
// 					// }
// 				} catch (Exception e) {
// 					e.printStackTrace();
// 				}
 		finally {
 					try {
 						long endTime = System.currentTimeMillis();
 						log.info("Total execution time all process: " + (endTime - startTime) + "ms");

 						log.info("ESPERAR POR " + timeToWait + " SEGs");
 						log.info("CICLO " + counter.incrementAndGet());
 						TimeUnit.SECONDS.sleep(Integer.parseInt(timeToWait));
 					} catch (InterruptedException e) {
 						log.info(e.toString());
 					}
 				}
// 		}
 	}

 	/**
 	 * Method who return if the route it's unix or windows
 	 * 
 	 * @param route
 	 * @return
 	 */
 	private String route(String route, String pathSystemLetter, String pathLetter) {
 		String wLetter = "W";
 		String routeComplete = (pathSystemLetter.equals(wLetter) ? pathLetter + ":" + route : route);
 		return routeComplete;
 	}

 	/**
 	 * Se encarga de listar los archivos del directorio FILE_ROOT y los filtra
 	 * por su extension .DAT para luego leer su contenido e insertarlo en el
 	 * hashmap con el nombre como llave
 	 */
 	public void findAndLoadRtlogs(String fileToSend, String pathSystemLetter, String pathLetter, String fileExtension) {
 		File[] files = null;
 		File file = new File(route(fileToSend, pathSystemLetter, pathLetter));
 		log.info("file.toString() " + file.toString());
 		log.info("findAndLoadRtlogs ");
 		log.info("fileToSend " + fileToSend);
 		log.info("pathSystemLetter " + pathSystemLetter);
 		log.info("pathLetter " + pathLetter);
 		log.info("fileExtension " + fileExtension);

 		files = file.listFiles(); // retorna una lista de los archivos que estan

 		log.info("BUSCAMOS LOS RTLOGS EN TO SEND Y LOS CARGAMOS");
 		try {
 			for (File f : files) {
 				if (f.getName().endsWith("." + fileExtension)) {
 					String fileString = readFile(f.getPath(), StandardCharsets.UTF_8); // leo
 					hmap.put(f.getName(), fileString);// lleno el hashmap
 					log.info("The RTLOGS in the folder are -> " + f.getName());
 				}
 			}
 			if (hmap.size() == 0)
 				log.info("NO SE ENCONTRARON RTLOGS");
 		} catch (Exception e) {
 			log.info(e.toString());
 		}

 	}

 	/**
 	 * Metodo que carga algunos Rtlogs al directorio TO SEND
 	 */
 	public void findAndMoveToSend(String fileRoot, String fileToSend, String pathSystemLetter, String pathLetter,
 			String fileExtension) {
 		File[] files = null;
 		File file = new File(route(fileRoot, pathSystemLetter, pathLetter));

 		files = file.listFiles(); // retorna una lista de los archivos que estan
 									// en la ruta file

 		log.info("findAndMoveToSend");
 		log.info("fileRoot " + fileRoot);
 		log.info("fileToSend " + fileToSend);
 		log.info("pathSystemLetter " + pathSystemLetter);
 		log.info("pathLetter " + pathLetter);
 		log.info("fileExtension " + fileExtension);
 		log.info("BUSCAMOS LOS RTLOGS");
 		try {
 			for (File f : files) {

 				if (f.getName().endsWith("." + fileExtension)) {
 					log.info("MOVIENDO RTLOS A TO SEND");
 					try {
 						moveToSend(route(fileRoot, pathSystemLetter, pathLetter),
 								route(fileToSend, pathSystemLetter, pathLetter), f.getName());
 					} catch (Exception e) {
 						log.info(e.toString());
 					}
 				}
 			}
 			if (hmap.isEmpty())
 				log.info("NO SE ENCONTRARON RTLOGS");
 		} catch (Exception e) {
 			log.info(e.toString());
 		}

 	}

 	/**
 	 * Metodo que se encarga de enviar el contenido de los Rtlogs a la cola
 	 * Oracle AQ JMS y luego guardar el resultado en el archivo de log
 	 * respectivo
 	 */
 	public void sendToQueue(String fileToSend, String fileToChecksum, String pathSystemLetter, String pathLetter) {

 		log.info("PROCEDEMOS A ENVIAR A LA COLA");
 		Set<Entry<String, String>> set = null;
 		Iterator<Entry<String, String>> iterator = null;
 		try {
 			set = hmap.entrySet();
 			iterator = set.iterator();
 			while (iterator.hasNext()) {
 				Entry<String, String> mentry = iterator.next();
 				log.info("HASHMAP -> key is: " + mentry.getKey());
 				Rtlog rtlog = new Rtlog();
 				rtlog.setRtlogName(mentry.getKey().toString());
 				rtlog.setMessage(mentry.getValue().toString());
 				rtlog.setQueueUserName(queueUserName);
 				rtlog.setQueuePassword(queuePassword);
 				rtlog.setQueueHostname(queueHostname);
 				rtlog.setOracleSid(oracleSid);
 				rtlog.setOracleSid2(oracleSid2);
 				rtlog.setQueuePort(queuePort);
 				rtlog.setQueueDriver(queueDriver);
 				rtlog.setQueueName(queueName);
 				log.info("rtlog.getRtlogName()-> " + rtlog.getRtlogName());
 				log.info("rtlog.getMessage()-> " + rtlog.getMessage());
 				log.info("rtlog.getQueueUserName()-> " + rtlog.getQueueUserName());
 				log.info("rtlog.getQueuePassword()-> " + rtlog.getQueuePassword());
 				log.info("rtlog.getQueuePassword()-> " + rtlog.getQueueHostname());
 				log.info("rtlog.getOracle_sid()-> " + rtlog.getOracleSid());
 				log.info("rtlog.getOracle_sid2()-> " + rtlog.getOracleSid2());
 				log.info("rtlog.getQueuePort()-> " + rtlog.getQueuePort());
 				log.info("rtlog.getQueuePort()-> " + rtlog.getQueueDriver());
 				log.info("rtlog.getQueueName()-> " + rtlog.getQueueName());
 				oracleAQClient.sendMessage(rtlog);
 				log.info("SE ENVIO A LA COLA");
 				logFile(mentry.getKey().toString());
 				log.info("SE ENVIO AL LOG");
 				boolean dbSuccess = sendToDataBase(rtlog, format);
 				if (dbSuccess) {
 					log.info("-------------SE ENVIO A LA BASE DE DATOS--------------");
 					moveToChecksum(route(fileToSend, pathSystemLetter, pathLetter),
 							route(fileToChecksum, pathSystemLetter, pathLetter), mentry.getKey().toString());
 					boolean dbPosboSuccess = sendToDataBasePosbo(rtlog);
 					if (dbPosboSuccess){
 						log.info("SE ENVIO FECHA DE ENVIO A POSBO");
 					}else{
 						log.info("NO SE ENVIO FECHA DE ENVIO A POSBO");
 					}
 				} else {
 					log.info("CAN NOT SEND TO DATA BASE");
 				}
 			}

 		} catch (Exception e) {
 			log.info(e.toString());
 		}

 	}

 	private boolean sendToDataBasePosbo(Rtlog rtlog) {
 		
 		log.info("-------------SE ENVIA A POSBO--------------");
 		
 		boolean dbSuccess = false;
 		DateFormat dateFormat = new SimpleDateFormat(format); // J7
 		Date date = new Date();
 		log.info(dateFormat.format(date)); // 2016/11/16 12:08:43

 		rtlog.setDbUrlPosbo(driver + getHostName() + ":" + dbPortPosbo + "/" + oracleServiceNamePosbo);
 		rtlog.setDbUrlBackupPosbo(driver + dbHostnamePosboBup + ":" + dbPortPosboBup + "/" + oracleServiceNamePosboBup);
 		rtlog.setDbPortPosbo(dbPortPosbo);
 		rtlog.setDbUserNamePosbo(dbUserNamePosbo);
 		rtlog.setDbPasswordPosbo(dbPasswordPosbo);
 		
 		log.info(rtlog.getDbUrlPosbo());
 		log.info(rtlog.getDbUrlBackupPosbo());
 		log.info(rtlog.getDbPortPosbo());
 		log.info(rtlog.getDbUserNamePosbo());
 		log.info(rtlog.getDbPasswordPosbo());
 		
 		try {
 			log.info("Preparing sendToDataBasePosbo");
 			dbSuccess = rtlogClientDao.updateDateInPosbo(rtlog);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return dbSuccess;
 	}

 	/**
 	 * 
 	 * @param rtlogName
 	 * @return
 	 */
 	public boolean sendToDataBase(Rtlog rtlog, String format) {
 		// String storeId = rtlog.getRtlogName();
 		boolean dbSuccess = false;
 		DateFormat dateFormat = new SimpleDateFormat(format); // J7
 		Date date = new Date();
 		log.info("dateFormat -> " + dateFormat.format(date)); // 2016/11/16
 																// 12:08:43

 		// rtlog.setStoreId(Integer.parseInt(rtlog.getRtlogName().substring(8,
 		// 11)));

 		rtlog.setStatus(RtlogClientDao.ENVIADO);
 		rtlog.setSendedTime(dateFormat.format(date));
 		rtlog.setDbUrl(driver + dbHostname + ":" + dbPortNo + "/" + oracleServiceName);
 		rtlog.setDbUrlBackup(driver + dbHostnameBup + ":" + dbPortNoBup + "/" + oracleServiceNameBup);
 		rtlog.setDbUserName(dbUserName);
 		rtlog.setDbPassword(dbPassword);
 		// rtlog.setMd5Origin();
 		// rtlog.setMd5Destination();
 		// rtlog.setReceivedTime();
 		// rtlog.setQtyProcessed();

 		try {
 			dbSuccess = rtlogClientDao.sendToDataBase(rtlog);
 		} catch (Exception e) {
// 			log.info("EXCEPCION SEND TO DATA BASE 1");
 			e.printStackTrace();
// 			log.info("EXCEPCION SEND TO DATA BASE 2");
 		}
 		return dbSuccess;
 	}

 	/**
 	 * 
 	 */
 	public void moveRtlogs() {
 		log.info("PROCEDEMOS A MOVER PARA REALIZAR EL CHECKSUM");

 		try {
 			Set<Entry<String, String>> set = null;
 			Iterator<Entry<String, String>> iterator = null;
 			set = hmap.entrySet();
 			iterator = set.iterator();
 			while (iterator.hasNext()) {
 				Entry<String, String> mentry = iterator.next();

 				log.info("HASHMAP -> key is: " + mentry.getKey());
 				Path origen = Paths.get(route(fileToSend, pathSystemLetter, pathLetter), mentry.getKey().toString());
 				Path destino = Paths.get(route(fileToChecksum, pathSystemLetter, pathLetter),
 						mentry.getKey().toString());
 				Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);
 				log.info("FILE MOVED -> " + mentry.getKey());
 			}

 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 	}

 	/**
 	 * 
 	 * @param origenF
 	 * @param destinoF
 	 * @param fileName
 	 */
 	public void moveToChecksum(String origenF, String destinoF, String fileName) {
 		log.info("SE MUEVEN A TO-CHECKSUM");
 		try {
 			Path origen = Paths.get(origenF, fileName);
 			Path destino = Paths.get(destinoF, fileName);
 			Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);
 		} catch (Exception e) {
 			log.info(e.toString());
 		}
 	}

 	/**
 	 * 
 	 * @param origenF
 	 * @param destinoF
 	 * @param fileName
 	 */
 	public void moveToSend(String origenF, String destinoF, String fileName) {
 		log.info("SE MUEVEN A TO SEND");

 		try {
 			Path origen = Paths.get(origenF, fileName);
 			Path destino = Paths.get(destinoF, fileName);
 			Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);
 		} catch (Exception e) {
 			log.info(e.toString());
 		}
 	}

 	/**
 	 * 
 	 * @param path
 	 * @param encoding
 	 * @return
 	 * @throws IOException
 	 */
 	public String readFile(String path, Charset encoding) throws IOException {
 		long startTime = System.currentTimeMillis();
 		byte[] encoded = null;
 		try {
 			encoded = Files.readAllBytes(Paths.get(path));
 		} catch (Exception e) {
 			log.info(e.toString());
 		}
 		long endTime = System.currentTimeMillis();
 		log.info("Total execution time READING: " + (endTime - startTime) + "ms");
 		return new String(encoded, encoding);
 	}

 	/**
 	 * 
 	 * @param rtlogName
 	 */
 	public void logFile(String rtlogName) {

 		DateFormat dateFormat = new SimpleDateFormat(format);
 		Date date = new Date();

 		Path rtlogTrans = Paths.get(route(logProcess, pathSystemLetter, pathLetter),
 				logFileName + "." + logFileExtension);
 		try (FileWriter fw = new FileWriter(rtlogTrans.toString(), true);
 				BufferedWriter bw = new BufferedWriter(fw);
 				PrintWriter out = new PrintWriter(bw)) {
 			out.println(dateFormat.format(date) + " " + rtlogName);

 		} catch (IOException e) {
 			log.info(e.toString());
 		}
 	}

 	////// -------------checksum-----------------
 	// see this How-to for a faster way to convert
 	// a byte array to a HEX string
 	public String getMD5Checksum(String filename) throws Exception {
 		byte[] b = createChecksum(filename);
 		String result = "";

 		for (int i = 0; i < b.length; i++) {
 			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
 		}
 		return result;
 	}

 	/**
 	 * Metodo encargado de crear el Checksum
 	 * 
 	 * @param filename
 	 * @return
 	 * @throws Exception
 	 */
 	public byte[] createChecksum(String filename) throws Exception {
 		InputStream fis = new FileInputStream(filename);

 		byte[] buffer = new byte[1024];
 		MessageDigest complete = MessageDigest.getInstance("MD5");
 		int numRead;

 		do {
 			numRead = fis.read(buffer);
 			if (numRead > 0) {
 				complete.update(buffer, 0, numRead);
 			}
 		} while (numRead != -1);

 		fis.close();
 		return complete.digest();
 	}

 	public boolean sendMD5toDb(String rtlogName, String md5) {
 		// String storeId = rtlogName;
 		boolean dbSuccess = false;
 		Rtlog rtlog = new Rtlog();
 		rtlog.setRtlogName(rtlogName);
 		rtlog.setMd5Origin(md5);
 		rtlog.setStatus(RtlogClientMd5Dao.ENVIADO);
 		rtlog.setDbUrl(driver + dbHostname + ":" + dbPortNo + "/" + oracleServiceName);
 		rtlog.setDbUserName(dbUserName);
 		rtlog.setDbPassword(dbPassword);
 		log.info("PARAMETER 1-> " + rtlog.getRtlogName());
 		log.info("PARAMETER 2-> " + rtlog.getMd5Origin());
 		log.info("PARAMETER 3-> " + rtlog.getStatus());
 		log.info("PARAMETER 4-> " + rtlog.getDbUrl());
 		log.info("PARAMETER 5-> " + rtlog.getDbUserName());
 		log.info("PARAMETER 6-> " + rtlog.getDbPassword());
 		// Rtlog rtlogDbMessage = new Rtlog(rtlogName, md5);
 		try {
 			dbSuccess = rtlogClientMd5Dao.updateRtlog(rtlog);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return dbSuccess;
 	}

 	/**
 	 * Se encarga de crear o de agregar al archivo rtlog tomando como parametros
 	 * el nombre del Rtlog
 	 * 
 	 * @param RtlogName
 	 * @param md5
 	 */
 	public void md5LogFile(String rtlogName, String md5) {
 		log.info("GUARDANDO LOG");
 		// DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd
 		// HH:mm:ss");
 		// LocalDateTime now = LocalDateTime.now();
 		log.info("rtlogName " + rtlogName);
 		log.info("md5 " + md5);
 		DateFormat dateFormat = new SimpleDateFormat(format);
 		Date date = new Date();
 		log.info(dateFormat.format(date));

 		Path rtlogTrans = Paths.get(route(logProcess, pathSystemLetter, pathLetter),
 				logFileName + "." + logFileExtension);
 		try (FileWriter fw = new FileWriter(rtlogTrans.toString(), true);
 				BufferedWriter bw = new BufferedWriter(fw);
 				PrintWriter out = new PrintWriter(bw)) {
 			out.println(rtlogName + " " + md5 + " " + dateFormat.format(date));

 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}

 	/**
 	 * Se encarga de mover un archivo de manera atomica tomando en cuenta el
 	 * origen, destino y nombre del archivo
 	 * 
 	 * @param origenF
 	 * @param destinoF
 	 * @param fileName
 	 */
 	public void moveToValidated(String origenF, String destinoF, String fileName) {
 		log.info("PROCEDEMOS A REALIZAR EL BACKUP");

 		try {

 			Path origen = Paths.get(origenF, fileName);
 			Path destino = Paths.get(destinoF, fileName);
 			Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);

 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 	}

 	// ---------------VALIDATION---------------------
 	public List<Rtlog> getRtlogByStatusF() {

 		Rtlog rtlog = new Rtlog();

 		rtlog.setDbUrl(driver + dbHostname + ":" + dbPortNo + "/" + oracleServiceName);
 		rtlog.setDbUserName(dbUserName);
 		rtlog.setDbPassword(dbPassword);
 		rtlog.setStoreId(getIdStoreFromHostName());
 		// rtlog.setStoreId(114);

 		List<Rtlog> rtlListDiff = rtlogClientValidatorDao.selectRtlogNameByStatusF(rtlog);
 		return rtlListDiff;
 	}

 	public Integer getIdStoreFromHostName() {
 		String hName = getHostName();
 		String idStore = hName.substring(2, 5); // just get the store
 								// number
 		return Integer.parseInt(idStore);
 	}

 	public String getHostName() {

 		String hostName = "Unknown";
 		try {
 			// InetAddress addr;
 			hostName = InetAddress.getLocalHost().getHostName();
 		} catch (UnknownHostException ex) {
 			ex.printStackTrace();
 			log.info("Hostname can not be resolved");
 		}
 		return hostName;
 	}

 	public boolean moveAndConfirm(String origenF, String destinoF, String fileName) {
 		log.info("PROCEDEMOS A MOVER");
 		boolean moved = false;
 		try {
 			Path origen = Paths.get(origenF, fileName);
 			Path destino = Paths.get(destinoF, fileName);
 			File varTmpDir = new File(origen.toString());
 			if (varTmpDir.exists()) {
 				Files.move(origen, destino, StandardCopyOption.ATOMIC_MOVE);
 				moved = true;
 			} else {
 				log.info("ARCHIVO " + fileName + " NO EXISTE EN ORIGEN");
 			}
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
 		return moved;
 	}

 	public void rtlogToReprocess(String RtlogName) {
 		log.info("GUARDANDO EN REPROCESS");
 		DateFormat dateFormat = new SimpleDateFormat(format);
 		Date date = new Date();

 		Path rtlogTrans = Paths.get(route(logProcess, pathSystemLetter, pathLetter),
 				rtlogToReprocess + "." + logFileExtension);
 		try (FileWriter fw = new FileWriter(rtlogTrans.toString(), true);
 				BufferedWriter bw = new BufferedWriter(fw);
 				PrintWriter out = new PrintWriter(bw)) {
 			out.println(RtlogName + " " + dateFormat.format(date));

 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 	}

 	public boolean UpdateStatusToFail(Rtlog rtlog) {

 		rtlog.setDbUrl(driver + dbHostname + ":" + dbPortNo + "/" + oracleServiceName);
 		rtlog.setDbUserName(dbUserName);
 		rtlog.setDbPassword(dbPassword);
 		boolean dbSuccess = rtlogClientValidatorDao.updateRtlogStatusFail(rtlog);

 		return dbSuccess;
 	}

    

    
}
