package org.overture.ide.vdmrt.debug;

import java.io.IOException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.LibraryLocation;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;

public class VdmjRtInstallType extends AbstractInterpreterInstallType {

	
	
	@Override
	protected IPath createPathFile(IDeployment deployment) throws IOException {
		throw new RuntimeException("OvertureEditor: This method should not be used");
	}
	
	@Override
	public synchronized LibraryLocation[] getDefaultLibraryLocations(IFileHandle installLocation, EnvironmentVariable[] variables, IProgressMonitor monitor) {
		return new LibraryLocation[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType#doCreateInterpreterInstall(java.lang.String)
	 */
	@Override
	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		// VDM++
		//TODO return a VDM++ Interpreter install
		return new VdmjVdmRtInterpreterInstall(this,id);			
	}
	
	/* (non-Javadoc)
	 * This mehthod is overrided because the interpreters install location is set to the current eclipse path
	 * .... 
	 * @see org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType#validateInstallLocation(org.eclipse.dltk.core.environment.IFileHandle)
	 */
	@Override
	public IStatus validateInstallLocation(IFileHandle installLocation) {
		return Status.OK_STATUS;
	}

	@Override
	protected ILog getLog() {
		return DebugPlugin.getDefault().getLog();
	}

	/*** returns the id of the plug-in 
	 * @see org.eclipse.dltk.internal.launching.AbstractInterpreterInstallType#getPluginId()
	 */
	@Override
	protected String getPluginId() {
		return VdmRtDebugConstants.vdmrt_DEBUG_PLUGIN_ID;
	}

	@Override
	protected String[] getPossibleInterpreterNames() {
		return new String[] {VdmRtDebugConstants.vdmrt_VDMJ_InterpreterType}; //TODO more than one VDMJ interpreter (like version 0.1, 0.2)????? 
		//return new String[] {"eclipse","eclipse.exe"}; //TODO more than one VDMJ interpreter (like version 0.1, 0.2)?????
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.dltk.launching.IInterpreterInstallType#getName()
	 */
	public String getName() {
		return VdmRtDebugConstants.vdmrt_VDMJ_InterpreterName;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.dltk.launching.IInterpreterInstallType#getNatureId()
	 */
	public String getNatureId() {
		return VdmRtProjectNature.VDM_RT_NATURE;
	}

}
