Last updated on March, 20 2019

CONTENT OF THIS README
----------------------
 SUMMARY
 HOW TO BUILD A TOOL
 INSTALLATION
 USAGE
 ADDITIONAL EXAMPLES
 SHORT DESCRIPTION OF REPORTS
 TROUBLESHOOTING
 OPENSOURCE LICENSES 


SUMMARY
-------

HCPAWRepTool is HCP Anywhere Reporting Tool, an add-on to Hitachi Content Platform Anywhere (HCP Anywhere).

It collects the audit and admin reports from an HCP Anywhere server and converts these reports into CSV format. 

The tool can generate 22 types of reports (see below for the complete list). 

In a GUI mode, the tool collects all applicable reports for a specified scope (user, profile, or system). 
In a command line mode, the tool has additional options, such as time interval, output file name, and many more. 

This is an open source project. 


HOW TO BUILD THIS TOOL
----------------------

The tool's binary is available in /bin/HCPAWRepTool.jar
You can skip to the next section (INSTALLATION) if you don't plan to build the tool.

* Install Java 8 or later.
  Download JDK file (Java Development Kit) from https://java.com and install it.

* Clone or download the HCPAWRepTool package from GitHub. 

* Run a build script:
      - .\build\buildme.bat	on Windows
      - ./build/buildme		on Linux 
  It will produce HCPAWRepTool.jar file in bin directory

HCPAWRepTool.jar file runs on any system with Java 8 or later. See the next section.


INSTALLATION
------------

 * Install Java 8 or later.
   Download JRE file (Java Runtime Environment) from https://java.com and install it.

 * No other installation is required 
   - simply download HCPAWRepTool.jar 
     from https://github.com/Hitachi-Data-Systems/HCPAWRepTool/blob/master/bin/HCPAWRepTool.jar 
   or 
   - build HCPAWRepTool.jar file as described in the previous section.
 
 * Run HCPAWRepTool.jar in either a GUI mode or in a command line mode. 
      -  java -jar HCPAWRepTool.jar
	  or
      -  java -jar HCPAWRepTool.jar --help

      -  Alternatively, if on Windows, click on bin\start_gui.bat or bin\start_cmd.bat
 
 * Check the command line usage below or use --help option for the command line usage.


ADDITIONAL DETAILS
------------------

You can set a time interval for the report. It will instruct the tool to include only the events within a specified 
time interval. The default time interval is the last 30 days. The default end-time boundary is the current time of 
the command execution. The default start-time boundary is 30 days prior to the end-time, whatever the end-time is.

All times are presented in "YYYY-MM-DD HH-MM-SS GMT" format. Optionally, you can set a different timezone.

The tool has an option to gather all available audit and admin reports for a specified scope (either 'user', or 'profile', or 'system').

You can define the output file name, and also add a timestamp to the output file name. 

The tool overrides a file if the file with the same name already exists. Use --csv-time-suffix option which is designed to 
avoid an accidental overriding of an existing file. 


USAGE
-----

 HCPAWRepTool v0.91 - HCP Anywhere Reporting Tool - (c) 2017-2019 Hitachi Vantara Corporation
 usage: java -jar HCPAWRepTool.jar [options]
  -a,--aw-server <awserver-name-or-ip>    Domain name or IP address of the HCP Anywhere server
  -b,--total-records <number>             Total number of records to be collected/saved, default 0 (all records)
  -c,--csvfile <csv-filename>             Output CSV file name
  -e,--end-time <time>                    Auditing end time, e.g. "2017-01-27 21:00:00", default is current time
  -g,--get-all                            Get all reports available for a specified scope
  -h,--help                               Shows this message
  -i,--csv-time-suffix                    Add timestamp to csv filename, e.g. myfile_20170104_174608.csv
  -j,--jsonfile <json-filename>           Input JSON file name
  -l,--loglevel <number>                  Log level (0-3): default 2
  -n,--num-records-reply <number>         Number of records in a single API reply, default 100
  -o,--port <awserver-port-number>        Management port number of the HCP Anywhere server, default 8000
  -p,--password <password>                Password for admin account on HCP Anywhere server
  -q,--description <number>               Enable/disable a short description of the Report in CSV file: 0 - disable; 1 [default] -
                                          enable, place it on the first line of CSV (above the header); 2 - enable, place it in the
                                          header of CSV, on the last column
  -r,--report <report-name-or-number>     Name or number of the report. Reporting APIs:
                                          Audit Reports:
                                          1 : /mapi/report/audit/user/activity/account
                                          2 : /mapi/report/audit/user/activity/file/reads
                                          3 : /mapi/report/audit/user/activity/file/modifications
                                          4 : /mapi/report/audit/user/activity/file/linkReads
                                          5 : /mapi/report/audit/user/activity/file/ownedModifications
                                          6 : /mapi/report/audit/user/activity/file/ownedReads
                                          7 : /mapi/report/audit/user/activity/file/ownedLinkReads
                                          8 : /mapi/report/audit/user/activity/path/modifications
                                          9 : /mapi/report/audit/user/activity/path/reads
                                          10 : /mapi/report/audit/user/activity/path/linkReads
                                          11 : /mapi/report/audit/user/activity/collaboration/sharedLink
                                          12 : /mapi/report/audit/user/activity/collaboration/share
                                          Admin Reports:
                                          13 : /mapi/report/admin/user/profileOverrides
                                          14 : /mapi/report/admin/user/authProviderAccess
                                          15 : /mapi/report/admin/user/access
                                          16 : /mapi/report/admin/user/orphaned
                                          17 : /mapi/report/admin/user/lastAccess
                                          18 : /mapi/report/admin/user/storage
                                          19 : /mapi/report/admin/system/storage
                                          20 : /mapi/report/admin/user/highQuotaUsage
                                          21 : /mapi/report/admin/user/devices
                                          22 : /mapi/report/admin/teamFolders
  -s,--start-time <time>                  Auditing start time, e.g. "2016-12-28 21:00:00", default is 30 days prior to end-time
  -t,--audited-path <audited-path>        Audited path, for example "/MyFolder/myfile2"
  -u,--username <username>                Admin/auditor username for HCP Anywhere server
  -v,--audited-user <audited-user-name>   Username of audited user
  -w,--gui                                Start GUI
  -x,--audited-profile <profile-name>     Name of audited profile
  -y,--system-scope                       Set SYSTEM scope
  -z,--timezone <timezone-abbreviation>   Timezone used in the report, default "GMT"
  
 Few examples:  java -jar HCPAWRepTool.jar ...
 
 Profile scope:
     -a awserver.example.com -u auditorname -x fssprofile1 -c csvfile1.csv -i -r 1
     -a awserver.example.com -u auditorname -x fssprofile1 -c csvfile1.csv -i -r /mapi/report/audit/user/activity/account
 
 User scope:
     -a awserver.example.com -u auditorname -v user2 -c csvfile2.csv -i -r 3
  
 System scope:
     -a awserver.example.com -u auditorname -y -i -r 19
 
 All reports of system scope:
     -a awserver.example.com -u auditorname -y -i -g
 
 All reports scoped to a user:
     -a awserver.example.com -u auditorname -v user2 -i -g
 
 All reports scoped to a profile, within time interval:
     -a awserver.example.com -u auditorname -g -x fssprofile -s "2016-11-01 00:00:00" -e "2017-01-01 12:00:00" -i
 
 Convert a json file to csv:
     -j yourjsonfile.json
 

ADDITIONAL EXAMPLES
-------------------

How to set a timezone:
    -a awserver.example.com -u auditorname -x fssprofile1 -r 2 -z EST

How to set an audited path for /mapi/report/audit/user/activity/path/ reports (#8,9,10):
    -a awserver.example.com -u auditorname -v user2 -t /MyFolder1/myfile1 -r 8 


SHORT DESCRIPTION OF REPORTS
-----------------------------

HCP Anywhere supports Audit and Administrative reporting.

AUDIT reports provide information on a user's file sync and share activity. To generate an audit report you need to have 
the Audit role.

ADMINISTRATIVE reports provide information on user configuration and system resource usage. To generate an administrative 
report you need to have the Administrator role.


AUDIT REPORTS:
#1:	/mapi/report/audit/user/activity/account
	* Reports all account activity of the audited user(s). The report includes events such as user authentication.
	* Supported scope: User, Profile

#2:	/mapi/report/audit/user/activity/file/reads
	* Reports all files and folders accessed by a user, including reads made by a user on files owned by other users. 
	  The report does not include files and folders accessed through a link.
	* Supported scope: User, Profile
		
#3:	/mapi/report/audit/user/activity/file/modifications
	* Reports file and folder modifications performed by a user, including modifications performed by a user on files 
	  and folders owned by other users.
	* Supported scope: User, Profile

#4:	/mapi/report/audit/user/activity/file/linkReads
	* Reports all files and folders on which the user performed an authenticated read through a link.
	* Supported scope: User

#5:	/mapi/report/audit/user/activity/file/ownedModifications
	* Reports all modifications performed on files and folders owned by a user. This includes modifications made by other 
	  users to the audited user(s) files and folders.
	* Supported scope: User, Profile

#6:	/mapi/report/audit/user/activity/file/ownedReads
	* Reports each time a file or folder that the user owned was accessed, including reads by other users of shared files 
	  and folders that are owned by the audited user(s).
	* Supported scope: User, Profile

#7:	/mapi/report/audit/user/activity/file/ownedLinkReads
	* Reports each time a file or folder that the user(s) owned was accessed through a link.
	* Supported scope: User, Profile

#8:	/mapi/report/audit/user/activity/path/modifications
	* Reports all modifications to the specified path of the user(s), including modifications made by other users if the 
	  file or folder resides in a shared or Team folder.
	* Supported scope: User (requires a path option)

#9:	/mapi/report/audit/user/activity/path/reads
	* Reports all reads to the specified path of the audited user(s), including reads made by other users if the file or 
	  folder resides in a shared or Team folder.
	* Supported scope: User (requires a path option)

#10:	/mapi/report/audit/user/activity/path/linkReads
	* Reports all link reads to the specified path of the audited user(s), including link reads made by other users if  
	  the file or folder resides in a shared or Team Folder.
	* Supported scope: User (requires a path option)

#11:	/mapi/report/audit/user/activity/collaboration/sharedLink 
	* Reports all link activity of the audited user(s). The report includes events such as link creation, link deletion, 
	  and modification of link expiration dates.
	* Supported scope: User, Profile

#12:	/mapi/report/audit/user/activity/collaboration/share
	* Reports all share activity of a specified user. The report includes events such as a folder being shared or unshared, 
	  users accepting shared folder invitations, and Team Folder creation.
	* Supported scope: User, Profile

ADMINISTRATIVE REPORTS:
#13:	/mapi/report/admin/user/profileOverrides
	* Returns all users in the system or specified profile that have settings that have been overridden, and reports the 
	  overridden settings
	* Supported scope: Profile, System

#14:	/mapi/report/admin/user/authProviderAccess
	* Reports the external state in the authentication provider of users in the system or specified profile.
	  The possible values that can be returned for a user's external state are DELETED or DISABLED.
	  Only users with one of these external states are returned. Active users are not included in the report output.
	* Supported scope: Profile, System

#15:	/mapi/report/admin/user/access
	* Reports the HCP Anywhere access status of all users in the HCP Anywhere system or specified profile.
	  A user may not be able to access HCP Anywhere if:
	  - Their account is disabled in HCP Anywhere system
	  - They are not a member of a profile that has File Sync and Share access
	* Supported scope: Profile, System

#16:	/mapi/report/admin/user/orphaned
	* Reports all users in the system that are not a member of any profile.
	* Supported scope: System

#17:	/mapi/report/admin/user/lastAccess
	* Reports the last access time for a user in the system or specified profile.
	* Supported scope: Profile, System

#18:	/mapi/report/admin/user/storage
	* Reports the amount of data stored in the HCP Anywhere system by users in the specified profile.
	* Supported scope: Profile, System

#19:	/mapi/report/admin/system/storage
	* Reports the amount of data stored in the HCP Anywhere system.
	* Supported scope: System

#20:	/mapi/report/admin/user/highQuotaUsage
	* Reports all users in the system or specified profile near or above their allocated quota.
	* Supported scope: Profile, System

#21:	/mapi/report/admin/user/devices
	* Reports all registered HCP Anywhere devices of users in the system or specified profile.
	* Supported scope: Profile, System

#22:	/mapi/report/admin/teamFolders
	* Reports all existing Team Folders in the system.
	* Supported scope: User, Profile, System


TROUBLESHOOTING
---------------

* If you get the message "ERROR: Not supported scope ... " :

   - Check whether a specified scope is supported for this request. You may need to change the scope option in the command line.
     A report can be scoped either to a user (-v) or to a profile (-x) or to whole system (-y).

* If you get the message "ERROR: Report's number is out of range. Select the number between 1 and 22":

   - Check -r (--report) option in the command line.

* If you get the message "ERROR:  report (#8) /mapi/report/audit/user/activity/path/modifications - audited path is required."

   - Make sure an audited path option (-t) is specified in the command line. It's required for the reports #8, 9, 10.

* If you get the message "ERROR: Failed to process a reply from HCP Anywhere server 
                          [ Server returned HTTP response code: 403 for URL: ..."

   - Make sure that a specified username (under option -u) has an Audit and/or Administrator role in HCP Anywhere system.

* If you get the message "ERROR: Failed to process a reply from HCP Anywhere Server 
			  [ Server returned HTTP response code: 400 for URL: ... "

   - Check an audited username in the command line, if a report is for a user (option -v).
   
   - Check an audited profile name in the command line, if a report is for a profile (option -x).



OPENSOURCE LICENSE
------------------

 Copyright © 2017-2019 Hitachi Vantara Corporation

 Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 software and associated documentation files (the "Software"), to deal in the Software 
 without restriction, including without limitation the rights to use, copy, modify, merge, 
 publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons 
 to whom the Software is furnished to do so, subject to the following conditions:
 
 The above copyright notice and this permission notice shall be included in all copies or 
 substantial portions of the Software.
 
 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 DEALINGS IN THE SOFTWARE.
