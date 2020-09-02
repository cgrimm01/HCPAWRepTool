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


import org.apache.commons.lang.StringUtils;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.TreeSet;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


public class CsvWriter {
	
	public static final int DESCRIPTION_DISABLED = 0;	
	public static final int DESCRIPTION_FIRST_LINE = 1;
	public static final int DESCRIPTION_LAST_COLUMN = 2;

	final static public String NEWLINE = Helper.NEWLINE;	

    final static int LOG_ERROR = Helper.LOG_ERROR; // error log level

	private Map<String, String> prevMap = null;
	private Set<String> headers = null; 	
	private boolean firstWrite = true;
	private String csvFileName = null;
	private String descriptionLine = "";
	private int descriptionPlace = DESCRIPTION_FIRST_LINE; 
	
	public CsvWriter(String fileName) {
		csvFileName = fileName;
	}
	
    public void SaveCSV(List<Map<String, String>> flatJson) throws FileNotFoundException {
        Set<String> headers = getHeaderKeys(flatJson);
        String output = "";
        
        if (firstWrite) {
        	if (descriptionPlace == DESCRIPTION_FIRST_LINE) {
				output += descriptionLine + NEWLINE;
			}
        	output += getCsvHeader(headers);
            if (descriptionPlace == DESCRIPTION_LAST_COLUMN) {
				output += "," + descriptionLine;
			}
            output += NEWLINE;        	
        }
        
        if (isEmpty()) { // is file empty (a single field, "ID", counts as empty)
        	// Do NOT create an empty file
            // writeToFile("", csvFileName, !firstWrite); // append = !firstFlag
            return;
        }
        
        for (Map<String, String> map : flatJson) {
        	inheritParentValues(headers,prevMap, map);       	
            output += getCsvLine(headers, map) + NEWLINE;
            prevMap = map;
        }
        writeToFile(output, csvFileName, !firstWrite); // append = !firstFlag
        firstWrite = false; // the next write won't be the first one            
    }

    public void writeCSV(String output) throws FileNotFoundException {
    	writeToFile(output, csvFileName, !firstWrite);
    }

    public void setDescriptionPlace(int place) {
    	descriptionPlace = place;
    }
    
    public int getDescriptionPlace() {
    	return descriptionPlace;
    }
    
    public void setDescriptionLine(String description) {
    	descriptionLine = description;
    }
    
    public String getDescriptionLine() {
    	return descriptionLine;
    }
    
    public void addToDescriptionLine(String description) {
    	descriptionLine += description + " -- ";
    }
    
    
	public String getCsvHeader() {
		return getCsvHeader(headers);
	}
    
	// Is writer empty (a single field, "ID", counts as empty)
	public boolean isEmpty() {
		return (headers.size() <= 1);
	}
	
    public Set<String> getHeaderKeys(List<Map<String, String>> flatJson) {
    	if (headers == null ) headers = new TreeSet<String>();
        
		
        for (Map<String, String> map : flatJson) {
            headers.addAll(map.keySet());
        }
        return headers;
    }

    
    private boolean inheritParentValues(Set<String> headers, Map<String, String> stringMapPrev, Map<String, String> stringMap) {
    	boolean isChild = false;  // is it a child?  
    	
    	if (stringMapPrev == null) return isChild;

    	int ii = 0; // counter of inherited values
        for (String header : headers) {
			if (header == Helper.indexKey) continue;
		
			String prevValue = stringMapPrev.get(header);
			String value = stringMap.get(header);
			
			if (!Helper.isEmpty(prevValue) && Helper.isEmpty(value)) {
				stringMap.put(header, prevValue); // add a parent value to the child
				isChild = true; // it's a child - inherit some values
	        	ii++;	// increment # of inherited values
				
    			Helper.mylog(8, "insertParentValues: " + header + " : " + prevValue);   				
			} else if (!Helper.isEmpty(value)) {
				break;  // stop inheriting - I have my own values too
			}   							
        }
        
        if (isChild) {
        	Helper.mylog(8, "insertParentValues: id=" + stringMapPrev.get(Helper.indexKey) + " -> " + 
					    stringMap.get(Helper.indexKey) + 
					    ", enherited " + ii + " values, out of " + headers.size());
        } else {
        	Helper.mylog(8, "insertParentValues: id=" + stringMapPrev.get(Helper.indexKey) + " -> " + 
					    stringMap.get(Helper.indexKey) + " not a child");        	
        }
    	
    	return isChild;
    }
    
	private static final String firstJsonTag = "users1";

    private String getCsvHeader(Set<String> headers) { 

        List<String> items = new ArrayList<String>();
        for (String header : headers) {
        	String newHeader = "";
        	if (header != null) {
        		newHeader = header.replace("_", "");
        		newHeader = newHeader.replace("0", "");

        		// remove the first character if it's a number: 
        		if (Character.isDigit(newHeader.charAt(0))) {
        			newHeader = newHeader.substring(1);
        		}
        		
        		// drop the first tag - beautification
        		if (newHeader.startsWith(firstJsonTag)) {
        			newHeader = newHeader.substring(firstJsonTag.length());
        			if (newHeader.startsWith("-")) { 
        				// remove "-" if it's the first character
        				newHeader = newHeader.substring(1);
        			}
        		}
        		
        	}
            items.add(newHeader);
        }   	
    	return StringUtils.join(items.toArray(), ",");
    }
    
    private String getCsvLine(Set<String> headers, Map<String, String> map) {
        List<String> items = new ArrayList<String>();
        for (String header : headers) {
        	String newValue = "";
        	String value = map.get(header);
        	if (value != null) {
                newValue = value.replace(",", "");
        	}
            items.add(newValue);
        }
        return StringUtils.join(items.toArray(), ",");
    }
  
    public static String readFile(String filename) {

        String fileContent = "";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                fileContent += sCurrentLine + NEWLINE;
            }

        } catch (IOException e) {
            Helper.mylog(LOG_ERROR,
                         "readFile: ERROR reading from a file " + "\n[ " + e.getMessage() + " ]");
            // e.printStackTrace();
        }

        return fileContent;
    }

    public static void writeToFile(String output, String fileName, boolean append)
            throws FileNotFoundException {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream(fileName, append), StandardCharsets.UTF_8);
			if (!append) {  // only at the beginning of the file
				// set '\uFEFF' is a UTF Byte Order Mark (BOM) - indicates encoding for Excel:
	            writer.write('\uFEFF');   
			}
            writer.write(output);
        } catch (IOException e) {
            Helper.mylog(LOG_ERROR,
                         "writeToFile: ERROR writing to a file " + "\n[ " + e.getMessage() + " ]");
            // e.printStackTrace();
        } finally {
            close(writer);
        }
    }

    public static void close(OutputStreamWriter writer) {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            Helper.mylog(LOG_ERROR,
                         "writeToFile: ERROR closing a file " + "\n[ " + e.getMessage() + " ]");
            // e.printStackTrace();
        }
    }
	
}
