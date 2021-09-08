package com.vin.server.monitor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
public class AllClientMonitor {
	static systemInfo sysInfo =new systemInfo();
	static H2JDBCUtils h2jdbsUtil=new H2JDBCUtils();
	static H2JDBCUtils h2jdbsUtilDb=new H2JDBCUtils();
	static Connection connection;
	static SmsSendOut smsSendOut=new SmsSendOut();
	static DeviceDatails dDatails=new DeviceDatails();
	private static final long  MEGABYTE = 1024L * 1024L;
	static SimpleDateFormat sdfWdthiSec = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	static SimpleDateFormat ddMMMYYHHMMSS = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
	static int scheduleTime=0;
	static Runnable runnable;
	static List<DeviceDatails> deviceDatailsList = new ArrayList();
	public static void main(String[] args) {
		try {
			if(connection==null) {
				Properties prop = new Properties();
				try {

				    prop.load(ClientToServerMonitor.class.getClassLoader().getResourceAsStream("config.properties"));

				    //get the property value and print it out
				    h2jdbsUtil.setDriverClass(prop.getProperty("driverClassName"));
				    h2jdbsUtil.setJdbcURL(prop.getProperty("database"));
				    h2jdbsUtil.setJdbcUsername(prop.getProperty("dbuser"));
				    h2jdbsUtil.setJdbcPassword(prop.getProperty("dbpassword"));
				    h2jdbsUtil.setClientName(prop.getProperty("clientName"));
				    h2jdbsUtil.setIpAdress(prop.getProperty("cliendIPAddress"));
				    scheduleTime=Integer.parseInt(prop.getProperty("scheduleTime"));
				} 
				catch (IOException ex) {
				    ex.printStackTrace();
				}				
				if (connection == null) {
					connection = h2jdbsUtil.getConnection();
				}				
				deviceDatailsList=h2jdbsUtil.selectAllCliendIPDetailsRecord(connection, dDatails);
				if (deviceDatailsList.size() > 0) {
					for (DeviceDatails dd : deviceDatailsList) {
						dd.setAlarmStatus(0);
						h2jdbsUtil.updateDeviceDatailsRecord(connection, dd);
					}
				}
				
				runnable = new Runnable() {

					public void run() {
						try {
							if (connection == null) {
								connection = h2jdbsUtil.getConnection();
							}
							deviceDatailsList=h2jdbsUtil.selectAllCliendIPDetailsRecord(connection, dDatails);
							if (deviceDatailsList.size() > 0) {
								try {
									if (connection == null) {
										connection = h2jdbsUtil.getConnection();
									} else {
										connection = h2jdbsUtil.getConnection();
									}

									deviceDatailsList=h2jdbsUtil.selectAllCliendIPDetailsRecord(connection, dDatails);
									int temp = 0;
									for (DeviceDatails dd : deviceDatailsList) {
										if (dd.getIpAddress() != null && dd.getCheckingPinStatus() == 1) {
											if (dd.getDeviceName().contains("Firewall")) {
												temp = sendPingRequestFireWall(dd);
											} else {
												temp = sendPingRequestServer(dd);
											}
										} else if (dd.getDeviceName().equalsIgnoreCase("MEMSTATUS")	&& dd.getCheckingValueCompare() > 0) {
											temp = getProcessCpuLoad(dd);
										} else if (dd.getDeviceName().equalsIgnoreCase("CPUSTATUS")	&& dd.getCheckingValueCompare() > 0) {
											temp = getMemoryLoad(dd);
										} else if (dd.getDeviceName().equalsIgnoreCase("DISKSTATUS")&& dd.getCheckingValueCompare() > 0) {
											temp = getProcessDiskLoad(dd);
										}
										dd.setAlarmStatus(temp);
										h2jdbsUtil.updateDeviceDatailsRecord(connection, dd);
									}

								} catch (SQLException e) {
									connection = null;

								}

							}
						} catch (Exception e) {
							e.printStackTrace();
							connection = null;
						}
					}

				};
				
				ScheduledExecutorService service = Executors
						.newSingleThreadScheduledExecutor();
				service.scheduleAtFixedRate(runnable, 0, scheduleTime, TimeUnit.SECONDS);				
			}
		}catch (Exception e) {
			// TODO: handle exception
			
			e.printStackTrace();
		}
	}
	
	public static int sendPingRequestServer(DeviceDatails dd) 
            throws UnknownHostException, IOException, SQLException 
{ 
  InetAddress geek = InetAddress.getByName(dd.getIpAddress()); 
  if (geek.isReachable(5000)) {
    System.out.println("Host is reachable "+ dd.getClientName()+" "+dd.getDeviceName()); 
   return 0;
  } else {
    System.out.println("Sorry ! We can't reach to this server "+ dd.getClientName()+" "+dd.getDeviceName()); 
 	  insertSMSSendTable(dd,"PigStatus");
    return 1;
  }
}  	  
	  
public static int sendPingRequestFireWall(DeviceDatails dd) 
            throws UnknownHostException, IOException, SQLException 
{ 
  InetAddress geek = InetAddress.getByName(dd.getIpAddress()); 
  if (geek.isReachable(5000)) {
  	System.out.println("Host is reachable "+ dd.getClientName()+" "+dd.getDeviceName()); 
   return 0;
  } else {
    System.out.println("Sorry ! We can't reach to this firewall "+ dd.getClientName()+" "+dd.getDeviceName()); 
    insertSMSSendTable(dd,"FIREWALL");
    return 1;
  }
} 
	
	// Sends ping request to a provided IP address 
	  public static int firewallStatus(String ipAddress) 
	              throws UnknownHostException, IOException 
	  { 
	    InetAddress geek = InetAddress.getByName(ipAddress); 
	   // System.out.println("Sending Ping Request to " + ipAddress); 
	    if (geek.isReachable(5000)) {
	      System.out.println("Host is reachable "+ ipAddress); 
	     return 0;
	    } else {
	      System.out.println("Sorry ! We can't reach to this host"+ ipAddress); 
	      try {
			smsSendOut.setMobileNo(h2jdbsUtil.selectUserByIPAddress(connection, h2jdbsUtil));
			smsSendOut.setMessages("Sorry ! We can't reach to this host"+" Please check the firewall : "+h2jdbsUtil.getFirewallIPAddress());
			smsSendOut.setSendStatus(0);
			smsSendOut.setOccurredOn(Calendar.getInstance().getTimeInMillis());
			h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return 1;
	    }
	  } 
	
	// Sends ping request to a provided IP address 
	  public static int sendPingRequest(String ipAddress) 
	              throws UnknownHostException, IOException 
	  { 
	    InetAddress geek = InetAddress.getByName(ipAddress); 
	   // System.out.println("Sending Ping Request to " + ipAddress); 
	    if (geek.isReachable(5000)) {
	      System.out.println("Host is reachable "+ ipAddress); 
	     return 0;
	    } else {
	      System.out.println("Sorry ! We can't reach to this host "+ ipAddress); 
	      try {
	    	  
			smsSendOut.setMobileNo(h2jdbsUtil.selectUserByIPAddress(connection, h2jdbsUtil));
			smsSendOut.setMessages("Sorry ! We can't reach to this host"+" Please check the client : "+h2jdbsUtil.getClientName());
			smsSendOut.setOccurredOn(Calendar.getInstance().getTimeInMillis());
			smsSendOut.setSendStatus(0);
			h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return 1;
	    }
	  } 
	  
	  
public static int sendPingRequest(DeviceDatails dd) 
              throws UnknownHostException, IOException, SQLException 
  { 
    InetAddress geek = InetAddress.getByName(dd.getIpAddress()); 
    //System.out.println("Sending Ping Request to " + dd.getIpAddress()); 
    if (geek.isReachable(5000)) {
      System.out.println("Host is reachable"+ dd.getIpAddress()); 
      dd.setAlarmStatus(0);
      h2jdbsUtil.updateDeviceDatailsRecord(connection, dd);
     return 0;
    } else {
      System.out.println("Sorry ! We can't reach to this host"+ dd.getIpAddress()); 
      if(dd.getAlarmStatus()==0) {
    	  insertSMSSendTable(dd,"PigStatus");
      }
      return 1;
    }
  }   
	  

	private static void insertSMSSendTable(DeviceDatails dd, String msgType) {
		
		if(dd.getAlarmStatus()==0) {
		try {
			long occurredOnLon = Calendar.getInstance().getTimeInMillis();
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(occurredOnLon);
			String smsMessages = h2jdbsUtil.selectSMSMessagesByKetStatus(connection, msgType);
			if (smsMessages == null || smsMessages == "") {
				if (msgType.equalsIgnoreCase("PigStatus")) {
					smsMessages = "Sorry ! We can't reach to this host. Please check the client :"
							+ h2jdbsUtil.getClientName();
				}else if (msgType.equalsIgnoreCase("Firewalls")) {
					smsMessages = "Sorry ! We can't reach to this fire walls. Please check the client :"
							+ h2jdbsUtil.getClientName();
				} else if (msgType.equalsIgnoreCase("MemStatus")) {
					smsMessages = "Cliend pc physical memory more than 75% pleasae check its client :"
							+ h2jdbsUtil.getClientName();
				} else if (msgType.equalsIgnoreCase("CpuStatus")) {
					smsMessages = "Cliend pc Cpu Load more than 75% pleasae check its client :"
							+ h2jdbsUtil.getClientName();
				} else if (msgType.equalsIgnoreCase("DikStatus")) {
					smsMessages = "Cliend pc disk space more than 75% pleasae check its client :"
							+ h2jdbsUtil.getClientName();
				}

			} else {
				String temp = smsMessages + " " + dd.getClientName();
				smsMessages = temp;
			}

			List<String> mobileNo = h2jdbsUtil.selectUserByIPAddress(connection, dd);
			for (String m : mobileNo) {
				smsSendOut = new SmsSendOut();
				smsSendOut.setMobileNo(m);
				//smsSendOut.setMessages(smsMessages + " " + sdfWdthiSec.format(cal.getTime()));
				smsSendOut.setMessages(ddMMMYYHHMMSS.format(cal.getTime())+" "+smsMessages);
				smsSendOut.setOccurredOn(occurredOnLon);
				smsSendOut.setSendStatus(0);
				h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}



		private static int getRandomDoubleBetweenRange(double min, double max){
		    double x = (Math.random()*((max-min)+1))+min;
		    if(x>75){
		    	return 1;
		    }
		    return 0;
		}
	  
		public static int getProcessCpuLoad(DeviceDatails dd) throws Exception {
	        Object value = null;
			OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
			  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			    method.setAccessible(true);
			    if (method.getName().startsWith("get") && method.getName().contains("CpuLoad")
			        && Modifier.isPublic(method.getModifiers())) {

			        try {
			            value = method.invoke(operatingSystemMXBean);
			        } catch (Exception e) {
			            value = e;
			        } // try
			        //System.out.println(method.getName() + " = " + value);
			    } // if
			  } // for
			  double cpuLoad=Math.round(Double.parseDouble(value.toString())*100);
			  if(cpuLoad>=dd.getCheckingValueCompare()){
				  
	/*			  try {
					  List<String> mobileNo=h2jdbsUtil.selectUserByIPAddress(connection, dd);
					  for(String m:mobileNo) {
						  smsSendOut=new SmsSendOut();
						smsSendOut.setMobileNo(m);
						smsSendOut.setMessages("Sorry ! Cliend pc Cpu Load more than "+dd.getCheckingValueCompare()+"% pleasae check its  : "+dd.getClientName());
						smsSendOut.setOccurredOn(Calendar.getInstance().getTimeInMillis());
						smsSendOut.setSendStatus(0);
						h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
					  }
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	*/			  
				  insertSMSSendTable(dd,"CpuStatus");
				  return 1;
			  }
			  return 0;
		}
		public static int getMemoryLoad(DeviceDatails dd) throws Exception {
	        Object value = null;
	        long totalPhysicalMemory=0;
	        long freePhysicalMemory=0;
	        long usedPhysicalMemory=0;
	        long usedPhysicalMemoryPercentage=0;
			OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
			  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
			    method.setAccessible(true);
			    if (method.getName().startsWith("get") && (method.getName().contains("TotalPhysicalMemory") || method.getName().contains("FreePhysicalMemory"))
			        && Modifier.isPublic(method.getModifiers())) {

			        try {
			        	if(method.getName().contains("TotalPhysicalMemory")) {
			        		 value = method.invoke(operatingSystemMXBean);
			        		totalPhysicalMemory=bytesToMeg(Long.parseLong(value.toString()));
			        	}
			        	if(method.getName().contains("FreePhysicalMemory")) {
			        		 value = method.invoke(operatingSystemMXBean);
			        		freePhysicalMemory=bytesToMeg(Long.parseLong(value.toString()));
			        	}	
			        	
			            value = method.invoke(operatingSystemMXBean);
				       // System.out.println(method.getName() + " = " + bytesToMeg(Long.parseLong(value.toString())));
			        } catch (Exception e) {
			            value = e;
			        } // try

			    } // if
			  } // for
			  usedPhysicalMemory=(totalPhysicalMemory-freePhysicalMemory);
			  usedPhysicalMemoryPercentage=((usedPhysicalMemory*100)/totalPhysicalMemory);
			  if(usedPhysicalMemoryPercentage>=dd.getCheckingValueCompare()){
				  
				  insertSMSSendTable(dd,"MemStatus");
				 
				/*  try {
					  List<String> mobileNo=h2jdbsUtil.selectUserByIPAddress(connection, dd);
					  for(String m:mobileNo) {
						  smsSendOut=new SmsSendOut();
						smsSendOut.setMobileNo(m);
						smsSendOut.setMessages("Sorry ! Cliend pc physical memory more than "+dd.getCheckingValueCompare()+"% pleasae check its  : "+dd.getClientName());
						smsSendOut.setOccurredOn(Calendar.getInstance().getTimeInMillis());
						smsSendOut.setSendStatus(0);
						h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
					  }
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				  */
				  return 1;
			  }
			  return 0;
		}
		 public static long bytesToMeg(long bytes) {
			  return bytes / MEGABYTE ;
		}
		 
		 
		 
			public static int getProcessDiskLoad(DeviceDatails dd) throws Exception {
				for (Path root : FileSystems.getDefault().getRootDirectories()) {
					double userDiskSpace=0;
				    try {
				        FileStore store = Files.getFileStore(root);
				        userDiskSpace=(((bytesToMeg1(store.getTotalSpace())-bytesToMeg1(store.getUsableSpace()))*100)/bytesToMeg1(store.getTotalSpace()));
				        //System.out.println("available=" + bytesToMeg1(store.getUsableSpace()) + ", total=" + bytesToMeg1(store.getTotalSpace()));
				        if(userDiskSpace>dd.getCheckingValueCompare()) {
					       // System.out.println(root+"available=" + bytesToMeg1(store.getUsableSpace()) + ", total=" + bytesToMeg1(store.getTotalSpace())+" Used % : "+userDiskSpace);
					        
					        insertSMSSendTable(dd,"DikStatus");
				        /*	
					        try {
								  List<String> mobileNo=h2jdbsUtil.selectUserByIPAddress(connection, dd);
								  for(String m:mobileNo) {
									  smsSendOut=new SmsSendOut();
								smsSendOut.setMobileNo(m);
								smsSendOut.setMessages("Sorry ! Cliend pc disk space more than "+dd.getCheckingValueCompare()+"% pleasae check its  : "+h2jdbsUtil.getClientName());
								smsSendOut.setOccurredOn(Calendar.getInstance().getTimeInMillis());
								smsSendOut.setSendStatus(0);
								h2jdbsUtil.insertSMSSend(connection, h2jdbsUtil, smsSendOut);
								  }
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}		
						  
					        */
					        return 1;
				        }
				        
				    } catch (IOException e) {
				        System.out.println("error querying space: " + e.toString());
				    }
				}
			  return 0;
			}		 
		 
	  private static void printUsage() {
		  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
		    method.setAccessible(true);
		    if (method.getName().startsWith("get")
		        && Modifier.isPublic(method.getModifiers())) {
		            Object value;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		        } catch (Exception e) {
		            value = e;
		        } // try
		       // System.out.println(method.getName() + " = " + value);
		    } // if
		  } // for
		}
		 public static long bytesToMeg1(long bytes) {
			  return bytes / 1073741824 ;
		}
	  private static void printCPUUsage(int i) {
		  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
		  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
		    method.setAccessible(true);
		   
		    if (method.getName().startsWith("get") && method.getName().contains("SystemCpuLoad") && method.getName().contains("Load")
	                 && Modifier.isPublic(method.getModifiers())
		        && Modifier.isPublic(method.getModifiers())) {
		            Object value;
		        try {

		            value = method.invoke(operatingSystemMXBean);
		          //  System.out.println(i+" method.getName() :  "+method.getName()+" value "+Math.round((Double.parseDouble(value.toString())*100)));
		        } catch (Exception e) {
		            value = e;
		        } // try
		       // System.out.println(method.getName() + " = " + value);
		    } // if
		  } // for
		}	  
}
