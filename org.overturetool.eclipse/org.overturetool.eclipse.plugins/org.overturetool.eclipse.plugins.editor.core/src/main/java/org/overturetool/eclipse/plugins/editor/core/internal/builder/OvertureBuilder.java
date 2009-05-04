package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.OvertureSourceParserFactory.Dialect;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.OvertureSourceParserFactory.ToolType;
//
public class OvertureBuilder implements IScriptBuilder {

	private VDMJBuilder vdmjBuilder = null;
	private VDMToolsBuilder vdmToolsBuilder = null;
	
	public IStatus buildModelElements(IScriptProject project, List elements, IProgressMonitor monitor, int status) {
		
		ToolType toolType = ToolType.VDMJ;

		IInterpreterInstall interpreterInstall;
		try {
			interpreterInstall = ScriptRuntime.getInterpreterInstall(project);
			interpreterInstall.getInstallLocation();
			if (interpreterInstall.getInterpreterInstallType().getName().equals("VDMJ")) {
				toolType = ToolType.VDMJ;
			} else if (interpreterInstall.getInterpreterInstallType().getName().equals("VDMTools")) {
				toolType = ToolType.VDMTools;
			}

			// OverturePlugin.getDefault().getPluginPreferences().getString()

			// TODO get project options dialect and tool
			Dialect dialect = Dialect.VDM_PP; // ??

			switch (toolType) {
			case VDMTools:
				vdmToolsBuilder = new VDMToolsBuilder(project, interpreterInstall.getInstallLocation().toOSString());
				return vdmToolsBuilder.typeCheck();
			case VDMJ:
				vdmjBuilder = new VDMJBuilder(project);
				return vdmjBuilder.typeCheck();
			default:
				break;
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public IStatus buildResources(IScriptProject project, List resources, IProgressMonitor monitor, int status) {
		// TODO VDMJ
//		if (true) {
//			VDMJBuilder vdmjBuilder = new VDMJBuilder(project);
//			return vdmjBuilder.typeCheck();
//
//		} else {
//			VDMToolsBuilder vdmToolsBuilder = new VDMToolsBuilder(project);
//			return vdmToolsBuilder.typeCheck();
//		}
		return null;
	}

	public void clean(IScriptProject project, IProgressMonitor monitor) {
		
	}

	public DependencyResponse getDependencies(IScriptProject project,
			int buildType, Set localElements, Set externalElements,
			Set oldExternalFolders, Set externalFolders) {
		return null;
	}

	public void initialize(IScriptProject project) {
		
	}

	public void reset(IScriptProject project) {
		
	}
	
}
