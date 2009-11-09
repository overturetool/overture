package org.overture.ide.debug.launching;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.osgi.framework.BundleContext;

public class LaunchingPlugin extends Plugin {

	// The shared instance
	private static LaunchingPlugin plugin;
	
	/**
	 * The constructor
	 */
	public LaunchingPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LaunchingPlugin getDefault() {
		return plugin;
	}
	
	public static void error(Throwable t) {
		plugin.getLog().log(new Status(IStatus.ERROR,  VDMLaunchingConstants.VDMPP_LaunchPluginID, IStatus.OK, t.toString(),t));
	}

	public IFileHandle getConsoleProxy(IExecutionEnvironment exeEnv) throws IOException {
		IDeployment deployment = exeEnv.createDeployment();
		IPath path = deployment.add(this.getBundle(), "console");
		path.append("ConsoleProxy.js");
		return deployment.getFile(path);
	}

}
