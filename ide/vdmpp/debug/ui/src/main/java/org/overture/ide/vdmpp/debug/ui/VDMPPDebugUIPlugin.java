package org.overture.ide.vdmpp.debug.ui;

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
import org.overture.ide.vdmpp.debug.core.VDMPPDebugConstants;

public class VDMPPDebugUIPlugin extends AbstractUIPlugin implements IDebugEventSetListener, IPropertyChangeListener {
	private static final String PREF_BREAK_ON_METHOD_ENTRY = "breakOnMethodEntry";
	private static final String SUSPEND_ON_ENTRY = "suspendOnEntry";
	// The shared instance.
	private static VDMPPDebugUIPlugin plugin;

	public VDMPPDebugUIPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static VDMPPDebugUIPlugin getDefault() {
		return plugin;
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (int i = 0; i < events.length; i++) {
			DebugEvent event = events[i];
			if (event.getKind() == DebugEvent.CREATE && checkThread(event.getSource())) {
				IScriptThread thread = (IScriptThread) event.getSource();
				setupBreakOnMethodEntry(thread);
			}
		}
	}

	private void setupBreakOnMethodEntry(IScriptThread thread) {
		IDbgpPropertyCommands propCmds = thread.getDbgpSession().getCoreCommands();
		try {
			Preferences prefs = getPluginPreferences();
			String suspendOnEntry = prefs.getString(PREF_BREAK_ON_METHOD_ENTRY);
			propCmds.setProperty(SUSPEND_ON_ENTRY, -1, suspendOnEntry);
		} catch (DbgpException e) {
			DLTKDebugPlugin.log(e);
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
			return VDMPPDebugConstants.VDMPP_DEBUG_MODEL.equals(thread.getModelIdentifier());
		}
		return false;
	}
	
	
	private interface IDebuggerThreadVisitor {
		void visit(IScriptThread thread);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (PREF_BREAK_ON_METHOD_ENTRY.equals(event.getProperty())) {
			runForEachDebuggerThread(new IDebuggerThreadVisitor() {
				public void visit(IScriptThread thread) {
					setupBreakOnMethodEntry(thread);
				}
			});
		}		
	}
	
	private void runForEachDebuggerThread(IDebuggerThreadVisitor visitor) {
		ILaunch[] launches = DebugPlugin.getDefault().getLaunchManager().getLaunches();
		for (int i = 0; i < launches.length; i++) {
			ILaunch launch = launches[i];
			if (false && !isThisDebuggerLaunch(launch))
				continue;

			IDebugTarget target = launch.getDebugTarget();
			if (target == null)
				continue;
			if (!VDMPPDebugConstants.VDMPP_DEBUG_MODEL.equals(target.getModelIdentifier()))
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
	
	private boolean isThisDebuggerLaunch(ILaunch launch) {
		String engineId = launch.getAttribute(DebuggingEngineRunner.LAUNCH_ATTR_DEBUGGING_ENGINE_ID);
		if (VDMPPDebugConstants.VDMPP_DEBUGGING_ENGINE_ID_KEY.equals(engineId)) {
			return true;
		}
		return false;
	}

}
