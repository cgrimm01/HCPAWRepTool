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

import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

public class AppManifest {

	static private String appJarName = "HCPAWRepTool.jar"; 				// overwritten by manifest.mf
	static private String appVendorName = "Hitachi Vantara Corporation";// overwritten by manifest.mf
	static private String appCopyrightYears = "2017-2020";				// overwritten by manifest.mf
	static private String appTitle = "HCP Anywhere Reporting Tool";		// overwritten by manifest.mf
	static private String appVersionNumber = "0.12";					// overwritten by manifest.mf
	static private String appName = "HCPAWRepTool";


	private String appInfoS = "";  // short description 
	private String appInfoM = "";  // medium description 
	private String appInfoL = "";  // long description 

	static private AppManifest me;
	static public AppManifest getInstance() {
		if (null == me) {
			me = new AppManifest();
			
			me.initAppManifest();
		}
		
		return me;
	}
	
	// Initialize app properties from Manifest.mf file:
	//	appVendorName, appVersionNumber, appTitle, appCopyrightYears, appJarName
	private void initAppManifest() {
		try { 

			URLClassLoader cl = (URLClassLoader) getClass().getClassLoader();
			
		    URL url = cl.findResource("META-APP-DATA/MANIFEST.MF");
			Manifest manifest = new Manifest(url.openStream());
			Attributes attributes = manifest.getMainAttributes();
			
			// Find our attributes from Manifest.mf: 
			String val;
			val = attributes.getValue("Implementation-Vendor");
			if (!Helper.isEmpty(val)) appVendorName = val;
			val = attributes.getValue("Implementation-Version");
			if (!Helper.isEmpty(val)) appVersionNumber = val;
			val = attributes.getValue("Implementation-Title");
			if (!Helper.isEmpty(val)) appTitle = val;
			val = attributes.getValue("Implementation-Copyright");
			if (!Helper.isEmpty(val)) appCopyrightYears = val;
			val = attributes.getValue("Name");
			if (!Helper.isEmpty(val)) appName = val;

			// Only set the jarFile at run-time if it is actually in a jar file.
			File jarFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getFile());
			if (jarFile.isFile()) {
				appJarName = jarFile.getName();
			}
		}
		catch (IOException ioe) {
			Helper.mylog(Helper.LOG_BASE, "WARNING - Couldn't read tool properties from META-APP-DATA/MANIFEST.MF file");
		} finally {
			setAppInfoStrings();
		}
	}

	private void setAppInfoStrings() {
		// Set the app info strings: jarname, version, copyright, vendor, title
		appInfoS = appName + " v" + appVersionNumber;
		appInfoM = appInfoS + " - " + appTitle;
		appInfoL  = appInfoM + " - (c) " + appCopyrightYears + " " + appVendorName;
	}

	public String getAppJarName() {
		return appJarName;
	}

	public String getAppInfoShort() {
		return appInfoS;
	}
	public String getAppInfoMedium() {
		return appInfoM;
	}
	public String getAppInfoLong() {
		return appInfoL;
	}

}
