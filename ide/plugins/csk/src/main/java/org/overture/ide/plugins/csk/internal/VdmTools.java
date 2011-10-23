/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.csk.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.csk.Activator;
import org.overture.ide.plugins.csk.ICskConstants;
import org.overture.ide.ui.utility.PluginFolderInclude;

public class VdmTools
{
	public static final String HEADER1 = "b";
	public static final String HEADER2 = ",k13,ProjectFilePPf3,f";
	public static final String HEADER2_SL = ",k11,ProjectFilef3,f";

	public static final String HEADER_FILE = "e2,m4,filem";

	final String VDM_TOOLS_PROJECT_OPT = "FormatVersion:2\n" + "DTC:1\n"
			+ "PRE:1\n" + "POST:1\n" + "INV:1\n" + "CONTEXT:0\n"
			+ "MAXINSTR:1000\n" + "PRIORITY:0\n"
			+ "PRIMARYALGORITHM:instruction_number_slice\n" + "TASKSWITCH:0\n"
			+ "MAXTIME:1000\n" + "TIMEFACTOR:1\n" + "STEPSIZE:100\n"
			+ "JITTERMODE:Early\n" + "DEFAULTCPUCAPACITY:1000000\n"
			+ "DEFAULTVCPUCAPACITY:INFINITE\n" + "LOGARGS:\n"
			+ "PRINT_FORMAT:1\n" + "DEF:pos\n" + "errlevel:1\n" + "SEP:1\n"
			+ "VDMSLMOD:0\n" + "INDEX:0\n" + "PrettyPrint_RTI:0\n"
			+ "CG_RTI:0\n" + "CG_CHECKPREPOST:1\n" + "C_flag:0\n"
			+ "JCG_SKEL:0\n" + "JCG_GENPREPOST:0\n" + "JCG_TYPES:0\n"
			+ "JCG_SMALLTYPES:0\n" + "JCG_LONGS:1\n" + "JCG_PACKAGE:\n"
			+ "JCG_CONCUR:0\n" + "JCG_CHECKPREPOST:0\n" + "JCG_VDMPREFIX:1\n"
			+ "JCG_INTERFACES:\n" + "Seed_nondetstmt:-1\n"
			+ "j2v_stubsOnly:0\n" + "j2v_transforms:0";

	public void createProject(Shell shell, IVdmProject vdmProject,
			List<File> files) throws IOException
	{

		IProject project = (IProject) vdmProject.getAdapter(IProject.class);
		File location = project.getLocation().toFile();
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER1);
		sb.append(files.size() + 3);

		switch (vdmProject.getDialect())
		{
			case VDM_PP:
			case VDM_RT:
				sb.append(HEADER2);
				break;
			case VDM_SL:
				sb.append(HEADER2_SL);
				break;
		}

		sb.append(files.size());
		sb.append(",");

		for (File file : files)
		{
			String path = getFilePath(location, file);
			sb.append(HEADER_FILE + path.length() + "," + path);
		}

		File generated = vdmProject.getModelBuildPath().getOutput().getLocation().toFile();// new File(location,
		// "generated");
		generated.mkdirs();
		
		String projectFileName = vdmProject.getName().trim();

		PluginFolderInclude.writeFile(generated, projectFileName + ".prj", sb.toString());
		VdmToolsOptions options = new VdmToolsOptions();
		options.JCG_PACKAGE = (projectFileName.replaceAll(" ", "") + "." + "model").toLowerCase();
		// options.DTC = vdmProject.hasDynamictypechecks();
		// options.INV = vdmProject.hasInvchecks();
		// options.POST = vdmProject.hasPostchecks();
		// options.PRE = vdmProject.hasPrechecks();

		options.Save(generated, projectFileName);

//		String projectFileName = projectFileName + ".prj";

		String vdmToolsPath = getVdmToolsPath(shell, vdmProject);

		if (vdmToolsPath != null)
		{
			Runtime.getRuntime().exec(toPlatformPath(vdmToolsPath) + " "
					+ toPlatformPath(projectFileName+".prj"), null, generated);
		}
	}

	private String getFilePath(File location, File file)
	{
		return file.getAbsolutePath();// "./../"+file.getAbsolutePath().substring(location.getAbsolutePath().length()+1);
	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	protected static String toPlatformPath(String path)
	{
		if (isWindowsPlatform())
		{
			return "\"" + path + "\"";
		} else
		{
			return path.replace(" ", "\\ ");
		}
	}

	private static String getVdmToolsPath(Shell shell, IVdmProject project)
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String path = null;

		switch (project.getDialect())
		{
			case VDM_PP:
				path = store.getString(ICskConstants.VPPGDE_PATH);
				break;
			case VDM_RT:
				path = store.getString(ICskConstants.VRTGDE_PATH);
				break;
			case VDM_SL:
				path = store.getString(ICskConstants.VSLGDE_PATH);
				break;
		}

		boolean valid = path.length() > 0;
		// if(!valid)
		// {
		// store.setDefault(ICskConstants.VPPGDE_PATH,ICskConstants.DEFAULT_VPPGDE_PATH);
		// path = store.getString(ICskConstants.VPPGDE_PATH);
		// }

		if (valid)
		{
			valid = new File(path).exists();

			if (Platform.getOS().equalsIgnoreCase(Platform.OS_MACOSX))
			{
				path = "open " + path;
			}

		}
		if (!valid)
		{
			MessageDialog.openError(shell, "VDM Tools Error", "CSK VDM Tools Path not valid");
			path = null;
		}
		// Assert.isTrue(valid, "VDM Tools path is not valid");
		return path;
	}

	
}
