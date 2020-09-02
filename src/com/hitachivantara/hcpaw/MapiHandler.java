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

public class MapiHandler {

	// Configurable values:
	final static int getNumberOfEndpoints() {
		return mapiDescriptions.length;
	}

	final static String getMapiName(int mapiNumber) {
		return mapiDescriptions[mapiNumber].getMapiName();
	}

	final static public String getShortDescription(int mapiNumber) {
		return mapiDescriptions[mapiNumber].getShortDescription();
	}

	final static public String getMessage() {
		return retMessage;
	}

	// MAPI definitions:	
	final static private MapiDescription mapiDescriptions[] = {

			// the zero's element is reserved - not used
			new MapiDescription("", 
					"Unsupported and not predefined MAPI.",
					true, true, true, true),											// 0: +profile, +user, +system, +path
			
			new MapiDescription("/mapi/report/audit/user/activity/account", 
							"Reports all account activity of the audited user(s). The report includes events such as user authentication.",
							true, true, false, false),									// 1: +profile, +user

			new MapiDescription("/mapi/report/audit/user/activity/file/reads", 
							"Reports all files and folders accessed by a user, including reads made by a user on files owned by other users. " +
							"The report does not include files and folders accessed through a link.",
							true, true, false, false),									// 2: profile, user
			
			new MapiDescription("/mapi/report/audit/user/activity/file/modifications", 
							"Reports file and folder modifications performed by a user, " +
							"including modifications performed by a user on files and folders owned by other users.",
							true, true, false, false),									// 3: profile, user

			new MapiDescription("/mapi/report/audit/user/activity/file/linkReads", 
							"Reports all files and folders on which the user performed an authenticated read through a link.",
							false, true, false, false),									// 4: user-only (bug!) 

			new MapiDescription("/mapi/report/audit/user/activity/file/ownedModifications", 
							"Reports all modifications performed on files and folders owned by a user. " +
							"This includes modifications made by other users to the audited user(s) files and folders.",
							true, true, false, false),  								// 5: profile, user
			
			new MapiDescription("/mapi/report/audit/user/activity/file/ownedReads",	
							"Reports each time a file or folder that the user owned was accessed, " +
							"including reads by other users of shared files and folders that are owned by the audited user(s).",
							true, true, false, false),									// 6: profile, user
			
			new MapiDescription("/mapi/report/audit/user/activity/file/ownedLinkReads", 
							"Reports each time a file or folder that the user(s) owned was accessed through a link.",
							true, true, false, false),									// 7: profile, user

	    	new MapiDescription("/mapi/report/audit/user/activity/path/modifications", 
							"Reports all modifications to the specified path of the user(s), " +
							"including modifications made by other users if the file or folder resides in a shared or Team folder.",
							false, true, false, true),									// user+path
	    	new MapiDescription("/mapi/report/audit/user/activity/path/reads",	
	    					"Reports all reads to the specified path of the audited user(s), " +
	    					"including reads made by other users if the file or folder resides in a shared or Team folder.",
	    					false, true, false, true),									// user+path
	    					
	    	new MapiDescription("/mapi/report/audit/user/activity/path/linkReads", 
							"Reports all link reads to the specified path of the audited user(s), " + 
							"including link reads made by other users if the file or folder resides in a shared or Team Folder.", 
							false, true, false, true),									// user+path

	    	new MapiDescription("/mapi/report/audit/user/activity/collaboration/sharedLink", 
							"Reports all link activity of the audited user(s). " +
							"The report includes events such as link creation, link deletion, and modification of link expiration dates.",
							true, true, false, false),									// 8: profile, user

			new MapiDescription("/mapi/report/audit/user/activity/collaboration/share",	
							"Reports all share activity of a specified user. " + 
							"The report includes events such as a folder being shared or unshared, users accepting shared folder invitations, and Team Folder creation.",
							true, true, false, false),									// 9: profile, user
			
			new MapiDescription("/mapi/report/admin/user/profileOverrides", 
							"Returns all users in the system or specified profile that have settings that have been overridden, and reports the overridden settings",
							true, false, true, false),									// 10: profile, system
			
			new MapiDescription("/mapi/report/admin/user/authProviderAccess", 
							"Reports the external state in the authentication provider of users in the system or the specified profile.",
							// DOES this API support multiple profiles? TBD							
							// The possible values that can be returned for a user's external state are DELETED or DISABLED.
							// Only users with one of these external states are returned. Active users are not included in the report output.
							true, false, true, false),									// 11: profile, system
			
			new MapiDescription("/mapi/report/admin/user/access", 
							"Reports the HCP Anywhere access status of all users in the HCP Anywhere system or the specified profile.",
							// DOES this API support multiple profiles? TBD							
							// A user may not be able to access AW if:
							// *Their account is disabled in AW
							// *They are not a member of a profile that has File Sync and Share access
							true, false, true, false),									// 12: profile, system
			
			new MapiDescription("/mapi/report/admin/user/orphaned", 
							"Reports all users in the system that are not a member of any profile.",
							false, false, true, false),									// 13: system-only
			
			new MapiDescription("/mapi/report/admin/user/lastAccess", 
							"Reports the last access time for a user in the system or the specified profile.",
							// DOES this API support multiple profiles? TBD							
							// This command takes an optional inactivityTimeSeconds parameter. 
							// Only users that have been inactive for longer than the provided time are returned.
							// If the inactivityTimeSeconds parameter is not supplied, then all users in the specified profile(s) are returned in the report output.
							true, false, true, false),									// 14: profile, system
			
			new MapiDescription("/mapi/report/admin/user/storage", 
							"Reports the amount of data stored in the HCP Anywhere system by users in the specified profile.",
							// DOES this API support multiple profiles? TBD							
							true, false, true, false),									// 15: profile, system?
			
			new MapiDescription("/mapi/report/admin/system/storage", 
							"Reports the amount of data stored in the HCP Anywhere system.",	
							false, false, true, false),									// 16: system-only
			
			new MapiDescription("/mapi/report/admin/user/highQuotaUsage", 
							"Reports all users in the system or the specified profile near or above their allocated quota.",
							// DOES this API support multiple profiles? TBD							
							true, false, true, false),									// 17: profile, system
			
			new MapiDescription("/mapi/report/admin/user/devices", 
							"Reports all registered HCP Anywhere devices of users in the system or the specified profile.",
							// DOES this API support multiple profiles? TBD							
							true, false, true, false), 									// 18: profile, system
			
			new MapiDescription("/mapi/report/admin/teamFolders", 
							"Reports all existing Team Folders in the system.",
							true, true, true, false) 									// 19: profile?, user?, system		
	};	

	static private String retMessage = "";

	public enum MapiType {
		MAPI_ADMIN, MAPI_AUDIT;
	}
	
    //
    // Set the scope of the API request, one of the following: system, profile, user
    //
    public static boolean setScope(AWApi awAPI, int mapiNumber, String auditedUser, 
    								String auditedProfile, boolean systemScope, String auditedPath, 
    								boolean warn) {
	
    	MapiDescription mapiDescr = mapiDescriptions[mapiNumber];
		String request = mapiDescr.getMapiName();
		boolean ret = true;

		// if mapiNumber > 0, do the MAPI scope validation; otherwise skip it 
		// if mapiNumber is 0 => we didn't recognize this MAPI name and don't have a MAPI description
		// Thus, we cannot validate teh scope for this mapi  
		if (mapiNumber == 0) {
			retMessage = "Report #0 is not supported";
			return false; // error
		}
		
    	if ((!Helper.isEmpty(auditedUser) && mapiDescr.isUserScopeSupported()) ||
    		(!Helper.isEmpty(auditedProfile) && mapiDescr.isProfileScopeSupported()) || 
    		(systemScope && mapiDescr.isSystemScopeSupported())) {
    		    		
    		String scopeDescription = "";
			if (!Helper.isEmpty(auditedUser) && mapiDescr.isUserScopeSupported()) {
				awAPI.setAuditedUser(auditedUser);   	
				awAPI.setAuditedProfile(null);   	
				awAPI.setSystemScope(false);				
				scopeDescription = "Scope \"user\" : " + "\"" + auditedUser + "\"";	
				
				if (!Helper.isEmpty(auditedPath) && mapiDescr.isPathRequired()) {
					awAPI.setAuditedPath(auditedPath);
				} else if (Helper.isEmpty(auditedPath) && mapiDescr.isPathRequired()){
				
					retMessage = " report (#" + mapiNumber + ") " + request + " - audited path is required.";
					ret = false;  // error case: the scope is not fully specified - missing the 'path' option
				
				} else if (!mapiDescr.isPathRequired()) {
					// path is not required, set it to null
					awAPI.setAuditedPath(null);
				}
				
			} else if (!Helper.isEmpty(auditedProfile) && mapiDescr.isProfileScopeSupported()) {
				awAPI.setAuditedUser(null);   	
				awAPI.setAuditedProfile(auditedProfile); 
				awAPI.setSystemScope(false);
				awAPI.setAuditedPath(null);				
				scopeDescription = "Scope \"profile\" : " + "\"" + auditedProfile + "\"";	    	    			    	    	

			} else if (systemScope && mapiDescr.isSystemScopeSupported()) {
				awAPI.setAuditedUser(null);   	
				awAPI.setAuditedProfile(null); 
				awAPI.setSystemScope(true);
				awAPI.setAuditedPath(null);				
				scopeDescription = "Scope \"SYSTEM\"";
			}

			if (ret) {
				retMessage = "Requesting report (#" + mapiNumber + ") " + request + " , " + scopeDescription;
			} 
			
    	} else {
    		ret = false; // error case
    		String scope = "";
    		if (!Helper.isEmpty(auditedUser)) {
    			scope = "'user'";
    		} else if (!Helper.isEmpty(auditedProfile)) {
    			scope = "'profile'";
    		} else if (systemScope) {
    			scope = "'system'";
    		}
    		if (warn) {
    			retMessage = "Skipping report (#" + mapiNumber + ") " + request + " - scope " + scope + " is not supported for this report";
    		} else {
    			retMessage = "Not supported scope " + scope + " for report (#" + mapiNumber + ")" + request;    			
    		}
    	}

		if (mapiDescr.getMapiType() == MapiType.MAPI_ADMIN) {
			// admin endpoints don't accept start/end times anyway:  
			awAPI.clearStartTime();
			awAPI.clearEndTime();
		}

		if (mapiDescr.isIncludeVersionsSupported()) {
			awAPI.setIncludeVersions(true);
		}

		if (mapiDescr.isInactivityTimeSecondsSupported()) {
			awAPI.setInactivityTimeSeconds(0); // TBD: add an input parameter for "inactivity time in seconds" in the cmd line
		}
		
    	return ret;
    }   
    
    
    //
    // MAPI description class 
    //   
	private static class MapiDescription {
		private String mapiName;
		private String shortDescription;	
		private boolean isProfileScopeSupported;
		private boolean isUserScopeSupported;
		private boolean isSystemScopeSupported;		
		private boolean isPathRequired;
		
		public MapiDescription (String name, String shortDescr, boolean b1, boolean b2, boolean b3, boolean b4 ) {
			mapiName = name;
			shortDescription = shortDescr;
			isProfileScopeSupported = b1;
			isUserScopeSupported = b2;
			isSystemScopeSupported = b3;
			isPathRequired = b4;
		}
		
		String getMapiName() {
			return mapiName;
		}
		String getShortDescription() {
			return shortDescription;
		}		
		boolean isProfileScopeSupported() {
			return isProfileScopeSupported;
		}
		boolean isUserScopeSupported() {
			return isUserScopeSupported;
		}
		boolean isSystemScopeSupported() {
			return isSystemScopeSupported;
		}
		boolean isPathRequired() {
			return isPathRequired;
		}
		MapiType getMapiType() {
			return (mapiName.contains("/mapi/report/audit") ? MapiType.MAPI_AUDIT : MapiType.MAPI_ADMIN);
		}
		boolean isIncludeVersionsSupported() {
			return (mapiName.equals("/mapi/report/admin/user/storage"));
		}
		boolean isInactivityTimeSecondsSupported() {
			return (mapiName.equals("/mapi/report/admin/user/lastAccess"));
		}

	}
	
}
