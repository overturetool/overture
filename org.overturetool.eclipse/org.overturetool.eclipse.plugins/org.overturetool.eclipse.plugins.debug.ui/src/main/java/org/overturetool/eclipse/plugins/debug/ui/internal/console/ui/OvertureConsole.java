package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui;

import org.eclipse.dltk.console.ui.ScriptConsole;
import org.overturetool.eclipse.plugins.launching.console.OvertureInterpreter;


public class OvertureConsole extends ScriptConsole {
	public static final String CONSOLE_TYPE = "overture_console";

	public static final String CONSOLE_NAME = "Overture Console";
	
	public OvertureConsole(OvertureInterpreter interpreter, String id) {
		super(CONSOLE_NAME + " [" + id + "]", CONSOLE_TYPE);

		setInterpreter(interpreter);
		setTextHover(new OvertureConsoleTextHover(interpreter));
		setContentAssistProcessor(new OvertureConsoleCompletionProcessor(interpreter));
	}	
}
