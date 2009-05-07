package org.overturetool.vdmtools.dbgp;

import java.io.File;

public class StackGetCommand extends CommandResponse {
	BreakpointManager bpManager = null;
	
	public StackGetCommand() {
		bpManager = BreakpointManager.getInstance();
	}
	@Override
	protected String parseAndExecute(DBGPCommand command) throws DBGPException {
		String depth = null;
		try {
			depth = command.getOption(DBGPOptionType.D).value;
		} catch (Exception e) {}
		
		
		int level = 0;
		if (depth != null) {
			level = Integer.parseInt(depth);
		}
		
		String stack = appendLevel(level);
		DBGPOption option = command.getOption(DBGPOptionType.TRANSACTION_ID);
		System.out.println(option.value);
		return 
			"<response " +
				"command=\"stack_get\"\r\n" + 
				"transaction_id=\"" + command.getOption(DBGPOptionType.TRANSACTION_ID).value + "\"" +
			">\r\n" +
				stack + 
			"</response>\r\n";

	}

	private String appendLevel(int level) {
//		DBGPDebugFrame stackFrame = this.debugger.stackmanager.getStackFrame(level);
		// TODO need to get 
		Breakpoint bp = bpManager.getBreakPoints().get(0);
		String stackFrame = 
				"<stack level=\"" + level + "\"\r\n" + 
				"type=\"file\"\r\n" + 
				"filename=\"" + new File(bp.getFilename()).toURI().toASCIIString() + "\"\r\n" +
				"lineno=\"" + (bp.getLineNumber()) + "\"\r\n" + 
				"where=\"" + "module" + "\"\r\n" + 
				"cmdbegin=\"" + bp.getLineNumber() + ":0\"\r\n" + 
				"cmdend=\"" + bp.getLineNumber() + ":-1\"/>";
		return stackFrame;
	}
}
