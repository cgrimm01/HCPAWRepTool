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
package com.hitachivantara.hcpaw.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.hitachivantara.hcpaw.AppManifest;
import com.hitachivantara.hcpaw.Helper;
import com.hitachivantara.hcpaw.InputOptions;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;


public class Gui {

	// Exit codes:
	final static int EXIT_OK = Helper.EXIT_OK;	
	final static int EXIT_USAGE_OK = Helper.EXIT_USAGE_OK;
    final static int EXIT_USAGE_ERROR = Helper.EXIT_USAGE_ERROR;
    final static int EXIT_CONNECT_ERROR = Helper.EXIT_CONNECT_ERROR;    
    final static int EXIT_LOGIN_ERROR = Helper.EXIT_LOGIN_ERROR;
    final static int EXIT_SEND_ERROR = Helper.EXIT_SEND_ERROR;
	    
    final static int LOG_BASE = Helper.LOG_BASE; // base log level
    final static int LOG_PROGRESS = Helper.LOG_PROGRESS; // progress log level  
    final static int LOG_DETAILS = Helper.LOG_DETAILS; //details log level    
    final static int LOG_ERROR = Helper.LOG_ERROR; // error log level
    final static int LOG_WARNING = Helper.LOG_WARNING; // warning log level

	// Configurable values:
	private static String awName = null; // e.g. "cluster64d-vm4-0.lab.archivas.com"
	private static String username = null; //e.g. "vrevsin"
	private static String password = null; 
	private static String auditedProfile = null; // e.g. "fssusers"
	private static String auditedUser = null; // e.g. "testuser"
	private static String auditedPath = null; // e.g. "/myfolder/myfile
	private static boolean systemScope = false;

	///////////////////////  GUI handling  ////////////////////////////

    //
    // Dialog to get a text
	// 
    public String createDialogGetText(String promptText, String defaultText) {
		    	
		final JFrame parent = new JFrame(AppManifest.getAppInfoLong());		
		String retText = (String)JOptionPane.showInputDialog(
							parent,
		                    promptText, defaultText);

		// check if a text was not provided 
		if ((retText == null) || (retText.length() <= 0)) {
		    // ask again?
		}
		
		return retText;
	}   

    //
    // Dialog to get a password
	// 
    public String createDialogGetPassword(String msg) {
		final JFrame parent = new JFrame(AppManifest.getAppInfoLong());	    	
        JPasswordField jpf = new JPasswordField(24);
        JLabel jl = new JLabel(msg + "  ");
        Box box = Box.createVerticalBox();
        box.add(jl);
        box.add(jpf);
        int x = JOptionPane.showConfirmDialog(parent, box, AppManifest.getAppInfoLong(), JOptionPane.OK_CANCEL_OPTION);

        if (x == JOptionPane.OK_OPTION) {       	
          return new String(jpf.getPassword());
        }
        return null;
    }

    //
    // Dialog with a dropdown menu 
	//    
    public String createDropdownList(String msg, String [] dropdownMsg) {
   	
    	JComboBox<String> dropdownList = new JComboBox<>(dropdownMsg);
		//final JFrame parent = new JFrame(MYPROGSTRING);
        JLabel jl = new JLabel(msg + "  ");
        Box box = Box.createVerticalBox();
        box.add(jl);      
        
    	//	add to the parent container (e.g. a JFrame):
        box.add(dropdownList);
        box.setVisible(true);

        int x = JOptionPane.showConfirmDialog(null, box, AppManifest.getAppInfoLong(), JOptionPane.OK_CANCEL_OPTION);      

        if (x == JOptionPane.OK_OPTION) {
            return (String) dropdownList.getSelectedItem();
        }

    	return null;
    }

    //
    // Three lines dialog: AW Server, username and password  
	//       
    public void createDialogAwServer() {
    
	    JPanel myPanel = new JPanel(new BorderLayout(5,5));
	
	    JPanel labels = new JPanel(new GridLayout(0,1,2,2)); //
	    labels.add(new JLabel("HCP Anywhere Server", SwingConstants.RIGHT));
	    labels.add(new JLabel("Admin/auditor username", SwingConstants.RIGHT));
	    labels.add(new JLabel("Password", SwingConstants.RIGHT));
	    myPanel.add(labels, BorderLayout.WEST);
	
		
	    JPanel controls = new JPanel(new GridLayout(0,1,2,2));
	    JTextField awserverField = new JTextField(InputOptions.getAwName(), 40);
		controls.add(awserverField);
	    
	    JTextField usernameField = new JTextField(InputOptions.getUsername(),15);
		controls.add(usernameField);

	    JPasswordField passwordField = new JPasswordField(15);
	    // passwordField.addAncestorListener(new RequestFocusListener(false));
	    controls.add(passwordField);
	    
	    myPanel.add(controls, BorderLayout.CENTER);
    
		myPanel.setPreferredSize(new Dimension(550, 80));
	    JOptionPane.showMessageDialog(
	        null, myPanel, AppManifest.getAppInfoLong(), JOptionPane.PLAIN_MESSAGE);
	    
  	   	awName = awserverField.getText();
  	    username = usernameField.getText();
  	    password = new String(passwordField.getPassword());
    }

    //
    // TextArea for the "console" output   
    //
	public class JTextAreaOutputStream extends OutputStream
	{
	    private final JTextArea destination;

	    private JTextAreaOutputStream (JTextArea destination)
	    {
	        if (destination == null)
	            throw new IllegalArgumentException ("Destination is null");

	        this.destination = destination;
	    }

	    @Override
	    public void write(byte[] buffer, int offset, int length) throws IOException
	    {
	        final String text = new String (buffer, offset, length);
	        SwingUtilities.invokeLater(new Runnable ()
	            {
	                @Override
	                public void run() 
	                {
	                    destination.append (text);
	                }
	            });
	    }

	    @Override
	    public void write(int b) throws IOException
	    {
	        write (new byte [] {(byte)b}, 0, 1);
	    }
	}
	
	//
	// Start a TextArea for the "console" output
	// 
    public void startGUI () throws Exception
    {
        JTextArea textArea = new JTextArea (30, 100);

        textArea.setEditable (false);

        JFrame frame = new JFrame (AppManifest.getAppInfoLong());
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane ();
        contentPane.setLayout (new BorderLayout ());
        contentPane.add (
            new JScrollPane (
                textArea, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
            BorderLayout.CENTER);
        frame.pack ();
        frame.setVisible (true);

        JTextAreaOutputStream out = new JTextAreaOutputStream (textArea);
        System.setOut (new PrintStream (out));	        
    }


	// Process input parameters
    public static void processGUI() throws Exception	
	{
    		InputOptions.setGui(true);

    		InputOptions.setCsvTimeStampSuffix(true);  // force timestamp suffix for GUI
    		
    		Gui me = new Gui();
    		me.createDialogAwServer();  // ask for AW server, username and password information
    		    		     		
    		if (Helper.isEmpty(awName) || Helper.isEmpty(username) || Helper.isEmpty(password)) {
    			String infoMessage = "ERROR: Missing HCP Anywhere server and/or credential information. Exiting.";
    			JOptionPane.showMessageDialog(null, infoMessage, 
    					AppManifest.getAppInfoLong(), 
    					JOptionPane.INFORMATION_MESSAGE);    			
    			myExit(EXIT_USAGE_ERROR);
    		}    		
    		
    		// Ask user about the scope type: user, profile or system
    		boolean scopeDefined = false;
    		while (!scopeDefined) {
    			// String scope = Gui.createDialogGetText("Enter the scope [user | profile | system]", "system");
    			String[] scopeList = new String[] {"profile", "user", "system"};
    			String scope = me.createDropdownList("Select the scope", scopeList);
    			if (Helper.isEmpty(scope)) {
    				String infoMessage = "ERROR: Cannot proceed without a scope. Exiting.";
        			JOptionPane.showMessageDialog(null, infoMessage, 
        					AppManifest.getAppInfoLong(), 
        					JOptionPane.INFORMATION_MESSAGE);  				
        			myExit(EXIT_USAGE_ERROR);
    			}
    			
    			if (scope.equalsIgnoreCase("system")) {
    				auditedUser = null;   
    				auditedProfile = null;
    				systemScope = true;
    				scopeDefined = true;    			
    			} else if (scope.equalsIgnoreCase("profile")) {
    				auditedProfile = me.createDialogGetText("Enter audited profile name", "");
    				if (auditedProfile == null) {
    					String infoMessage = "ERROR: Cannot proceed without a profile name. Exiting.";
    					JOptionPane.showMessageDialog(null, infoMessage, 
    							AppManifest.getAppInfoLong(), 
    							JOptionPane.INFORMATION_MESSAGE);
            			myExit(EXIT_USAGE_ERROR);
    				} else {
        				auditedUser = null;    			
    					systemScope = false;
    					scopeDefined = true;
    				}
    			} else if (scope.equalsIgnoreCase("user")) {
    				auditedUser = me.createDialogGetText("Enter audited user", "");
    				if (auditedUser == null) {
    					String infoMessage = "ERROR: Cannot proceed without a user name. Exiting.";
    					JOptionPane.showMessageDialog(null, infoMessage, 
    							AppManifest.getAppInfoLong(), 
    							JOptionPane.INFORMATION_MESSAGE);  				    					
            			myExit(EXIT_USAGE_ERROR);
					} else {
    					auditedProfile = null;    			
    					systemScope = false;
    					scopeDefined = true;
    				}
    			} else {
    				// Should NEVER be here 
    				String infoMessage = "ERROR: Cannot proceed with a scope. Exiting...";
        			JOptionPane.showMessageDialog(null, infoMessage, 
        					AppManifest.getAppInfoLong(), 
        					JOptionPane.INFORMATION_MESSAGE);
        			myExit(EXIT_USAGE_ERROR);
    			}   			
    		}    		
    		
    		InputOptions.setIsSingleRequest(false);  // GUI only supports bulk reporting
    		InputOptions.setAwName(awName);
			InputOptions.setUsername(username);
			InputOptions.setPassword(password);
    		InputOptions.setAuditedProfile(auditedProfile);
			InputOptions.setAuditedUser(auditedUser);
			InputOptions.setAuditedPath(auditedPath);
    		InputOptions.setSystemScope(systemScope);

    		me.startGUI(); // show log messages in the GUI
	}


    //
    // Process an exit code.
    //
	private static void myExit(int exitCode) {
		System.exit(exitCode);
	}

}
