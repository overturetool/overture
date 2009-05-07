package org.overturetool.vdmtools.dbgp;

import java.util.ArrayList;


public class OvertureRunner {
	
	
	public void run(String[] args) throws Exception{
		if (args.length > 1) {			
			//Get host ip and port from args
			String host = args[1];
			String port = args[2];
			String sessionID = args[3];
			String dialect = args[4];
			String launchExpression = args[5];
			String tool = args[6];
			//Get files, 
			ArrayList<String> files = new ArrayList<String>();//All files in project
			for (int i = 7; i < args.length; i++) {
				String temp = args[i];
				files.add(temp);
			}

			if (tool.equals("VDMJ")){
			}
			else if (tool.equals("VDMTools")) {
				
			}
			else
			{
				throw new Exception("No support for the tool: " + tool);
			}
			
			
			
		}
		
		
	}

}
