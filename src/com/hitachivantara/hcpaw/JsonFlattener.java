package com.hitachivantara.hcpaw;
/**
 * HCP Anywhere Reporting Tool 
 * 
 * This file was originally taken from SEL-Columbia's json-to-csv repository 
 * and is licensed under Apache 2.0  to "Dristhi software" 
 * (https://github.com/SEL-Columbia/json-to-csv).
 *
 * See relevant licensing from Dristhi software below for more information.
 * 
 * It has been modified by Hitachi Vantara Corporation
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
 *
 * ----------------------------

    Dristhi software

	Copyright 2012-2014
	
	Foundation for Research in Health Systems; Sustainable Engineering
	Lab, Columbia University; and The Special Programme of Research,
	Development and Research Training in Human Reproduction (HRP), World
	Health Organization.
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	License for the specific language governing permissions and limitations
	under the License.
	
	Notice: The licensors gratefully acknowledge the generous support of the
	Wellcome Trust, the Norwegian Agency for Development Cooperation, and
	the Bill and Melinda Gates Foundation; and the technical contributions
	of ThoughtWorks; which have contributed to making this software possible.

 * ----------------------------
 *
 */ 

import org.apache.wink.json4j.JSONArray; 
import org.apache.wink.json4j.JSONException; 
import org.apache.wink.json4j.JSONObject;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class JsonFlattener {
    List<Map<String, String>> iflatJson = new ArrayList<Map<String, String>>();
    
    public Map<String, String> parse(JSONObject jsonObject) {
        Map<String, String> flatJson = new LinkedHashMap<String, String>();
        Helper.mylog(5,"parse JSONObject start");
        flatten(jsonObject, flatJson, "");
        Helper.mylog(5,"parse JSONObject end");

        return flatJson;
    }
    
    public Map<String, String> parse(JSONObject jsonObject, String prefix) {
        Map<String, String> flatJson = new LinkedHashMap<String, String>();
        Helper.mylog(5, "parse JSONObject start");
        flatten(jsonObject, flatJson, prefix);
        Helper.mylog(5, "parse JSONObject end, prefix::" + prefix);

        return flatJson;
    }
    

    public List<Map<String, String>> parse(JSONArray jsonArray) {
        int length = jsonArray.length();
        Helper.mylog(5, "parse JSONArray start, length=" + length) ;
        try {        
	        for (int i = 0; i < length; i++) {
	        	Helper.mylog(5, "parse JSONArray: " + i);
	            JSONObject jsonObject = jsonArray.getJSONObject(i);
	            int index= iflatJson.size();
	            Helper.mylog(5, "parse JSONArray start: arraylength= " + length + ", iflatJson.size=" + index) ;
	            Helper.mylog(5, "parse JSONArray: add(index=" + index + ")" ) ;
	            
	            Map<String, String> stringMap = parse(jsonObject);
	            // addLine(iflatJson, stringMap, index);
	        } 
        } catch (JSONException je) {
            Helper.mylog(0, "ERROR: parse JSONArray exception!") ;
        }
        Helper.mylog(5, "parse JSONArray end") ;
        return iflatJson;
    }

    public List<Map<String, String>> parseJson(String json) throws Exception {
        // List<Map<String, String>> flatJson = null;
        Helper.mylog(5, "parseJson start") ;

        try {
            JSONObject jsonObject = new JSONObject(json);  /// ENTRY POINT
           // flatJson = new ArrayList<Map<String, String>>();
            
            Map<String, String> stringMap = parse(jsonObject);

            addLine(iflatJson, stringMap);            
            Helper.mylog(5, "parseJson: added (index=" + iflatJson.size() + ", obj: " +  jsonObject);            
        } catch (JSONException je) {
        	Helper.mylog(5, "parseJson:: " + json);            
            iflatJson = handleAsArray(json);
            Helper.mylog(5, "parseJson::: " + iflatJson);            
        }
        Helper.mylog(5, "parseJson end") ;
        return iflatJson;
    }

    private List<Map<String, String>> handleAsArray(String json) throws Exception {
        List<Map<String, String>> flatJson = null;
        Helper.mylog(5, "handleAsArray start") ;
        
        try {
            JSONArray jsonArray = new JSONArray(json);
            flatJson = parse(jsonArray);
        } catch (Exception e) {
        	 Helper.mylog(0, "ERROR: Json might be malformed");
        }
        Helper.mylog(5, "handleAsArray end") ;
        return flatJson;
    }

    private void flatten(JSONArray obj, Map<String, String> flatJson, String prefix) {
    	classLevel++;
    	int length = obj.length();
    	Helper.mylog(6, "flatten JSONArray start: length=" + length + ", classLevel = " + classLevel);       
       
    	for (int i = 0; i < length; i++) {
    		Helper.mylog(7, "flatten: JSONArray " + i);

        	if (obj.get(i).getClass() == JSONArray.class) {
                JSONArray jsonArray = (JSONArray) obj.get(i);
                if (jsonArray.length() < 1) continue;
                Helper.mylog(7, "flatten: JSONArray obj(" + i + ") == JSONArray.class - starting flatten jsonArray");               
                flatten(jsonArray, flatJson, prefix + i);
                Helper.mylog(7, "flatten: JSONArray obj(" + i + ") == JSONArray.class - ended flatten jsonArray");               
                
            } else if (obj.get(i).getClass() == JSONObject.class) {
             
                JSONObject jsonObject = (JSONObject) obj.get(i);
                
                if (i == 0) {
                	Helper.mylog(7, "flatten: JSONArray obj(" + i + ") === starting JSONObject.class");               	

                	flatten(jsonObject, flatJson, prefix + 1); // +1
                	//// putUniqueId(flatJson);
                    addLine(iflatJson, flatJson);      

                    Helper.mylog(7, "flatten: JSONArray obj(" + i + ") === ended JSONObject.class ");               	

                } else {
                	Helper.mylog(7, "flatten: JSONArray obj(" + i + ") calling parse object! prefix=" + prefix); 
                	
                    Map<String, String> stringMap  = parse(jsonObject, prefix + 1); // +1                  
                   	//// putUniqueId(stringMap);
                   	addLine(iflatJson, stringMap);
                    
                    Helper.mylog(7, "flatten: JSONArray obj added (iflatJson.size=" + iflatJson.size() + ")");
                }                              
                	             	
            } else {
            	Helper.mylog(9, "flatten: JSONArray obj(" + i + ") != JSONArray.class/JSONObject.class");
            	try {
	                String value = obj.getString(i);
	                if (value != null) {
	                    Helper.mylog(9, "flatten: JSONArray obj put: prefix=" + (prefix + (i+1)) + "), value=" + value);
	                    flatJson.put(prefix + (i + 1), value);
	                }
            	} catch (JSONException je) {
            		Helper.mylog(0, "ERROR: flatten: JSONArray obj exception!") ;
            	}

            }      	
        }   	
        Helper.mylog(6, "flatten JSONArray end, classLevel=" + classLevel + ", prefix=" + prefix);
        classLevel--;
    }


    private void flatten(JSONObject obj, Map<String, String> flatJson, String prefix) {
        objectLevel++;
    	int ii=1;
        Helper.mylog(8, "flatten JSONObject start, objectLevel = " + objectLevel);

        try {
	    	Iterator iterator = obj.keys();
	        while (iterator.hasNext()) {
	
	        	String key = iterator.next().toString();
	        	Helper.mylog(9, "flatten JSONObject: " + ii + ", key: " + key);
	        	
	            if (obj.get(key).getClass() == JSONObject.class) {
	                JSONObject jsonObject = (JSONObject) obj.get(key);
	                Helper.mylog(9, "flatten: JSONObject obj(" + ii + ") == JSONObject.class");                              
	                flatten(jsonObject, flatJson, prefix + key); // + key *****
	            } else if (obj.get(key).getClass() == JSONArray.class) {
	                JSONArray jsonArray = (JSONArray) obj.get(key);
	                if (jsonArray.length() < 1) continue;
	                Helper.mylog(9, "flatten: JSONObject obj(" + ii + ") == JSONArray.class"); 
	                
	                // if (ii==1 && key.equals("users")) key=""; // don't use the first "users" as a prefix
	                
	                flatten(jsonArray, flatJson, prefix + key); // prefix + ****
	            } else {
	                String value = obj.getString(key);
	                Helper.mylog(9, "flatten JSONObject: else != " + ii + ", value: " + value);
	                if (value != null && !value.equals("null")) {
	                    if (key.contains("aaindex")) {
	                    	Helper.mylog(7, "flatten JSONObject::: put " + ii + ", prefix: " + prefix + ", key:" + key + ", value: " + value + "<");                	                   	
	                    } else {
	                    	Helper.mylog(9, "flatten JSONObject::: put " + ii + ", prefix: " + prefix + ", key:" + key + ", value: " + value + "<");
	                    }
	                    
	                    if (key.contains("time") || key.contains("Time") || key.contains("lastAccess")) {                    	
	                    	value = Helper.convertTimeFromEpoch(value);
	                    	key = "_" + key;
	                    }
	                    int count = StringUtils.countMatches(prefix, "1");
	                	flatJson.put(count + prefix + (Helper.isEmpty(prefix) ? "" : "-") + key, value); //  
	                }
	            }
	            ii++;
	        }
        } catch (JSONException je) {
            Helper.mylog(0, "ERROR: failed to flatten JSONObject !");        	
        }
        Helper.mylog(8, "flatten JSONObject end, objectLevel = " + objectLevel);
        objectLevel--;
    }
    
    ///////////////////////////////////////////
    private int classLevel =0;
    private int objectLevel =0;
    static private int uniqueId = 1;
   
    public void resetUniqueId() {
    	uniqueId = 1;
    }
    
    private int putUniqueId(Map<String, String> stringMap) {
    	int id = -1;
    	if (null == stringMap.get(Helper.indexKey)) {
    		id = uniqueId++;
    		stringMap.put(Helper.indexKey, Integer.toString(id) );
        	Helper.mylog(5, "putUniqueId: id=" + id);
    	} else {
    		Helper.mylog(5, "putUniqueId: skipping (already set)");	
    	}
    	return id;
    }
    
    public int getUniqueId() {
    	return uniqueId;
    }
    /***
    private void addLine(List<Map<String, String>> jsonList, Map<String, String> stringMap, int index) {
    	Helper.mylog(5, "Add line: index=" + index + ", sizebefore=" + jsonList.size());
    	
    	int id = -1;
    	if (null != stringMap.get(indexKey)) {
    		id = Integer.parseInt(stringMap.get(indexKey));
    	}
    	if (!jsonList.contains(stringMap)) {
    		jsonList.add(index, stringMap);
    		Helper.mylog(5, "Add line: index=" + index + " (id=" + id +"), sizeafter=" + jsonList.size());
    	} else {
    		Helper.mylog(5, "Don't Add line: index=" + index + " (id=" + id +"), sizeafter=" + jsonList.size());
    	}
    }
    ***/
    
    private void addLine(List<Map<String, String>> jsonList, Map<String, String> stringMap) {
    	Helper.mylog(5, "Add line: sizebefore=" + jsonList.size());
    	int id = putUniqueId(stringMap);
    	
    	if (id > 0  && !jsonList.contains(stringMap)) {
    		jsonList.add(stringMap);
    		Helper.mylog(5, "Add line: (id=" + id + "), sizeafter=" + jsonList.size());
    	} else {
    		
        	if (null != stringMap.get(Helper.indexKey)) {
        		id = Integer.parseInt(stringMap.get(Helper.indexKey));
        	}
    		
    		Helper.mylog(5, "Don't Add line: (id=" + id + "), sizeafter=" + jsonList.size());   		
    	}
    }    
   
}

