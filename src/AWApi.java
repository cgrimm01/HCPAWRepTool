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


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import org.apache.wink.json4j.JSONObject;

/**
 * This class handles interactions with HCP Anywhere server using the HCP Anywhere Reporting API
 *
 * <p>NOTE: It uses a self signed certificate to get an authentication token. 
 *
 */

public class AWApi {
	
    final static int LOG_ERROR = Helper.LOG_ERROR; // error log level
    final static int LOG_WARNING = LOG_ERROR; // warning log level, same as error  
	
	private static String authKey= "Authorization";			
	private static String deviceidKey = "X-HCPAW-DEVICE-ID";
	private static String apiversionKey = "X-HCPAW-MANAGEMENT-API-VERSION";
	private static String apiversionValue = "2.1.0";
	private static String acceptKey = "Accept";
	private static String acceptValue = "application/json";
	
	private static String deviceidValue= "969334AD-44FA-4023-8D49-A1817E12F5001"; // regenerate it! 
	private static String clientNickname  = "HCPAWRepTool"; 
	
	private static String partialTag = ",\"pageToken\":";
	
	private String _awServer;
	private String _username;
	private String _password;
	private String _request;	
	
	private String _auditedProfile = null;
	private String _auditedUser = null;
	private String _auditedPath = null;
	private boolean _systemScope = false;	

	private boolean _includeVersions = false; 
	private int _inactivityTimeSeconds = 0;

	private String _auditedStartTime = "2";
	private String _auditedEndTime = "1990000000000"; // year 2033 
		
	private String _auditedTimezone;
	private String _accessToken = null;
	private String _tokenType = null;	// "Bearer"
	private int _numResults = 100;
	private int _port = 8000;
	private int _maxRetry = 3;	// number of retries for API requests
	
	
	/**
	 * Constructor - interactions with HCP Anywhere server 
	 * 
	 * @param awServer 	- HCP Anywhere server name (e.g. "hcpanywhere.example.com")
	 * @param port		- HCP Anywhere management port (e.g. 8000)
	 * @param username	- auditor/admin user name (cannot use the built-in "admin" account) 
	 * @param password	- auditor's/admin's password
	 * @param request	- MAPI report request (e.g. /mapi/report/audit/user/activity/account)
	 * @param auditedProfile	- audited profile name 
	 * @param auditedUser		- audited user name (end-user's username)
	 */
	public AWApi(String awServer, int port, String username, String password, String request, String auditedProfile, String auditedUser) {
		
		_awServer = awServer;
		_port = port;
		_username = username;
		_password = password;
		_request = request;
		_auditedProfile = auditedProfile;
		_auditedUser = auditedUser;
	}

	/**
	 *  Getters and Setters
	 */
	public void setRequest(String request) {
		_request = request;
	}

	public void setAuditedProfile(String auditedProfile) {
		_auditedProfile = auditedProfile;
	}
	
	public void setAuditedUser(String auditedUser) {
		_auditedUser = auditedUser;
	}

	public void setAuditedPath(String auditedPath) {
		_auditedPath = auditedPath;
	}

	public void setSystemScope(boolean systemScope) {
		_systemScope = systemScope;
	}

	public void setTimezone(String timezone) {
		_auditedTimezone = timezone;
	}
	
	public void setNumResults(int numResults) {
		_numResults = numResults;
	}

	public void setNumNumRetries(int numRetries) {
		_maxRetry = numRetries;
	}
	
	public void setHTTPPort(int port) {
		_port = port;
	}
	
	public void setStartTime(String startTime) {
		if (startTime != null) _auditedStartTime = startTime;
	}

	public void clearEndTime() {
		_auditedEndTime = null;
	}
	public void clearStartTime() {
		_auditedStartTime = null;
	}


	public void setEndTime(String endTime) {
		if (endTime != null) _auditedEndTime = endTime;
	}

	public void setIncludeVersions(boolean includeVersions) {
		_includeVersions = includeVersions;
	}

	public void setInactivityTimeSeconds(int inactivityTimeSeconds) {
		_inactivityTimeSeconds = inactivityTimeSeconds;
	}

	public String getAwServer() {
		return _awServer;
	}
	public String getAwUsername() {
		return _username;
	}
	
	public String getAwPassword() {
		return _password;
	}

	public int getNumResults() {
		return _numResults;
	}

	public String getAuditedProfile() {
		return _auditedProfile;
	}

	public String getAuditedUser() {
		return _auditedUser;
	}

	public String getAuditedPath() {
		return _auditedPath;		
	}
	
	public boolean getSystemScope() {
		return _systemScope ;
	}

	public String getTimezone() {
		return _auditedTimezone;
	}
	
	public String getStartTime() {
		return _auditedStartTime;
	}

	public String getEndTime() {
		return _auditedEndTime;
	}
	
	public boolean getIncludeVersions() {
		return _includeVersions;
	}

	public int getInactivityTimeSeconds() {
		return _inactivityTimeSeconds;
	}
	
	/**
	 * isReplyPartial - detect if the reply is partial or final
	 * 
	 * @throws: IOException
	 */
	public boolean isReplyPartial(String prevReply) throws IOException {
		return prevReply.indexOf(partialTag) > 0 ;
	}

	/**
	 * trimPartialReply - trim the partial reply
	 * 
	 *  Remove the last section of the reply: ",\"pageToken\":" 
	 *  in preparation for the reply processing
	 * 
	 * @throws: IOException
	 */
	public String trimPartialReply(String reply) throws IOException {

		String ret = reply;
	    int offset1 = reply.indexOf(partialTag);
	    if (offset1 > 0) {
	    	ret = reply.substring(0, offset1);
	    	ret += "}";
	    }
		
		return ret;
	}
	
	/**
	 * postRequest - form the body of the request and send it out 
	 * 
	 * @throws IOException
	 */
	public String postRequest(String request) throws IOException {
		
		setRequest(request);
		String body = "{" + "\n" + 
				"\"scope\":" + (_systemScope ? "\"SYSTEM\"" : "\"PROFILE\"") + 
				(_auditedProfile != null ? ",\n\"profile\": \"" + _auditedProfile + "\"" : "") +
				(_auditedUser != null ? ",\n\"user\": \"" + _auditedUser + "\"" : "") +
				(_auditedPath != null ? ",\n\"path\": \"" + _auditedPath + "\"" : "") +
				(_auditedStartTime != null ? ",\n\"startTime\": " + _auditedStartTime : "") +
				(_auditedEndTime != null ? ",\n\"endTime\": " + _auditedEndTime : "") +
			    (_inactivityTimeSeconds > 0 ? ",\n\"inactivityTimeSeconds\": \"" + _inactivityTimeSeconds + "\"" : "") + 
				(_includeVersions ? ",\n\"includeVersions\": true" : "") + 
				(_numResults > 0 ? ",\n\"maxResults\": " + _numResults : "") + 
				"\n" + "}" + "\n";	
		Helper.mylog(4, body);		
		return postRequestBody(body);
	}

	/**
	 * postRequestNext - post the next request, when the previous reply was partial 
	 * 
	 * @throws IOException
	 */
	public String postRequestNext(String prevReply) throws IOException {

		String ret = null;

	    int offset1 = prevReply.indexOf(partialTag);
    
	    if (offset1 > 0) {
	    	String nextBody= "{\"maxResults\": " + _numResults + "\n";	    	
	    	nextBody += prevReply.substring(offset1, prevReply.length());
	    	Helper.mylog(4, nextBody);	    
		    ret = postRequestBody(nextBody);    	
	    } else {
	    	Helper.mylog(4, "postRequestNext: prevReply:" + prevReply);
	    }
	    	
			    
		return ret;
	}
	
	/**
	 * postRequestBody - prepare the request (properties/headers) and send it out 
	 * 
	 * @param mybody - request body
	 * @throws IOException 
	 */
	private String postRequestBody(String mybody) throws IOException {
		
	    String ret = null;
	    
		// curl_init and url
	    URL url = new URL("https://" + _awServer + ":" + _port + _request);
	    HttpURLConnection myCon = (HttpURLConnection) url.openConnection();

	    //  CURLOPT_POST
	    myCon.setRequestMethod("POST");
	    
	    myCon.setRequestProperty (authKey, _tokenType + " " + _accessToken); // "Bearer wivzjQjL/I4FGHG..."	    
	    myCon.setRequestProperty (acceptKey, acceptValue);	    
	    myCon.setRequestProperty (deviceidKey, deviceidValue);	    
	    myCon.setRequestProperty (apiversionKey, apiversionValue);
	    myCon.setRequestProperty ("Content-type", "application/json");	    

	    // CURLOPT_FOLLOWLOCATION
	    // myCon.setInstanceFollowRedirects(true);

	    String postData = mybody;
	    
	    myCon.setRequestProperty("Content-length", String.valueOf(postData.length()));

	    myCon.setDoOutput(true);
	    myCon.setDoInput(true);

	    for (int ii=0; ii < _maxRetry; ii++) {
		    try {
		    	DataOutputStream output = new DataOutputStream(myCon.getOutputStream());
		    	output.writeBytes(postData);
		    	output.close();
		    } catch (Exception je) {
		    	String errmsg = je.getMessage();
		    	if (ii == _maxRetry - 1) {
			    	Helper.mylog(LOG_ERROR, "ERROR: Failed to send a request after " + (ii+1) + " attempts : " + 
			    																		"[ " + errmsg + " ]");
			    	return null;
		    	}
		    	Helper.mylog(3, "WARNING: Failed to send a request, attempt " + (ii+1) + " : [ " + errmsg + " ]");		    			    	
		    	continue; // retry
		    }
	    }
	    
	    // "Post data send ... waiting for reply");
	    int code = myCon.getResponseCode(); // 200 = HTTP_OK
	    Helper.mylog(5, "Response    (Code):" + code);
	    Helper.mylog(5, "Response (Message):" + myCon.getResponseMessage());
	    
	    // read the response
	    try {
	            BufferedReader input = new BufferedReader(new InputStreamReader(myCon.getInputStream(), StandardCharsets.UTF_8));
		    int c;
		    StringBuilder resultBuf = new StringBuilder();
		    while ( (c = input.read()) != -1) {
		        resultBuf.append((char) c);
		    }
		    input.close();
		    ret = resultBuf.toString();
	    } catch (Exception je) {
			Helper.mylog(LOG_ERROR, "ERROR: Failed to process a reply from HCP Anywhere server " + "\n[ " + je.getMessage() + " ]");
			Helper.mylog(LOG_ERROR, "INFO: Request body: " + postData);
		    return null;
	    }
	    
	    return ret;
	}

	/**
	 * getAccessToken() - 	authenticate (user/password) and get access_token and token_type
	 * 						for the Reporting requests
	 * 
	 * throws IOException
	 */
	public boolean getAccessToken() throws IOException {

		try {
			selfCert();	// Using a self signed cert is not the best idea
		} catch (Exception je) {
			Helper.mylog(LOG_ERROR, "ERROR: Failed to self certify " + "\n[ " + je.getMessage() + " ]");
			return false;
		}

		// curl_init and url
	    URL url = new URL("https://" + _awServer + ":" + _port + "/login/oauth/");
	    HttpURLConnection myCon = (HttpURLConnection) url.openConnection();

	    //  CURLOPT_POST
	    myCon.setRequestMethod("POST");
	    
	    myCon.setRequestProperty (acceptKey, "*/*");	    
	    myCon.setRequestProperty (apiversionKey, apiversionValue);
	    myCon.setRequestProperty ("Content-type", "application/x-www-form-urlencoded");	    

	    // CURLOPT_FOLLOWLOCATION
	    // myCon.setInstanceFollowRedirects(true);

	    String postData = "grant_type=urn:hds:oauth:negotiate-client"+
	    					"&version=" + apiversionValue + 
	    					"&uniqueId=" + deviceidValue + 
	    					"&clientNickname=" + clientNickname +
	    					"&username=" + _username + 
	    					"&password=" + _password;   			
	    
	    myCon.setRequestProperty("Content-length", String.valueOf(postData.length()));

	    myCon.setDoOutput(true);
	    myCon.setDoInput(true);

	    DataOutputStream output = new DataOutputStream(myCon.getOutputStream());
	    output.writeBytes(postData);
	    output.close();

	    // "Post data send ... waiting for reply");
	    int code = myCon.getResponseCode(); // 200 = HTTP_OK
	    Helper.mylog(5, "Response    (Code):" + code);
	    Helper.mylog(5, "Response (Message):" + myCon.getResponseMessage());

	    String ret = "";	    
	    // read the response
	    try {
		    DataInputStream input = new DataInputStream(myCon.getInputStream());
		    int c;
		    StringBuilder resultBuf = new StringBuilder();
		    while ( (c = input.read()) != -1) {
		        resultBuf.append((char) c);
		    }
		    input.close();
		    ret = resultBuf.toString();	    
	    } catch (Exception je) {
	    	Helper.mylog(LOG_ERROR, "ERROR: Error connecting to " + _awServer + " [ " + je.getMessage() + " ]");
			return false;
	    }

	    _tokenType = null;
	    _accessToken = null;
	    try {
	    	// Save token_type and access_token from a reply 
	    	JSONObject json = new JSONObject(ret);
	    	_tokenType = json.getString("token_type");
	    	_accessToken = json.getString("access_token");
	    } catch (Exception je) {
	    	Helper.mylog(LOG_ERROR, "ERROR: Error parsing access token!" + "\n[ " + je.getMessage() + " ]" + " \n" + ret);
			return false;
	    }
	    	   
	    return true; 
	}
	
	/**
	 * selfCert() - self signing certificate
	 * 
	 * NOTE: OBTAIN A PUBLIC CERTIFICATE FROM A SERVER INSTEAD
	 * 
	 * @throws Exception
	 */
	public static void selfCert() throws Exception {
	    /*
	     *  fix for
	     *    Exception in thread "main" javax.net.ssl.SSLHandshakeException:
	     *       sun.security.validator.ValidatorException:
	     *           PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException:
	     *               unable to find valid certification path to requested target
	     */
	    TrustManager[] trustAllCerts = new TrustManager[] {
	       new X509TrustManager() {
	          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	            return null;
	          }

	          public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

	          public void checkServerTrusted(X509Certificate[] certs, String authType) {  }

	       }
	    };

	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    // Create all-trusting host name verifier
	    HostnameVerifier allHostsValid = new HostnameVerifier() {
	        public boolean verify(String hostname, SSLSession session) {
	          return true;
	        }
	    };
	    // Install the all-trusting host verifier
	    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    /*
	     * end of the fix
	     */
	}
	
	
	//
	// main() method for testing the interactions with HCP Anywhere server using Reporting APIs
	//
 	public static void main(String[] args) throws Exception {
 		
 			String request = "/mapi/report/admin/user/storage";
 			String username = "auditadminusername";
 			String password = "myP@$$w0rd";
 			String awName = "hcpawserver.youcompany.com";
	 		AWApi awAPI = new AWApi(awName, 8000, username, password, request, "", "");
	 
 			try {
	    		if (!awAPI.getAccessToken()) {
	    			System.out.println("ERROR: Failed to login as " + username);
	    			// PASSWORD: System.out.println("ERROR: Failed to login as " + username + "(password: " + awAPI.getAwPassword() + ")");	    					
	    			return;
	    		}   		
	    	} catch (Exception je) {
	    		System.out.println("ERROR: Failed to connect to " + awName + "\n[ " + je.getMessage() + " ]");
				return;    			
	    	}
 			System.out.println("Authenticated and connected. Sending Reporting API requests...");
    	
    		// Send API requests, process/parse the replies and create the header line
 			
 			awAPI.setSystemScope(true);  // set the "SYSTEM" scope
 			
			String reply = awAPI.postRequest(request);
			if (reply == null) {
				System.out.println("ERROR: post request failed");
				return;		// exit
			}
			System.out.println("Successfully sent Reporting API request " + request);			
			// ...
			// <process reply>
			
			return;
	 }
}
