package org.overturetool.eclipse.plugins.launching.internal.launching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.launching.LaunchingPlugin;
import org.overturetool.eclipse.plugins.launching.OvertureLaunchConstants;

public class VDMToolsInstallType extends
		AbstractInterpreterInstallType implements IOvertureInstallType {
	
	

	public static final String DBGP_FOR_VDMTOOLS_BUNDLE_ID = "org.overturetool.eclipse.plugins.dbgp.core"; //$NON-NLS-1$

	
	public final static String OVERTURE_VDM_PLUS_PLUS = "VDM++";
	public final static String OVERTURE_VDM_SPECIFICATION_LANGUAGE = "VDM-SL";
	
	public final static String OVERTURE_VDM_PLUS_PLUS_ID = "PP_TOOLBOX";
	public final static String OVERTURE_VDM_SPECIFICATION_LANGUAGE_ID = "SL_TOOLBOX";
	
	public final static Dialect[] DIALECTS = {new Dialect(OVERTURE_VDM_PLUS_PLUS, OVERTURE_VDM_PLUS_PLUS_ID),
		new Dialect(OVERTURE_VDM_SPECIFICATION_LANGUAGE, OVERTURE_VDM_SPECIFICATION_LANGUAGE_ID)};
		
	public String getNatureId() {
		return OvertureNature.NATURE_ID;
	}

	public String getName() {
		return "VDMTools Interpreter"; //$NON-NLS-1$
	}

	public LibraryLocation[] getDefaultLibraryLocations(
			IFileHandle installLocation, EnvironmentVariable[] variables,
			IProgressMonitor monitor) {
		// final List result = new ArrayList();
		// ClasspathUtils.collectClasspath(
		// new String[] { EMBEDDED_RHINO_BUNDLE_ID }, result);
		// if (!result.isEmpty()) {
		// final IPath fullPath = EnvironmentPathUtils.getFullPath(
		// LocalEnvironment.getInstance(), new Path((String) result
		// .get(0)));
		// return new LibraryLocation[] { new LibraryLocation(fullPath) };
		// }
		return new LibraryLocation[0];
	}

	private static String[] possibleExes = { "eclipse", "eclipse.exe", "" };

	protected String getPluginId() {
		return OvertureLaunchConstants.PLUGIN_ID;
	}

	protected String[] getPossibleInterpreterNames() {
		return possibleExes;
	}

	protected IInterpreterInstall doCreateInterpreterInstall(String id) {
		return new GenericOvertureInstall(this, id);
	}

	protected void filterEnvironment(Map environment) {
		// block wish from showing window under linux
		environment.remove("DISPLAY");
	}

	public IStatus validateInstallLocation(IFileHandle installLocation) {
		return Status.OK_STATUS;
	}

	protected IPath createPathFile(IDeployment deployment) throws IOException {
		// this method should not be used
		throw new RuntimeException("This method should not be used");
	}

	protected String[] parsePaths(String result) {
		ArrayList<String> paths = new ArrayList<String>();
		String subs = null;
		int index = 0;
		while (index < result.length()) {
			// skip whitespaces
			while (index < result.length()
					&& Character.isWhitespace(result.charAt(index)))
				index++;
			if (index == result.length())
				break;

			if (result.charAt(index) == '{') {
				int start = index;
				while (index < result.length() && result.charAt(index) != '}')
					index++;
				if (index == result.length())
					break;
				subs = result.substring(start + 1, index);
			} else {
				int start = index;
				while (index < result.length() && result.charAt(index) != ' ')
					index++;
				subs = result.substring(start, index);
			}

			paths.add(subs);
			index++;
		}

		return (String[]) paths.toArray(new String[paths.size()]);
	}

	protected ILog getLog() {
		return LaunchingPlugin.getDefault().getLog();
	}

	public Dialect[] getSupportedDialects() {
		return DIALECTS;
	}
	
	public String getDialectNameFromId(String id) {
		for (Dialect dialect : DIALECTS) {
			if(dialect.getId().equals(id)){
				return dialect.getName();
			}
		}
		return null;
	}
}