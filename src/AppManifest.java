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

import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.io.IOException;

public class AppManifest {

	static private String appJarName = "HCPAWRepTool.jar"; 				// overwritten by manifest.mf
	static private String appVendorName = "Hitachi Vantara Corporation";// overwritten by manifest.mf
	static private String appCopyrightYears = "2017-2018";				// overwritten by manifest.mf
	static private String appTitle = "HCP Anywhere Reporting Tool";		// overwritten by manifest.mf
	static private String appVersionNumber = "v0.9";					// overwritten by manifest.mf
	static private String appName = "HCPAWRepTool";


	static private String appInfoS = "";  // short description 
	static private String appInfoM = "";  // medium description 
	static private String appInfoL = "";  // long description 

	// Initialize app properties from Manifest.mf file:
	//	appVendorName, appVersionNumber, appTitle, appCopyrightYears, appJarName
	public static void initAppManifest() {
		try { 

			// Get the name of executable jar file (e.g. hcpawreptool.jar): 
			String jarname = new java.io.File(Main.class.getProtectionDomain()
											.getCodeSource()
											.getLocation()
											.getPath())
											.getName();
			// get Manifest file from a jar file:
			JarFile jar = new JarFile(jarname); 
			Manifest manifest = jar.getManifest(); 
			Attributes attributes = manifest.getMainAttributes();

			// Find our attributes from Manifest.mf: 
			appVendorName = attributes.getValue("Implementation-Vendor");
			appVersionNumber = attributes.getValue("Implementation-Version");
			appTitle  = attributes.getValue("Implementation-Title");
			appCopyrightYears = attributes.getValue("Implementation-Copyright");
			appName = attributes.getValue("Name");

			appJarName = jarname;

			setAppInfoStrings(); // set the app info strings

		}
		catch (IOException ioe) {
			Helper.mylog(Helper.LOG_BASE, "WARNING - Couldn't read tool properties from manifest.mf file");
		}
	}

	static void setAppInfoStrings() {
		// Set the app info strings: jarname, version, copyright, vendor, title
		appInfoS = appName + " v" + appVersionNumber;
		appInfoM = appInfoS + " - " + appTitle;
		appInfoL  = appInfoM + " - (c) " + appCopyrightYears + " " + appVendorName;
	}

	public static String getAppJarName() {
		return appJarName;
	}

	public static String getAppInfoShort() {
		return appInfoS;
	}
	public static String getAppInfoMedium() {
		return appInfoM;
	}
	public static String getAppInfoLong() {
		return appInfoL;
	}

}
