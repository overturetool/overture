package org.overturetool.vdmtools.dbgp;

import org.overturetool.vdmtools.interpreter.Interpreter.DebugState;

public class StackManager {
	private DebugState debugState;
	
	public DebugState getDebugState() {
		return debugState;
	}

	private static volatile StackManager INSTANCE;
	
	public static StackManager getInstance() {
		if (INSTANCE == null) {
			synchronized (StackManager.class) {
				if (INSTANCE == null) {
					try {
						INSTANCE = new StackManager();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return INSTANCE;
	}
	
	private StackManager() {
		
	}
	
	public String getResponse(DebugState debugState, String transActionID){
		this.debugState = debugState;
		
		
		if (debugState.equals(DebugState.BREAKPOINT)){
			 String response = 
				 	"<response command=\"run\"\r\n" + 
			 		 "status=\"" + DBGPStatus.BREAK + "\"" + 
			 		 " reason=\"" + DBGPReason.OK + "\"" + 
			 		 " transaction_id=\"" + transActionID +"\">\r\n" +
			 		"</response>\r\n";			 
			 return response;
			 
		}else if (debugState.equals(DebugState.SUCCES)){
			String response = 
			 	"<response command=\"run\"\r\n" + 
		 		 "status=\"" + DBGPStatus.STOPPED + "\"" + 
		 		 " reason=\"" + DBGPReason.OK + "\"" + 
		 		 " transaction_id=\"" + transActionID +"\">\r\n" +
		 		"</response>\r\n";			 
		 return response;
			
		}else if (debugState.equals(DebugState.INTERRUPT)){
			String response = 
			 	"<response command=\"run\"\r\n" + 
		 		 "status=\"" + DBGPStatus.BREAK + "\"" + 
		 		 " reason=\"" + DBGPReason.OK + "\"" + 
		 		 " transaction_id=\"" + transActionID +"\">\r\n" +
		 		"</response>\r\n";			 
		 return response;
		}
		// state error
		else{
			String response = 
			 	"<response command=\"run\"\r\n" + 
		 		 "status=\"" + DBGPStatus.STOPPED + "\"" + 
		 		 " reason=\"" + DBGPReason.OK + "\"" + 
		 		 " transaction_id=\"" + transActionID +"\">\r\n" +
		 		"</response>\r\n";			 
			return response;
		}
	}
	
}
