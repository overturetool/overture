package org.overturetool.eclipse.plugins.editor.internal.ui;

import org.eclipse.dltk.debug.ui.ScriptDebugConsoleTraceTracker;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

public class OvertureDebugConsoleTracker implements
		IPatternMatchListenerDelegate {

	private ScriptDebugConsoleTraceTracker delegate;

	public OvertureDebugConsoleTracker() {
		delegate = new ScriptDebugConsoleTraceTracker("\tat (.*):(\\d+)");
	}

	public void connect(TextConsole console) {
		delegate.connect(console);
	}

	public void disconnect() {
		delegate.disconnect();
	}

	public void matchFound(PatternMatchEvent event) {
		delegate.matchFound(event);
	}

}
