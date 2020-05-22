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

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.Console;

public class Main {
 
	// Exit codes:
	final static int EXIT_OK = Helper.EXIT_OK;	
	final static int EXIT_USAGE_OK = Helper.EXIT_USAGE_OK;
    final static int EXIT_USAGE_ERROR = Helper.EXIT_USAGE_ERROR;
    final static int EXIT_CONNECT_ERROR = Helper.EXIT_CONNECT_ERROR;    
    final static int EXIT_LOGIN_ERROR = Helper.EXIT_LOGIN_ERROR;
    final static int EXIT_SEND_ERROR = Helper.EXIT_SEND_ERROR;

	final static int RETURN_OK = Helper.RETURN_OK; // don't exit - continue	
	final static int RETURN_WARNING = Helper.RETURN_WARNING; // warning - continue	
	final static int RETURN_ERROR = Helper.RETURN_ERROR; // error - stop	
	    
    final static int LOG_BASE = Helper.LOG_BASE; // base log level
    final static int LOG_PROGRESS = Helper.LOG_PROGRESS; // progress log level  
    final static int LOG_DETAILS = Helper.LOG_DETAILS; //details log level    
    final static int LOG_ERROR = Helper.LOG_ERROR; // error log level
    final static int LOG_WARNING = Helper.LOG_WARNING; // warning log level
    final static int LOG_DEBUG = Helper.LOG_DEBUG; // debug log level
	final static int DEFAULT_LOGLEVEL = Helper.DEFAULT_LOGLEVEL;
	
	// Default values:
	final static private String DEFAULT_TIMEZONE = InputOptions.DEFAULT_TIMEZONE;
	final static private int DEFAULT_AWPORT = InputOptions.DEFAULT_AWPORT;
	final static private int DEFAULT_TOTAL_RECORDS = InputOptions.DEFAULT_TOTAL_RECORDS;

	// Configurable values - set by InputOptions
	private static boolean debug;  // set for debugging
 	private static boolean gui;  // GUI or Command Line?	
	private static boolean isAuthenticated;
	private static String startTimeEpoch;
	private static int port;
	private static String endTimeEpoch;
	private static long endTimeDefault;
	private static String startTimeFriendly;
	private static String endTimeFriendly;
	private static int numResults;
	private static int totalRecordsMax;
	private static int mapiNumber;
	private static int descriptionPlace;


   public static void main(String[] args) throws Exception {   	

		// Initialize app info (name, version, copyright, etc) - from Manifest.mf file
		AppManifest.initAppManifest();

		// Display the program-string
    	Helper.mylog(LOG_BASE, AppManifest.getAppInfoLong());
    	
    	// process the command line arguments
    	InputOptions.cmdLineParsing(args);

		debug = InputOptions.getDebug();

		gui = InputOptions.getGui();

		boolean isSingleRequest = InputOptions.getIsSingleRequest();
		String csvfile = InputOptions.getCsvfile();
		String jsonfile = InputOptions.getJsonfile();
		int logLevel = InputOptions.getLogLevel();
		String timezone = InputOptions.getTimezone();
		boolean csvTimeStampSuffix = InputOptions.getCsvTimeStampSuffix();
		String request = InputOptions.getRequest();
		String awName = InputOptions.getAwName();
		String username = InputOptions.getUsername();
		String userKeyStore = InputOptions.getUserKeyStore();
		String password = InputOptions.getPassword();
		String auditedProfile = InputOptions.getAuditedProfile();
		String auditedUser = InputOptions.getAuditedUser();
		String auditedPath = InputOptions.getAuditedPath();
		boolean systemScope = InputOptions.getSystemScope();
		String jfilename = InputOptions.getJfilename();
		boolean usejsonfile = InputOptions.getUsejsonfile();
		String cfilename = InputOptions.getCfilename();


		descriptionPlace = InputOptions.getDescriptionPlace();
		isAuthenticated = InputOptions.getIsAuthenticated();
		startTimeEpoch = InputOptions.getStartTimeEpoch();
		port = InputOptions.getPort();
		endTimeEpoch = InputOptions.getEndTimeEpoch();
		endTimeDefault = InputOptions.getEndTimeDefault();
		startTimeFriendly = InputOptions.getStartTimeFriendly();
		endTimeFriendly = InputOptions.getEndTimeFriendly();
		numResults = InputOptions.getNumResults();
		totalRecordsMax = InputOptions.getTotalRecordsMax();
		mapiNumber = InputOptions.getMapiNumber();


    	//*********** Start the handling of a command   
        long lStartTime = System.currentTimeMillis(); // to measure the processing time for this command 
    	
        List<Map<String, String>> flatJson = null;
    	JsonFlattener parser = null;
		String auditlog = null;     
        int totalRecords = 0;        
    	
    	if (usejsonfile) { // if jsonfile is specified
    		
    		// Convert a JSON file into CSV format (mostly for testing)
    		
    		// Form csv filename:
    		csvfile = (csvfile != null) ? csvfile : (jfilename + InputOptions.CSV_EXTENTION);

        	CsvWriter writer = new CsvWriter(csvfile); 			
    		
     		// Read from a json file 
            Helper.mylog(LOG_BASE,"Reading " + jsonfile + " file");
            auditlog = CsvWriter.readFile(jsonfile);
           
            Helper.mylog(LOG_DETAILS,"Parsing json content");  		  		
        	parser = new JsonFlattener();   	
            flatJson = parser.parseJson(auditlog);	        

            writer.SaveCSV(flatJson);
            totalRecords += flatJson.size();
            
            // if writer is empty (with only one mandatory field "ID"), clear the number of records
            if (totalRecords == 1 && writer.isEmpty()) {
            	totalRecords = 0;
            }
            if (totalRecords > 0) {
            	Helper.mylog(LOG_BASE,"Saved " + totalRecords + " records into " + csvfile);                
                Helper.mylog(LOG_DEBUG, "Created CSV file: " + csvfile + ", " + (parser.getUniqueId() - 1) + " records.");               
            } else {
            	Helper.mylog(LOG_BASE,"No records found.");
            }
            
            if (debug && (logLevel >= LOG_DEBUG)) {
            	Helper.mylog(LOG_DEBUG,"Displaying csv file:\n" + CsvWriter.readFile(csvfile));
            	Helper.mylog(LOG_DEBUG,"==========\nDone!");
            }
    	    myExit(EXIT_OK);
    	    return;
    	
    	} else {
    		
			////////////////////////////////////////////////////////////////////////////
    		// Collect data directly from HCP AW Server and convert it into CSV format /
    		////////////////////////////////////////////////////////////////////////////

    		// Display some applicable input parameters: 
    	    Helper.mylog(LOG_DETAILS, ":HCP Anywhere server: " + awName);
    	    if (port != DEFAULT_AWPORT) Helper.mylog(LOG_DETAILS, ":HCP Anywhere server port: " + port);
    	    if (null != username) {
    	    	Helper.mylog(LOG_DETAILS, ":Admin/auditor username: " + username);    	    
        		if (!gui && (password == null)) {
        			password = enterPassword("Please enter password for \"" + username + "\" : "); 
        		}
    	    }
    	    if (null != userKeyStore) {
    	    	Helper.mylog(LOG_DETAILS, ":Admin/auditor user certficate file: " + userKeyStore);    	    
        		if (!gui && ( ! userKeyStore.equals("Windows-MY") && password == null)) {
        			password = enterPassword("Please enter password for user key store file \"" + userKeyStore + "\" : "); 
        		}
    	    }

    		if (systemScope) Helper.mylog(LOG_DETAILS,":Audited scope: system");    		
    		if (auditedProfile != null) Helper.mylog(LOG_DETAILS,":Audited profile: " + auditedProfile);
    		if (auditedUser != null) Helper.mylog(LOG_DETAILS,":Audited username: " + auditedUser);
    		if (auditedPath != null) Helper.mylog(LOG_DETAILS,":Audited path: " + auditedPath);
    		
    		if (startTimeEpoch != null) Helper.mylog(LOG_DETAILS,":Auditing start time: " + startTimeFriendly);
    		if (endTimeEpoch != null) Helper.mylog(LOG_DETAILS,":Auditing end time: " + endTimeFriendly);
    		if (request != null) Helper.mylog(LOG_DETAILS,":Report: (#" + mapiNumber + ") " + request);
    		if (csvfile != null) Helper.mylog(LOG_DETAILS,":CSV filename: " + csvfile);
    		if (!timezone.equals(DEFAULT_TIMEZONE)) Helper.mylog(LOG_DETAILS,":Timezone: " + timezone);    		
    		        		

    		//*********** Start API processing **************/
	        AWApi awAPI = new AWApi(awName, port, username, password, request, auditedProfile, auditedUser, userKeyStore);
	        
	    	awAPI.setNumResults(numResults); // set the number of records to read in one API call
	    	awAPI.setStartTime(startTimeEpoch);   	
	    	awAPI.setEndTime(endTimeEpoch);
	    	awAPI.setSystemScope(systemScope);
	    				
			String timeStampSuffix = null;
			if (csvTimeStampSuffix) {
				// Generate time stamp suffix for CSV filename
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
				Date date = new Date();
				timeStampSuffix = dateFormat.format(date); 					 
			}
	    	

			// check if it's a command for a single MAPI request or it's a command for multiple MAPIs requests?
	    	if (isSingleRequest) {   

	    		// Get a single report - make a single MAPI request 
	    		
	    		// Form CSV filename	    		
	    		csvfile = makeCsvFilename(cfilename, awName, request, timeStampSuffix);
				
				if (MapiHandler.setScope(awAPI, mapiNumber, auditedUser, auditedProfile, systemScope, auditedPath, false)) {
					// Display a short description of the MAPI
					Helper.mylog(LOG_BASE,  MapiHandler.getShortDescription(mapiNumber));
    		    	
    		    	// Collect a report and write it to CSV
					getReportAndWriteIt(awAPI, request, csvfile);					
				} else {
					// error msg
					Helper.mylog(LOG_ERROR, "ERROR: " + MapiHandler.getMessage());

					myExit(EXIT_USAGE_ERROR);				
	    			return;					
				}

				
    	        if (debug && (logLevel >= LOG_DEBUG)) {
    	        	// Use it cautiously - csvfile can be large
    	        	Helper.mylog(LOG_DEBUG,"Displaying csv file:\n" + CsvWriter.readFile(csvfile));
    	        	Helper.mylog(LOG_DEBUG,"==========\nDone!");
    	        }    	
	    		
	    	} else {

	    		// Get all reports - make multiple MAPI requests
	    		
	    		for (int ii=1; ii < MapiHandler.getNumberOfEndpoints(); ii++) {
	    	    	request = MapiHandler.getMapiName(ii);
	    			mapiNumber = ii;
	    			
	    			// Form csv filename:
		    		csvfile = makeCsvFilename(cfilename, awName, request, timeStampSuffix, ii);
	    			
	    			if (MapiHandler.setScope(awAPI, ii, auditedUser, auditedProfile, systemScope, auditedPath, true)) {

	    		    	Helper.mylog(LOG_BASE,  MapiHandler.getShortDescription(ii));	    				
		    			getReportAndWriteIt(awAPI, request, csvfile);	    				
	    				Helper.mylog(LOG_BASE, MapiHandler.getMessage());	    			

	    			} else {
    					// error case: report it as a warning, not an error
    					Helper.mylog(LOG_WARNING, "Warning: " + MapiHandler.getMessage());
					}
	    		}
	    	}

	    	// DONE! 
	    	Helper.mylog(LOG_DEBUG, "Completed report collection and conversion to csv.");	    		
	    	
    	}
		// Measuring time it took to process this command 
        long lEndTime = System.currentTimeMillis();
        long output = lEndTime - lStartTime;
        Helper.mylog(LOG_DETAILS,"Processing time: " + output / 1000 + " seconds");
    	
    	myExit(EXIT_OK);
		            
    }
    
    //
    // Send APIs, process replies, convert to csv format and save it into the report file 
    //
    private static void getReportAndWriteIt(AWApi awAPI, String request, String csvfile) throws Exception {
    	    
    	String awName = awAPI.getAwServer();
    	String username = awAPI.getAwUsername();

    	if (!isAuthenticated) {
    		Helper.mylog(LOG_PROGRESS,"Connecting to HCP Anywhere server: " + awName + ":" + port);
                
    		try {
	    		if (!awAPI.getAccessToken()) {
	    			if (null != username) {
		    			Helper.mylog(LOG_ERROR,"ERROR: Failed to login as " + username);
	    			} else {
		    			Helper.mylog(LOG_ERROR,"ERROR: Failed to login using key file " + awAPI.getAwUserKeyStore());	    				
	    			}
	    			// PASSWORD: Helper.mylog(LOG_ERROR,"ERROR: Failed to login as " + username + "(password: " + awAPI.getAwPassword() + ")");	    			
	    			myExit(EXIT_LOGIN_ERROR);				
	    			return;
	    		}
	    		isAuthenticated = true; // indicate that we already authenticated with AW Server.
	    		
	    	} catch (Exception je) {
				Helper.mylog(LOG_ERROR,"ERROR: Failed to connect to " + awName + "\n[ " + je.getMessage() + " ]");
				myExit(EXIT_CONNECT_ERROR);				
				return;    			
	    	}
	        Helper.mylog(LOG_PROGRESS,"Connected. Sending Reporting API requests...");
    	}
    	
        //******* Send API requests, process/parse the replies and create the header line ****/

		String reply = awAPI.postRequest(request);
		if (reply == null) {
			myExit(EXIT_SEND_ERROR);
			return;		// exit
		}
     	
        List<Map<String, String>> flatJson = null;
    	JsonFlattener parser = null;
    	CsvWriter writer = new CsvWriter(csvfile); 			
		String auditlog = null;     

		// Set the Report description 1st line in the report
		writer.addToDescriptionLine("This report created by " + AppManifest.getAppInfoShort());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date();
		writer.addToDescriptionLine(dateFormat.format(date)); 					 

		writer.addToDescriptionLine(awName); 
		writer.addToDescriptionLine("Admin/Auditor:" + username);		
	    
		if (awAPI.getSystemScope()) writer.addToDescriptionLine("Scope:SYSTEM");    		
		if (awAPI.getAuditedProfile() != null) writer.addToDescriptionLine("Profile:" + awAPI.getAuditedProfile());
		if (awAPI.getAuditedUser() != null) writer.addToDescriptionLine("Audited-user:" + awAPI.getAuditedUser());
		if (awAPI.getAuditedPath() != null) writer.addToDescriptionLine("Audited-path:" + awAPI.getAuditedPath());
	
		if (request != null) writer.addToDescriptionLine("Report: #" + mapiNumber + " (" + request + ")");

		if (startTimeEpoch != null) writer.addToDescriptionLine("Audting start-time: " + startTimeFriendly);
		if (endTimeEpoch != null) writer.addToDescriptionLine("Auditing end-time: " + endTimeFriendly);
		// if (!timezone.equals(DEFAULT_TIMEZONE)) writer.addToDescriptionLine("Timezone : " + timezone);
		 
		writer.setDescriptionPlace(descriptionPlace); // place the report description on the first line
		
        int totalRecords = 0;        
		int numAPIcalls = 1; // number of sent API requests 			
		{						
			while (awAPI.isReplyPartial(reply)) {
				Helper.mylog(LOG_DEBUG, "Received reply (" + numAPIcalls + ")");
				Helper.mylog(LOG_DEBUG, reply);
								
				auditlog = awAPI.trimPartialReply(reply);		
				Helper.mylog(LOG_DEBUG, "Trimmed reply: " + auditlog);

				parser = new JsonFlattener();	        
		        flatJson = parser.parseJson(auditlog);
		        totalRecords += flatJson.size();		        
			       
		        Helper.mylogOnSameLine(LOG_PROGRESS,"Collecting records: " + totalRecords);
		        
		        writer.getHeaderKeys(flatJson);
		        
		        // Check if user requested a certain number of records and whether we got it already
		        if ((totalRecordsMax != DEFAULT_TOTAL_RECORDS) && (totalRecordsMax <= totalRecords)) {
		        	reply = null;
		        	break;
		        }
		        
				// send the next API request
				reply = awAPI.postRequestNext(reply);
				if (reply == null) {
					myExit(EXIT_SEND_ERROR);
					return;		// exit
				}
				
				numAPIcalls++;		// increment the counter of API requests			
			}
			if (reply != null) {
				auditlog = reply;
				Helper.mylog(LOG_DEBUG, "Received " + ((numAPIcalls>1) ? ("the final reply (" + numAPIcalls + ")") : "Reply"));
				Helper.mylog(LOG_DEBUG, reply);
    	
				// Parse json format
				parser = new JsonFlattener();		
				flatJson = parser.parseJson(auditlog);
			
				totalRecords += flatJson.size();
			
				// Only show it if we had multiple replies
				if (numAPIcalls > 1) Helper.mylogOnSameLine(LOG_PROGRESS,"Collecting records: " + totalRecords + ".");
			
				writer.getHeaderKeys(flatJson);
			
			}
			parser.resetUniqueId();
			
			if (totalRecords == 1 && writer.isEmpty()) {
	        	totalRecords = 0;
	        }			
			
	        Helper.mylogOnSameLine(LOG_PROGRESS,"Collected " + totalRecords + " records      ");
        
			Helper.mylog(LOG_DEBUG, "Processed " + totalRecords + " records: created a header line");				

		}   
        
        /******* Send API requests, process/parse the replies and write to CSV ****/
        Helper.mylog(LOG_DEBUG,"Processing " + totalRecords + " records again, writing them to " + csvfile + " file");        
		
        {
			reply = awAPI.postRequest(request);
			if (reply == null) {
				myExit(EXIT_SEND_ERROR);
				return;		// exit
			}
			
			numAPIcalls = 1; // number of sent API requests 
			totalRecords = 0;
					
			while (awAPI.isReplyPartial(reply)) {
				Helper.mylog(LOG_DEBUG, "Received reply (" + numAPIcalls + ")");
				Helper.mylog(LOG_DEBUG, reply);
								
				auditlog = awAPI.trimPartialReply(reply);		
		    	parser = new JsonFlattener();
		    			    	
		        flatJson = parser.parseJson(auditlog);
		        totalRecords += flatJson.size();		        
					        		        
		        writer.SaveCSV(flatJson);
		        Helper.mylogOnSameLine(LOG_PROGRESS,"Writing to CSV: " + totalRecords + " records");

		        // Check if user requested a certain number of records and whether we got it already
		        if ((totalRecordsMax != DEFAULT_TOTAL_RECORDS) && (totalRecordsMax <= totalRecords)) {
		        	reply = null;
		        	break;
		        }
		        		        
				// send the next API request
				reply = awAPI.postRequestNext(reply);
				if (reply == null) {
					myExit(EXIT_SEND_ERROR);
					return;		// exit
				}
				
				numAPIcalls++;		// increment the counter of API requests			
			}
			auditlog = reply;
			Helper.mylog(LOG_DEBUG, "Received " + ((numAPIcalls>1) ? ("the final reply (" + numAPIcalls +")") : "Reply"));
			Helper.mylog(LOG_DEBUG, reply);
        }

        //************** Parse json format: it's either the final reply or a json file 
    	if (auditlog != null) {

    		// Trim "pageToken" if necessary (if it's in a json)
    		if (awAPI.isReplyPartial(auditlog)) {						
				auditlog = awAPI.trimPartialReply(auditlog);		
			}
    		
    		parser = new JsonFlattener();   	
    		flatJson = parser.parseJson(auditlog);	        
    	
			if (debug) {  // only for debug
    			//Display a parsed json object  
    			Helper.mylog(LOG_DETAILS, "-------");        
    			Helper.mylog(LOG_DETAILS, flatJson);
    			Helper.mylog(LOG_DETAILS, "-------");        
			}
    		
    		writer.SaveCSV(flatJson);
    		totalRecords += flatJson.size();
    	}        
    		
        if (numResults < totalRecords) {
            // Don't display this log message, unless we received multiple replies
        	Helper.mylogOnSameLine(LOG_PROGRESS,"Writing to CSV: " + totalRecords + " records");
        }
        
        // if writer is empty (with only one mandatory field "ID"), clear the number of records
        if (totalRecords == 1 && writer.isEmpty()) {
        	totalRecords = 0;
        }
        if (totalRecords > 0) {
        	Helper.mylog(LOG_BASE,"Saved " + totalRecords + " record" + (totalRecords>1 ? "s":"") + " into " + csvfile);
            Helper.mylog(LOG_DEBUG, "Created CSV file: " + csvfile + ", " + (parser.getUniqueId() - 1) + " records.");                       
        } else {
        	Helper.mylog(LOG_BASE,"No records found.");
        }
        
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
  
    ////////////////////////////////////////////////////////////////////////////
    //
    // Build a CSV filename based on various parameters:
    //    specified csvfile name, Timestamp, AW server name and MAPI name, a report number
    //
    private static String makeCsvFilename(String cfilename, String awName, String request, String timeStampSuffix, int num) {
	
	    // Form csv filename:
    	String numStr = (num > 0) ? ("" + num) : "";
		String csvfile = ((cfilename != null) ? (cfilename + numStr) : (makeFilename(awName, request)));
		csvfile += timeStampSuffix != null ? ("_" + timeStampSuffix) : "";
		csvfile += InputOptions.CSV_EXTENTION;
		
		return csvfile;
    }

    //
    // Build a CSV filename based on various parameters:
    //      specified csvfile name, Timestamp, AW server name and MAPI name (w/o a report number)
    //
    private static String makeCsvFilename(String cfilename, String awName, String request, String timeStampSuffix) {
    	return makeCsvFilename(cfilename, awName, request, timeStampSuffix, 0); 
    }
    
    //
    // Build a filename based on AW server name and MAPI name
    //    
    private static String makeFilename( String awName, String request) {
		String filename = "";
		
		int offset1 = awName.indexOf(".");

		if (offset1 > 0) {
			filename = awName.substring(0, offset1);
			filename += "_";
		}
		String temp = request.substring("/mapi/report/".length(), request.length());
		temp = temp.replace("/", "");
		filename += temp;
		return filename;
	}

	
	//
    // Enter password from the console
	// 
	// @param msg - prompt 
	//
	private static String enterPassword(String msg) {
		String password = null;
		try {
			Console console = System.console();
			password = new String(console.readPassword(msg));			
		} catch (Exception e) {
			Helper.mylog(LOG_ERROR,"ERROR: Need a password to continue. Exiting...");
			myExit(EXIT_LOGIN_ERROR);				
		}
        return password;
    }	

}