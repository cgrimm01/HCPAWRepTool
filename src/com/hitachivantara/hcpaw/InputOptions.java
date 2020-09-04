package com.hitachivantara.hcpaw;
/**
 * HCP Anywhere Reporting Tool  
 * Copyright (C) 2017-2018 Hitachi Vantara Corporation
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
 * to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */

import org.apache.commons.cli.CommandLineParser;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;

import org.apache.commons.lang.StringUtils;

import com.hitachivantara.hcpaw.gui.Gui;

public class InputOptions {

    final static int LOG_BASE = Helper.LOG_BASE; // base log level
    final static int LOG_ERROR = Helper.LOG_ERROR; // error log level
    final static int LOG_WARNING = Helper.LOG_WARNING; // warning log level  
	final static int DEFAULT_LOGLEVEL = Helper.DEFAULT_LOGLEVEL;
	final static int LOG_NONDEBUG_MAX = Helper.LOG_NONDEBUG_MAX;

	final static int RETURN_OK = Helper.RETURN_OK; // OK	
	
	// Exit codes:
	final static int EXIT_OK = Helper.EXIT_OK;	
	final static int EXIT_USAGE_OK = Helper.EXIT_USAGE_OK;
    final static int EXIT_USAGE_ERROR = Helper.EXIT_USAGE_ERROR;

	final static private String newline = Helper.NEWLINE;	


	// Command line arguments:
	final static private String CMD_LINE_HELP = "help";
	final static private String CMD_LINE_AWSERVER = "aw-server";
	final static private String CMD_LINE_USERNAME = "username";
	final static private String CMD_LINE_PASSWORD = "password";
	final static private String CMD_LINE_USERKEYSTORE = "user-keystore";
	final static private String CMD_LINE_MAPI = "report";
	final static private String CMD_LINE_AUDITED_PROFILE = "audited-profile";
	final static private String CMD_LINE_AUDITED_USER = "audited-user";	
	final static private String CMD_LINE_AUDITED_PATH = "audited-path";	
	final static private String CMD_LINE_JSONFILE = "jsonfile";
	final static private String CMD_LINE_CSVFILE = "csvfile";
	final static private String CMD_LINE_LOGLEVEL = "loglevel";
	final static private String CMD_LINE_RECORDSINREPLY = "num-records-reply";
	final static private String CMD_LINE_STARTTIME = "start-time";
	final static private String CMD_LINE_ENDTIME = "end-time";
	final static private String CMD_LINE_SYSTEMSCOPE = "system-scope";
	final static private String CMD_LINE_MULTIPLE_REPORTS = "get-all";
	final static private String CMD_LINE_TIMEZONE = "timezone";
	final static private String CMD_LINE_GUI = "gui";
	final static private String CMD_LINE_AWPORT = "port";
	final static private String CMD_LINE_CSVSUFFIX = "csv-time-suffix";	
	final static private String CMD_LINE_TOTAL_RECORD_NUMBERS = "total-records";
	final static private String CMD_LINE_DESCRIPTION_PLACE = "description";
	final static private String CMD_LINE_BEARER_TOKEN = "bearer-token";
	final static private String CMD_LINE_EXCLUDE_VERSIONS = "exclude-versions";
	final static private String CMD_LINE_DEBUG = "debug";	

    // Default values:
	final static public String DEFAULT_TIMEZONE = "GMT";
	final static public int DEFAULT_AWPORT = 8000;
	final static public int DEFAULT_RECORDS_IN_REPLY = 100;
	final static public int MAX_RECORDS_IN_REPLY = 5000;
	final static public int DEFAULT_TOTAL_RECORDS = 0;
	final static public int DEFAULT_DESCRIPTION_PLACE = CsvWriter.DESCRIPTION_FIRST_LINE;
	final static public String CSV_EXTENTION = ".csv";
	final static public String JSON_EXTENTION = ".json";

	final private static long THIRTY_DAYS_MICRO = (long) (24 * 3600 * 1000) * 30;	    // 30 days in microseconds		
	final private static String MAPI_PREFIX = "/mapi/report/";

	// Configurable values:
	private static boolean debug = false;  // set for debugging

	private static boolean isSingleRequest = true;
	
	private static String csvfile = null; 
	private static String jsonfile = null;
 	
	private static int logLevel = DEFAULT_LOGLEVEL;
	private static String timezone = DEFAULT_TIMEZONE;  
	private static int port = DEFAULT_AWPORT;

	private static boolean csvTimeStampSuffix = false;
	private static String request = null; 
	private static int mapiNumber = 0;
	private static String awName = null; // e.g. "cluster64d-vm4-0.lab.archivas.com"
	private static String username = null; //e.g. "vrevsin"
	private static String password = null; 
	private static String userKeyStore = null;
	private static String auditedProfile = null; // e.g. "fssusers"
	private static String auditedUser = null; // e.g. "testuser"
	private static String auditedPath = null; // e.g. "/myfolder/myfile
	private static String startTimeEpoch = null;
	private static String endTimeEpoch = null;    	
	private static long endTimeDefault = System.currentTimeMillis();
	private static String startTimeFriendly = ""; // user friendly time
	private static String endTimeFriendly = ""; // user friendly time   	 
	private static boolean systemScope = false;
	private static int numResults = DEFAULT_RECORDS_IN_REPLY; // number of records to read at one time
	private static int totalRecordsMax = DEFAULT_TOTAL_RECORDS; // number of total requested records
	private static int descriptionPlace = DEFAULT_DESCRIPTION_PLACE; // where to place the report description in CSV file 
	private static String bearerToken = null;
	private static Boolean excludeVersions = Boolean.FALSE;
	
	private static String jfilename = null;
	private static boolean usejsonfile = false;	
	private static String cfilename = null;
	
	private static boolean isAuthenticated = false;

 	private static boolean gui = false;  // GUI or Command Line?

	//// Input Options Getters

	public static boolean getDebug() {
		return debug;	
	}

	public static void setIsSingleRequest(boolean value) {
		isSingleRequest = value;	
	}

	public static boolean getIsSingleRequest() {
		return isSingleRequest;	
	}

	public static boolean getGui() {
		return gui;
	}

	public static void setGui(boolean value) {
		Helper.setGui(value);
		gui = value;
	}

	public static String getCsvfile() {
		return csvfile; 
	}

	public static String getJsonfile() {
		return jsonfile;
 	}

	public static int getLogLevel() {
		return logLevel;
	}

	public static String getTimezone() {
		return timezone;
	}

	public static int getPort() {
		return port;
	}

	public static boolean getCsvTimeStampSuffix() {
		return csvTimeStampSuffix;
	}

	public static void setCsvTimeStampSuffix(boolean value) {
		csvTimeStampSuffix = value;
	}

	public static String getAwName() { 
		return awName; 
	}

    public static void setAwName(String value) {
		awName = value;
	}

    public static void setUsername(String value) { 
		username = value;
	}

	public static String getUsername() { 
		return username; 
	}

	public static void setUserKeyStore(String value) {
		userKeyStore = value;
	}
	
	public static String getUserKeyStore() {
		return userKeyStore;
	}
	
	public static void setPassword(String value) {
		password = value;
	}
	
	public static String getPassword() { 
		return password; 
	}

	public static String getAuditedProfile() {
		return auditedProfile;
	}

    public static void setAuditedProfile(String value) {
		auditedProfile = value;
	}

	public static String getAuditedUser() {
		return auditedUser;
	}

	public static void setAuditedUser(String value) {
		auditedUser = value;
	}

	public static String getAuditedPath() {
		return auditedPath;
	}

	public static void setAuditedPath(String value) {
		auditedPath = value;
	}

	public static boolean getSystemScope() {
		return systemScope;
	}

	public static void setSystemScope(boolean value) {
		systemScope = value;
	}

	public static String getStartTimeEpoch() {
		return startTimeEpoch;
	}

	public static String getEndTimeEpoch() {
		return endTimeEpoch;
	}

	public static long getEndTimeDefault() {
		return endTimeDefault;
	}

	public static String getStartTimeFriendly() {
		return startTimeFriendly;
	}

	public static String getEndTimeFriendly() {
		return endTimeFriendly;
	}

	public static String getRequest() { 
		return request; 
	}

	public static int getMapiNumber() {
		return mapiNumber;
	}

	// number of records to read at one time
	public static int getNumResults() { 
		return numResults;
	}

	// number of total requested records
	public static int getTotalRecordsMax() {
		return totalRecordsMax; 
	}

	// where to place the report description in CSV file 
	public static int getDescriptionPlace() { 
		return descriptionPlace;
	}
	
	public static String getJfilename() {
		return jfilename;
	}

	public static boolean getUsejsonfile() {
		return usejsonfile;
	}

	public static String getCfilename() {
		return cfilename;
	}

	public static boolean getIsAuthenticated() {
		return isAuthenticated;
	}

	public static void setBearerToken(String inToken) {
		bearerToken = inToken;
	}
	
	public static String getBearerToken() {
		return bearerToken;
	}
	
	public static Boolean getExcludeVersions() {
		return excludeVersions;
	}
	
	//////////////////////////////////////////////////////////////////////////
    //
    //  Process the command line arguments
    // 
    public static int cmdLineParsing( String[] args) throws Exception {


    	// Get all the options:
    	Option helpOption = Option.builder("h")
                .longOpt( CMD_LINE_HELP )
                .required(false)
                .desc("Shows this message")
                .build();
   	
    	Option awOption = Option.builder("a")
    	    .longOpt( CMD_LINE_AWSERVER )
    	    .desc( "Domain name or IP address of the HCP Anywhere server"  )
    	    .hasArg()
    	    .argName( "awserver-name-or-ip" )
    	    .build();

     	Option userOption = Option.builder("u")
                 .longOpt( CMD_LINE_USERNAME )
                 .required(false)
         	     .hasArg()
        	     .argName( "username" )
                 .desc("Admin/auditor username for HCP Anywhere server")
                 .build();

     	Option userKeyStoreOption = Option.builder("k")
                .longOpt( CMD_LINE_USERKEYSTORE )
                .required(false)
        	     .hasArg()
       	     .argName( "user-keystore" )
                .desc("Admin/auditor user certificate key store file for HCP Anywhere server")
                .build();

     	Option portOption = Option.builder("o")
                .longOpt( CMD_LINE_AWPORT )
                .required(false)
        	     .hasArg()
        	     .argName( "awserver-port-number" )
                .desc("Management port number of the HCP Anywhere server, default " + DEFAULT_AWPORT)
                .build();

     	Option passwordOption = Option.builder("p")
                .longOpt( CMD_LINE_PASSWORD )
                .required(false)
        	     .hasArg()
        	     .argName( "password" )
                .desc("Password for admin account on HCP Anywhere server")
                .build();

     	Option bearerTokenOption = Option.builder("bt")
     			.longOpt( CMD_LINE_BEARER_TOKEN )
     			.required( false )
     			.hasArg()
     			.argName( "bearer-token")
     			.desc("OAuth 2.0 Bearer Token obtained via other mechanism")
     			.build();
     	
     	// Build the list of all reports for Help (usage)
     	String mapi_usage = "";
     	boolean auditTitle = false;
     	boolean adminTitle = false;
     	for (int jj=1; jj < MapiHandler.getNumberOfEndpoints(); jj++) {
     		String mapiName = MapiHandler.getMapiName(jj);
		 	if (!auditTitle && mapiName.contains("/audit/")) { 
		 		mapi_usage += "Audit Reports: " + newline;
		 		auditTitle = true;	 		
		 	}
		 	if (!adminTitle && mapiName.contains("/admin/")) { 
		 		mapi_usage += "Admin Reports: " + newline;
		 		adminTitle = true;	 		
		 	}		 	
     		mapi_usage += jj + " : " + mapiName + newline; 
     	}
    	Option mapiOption = Option.builder("r")
                 .longOpt( CMD_LINE_MAPI )
                 .required(false)
        	     .hasArg()
        	     .argName( "report-name-or-number" )
                 .desc("Name or number of the report. Reporting APIs: " + newline + mapi_usage)
                 .build();   	

    	Option auditeduserOption = Option.builder("v")
				.longOpt( CMD_LINE_AUDITED_USER )
				.required(false)
       	     	.hasArg()
       	     	.argName( "audited-user-name" )
				.desc("Username of audited user")
				.build();   	
    	
    	Option auditedPathOption = Option.builder("t")
				.longOpt( CMD_LINE_AUDITED_PATH )
				.required(false)
       	     	.hasArg()
       	     	.argName( "audited-path" )
				.desc("Audited path, for example \"/MyFolder/myfile2\"")
				.build();   	

    	Option profileOption = Option.builder("x")
				.longOpt( CMD_LINE_AUDITED_PROFILE )
				.required(false)
       	     	.hasArg()
       	     	.argName( "profile-name" )
				.desc("Name of audited profile")
				.build();
    	
     	Option jsonfileOption = Option.builder("j")
                .longOpt( CMD_LINE_JSONFILE )
                .required(false)
        	     .hasArg()
        	     .argName( "json-filename" )
                .desc("Input JSON file name")
                .build();

     	Option csvfileOption = Option.builder("c")
                .longOpt( CMD_LINE_CSVFILE )
                .required(false)
        	     .hasArg()
        	     .argName( "csv-filename" )
                .desc("Output CSV file name")
                .build();

     	Option logLevelOption = Option.builder("l")
                .longOpt( CMD_LINE_LOGLEVEL )
                .required(false)
        	     .hasArg()
       	     .argName( "number" )
                .desc("Log level (0-" + LOG_NONDEBUG_MAX + "): default " + DEFAULT_LOGLEVEL)
                .build();
     	
     	Option numRecordsOption = Option.builder("n")
                .longOpt( CMD_LINE_RECORDSINREPLY )
                .required(false)
        	     .hasArg()
       	     .argName( "number" )
                .desc("Number of records in a single API reply, default " + DEFAULT_RECORDS_IN_REPLY)
                .build();
	
     	Option numTotalRecordsOption = Option.builder("b")
                .longOpt( CMD_LINE_TOTAL_RECORD_NUMBERS )
                .required(false)
        	     .hasArg()
       	     .argName( "number" )
                .desc("Total number of records to be collected/saved, default " + DEFAULT_TOTAL_RECORDS + " (all records)")
                .build();
     	     	
     	Option startTimeOption = Option.builder("s")
                .longOpt( CMD_LINE_STARTTIME )
                .required(false)
        	     .hasArg()
       	     .argName( "time" )
                .desc("Auditing start time, e.g. \"2016-12-28 21:00:00\", default is 30 days prior to end-time")
                .build();
     	
     	Option endTimeOption = Option.builder("e")
                .longOpt( CMD_LINE_ENDTIME )
                .required(false)
        	     .hasArg()
       	     .argName( "time" )
                .desc("Auditing end time, e.g. \"2017-01-27 21:00:00\", default is current time")
                .build();

     	Option timeZoneOption = Option.builder("z")
                .longOpt( CMD_LINE_TIMEZONE )
                .required(false)
        	     .hasArg()
       	     .argName( "timezone-abbreviation" ) 
                .desc("Timezone used in the report, default \"" + DEFAULT_TIMEZONE + "\"")
                .build();
     	
     	Option systemScopeOption = Option.builder("y")
                .longOpt( CMD_LINE_SYSTEMSCOPE )
                .required(false)
                .desc("Set SYSTEM scope" )
                .build();

     	Option allReportsOption = Option.builder("g")
                .longOpt( CMD_LINE_MULTIPLE_REPORTS )
                .required(false)
                .desc("Get all reports available for a specified scope" )
                .build();

     	Option guiOption = Option.builder("w")
                .longOpt( CMD_LINE_GUI )
                .required(false)
                .desc("Start GUI" )
                .build();

     	Option csvTimeSuffixOption = Option.builder("i")
                .longOpt( CMD_LINE_CSVSUFFIX )
                .required(false)
                .desc("Add timestamp to csv filename, e.g. myfile_20170104_174608.csv" )
                .build();
     	
     	Option descriptionPlacementOption = Option.builder("q")
                .longOpt( CMD_LINE_DESCRIPTION_PLACE )
                .required(false)
       	     	.hasArg()
       	     	.argName( "number" )               
                .desc("Enable/disable a short description of the Report in CSV file: 0 - disable; 1 [default] - enable, place it on the first line of CSV (above the header); 2 - enable, place it in the header of CSV, on the last column")
                .build();

     	Option excludeVersionOption = Option.builder("xv")
                .longOpt( CMD_LINE_EXCLUDE_VERSIONS )
                .required(false)
                .desc("Exclude older versions of files")
                .build();

     	Option debugOption = Option.builder("d")
                .longOpt( CMD_LINE_DEBUG )
                .required(false)
                .desc("Debug logging")
                .build();
     	
     	// List of visible options:
     	Option visibleOptions[] = { helpOption, awOption, userOption, passwordOption, userKeyStoreOption,
     			            bearerTokenOption, mapiOption,
     	    				profileOption, auditeduserOption, auditedPathOption, systemScopeOption,    	    	
     	    				csvfileOption, jsonfileOption, startTimeOption, endTimeOption, timeZoneOption,
     	    				csvTimeSuffixOption, allReportsOption, guiOption, portOption, numRecordsOption,
     	    				numTotalRecordsOption, descriptionPlacementOption, logLevelOption, excludeVersionOption
     						};
     	// Hidden options:
     	Option hiddenOptions[] = { debugOption	
							};

     	// Keep visible and debug (hidden) options separate   
    	Options cmdOptions = new Options(); // handle all options
    	Options helpOptions = new Options(); // show only visible options    	

    	for(int ii=0; ii < visibleOptions.length; ii++) {
    		cmdOptions.addOption(visibleOptions[ii]); // add all visible options
    	}
    	for(int ii=0; ii < hiddenOptions.length; ii++) {
    		cmdOptions.addOption(hiddenOptions[ii]); // add hidden options
    	}
    	for(int ii=0; ii < visibleOptions.length; ii++) {
    		helpOptions.addOption(visibleOptions[ii]); // add all visible options
    	}
    	    	
    	CommandLineParser cmdparser = new DefaultParser();
    	CommandLine cmdLine = null;

    	try {
    		cmdLine = cmdparser.parse(cmdOptions, args);
    	} catch (Exception e) {
    		Helper.mylog(LOG_ERROR, e.getMessage() + "\nInvalid command. Use -h for usage.");
    		return myExit(EXIT_USAGE_ERROR);
    	}    		
		
    	// Set some values right away. 
    	// Though, they may be changed later in the processing anyway: 
    	Helper.setLogLevel(logLevel); 	// set the log level
		Helper.setTimezone(timezone);	// set the timezone    		   	
    	
    	
    	//************* Process the command line *****************/
    	if (cmdLine.hasOption(CMD_LINE_HELP)) {  
    		// If help/usage was requested - just show it and exit
    		HelpFormatter formatter = new HelpFormatter();
    		formatter.setWidth(130);
    		String javacmd =  "java -jar " + AppManifest.getInstance().getAppJarName();
    		String header = javacmd + " [options]";
    		String footer = "\nFew examples:  " + javacmd + " ...\n\n" + 
    				"Profile scope:\n" +
    				"    -a awserver.example.com -u auditorname -x fssprofile1 -c csvfile1.csv -i -r 1 \n" +
    			    "    -a awserver.example.com -u auditorname -x fssprofile1 -c csvfile1.csv -i -r /mapi/report/audit/user/activity/account \n\n" +      				
    				"User scope:\n" + 
    			    "    -a awserver.example.com -u auditorname -v user2 -c csvfile2.csv -i -r 3 \n\n" +
    				"System scope:\n" +
    			    "    -a awserver.example.com -u auditorname -y -i -r 19 \n\n" + 
    				"All reports of system scope:\n" + 
    			    "    -a awserver.example.com -u auditorname -y -i -g \n\n" +
    				"All reports scoped to a user:\n" +
    			    "    -a awserver.example.com -u auditorname -v user2 -i -g \n\n" +
    				"All reports scoped to a profile, within time interval:\n" + 
    			    "    -a awserver.example.com -u auditorname -g -x fssprofile -s \"2016-11-01 00:00:00\" -e \"2017-01-01 12:00:00\" -i \n\n" +    				
    				"Convert a json file to csv:\n" +
    			    "    -j yourjsonfile.json\n";    				
    		formatter.printHelp( header, "", helpOptions, footer);
    	    return myExit(EXIT_USAGE_OK);    		
    		
    	} 
    	if (cmdLine.getOptions().length == 0) {
    		// Start GUI mode - if no input arguments were given 
    		Gui.processGUI();
    		
    	} else {

    		// non-GUI processing
    		
    		debug = cmdLine.hasOption(CMD_LINE_DEBUG);
    		Helper.setDebug(debug); // set the log level
    		
    		logLevel = cmdLine.hasOption(CMD_LINE_LOGLEVEL) ? 
    					Integer.parseInt((String) (cmdLine.getParsedOptionValue(CMD_LINE_LOGLEVEL))) : DEFAULT_LOGLEVEL;
    		Helper.setLogLevel(logLevel); // set the log level
    		if (debug) {
    			Helper.mylog(LOG_BASE,"Debug mode is ON, log level: " + logLevel);
    		}

    		timezone = cmdLine.hasOption(CMD_LINE_TIMEZONE) ? (String) (cmdLine.getParsedOptionValue(CMD_LINE_TIMEZONE)) : DEFAULT_TIMEZONE;
    		Helper.setTimezone(timezone);	// set the timezone
    		
    	    isSingleRequest = !cmdLine.hasOption( CMD_LINE_MULTIPLE_REPORTS );
    		
    		numResults = cmdLine.hasOption(CMD_LINE_RECORDSINREPLY) ? 
    					Integer.parseInt((String) (cmdLine.getParsedOptionValue(CMD_LINE_RECORDSINREPLY))) : DEFAULT_RECORDS_IN_REPLY;
    		if (numResults > MAX_RECORDS_IN_REPLY) {
    			numResults = MAX_RECORDS_IN_REPLY;
    			Helper.mylog(LOG_WARNING,"WARNING: Specified number of records is greater then the maximum allowed. Using " + MAX_RECORDS_IN_REPLY);
    		}
    		
			totalRecordsMax = cmdLine.hasOption(CMD_LINE_TOTAL_RECORD_NUMBERS) ? 
					Integer.parseInt((String) (cmdLine.getParsedOptionValue(CMD_LINE_TOTAL_RECORD_NUMBERS))) : DEFAULT_TOTAL_RECORDS;

    		awName = cmdLine.hasOption(CMD_LINE_AWSERVER) ?
    						(String) (cmdLine.getParsedOptionValue(CMD_LINE_AWSERVER)) : null; 
    						
    		port = cmdLine.hasOption(CMD_LINE_AWPORT) ?
    					Integer.parseInt((String) (cmdLine.getParsedOptionValue(CMD_LINE_AWPORT))) : DEFAULT_AWPORT; 
    						
    		username = cmdLine.hasOption(CMD_LINE_USERNAME) ?
    						(String) (cmdLine.getParsedOptionValue( CMD_LINE_USERNAME )) : null; 

    		userKeyStore = cmdLine.hasOption(CMD_LINE_USERKEYSTORE) ?
    				(String) (cmdLine.getParsedOptionValue( CMD_LINE_USERKEYSTORE )) : null; 
    		
    		password = cmdLine.hasOption(CMD_LINE_PASSWORD) ?
    				(String) (cmdLine.getParsedOptionValue(CMD_LINE_PASSWORD)) : null; 
					
    		bearerToken = cmdLine.hasOption(CMD_LINE_BEARER_TOKEN) ?
    				(String) (cmdLine.getParsedOptionValue( CMD_LINE_BEARER_TOKEN )) : null; 
    	    
    		if (cmdLine.hasOption(CMD_LINE_EXCLUDE_VERSIONS)) {
    			excludeVersions = Boolean.TRUE;
    		}

            // Issue warning if both username and user key store was specified.
    		if (null != username && null != userKeyStore) {
	    		Helper.mylog(LOG_WARNING,"WARNING: Ignoring " + CMD_LINE_USERNAME + " input paramater since " + CMD_LINE_USERKEYSTORE + " was also specified.");
	    		
    			username = null;
    		}

    		// Validate any command line supplied user key store.  If not a valid file, will be ignoring and
    		//   falling back to any JVM configured info.
    		if ( null != userKeyStore && ! userKeyStore.equals("Windows-MY") && ! userKeyStore.contentEquals("CAC")) {
    			File f = new File(userKeyStore);
    			
    			if ( ! f.exists() ) {
    				Helper.mylog(LOG_ERROR, "ERROR: User key store file does not exist: " + userKeyStore);
    				
    				userKeyStore = null; // Fall back to use the JVM configured one.
    			}
    		}
    		
    		// If neither username or valid userCertFile was specified, going to default to using JVM information
    		//   for keystore file.
    		if (null == username && null == userKeyStore && null == bearerToken ) {
	    		Helper.mylog(LOG_BASE, "Using JVM configured key store for user authentication.");
	    		
	        	String keystoreFilename = System.getProperty("javax.net.ssl.keyStore");

	        	if (! Helper.isEmpty(keystoreFilename)) {
	        		
	        		userKeyStore = keystoreFilename;

	        		// Get key store password from JVM environment.  If none, just leave password
	        		//   to whatever might have been provided in command parameters.
	        		String keystorePassword = System.getProperty("javax.net.ssl.keyStorePassword");
	        		if (null != keystorePassword) {
	        			password = keystorePassword;
	        		}
	        	}
    		}
    		
    		auditedProfile = cmdLine.hasOption(CMD_LINE_AUDITED_PROFILE) ?
    	    				(String) (cmdLine.getParsedOptionValue(CMD_LINE_AUDITED_PROFILE)) : null;
    	    			
    	    auditedUser = cmdLine.hasOption(CMD_LINE_AUDITED_USER) ?
	    					(String) (cmdLine.getParsedOptionValue(CMD_LINE_AUDITED_USER)) : null;

	    	auditedPath = cmdLine.hasOption(CMD_LINE_AUDITED_PATH) ?
	    	    					(String) (cmdLine.getParsedOptionValue(CMD_LINE_AUDITED_PATH)) : null;
	    					
	    	endTimeEpoch = null;    					
    	    if (cmdLine.hasOption(CMD_LINE_ENDTIME)) {
    	    	endTimeFriendly = (String) (cmdLine.getParsedOptionValue(CMD_LINE_ENDTIME));
    	    	endTimeEpoch = Helper.convertTimeToEpoch( endTimeFriendly );
    	    	if (endTimeEpoch == null) {
    	    		// Show a warning. (TBD: Should it be an error?)
    	    		Helper.mylog(LOG_WARNING,"WARNING: ignoring " + CMD_LINE_ENDTIME + " input paramater: " + endTimeFriendly);
    	    	}
    	    }
    	    
    	    boolean endTimeShown = false;
	    	if (endTimeEpoch == null) {
	    		endTimeEpoch = "" + endTimeDefault;	
    	    	endTimeFriendly = Helper.convertTimeFromEpoch(endTimeEpoch);
	    		Helper.mylog(LOG_BASE,"Auditing end time (default, current time): " + endTimeFriendly);
	    		endTimeShown = true;
	    	}    	    
    	       	    
	    	startTimeEpoch = null;
    	    if (cmdLine.hasOption(CMD_LINE_STARTTIME)) {
    	    	startTimeFriendly = (String) (cmdLine.getParsedOptionValue(CMD_LINE_STARTTIME));   	    	    			
    	    	startTimeEpoch = Helper.convertTimeToEpoch( startTimeFriendly );
    	    	if (startTimeEpoch == null) {
    	    		// Show a warning. (TBD: Should it be an error?)   	    		
    	    		Helper.mylog(LOG_WARNING,"WARNING: ignoring " + CMD_LINE_STARTTIME + " input paramater: " + startTimeFriendly);
    	    	}
    	    } 
	    	if (startTimeEpoch == null) {
    	    	long startTimeDefault = Long.parseLong(endTimeEpoch) - THIRTY_DAYS_MICRO; // startTime default is 30 days before endTime  	    	
    	    	startTimeEpoch = "" + startTimeDefault;
    	    	startTimeFriendly = Helper.convertTimeFromEpoch(startTimeEpoch);
    	    	Helper.mylog(LOG_BASE,"Auditing start time (default, 30 days prior to end-time): " + startTimeFriendly);
    	    	if (!endTimeShown) {
        	    	Helper.mylog(LOG_BASE,"Auditing end time: " + endTimeFriendly);    	    		
    	    	}
	    	}
    	    
    	    descriptionPlace = cmdLine.hasOption(CMD_LINE_DESCRIPTION_PLACE) ?
    	    		Integer.parseInt((String) (cmdLine.getParsedOptionValue(CMD_LINE_DESCRIPTION_PLACE))) : DEFAULT_DESCRIPTION_PLACE;
    	    
    	    systemScope = cmdLine.hasOption( CMD_LINE_SYSTEMSCOPE );
    	    		    
    		jsonfile = cmdLine.hasOption(CMD_LINE_JSONFILE) ?
    					(String) (cmdLine.getParsedOptionValue(CMD_LINE_JSONFILE)) : null;
    		
    		jfilename = jsonfile; 
    		if (jsonfile != null) {
    			int jsonInd = jsonfile.indexOf(JSON_EXTENTION);
    			jfilename = (jsonInd < 0) ? jsonfile : jsonfile.substring(0, jsonInd);   
    		}

		    usejsonfile = !Helper.isEmpty(jsonfile);
	
    	    // If an output filename is not specified, then generate it from AW server name and an api name
    	    csvfile = cmdLine.hasOption(CMD_LINE_CSVFILE) ?
    				(String) (cmdLine.getParsedOptionValue(CMD_LINE_CSVFILE)) : null;
    				
    	    if (csvfile != null) {
    	    	int csvInd = csvfile.indexOf(CSV_EXTENTION);
    	    	cfilename = (csvInd < 0) ? csvfile : csvfile.substring(0, csvInd);
    	    }
    		
    	    csvTimeStampSuffix = cmdLine.hasOption( CMD_LINE_CSVSUFFIX);    	    
    	    
    	    request = cmdLine.hasOption(CMD_LINE_MAPI) ? (String) (cmdLine.getParsedOptionValue(CMD_LINE_MAPI)) : null;
		    if (StringUtils.isNumeric(request)) {
		    	mapiNumber = Integer.parseInt(request);  // internal index starts from 0
		    	if (mapiNumber >= 1 && mapiNumber < MapiHandler.getNumberOfEndpoints()) {
		    		request = MapiHandler.getMapiName(mapiNumber);
		    	} else {
		    		Helper.mylog(LOG_ERROR,"ERROR: Report's number is out of range. Select the number between 1 and " + 
							(MapiHandler.getNumberOfEndpoints()-1) + newline + mapi_usage);

		    		return myExit(EXIT_USAGE_ERROR);		    		

		    	}
		    } else {
		    	for (int ii=1; ii < MapiHandler.getNumberOfEndpoints(); ii++ ) {
		    		if (MapiHandler.getMapiName(ii).equals(request)) {
		    			mapiNumber = ii+1;
		    			break;
		    		}
		    	}
			    if (!Helper.isEmpty(request) && (mapiNumber == 0) && isSingleRequest && !usejsonfile) {
					int offset1 = request.indexOf(MAPI_PREFIX);
					if (offset1 < 0) {
			    		Helper.mylog(LOG_ERROR,"ERROR: Invalid MAPI name: '" + request + "'. Exiting...");
			    		return myExit(EXIT_USAGE_ERROR);		    		
					} else {			    		
						Helper.mylog(LOG_WARNING,"WARNING: a specified MAPI name does not match the predefined names. Skipping scope validation.");
					}
			    }		    	
		    }    
    	}

   		// Start GUI mode - if gui option was explicitly used
		if (cmdLine.hasOption( CMD_LINE_GUI)) {
    		Gui.processGUI();
		}

    	//************ Verify the command line (in addition to earlier verification above):    	
    	// set 'useMapi' to true if one of the mapi options is specified 
    	boolean useMapi = !Helper.isEmpty(awName) || !Helper.isEmpty(username) || !Helper.isEmpty(userKeyStore) ||
    			!isSingleRequest || !Helper.isEmpty(request) || !Helper.isEmpty(bearerToken) ||
				!Helper.isEmpty(auditedProfile) || !Helper.isEmpty(auditedUser) || systemScope ;    	    				
    	
    	if (usejsonfile) {
    		
            Helper.mylog(LOG_BASE,"Converting " + jsonfile + " file");
    		
    	} else if (useMapi) {
    		if (Helper.isEmpty(awName) ||
    			(Helper.isEmpty(username) && Helper.isEmpty(userKeyStore) && Helper.isEmpty(bearerToken)) ||
   				(isSingleRequest && Helper.isEmpty(request)) || 
  			    (Helper.isEmpty(auditedProfile) && Helper.isEmpty(auditedUser) && !systemScope) ){
    			
    			String errmsg = "Invalid command line: \n";
    			if (Helper.isEmpty(awName)) {
    				errmsg += "    Missing an HCP Anywhere server name/IP \n";
    			}
    			if (Helper.isEmpty(username) && Helper.isEmpty(userKeyStore) && Helper.isEmpty(bearerToken)) {
    				errmsg += "    Missing an auditor/admin username, key file, or bearer token.\n";
    			}
    			if (isSingleRequest && Helper.isEmpty(request)) {
    				errmsg += "    Missing a report number or a name \n";
    			}
    			if (Helper.isEmpty(auditedProfile) && Helper.isEmpty(auditedUser) && !systemScope) {
    				errmsg += "    Missing a scope option. Please set either user or profile or system scope. \n";    				
    			}
    			Helper.mylog(LOG_ERROR, errmsg);
    			return myExit(EXIT_USAGE_ERROR);	    		
    		}
    	} else {
    		// user specified a json option and one of the mapi options. Choose either one.  
    		Helper.mylog(LOG_ERROR,"Invalid command line: specify either options to connect to HCP Anywhere system or options for a json file conversion");

			return myExit(EXIT_USAGE_ERROR);	    		    		
    	}

    	// If we got here - everything is fine - don't exit, continue 
		return RETURN_OK;    	
    }
    	  
    //
    // Process an exit code. It works differently for GUI
    //
	private static int myExit (int exitCode) {
		
		if (!gui) {
			System.exit(exitCode);
		}
		return exitCode;
	}

}