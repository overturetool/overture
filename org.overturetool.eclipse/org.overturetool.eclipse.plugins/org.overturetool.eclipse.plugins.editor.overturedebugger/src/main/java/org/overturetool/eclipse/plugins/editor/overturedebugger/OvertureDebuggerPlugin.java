package org.overturetool.eclipse.plugins.editor.overturedebugger;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.dltk.dbgp.commands.IDbgpPropertyCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.DebuggingEngineRunner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;
import org.overturetool.eclipse.plugins.editor.overturedebugger.preferences.IOvertureDebuggerPreferenceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class OvertureDebuggerPlugin extends AbstractUIPlugin implements
		IDebugEventSetListener, IPropertyChangeListener {

	private static final String SUSPEND_ON_ENTRY = "suspendOnEntry"; //$NON-NLS-1$
	private static final String SUSPEND_ON_EXIT = "suspendOnExit"; //$NON-NLS-1$
	private static final String SUSPEND_ON_EXCEPTION = "suspendOnException"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "org.overturetool.overturedebugger"; //$NON-NLS-1$

	// The shared instance
	private static OvertureDebuggerPlugin plugin;

	/**
	 * The constructor
	 */
	public OvertureDebuggerPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);	
		plugin = this;
		
		Preferences prefs = getPluginPreferences();		
		resetSuspendProperties(prefs);

		prefs.addPropertyChangeListener(this);
		DebugPlugin.getDefault().addDebugEventListener(this);	
		
	}

	private void resetSuspendProperties(Preferences prefs) {
		// TODO this shouldnt be done or needed to be done!!
		// These preferences should be remembered but whatever i try its
		// impossible (ToggleStates, AbstractHandlerWithState, Action that is
		// pulldown)
//		prefs
//		.setValue(
//				SETUP_FILES,
//				"hass");
//		prefs
//				.setValue(
//						IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_ENTRY,
//						false);
//		prefs
//				.setValue(
//						IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_EXIT,
//						false);
//		prefs
//				.setValue(
//						IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_EXCEPTION,
//						false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		DebugPlugin.getDefault().removeDebugEventListener(this);
		getPluginPreferences().removePropertyChangeListener(this);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static OvertureDebuggerPlugin getDefault() {
		return plugin;
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			if (event.getKind() == DebugEvent.CREATE
					&& checkThread(event.getSource())) {
				IScriptThread thread = (IScriptThread) event.getSource();
//				//setupFiles(thread);
//				setupBreakOnMethodEntry(thread);
//				setupBreakOnMethodExit(thread);
//				setupBreakOnException(thread);
			}
		}
	}



	/**
	 * Checks that the specified object is {@link IScriptThread} and its model
	 * identifier is {@link JavaScriptDebugConstants#DEBUG_MODEL_ID}
	 * 
	 * @param source
	 * @return
	 */
	private boolean checkThread(Object source) {
		if (source instanceof IScriptThread) {
			final IScriptThread thread = (IScriptThread) source;
			return OvertureDebugConstants.DEBUG_MODEL_ID.equals(thread
					.getModelIdentifier());
		}
		return false;
	}

//	private void setupFiles(IScriptThread thread) {
//		IDbgpPropertyCommands propCmds = thread.getDbgpSession().getCoreCommands();
//		try {
//			Preferences prefs = getPluginPreferences();
//			String suspendOnEntry = prefs.getString(SETUP_FILES);
//			propCmds.setProperty(SETUP_FILES, -1, suspendOnEntry);
//		} catch (DbgpException e) {
//			DLTKDebugPlugin.log(e);
//		}		
//	}
//	
	private void setupBreakOnMethodEntry(IScriptThread thread) {
		IDbgpPropertyCommands propCmds = thread.getDbgpSession()
				.getCoreCommands();
		try {
			Preferences prefs = getPluginPreferences();
			String suspendOnEntry = prefs.getString(IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_ENTRY);
			propCmds.setProperty(SUSPEND_ON_ENTRY, -1, suspendOnEntry);
		} catch (DbgpException e) {
			DLTKDebugPlugin.log(e);
		}
	}

	private void setupBreakOnMethodExit(IScriptThread thread) {
		IDbgpPropertyCommands propCmds = thread.getDbgpSession()
				.getCoreCommands();
		try {
			Preferences prefs = getPluginPreferences();
			String suspendOnExit = prefs
					.getString(IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_EXIT);
			propCmds.setProperty(SUSPEND_ON_EXIT, -1, suspendOnExit);
		} catch (DbgpException e) {
			DLTKDebugPlugin.log(e);
		}
	}

	private void setupBreakOnException(IScriptThread thread) {
		IDbgpPropertyCommands propCmds = thread.getDbgpSession()
				.getCoreCommands();
		try {
			Preferences prefs = getPluginPreferences();
			String suspendOnException = prefs
					.getString(IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_EXCEPTION);
			propCmds.setProperty(SUSPEND_ON_EXCEPTION, -1, suspendOnException);
		} catch (DbgpException e) {
			System.out.println("setupBreakOnException " + e.getMessage());
			DLTKDebugPlugin.log(e);
		}
	}

	private boolean isThisDebuggerLaunch(ILaunch launch) {
		String engineId = launch.getAttribute(DebuggingEngineRunner.LAUNCH_ATTR_DEBUGGING_ENGINE_ID);
		if (OvertureDebuggerRunner.ENGINE_ID.equals(engineId)) {
			return true;
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_ENTRY
				.equals(event.getProperty())) {
			runForEachDebuggerThread(new IDebuggerThreadVisitor() {
				public void visit(IScriptThread thread) {
					setupBreakOnMethodEntry(thread);
				}
			});
		} else if (IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_METHOD_EXIT
				.equals(event.getProperty())) {
			runForEachDebuggerThread(new IDebuggerThreadVisitor() {
				public void visit(IScriptThread thread) {
					setupBreakOnMethodExit(thread);
				}
			});
		} else if (IOvertureDebuggerPreferenceConstants.PREF_BREAK_ON_EXCEPTION
				.equals(event.getProperty())) {
			runForEachDebuggerThread(new IDebuggerThreadVisitor() {
				public void visit(IScriptThread thread) {
					setupBreakOnException(thread);
				}
			});
		}
	}

	private interface IDebuggerThreadVisitor {
		void visit(IScriptThread thread);
	}

	private void runForEachDebuggerThread(IDebuggerThreadVisitor visitor) {
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager()
				.getLaunches();
		for (int i = 0; i < launches.length; i++) {
			ILaunch launch = launches[i];
			if (false && !isThisDebuggerLaunch(launch))
				continue;

			IDebugTarget target = launch.getDebugTarget();
			if (target == null)
				continue;
			if (!OvertureDebugConstants.DEBUG_MODEL_ID.equals(target
					.getModelIdentifier()))
				continue;

			IThread[] threads = null;
			try {
				threads = target.getThreads();
			} catch (DebugException e) {
				DLTKDebugUIPlugin.log(e);
			}

			if (threads == null || threads.length == 0)
				continue;

			for (int t = 0; t < threads.length; t++) {
				if (checkThread(threads[t])) {
					IScriptThread scriptThread = (IScriptThread) threads[t];
					visitor.visit(scriptThread);
				}
			}
		}
	}
}


