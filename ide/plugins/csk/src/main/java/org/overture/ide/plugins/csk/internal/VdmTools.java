package org.overture.ide.plugins.csk.internal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.Assert;
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

	public void createProject(Shell shell, IVdmProject project, List<File> files)
			throws IOException
	{
		File location = project.getLocation().toFile();
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER1);
		sb.append(files.size() + 3);
		sb.append(HEADER2);
		sb.append(files.size());
		sb.append(",");

		for (File file : files)
		{
			String path = getFilePath(location, file);
			sb.append(HEADER_FILE + path.length() + "," + path);
		}

		File generated = new File(location, "generated");
		generated.mkdirs();

		PluginFolderInclude.writeFile(generated, project.getName() + ".prj", sb.toString());
		VdmToolsOptions options = new VdmToolsOptions();
		options.JCG_PACKAGE = (project.getName().replaceAll(" ", "") + "." + "model").toLowerCase();
		options.DTC = project.hasDynamictypechecks();
		options.INV = project.hasInvchecks();
		options.POST = project.hasPostchecks();
		options.PRE = project.hasPrechecks();

		options.Save(generated, project.getName());
		Runtime.getRuntime().exec("\"" + getVdmToolsPath(shell, project)
				+ "\" " + project.getName() + ".prj", null, generated);
	}

	private String getFilePath(File location, File file)
	{
		return file.getAbsolutePath();// "./../"+file.getAbsolutePath().substring(location.getAbsolutePath().length()+1);
	}

	private static String getVdmToolsPath(Shell shell, IVdmProject project)
	{
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String path = "";

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

		}
		if (!valid)
		{
			MessageDialog.openError(shell, "VDM Tools Error", "CSK VPPGDE Path not valid");

		}
		Assert.isTrue(valid, "VPPGDE path is not valid");
		return path;
	}
}
