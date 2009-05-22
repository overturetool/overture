package org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.console;

import org.eclipse.dltk.debug.ui.ScriptDebugConsoleTraceTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class OvertureConsoleTracker implements IPatternMatchListenerDelegate {
	private TextConsole console;

	private ScriptDebugConsoleTraceTracker delegate;
	
	public OvertureConsoleTracker() {
		delegate = new ScriptDebugConsoleTraceTracker("*");//\tat (.*):(\\d+)");
	}

	public void connect(TextConsole console) {
		delegate.connect(console);
	}

	public void disconnect() {
		delegate.disconnect();
	}

	protected TextConsole getConsole() {
		return console;
	}

	public void matchFound(PatternMatchEvent event) {
		delegate.matchFound(event);
		
//		try {
//			int offset = event.getOffset();
//			int length = event.getLength();
//			IHyperlink link = new OvertureFileHyperlink(console);
//			console.addHyperlink(link, offset + 1, length - 2);
//		} catch (BadLocationException e) {
		//}
	}
}