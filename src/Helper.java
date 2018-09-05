
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.*;

public class Helper {

    final static public int LOG_ERROR = 0;
    final static public int LOG_BASE = 1;
    final static public int LOG_PROGRESS = 2;
    final static public int LOG_DETAILS = 3;
    final static public int LOG_DEBUG = 4;
	final static public int LOG_WARNING = LOG_ERROR; // warning log level, same as error  
    final static public int LOG_NONDEBUG_MAX = LOG_DETAILS;
	final static public int DEFAULT_LOGLEVEL = LOG_PROGRESS;

	final static public int EXIT_OK = 1;	
	final static public int EXIT_USAGE_OK = 2;
    final static public int EXIT_USAGE_ERROR = 3;
    final static public int EXIT_CONNECT_ERROR = 4;    
    final static public int EXIT_LOGIN_ERROR = 5;
    final static public int EXIT_SEND_ERROR = 6;

	final static public int RETURN_OK = 0; // don't exit - continue	
	final static public int RETURN_WARNING = 1; // warning - continue	
	final static public int RETURN_ERROR = -1; // error - stop	

	final static public String NEWLINE = "\n";	

	private static boolean _debug = false;
    private static boolean _gui = false;
    public static final String indexKey = "00ID";

    private static String _myTimeZone = "GMT";
    private static int _logLevel = LOG_BASE;

    public static void setGui(boolean gui) {
        _gui = gui;
    }

    public static void setLogLevel(int logLevel) {
        _logLevel = logLevel;
    }

    public static void setDebug(boolean debug) {
        _debug = debug;
    }

    public static void setTimezone(String timezone) {
        _myTimeZone = timezone;
    }

    public static boolean isEmpty(String str) {
        boolean ret = false;
        if ((str == null) || (str.length() == 0)) {
            ret = true;
        }
        return ret;
    }

    static private boolean sameLine = false;
    final static private String DEBUG_LOG_PROMPT = ":: ";

    public static void mylog(int level, Object message) {

        if ((level > LOG_NONDEBUG_MAX) && !_debug)
            return; // don't show debug level messages if debug is false

        if (level <= _logLevel) {
            System.out.println((sameLine ? "\n" : "") + (_debug ? level + DEBUG_LOG_PROMPT : "")
                    + message);
            sameLine = false;
        }
    }

    public static void mylogOnSameLine(int level, Object message) {

        if (_logLevel > LOG_NONDEBUG_MAX)
            mylog(level, message);

        else if (level <= _logLevel) {
            sameLine = true;
            if (_gui) {
                System.out.print(".");
            } else {
                System.out.print("\r" + (_debug ? level + DEBUG_LOG_PROMPT : "") + message);
            }
        }
    }

    public static String convertTimeFromEpoch(String value) {
        return convertTimeFromEpoch(Long.parseLong(value));
    }

    public static String convertTimeFromEpoch(long epochtime) {
        String datetimevalue = "" + epochtime;
        if (epochtime > 0) {
            Date date = new Date(epochtime);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
            format.setTimeZone(TimeZone.getTimeZone(_myTimeZone));
            datetimevalue = format.format(date);

            mylog(6, "convertTime: TIME epochtime=" + epochtime + " => " + datetimevalue);
        }
        return datetimevalue;
    }

    public static String convertTimeToEpoch(String value) {
        long epoch = 0;

        // value = "2016-12-04 04:05:10"; // for Test only - remove!

        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone(_myTimeZone)); // assume UTC
            Date datenew = df.parse(value);
            epoch = datenew.getTime();
        } catch (Exception e) {
            Helper.mylog(LOG_BASE,
                         "Warning: Failed to convert time " + "\n[ " + e.getMessage() + " ]" +
                                 " expected format is 2016-12-27 14:05:00");
            return null;
        }

        Helper.mylog(4, "Epoch time: " + epoch);
        return Long.toString(epoch);
    }


}