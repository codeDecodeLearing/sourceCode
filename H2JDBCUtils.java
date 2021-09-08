package com.vin.server.monitor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
public class H2JDBCUtils {


	private static String driverClass;
	private static String jdbcURL = "";
    private static String jdbcUsername = "";
    private static String jdbcPassword = "";
    private static String clientName = "";
    private static String ipAdress = "";
    private static String firewallIPAddress="";


	private static int pingStatus=0;
    private static int cpuStatus=0;
    private static int memStatus=0;
    private static int diskStatus=0;
    private static long occurredOn=0;
    private static int id = 0;
    private static int firewallStatus=0;



	private static final String UPDATE_USERS_SQL = "update SERVERDETAILS set PINGSTATUS =?,CPUSTATUS =?,MEMSTATUS =?,DISKSTATUS =?,OCCURREDON =?,FIREWALLSTATUS=? where id =?;";//UPDATE_USERS_SQL = "update users set name = ? where id = ?;";
    private static final String INSERT_USERS_SQL ="INSERT INTO SERVERDETAILS (IPADDRESS,SERVERNAME,REMARK,PINGSTATUS,CPUSTATUS,MEMSTATUS,DISKSTATUS,OCCURREDON,FIREWALLSTATUS,FIREWALLIPADDRESS) VALUES (?,?,?,?,?,?,?,?,?,?)"; //"INSERT INTO users(id, name, email, country, password) VALUES (?, ?, ?, ?, ?);";
    private static final String QUERY = "select count(*) as totalCount from SERVERDETAILS where SERVERNAME =?";//"select id,name,email,country,password from users where id =?";
    private static final String QUERY1 = "select id,IPADDRESS,SERVERNAME,PINGSTATUS,CPUSTATUS,MEMSTATUS,DISKSTATUS,FIREWALLSTATUS,FIREWALLIPADDRESS  from SERVERDETAILS where SERVERNAME =?";//"select id,name,email,country,password from users where id =?";
    private static final String INSERT_SEND_SQL ="INSERT INTO SMSSENDOUT (MobileNo ,Messages,SENDSTATUS, OCCURREDON ) VALUES (?,?,?,?)"; //"INSERT INTO users(id, name, email, country, password) VALUES (?, ?, ?, ?, ?);";
    private static final String userDetails = "select MobileNo from Users where CLIENTNAME=?";
    private static final String CLIENT_QUERY = "select count(*) as totalCount from DEVICEDATAILS where CLIENTNAME=?";
    private static final String INSERT_DeviceDetails_SQL ="INSERT INTO DEVICEDATAILS (CLIENTNAME ,IPADDRESS ,DEVICENAME ,CHECKINGPINSTATUS    ,CHECKINGVALUECOMPARE ,  ALARMSTATUS,OCCURREDON) VALUES (?,?,?,?,?,?,?)"; //"INSERT INTO users(id, name, email, country, password) VALUES (?, ?, ?, ?, ?);";
    private static final String SELECT_DeviceDetails_SQL ="SELECT ID,CLIENTNAME ,IPADDRESS ,DEVICENAME ,CHECKINGPINSTATUS    ,CHECKINGVALUECOMPARE ,  ALARMSTATUS,OCCURREDON FROM DEVICEDATAILS WHERE CLIENTNAME=?"; //"INSERT INTO users(id, name, email, country, password) VALUES (?, ?, ?, ?, ?);";
    private static final String SELECT_All_ClientIPAddress_SQL =" SELECT ID,CLIENTNAME ,IPADDRESS ,DEVICENAME ,CHECKINGPINSTATUS    ,CHECKINGVALUECOMPARE ,  ALARMSTATUS,OCCURREDON FROM DEVICEDATAILS WHERE CHECKINGPINSTATUS=1 and DEVICENAME ='SERVER'"; //"INSERT INTO users(id, name, email, country, password) VALUES (?, ?, ?, ?, ?);";
    private static final String UPDATE_DeviceDetails_SQL = "update DEVICEDATAILS set ALARMSTATUS =?,OCCURREDON=? where id =?";
    private static final String userListDetails = "select MobileNo from Users where CLIENTNAME=?";
    private static final String smsMessges = "select MESSAGES from SMSMESSAGES where KEYMESSAGE=?";
    public static Connection getConnection() throws ClassNotFoundException {
        Connection connection = null;
        try {
        	Class.forName(driverClass.trim());
            connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connection;
    }   
    
    public void insertDeviceRecord(Connection connection,DeviceDatails dDatails) throws SQLException {
        //System.out.println(INSERT_USERS_SQL);
        // Step 1: Establishing a Connection
        try (
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_DeviceDetails_SQL)) {
            //preparedStatement.setInt(1, 1);
            preparedStatement.setString(1,dDatails.getClientName());
            preparedStatement.setString(2, dDatails.getIpAddress());
            preparedStatement.setString(3, dDatails.getDeviceName());
            preparedStatement.setInt(4, dDatails.getCheckingPinStatus());
            preparedStatement.setInt(5, dDatails.getCheckingValueCompare());
            preparedStatement.setInt(6, dDatails.getAlarmStatus());
            preparedStatement.setLong(7, Calendar.getInstance().getTimeInMillis());
            //System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            e.printStackTrace();
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }     
    
    public void insertRecord(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
        //System.out.println(INSERT_USERS_SQL);
        // Step 1: Establishing a Connection
        try (
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
            //preparedStatement.setInt(1, 1);
            preparedStatement.setString(1,h2JDBCUtils.getIpAdress());
            preparedStatement.setString(2, h2JDBCUtils.getClientName());
            preparedStatement.setString(3, "");
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.setInt(6, 0);
            preparedStatement.setInt(7, 0);
            preparedStatement.setLong(8, Calendar.getInstance().getTimeInMillis());
            preparedStatement.setInt(9, 0);
            preparedStatement.setString(10, h2JDBCUtils.getFirewallIPAddress());
            //System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            e.printStackTrace();
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }    
    

    
    
    public void insertSMSSend(Connection connection,H2JDBCUtils h2JDBCUtils,SmsSendOut smsSendOut) throws SQLException {
       // System.out.println(INSERT_SEND_SQL);
        // Step 1: Establishing a Connection
        try (
            // Step 2:Create a statement using connection object
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SEND_SQL)) {
            //preparedStatement.setInt(1, 1);
            preparedStatement.setString(1,smsSendOut.getMobileNo());
            preparedStatement.setString(2, smsSendOut.getMessages());
            preparedStatement.setLong(3, smsSendOut.getSendStatus());
            preparedStatement.setLong(4, smsSendOut.getOccurredOn());
            //System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
            e.printStackTrace();
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }   
    
    public boolean checkingRecord(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
        boolean result=false;
    	PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
            preparedStatement.setString(1, h2JDBCUtils.getClientName());
            //System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("totalCount");
                if(id>0) {
                	return true;
                }
            }
            return false;
    }
    
    public boolean checkingClientRecord(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
        boolean result=false;
    	PreparedStatement preparedStatement = connection.prepareStatement(CLIENT_QUERY);
            preparedStatement.setString(1, h2JDBCUtils.getClientName());
            //System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            ResultSet rs = preparedStatement.executeQuery();

            // Step 4: Process the ResultSet object.
            while (rs.next()) {
                int id = rs.getInt("totalCount");
                if(id>0) {
                	return true;
                }
            }
            return false;
    }
    
    public String selectSMSMessagesByKetStatus(Connection connection,String keyStatus) throws SQLException {
    	PreparedStatement preparedStatement = connection.prepareStatement(smsMessges);
            preparedStatement.setString(1, keyStatus);
           System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {  
            	return rs.getString("MESSAGES");
            }
            return "";
    }
    
    public String selectUserByIPAddress(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
    	PreparedStatement preparedStatement = connection.prepareStatement(userDetails);
            preparedStatement.setString(1, h2JDBCUtils.getClientName());
            //System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {  
            	return rs.getString("MobileNo");
            }
            return "";
    }
    
    public List<String> selectUserByIPAddress(Connection connection,DeviceDatails deviceDatails) throws SQLException {
    	List<String> mobileNoList=new ArrayList();
    	PreparedStatement preparedStatement = connection.prepareStatement(userListDetails);
            preparedStatement.setString(1, deviceDatails.getClientName());
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {  
            	mobileNoList.add(rs.getString("MobileNo").trim());
            }
            return mobileNoList;
    }
    
    public List<DeviceDatails> selectAllCliendIPDetailsRecord(Connection connection,DeviceDatails deviceDatails) throws SQLException {
    	List<DeviceDatails> listDeviceDatails=new ArrayList();
    	PreparedStatement preparedStatement = connection.prepareStatement(SELECT_All_ClientIPAddress_SQL);
            //System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            
            DeviceDatails dDatails=new DeviceDatails();
            
            while (rs.next()) {  
            	dDatails=new DeviceDatails();
            	dDatails.setId(rs.getInt("ID"));
            	dDatails.setClientName(rs.getString("CLIENTNAME"));
            	dDatails.setIpAddress(rs.getString("IPADDRESS"));
            	dDatails.setDeviceName(rs.getString("DEVICENAME"));
            	dDatails.setCheckingPinStatus(rs.getInt("CHECKINGPINSTATUS"));
            	dDatails.setCheckingValueCompare(rs.getInt("CHECKINGVALUECOMPARE"));
            	dDatails.setAlarmStatus(rs.getInt("ALARMSTATUS"));
            	listDeviceDatails.add(dDatails);
            }
            return listDeviceDatails;
    } 

    public List<DeviceDatails> selectDeviceDatailsRecord(Connection connection,DeviceDatails deviceDatails) throws SQLException {
    	List<DeviceDatails> listDeviceDatails=new ArrayList();
    	PreparedStatement preparedStatement = connection.prepareStatement(SELECT_DeviceDetails_SQL);
            preparedStatement.setString(1, clientName);
            //System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            
            DeviceDatails dDatails=new DeviceDatails();
            
            while (rs.next()) {  
            	dDatails=new DeviceDatails();
            	dDatails.setId(rs.getInt("ID"));
            	dDatails.setClientName(rs.getString("CLIENTNAME"));
            	dDatails.setIpAddress(rs.getString("IPADDRESS"));
            	dDatails.setDeviceName(rs.getString("DEVICENAME"));
            	dDatails.setCheckingPinStatus(rs.getInt("CHECKINGPINSTATUS"));
            	dDatails.setCheckingValueCompare(rs.getInt("CHECKINGVALUECOMPARE"));
            	dDatails.setAlarmStatus(rs.getInt("ALARMSTATUS"));
            	listDeviceDatails.add(dDatails);
            }
            return listDeviceDatails;
    }
    
    public H2JDBCUtils selectRecord(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
    	PreparedStatement preparedStatement = connection.prepareStatement(QUERY1);
            preparedStatement.setString(1, h2JDBCUtils.getClientName());
            //System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {  
            	h2JDBCUtils.setId(rs.getInt("id"));
            	h2JDBCUtils.setClientName(rs.getString("SERVERNAME"));
            	h2JDBCUtils.setIpAdress(rs.getString("IPADDRESS"));
            	h2JDBCUtils.setPingStatus(rs.getInt("PINGSTATUS"));
            	h2JDBCUtils.setCpuStatus(rs.getInt("CPUSTATUS"));
            	h2JDBCUtils.setMemStatus(rs.getInt("MEMSTATUS"));
            	h2JDBCUtils.setDiskStatus(rs.getInt("DISKSTATUS"));
            	h2JDBCUtils.setFirewallStatus(rs.getInt("FIREWALLSTATUS"));
            	h2JDBCUtils.setFirewallIPAddress(rs.getString("FIREWALLIPADDRESS"));
            }
            return h2JDBCUtils;
    }
    
    public void updateDeviceDatailsRecord(Connection connection,DeviceDatails deviceDatails) throws SQLException {
        //System.out.println(UPDATE_USERS_SQL);
        // Step 1: Establishing a Connection
        try ( 
            // Step 2:Create a statement using connection object  SERVERDETAILS set PINGSTATUS =?,CPUSTATUS =?,MEMSTATUS =?,DISKSTATUS =?,OCCURREDON =? 
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DeviceDetails_SQL)) {
            preparedStatement.setInt(1, deviceDatails.getAlarmStatus());
            preparedStatement.setLong(2, Calendar.getInstance().getTimeInMillis());
            preparedStatement.setInt(3, deviceDatails.getId());
            //System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
        	e.printStackTrace();
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }    
    
    
    public void updateRecord(Connection connection,H2JDBCUtils h2JDBCUtils) throws SQLException {
       // System.out.println(UPDATE_USERS_SQL);
        // Step 1: Establishing a Connection
        try ( 
            // Step 2:Create a statement using connection object  SERVERDETAILS set PINGSTATUS =?,CPUSTATUS =?,MEMSTATUS =?,DISKSTATUS =?,OCCURREDON =? 
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USERS_SQL)) {
            preparedStatement.setInt(1, h2JDBCUtils.getPingStatus());
            preparedStatement.setInt(2, h2JDBCUtils.getCpuStatus());
            preparedStatement.setInt(3, h2JDBCUtils.getMemStatus());
            preparedStatement.setInt(4, h2JDBCUtils.getDiskStatus());
            preparedStatement.setLong(5, Calendar.getInstance().getTimeInMillis());
            preparedStatement.setInt(6, h2JDBCUtils.getFirewallStatus());
            preparedStatement.setInt(7, h2JDBCUtils.getId());
           // System.out.println(preparedStatement);
            // Step 3: Execute the query or update query
            preparedStatement.executeUpdate();
        } catch (SQLException e) {

            // print SQL exception information
        	e.printStackTrace();
        }

        // Step 4: try-with-resource statement will auto close the connection.
    }
    public static String getJdbcURL() {
		return jdbcURL;
	}

	public static void setJdbcURL(String jdbcURL) {
		H2JDBCUtils.jdbcURL = jdbcURL;
	}

	public static String getJdbcUsername() {
		return jdbcUsername;
	}

	public static void setJdbcUsername(String jdbcUsername) {
		H2JDBCUtils.jdbcUsername = jdbcUsername;
	}

	public static String getJdbcPassword() {
		return jdbcPassword;
	}

	public static void setJdbcPassword(String jdbcPassword) {
		H2JDBCUtils.jdbcPassword = jdbcPassword;
	}
	

    public static int getId() {
		return id;
	}

	public static void setId(int id) {
		H2JDBCUtils.id = id;
	}

	public static String getStatus() {
		return Status;
	}

	public static void setStatus(String status) {
		Status = status;
	}

	private static String Status = "0";
	public static String getIpAdress() {
		return ipAdress;
	}

	public static void setIpAdress(String ipAdress) {
		H2JDBCUtils.ipAdress = ipAdress;
	}


	public static long getOccurredOn() {
		return occurredOn;
	}

	public static void setOccurredOn(long occurredOn) {
		H2JDBCUtils.occurredOn = occurredOn;
	}

	public static int getPingStatus() {
		return pingStatus;
	}

	public static void setPingStatus(int pingStatus) {
		H2JDBCUtils.pingStatus = pingStatus;
	}

	public static int getCpuStatus() {
		return cpuStatus;
	}

	public static void setCpuStatus(int cpuStatus) {
		H2JDBCUtils.cpuStatus = cpuStatus;
	}

	public static int getMemStatus() {
		return memStatus;
	}

	public static void setMemStatus(int memStatus) {
		H2JDBCUtils.memStatus = memStatus;
	}

	public static int getDiskStatus() {
		return diskStatus;
	}

	public static void setDiskStatus(int diskStatus) {
		H2JDBCUtils.diskStatus = diskStatus;
	}
	
    public static String getFirewallIPAddress() {
		return firewallIPAddress;
	}

	public static void setFirewallIPAddress(String firewallIPAddress) {
		H2JDBCUtils.firewallIPAddress = firewallIPAddress;
	}
	
	public static int getFirewallStatus() {
		return firewallStatus;
	}

	public static void setFirewallStatus(int firewallStatus) {
		H2JDBCUtils.firewallStatus = firewallStatus;
	}

	public static String getClientName() {
		return clientName;
	}

	public static void setClientName(String clientName) {
		H2JDBCUtils.clientName = clientName;
	}

	public static String getDriverClass() {
		return driverClass;
	}

	public static void setDriverClass(String driverClass) {
		H2JDBCUtils.driverClass = driverClass;
	}
}
