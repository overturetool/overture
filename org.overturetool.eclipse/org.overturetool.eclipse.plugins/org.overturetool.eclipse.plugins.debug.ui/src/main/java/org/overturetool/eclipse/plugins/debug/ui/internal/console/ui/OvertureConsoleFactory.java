package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ScriptConsolePrompt;
import org.eclipse.dltk.console.ui.IScriptConsoleFactory;
import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.ScriptConsoleFactoryBase;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overturetool.eclipse.plugins.debug.ui.internal.debug.ui.OvertureDebugUIPlugin;
import org.overturetool.eclipse.plugins.launching.console.OvertureConsoleConstants;
import org.overturetool.eclipse.plugins.launching.console.OvertureInterpreter;


public class OvertureConsoleFactory extends ScriptConsoleFactoryBase implements
		IScriptConsoleFactory {
	protected IPreferenceStore getPreferenceStore() {
		return OvertureDebugUIPlugin.getDefault().getPreferenceStore();
	}

	protected ScriptConsolePrompt makeInvitation() {
		IPreferenceStore store = getPreferenceStore();
		return new ScriptConsolePrompt(store
				.getString(OvertureConsoleConstants.PREF_NEW_PROMPT), store
				.getString(OvertureConsoleConstants.PREF_CONTINUE_PROMPT));
	}

	protected OvertureConsole makeConsole(OvertureInterpreter interpreter, String id) {
		OvertureConsole console = new OvertureConsole(interpreter, id);
		console.setPrompt(makeInvitation());
		return console;
	}

	private OvertureConsole createConsoleInstance(IScriptInterpreter interpreter, String id) {
		if (interpreter == null) {
			try {
				id = "default";
				interpreter = new OvertureInterpreter();
//				JavaScriptConsoleUtil
//						.runDefaultTclInterpreter((JavaScriptInterpreter) interpreter);
			} catch (Exception e) {
				return null;
			}
		}

		return makeConsole((OvertureInterpreter) interpreter, id);
	}

	protected ScriptConsole createConsoleInstance() {
		return createConsoleInstance(null, null);
	}

	public OvertureConsoleFactory() {
	}

	public void openConsole(IScriptInterpreter interpreter, String id, ILaunch launch) {
		registerAndOpenConsole(createConsoleInstance(interpreter, id));
	}
}
