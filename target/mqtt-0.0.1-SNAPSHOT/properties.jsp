<%@ page language="java" contentType="text/html; charset=utf-8"    pageEncoding="utf-8"%>
<%@ page import="org.jepetto.util.PropertyReader" %>
<%@ page import="javax.naming.Context" %>
<%@ page import="javax.naming.InitialContext" %>
<%@ page import="javax.sql.DataSource" %>;

<html>
<head>
<meta charset="utf-8">
<title>개발 환경 확인</title>
</head>
<body>
<%

	PropertyReader reader = PropertyReader.getInstance();
	reader.load(System.getProperty("jepetto.properties"));
	String keys[] = reader.getProperties();
	String value = null;
	 
	for(int i = 0 ; i < keys.length ; i++){
	 out.println(keys[i]+ ":" + reader.getProperty(keys[i])+"<br>");
	}  
	
	Context ctx = new InitialContext();
	//Context initCtx  = (Context) ctx.lookup("java:/comp/env");
	DataSource ds = (DataSource) ctx.lookup(reader.getProperty("mqtt_mysql_datasource"));
	out.println("db connection is closed ? " +  ds.getConnection().isClosed() );
	
	//*/

 %>   
</body>
</html>