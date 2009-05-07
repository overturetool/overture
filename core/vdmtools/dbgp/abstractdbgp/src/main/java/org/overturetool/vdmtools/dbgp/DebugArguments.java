package org.overturetool.vdmtools.dbgp;

import java.util.ArrayList;

public class DebugArguments {
	private String launchExpression;
	private String tool;
	private String sessionID;
	private String dialect;
	
	
	public String getLaunchExpression() {
		return launchExpression;
	}

	public String getTool() {
		return tool;
	}

	public String getSessionID() {
		return sessionID;
	}

	public String getDialect() {
		return dialect;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String[] getFiles() {
		return files;
	}

	private String host;
	private String port;
	private String[] files;
	
	public DebugArguments(String[] args) {
		if (args.length > 1) {			
			//Get host ip and port from args
			host = args[1];
			port = args[2];
			sessionID = args[3];
			dialect = args[4];
			launchExpression = args[5];
			
			//Get files, 
			ArrayList<String> files = new ArrayList<String>();//All files in project
			for (int i = 6; i < args.length; i++) {
				String temp = args[i];
				files.add(temp);
			}
		}
	}
	
	
	
}
