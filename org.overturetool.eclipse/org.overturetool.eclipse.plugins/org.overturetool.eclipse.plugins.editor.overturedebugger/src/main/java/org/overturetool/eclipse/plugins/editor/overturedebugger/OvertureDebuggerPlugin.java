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
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.launching.DebuggingEngineRunner;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.overturetool.eclipse.debug.internal.debug.OvertureDebugConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class OvertureDebuggerPlugin extends AbstractUIPlugin implements
		IDebugEventSetListener, IPropertyChangeListener {

	
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
		//super.start(context);	
		plugin = this;
		
		//Preferences prefs = getPluginPreferences();	
		

		//prefs.addPropertyChangeListener(this);
		//DebugPlugin.getDefault().addDebugEventListener(this);	
		
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
	

	private boolean isThisDebuggerLaunch(ILaunch launch) {
		String engineId = launch.getAttribute(DebuggingEngineRunner.LAUNCH_ATTR_DEBUGGING_ENGINE_ID);
		if (OvertureDebuggerRunner.ENGINE_ID.equals(engineId)) {
			return true;
		}
		return false;
	}

	public void propertyChange(PropertyChangeEvent event) {
		
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


