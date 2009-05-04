package org.overturetool.vdmtools.dbgp;

import java.util.ArrayList;

import org.overturetool.vdmtools.VDMToolsProject;
import org.overturetool.vdmtools.dbgp.DBGPDebugger;


public class VDMToolsRunner {
	
	public static void run(String[] args) throws Exception{
		if (args.length > 1) {			
			//Get host ip and port from args
			String host = args[0];
			int port = Integer.parseInt(args[1]);
			String ideKey = args[2];
			String dialect = args[3];
			String expression = args[4];
			String PathToVDMTools = args[5];
			//Get files, 
			ArrayList<String> files = new ArrayList<String>();//All files in project
			for (int i = 6; i < args.length; i++) {
				files.add(args[i]);
			}
			try {
				System.out.println("Init debug");
				DBGPDebugger debugger = new DBGPDebugger(host,port,ideKey,dialect,expression,files);
				System.out.println("Start");
				
				// Start VDMTools init vdmtools:
				VDMToolsProject.getInstance().init(PathToVDMTools, dialect);
				
				debugger.start();
				synchronized (debugger) {
					try {
						System.out.println("Wait");
						debugger.wait();
						System.out.println("wait done... " + debugger.getId());
					} catch (InterruptedException e) {
						throw new IllegalStateException();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("exception waiting on debugger: " + e.getMessage());
			}
			
		}
	}
}