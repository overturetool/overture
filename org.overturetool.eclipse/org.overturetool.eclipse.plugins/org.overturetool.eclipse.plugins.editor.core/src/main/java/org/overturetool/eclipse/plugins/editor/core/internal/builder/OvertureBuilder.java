package org.overturetool.eclipse.plugins.editor.core.internal.builder;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.overturetool.eclipse.plugins.editor.core.EditorCoreConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
//
public class OvertureBuilder implements IScriptBuilder {

	private VDMJBuilder vdmjBuilder = null;
	private VDMToolsBuilder vdmToolsBuilder = null;
	private enum ToolType {VDMJ,VDMTools};
	
	public IStatus buildModelElements(IScriptProject project, List elements, IProgressMonitor monitor, int status) {
		ToolType toolType = ToolType.VDMJ;

		IInterpreterInstall interpreterInstall;
		try {
			// find interpreter
			interpreterInstall = ScriptRuntime.getInterpreterInstall(project);
			interpreterInstall.getInstallLocation();
			if (interpreterInstall.getInterpreterInstallType().getId().equals(OvertureConstants.VDMJ_INTERPRETER_ID)) {
				toolType = ToolType.VDMJ;
			} else if (interpreterInstall.getInterpreterInstallType().getId().equals(OvertureConstants.VDMTOOLS_INTERPRETER_ID)) {
				toolType = ToolType.VDMTools;
			}
			
			// find dialect
			QualifiedName qn = new QualifiedName(EditorCoreConstants.PLUGIN_ID, EditorCoreConstants.OVERTURE_DIALECT_KEY);
			String dialect = project.getProject().getPersistentProperty(qn);

			switch (toolType) {
				case VDMJ:
					vdmjBuilder = new VDMJBuilder(project, dialect);
					return vdmjBuilder.typeCheck();
				case VDMTools:
					vdmToolsBuilder = new VDMToolsBuilder(project, interpreterInstall.getInstallLocation().toOSString(), dialect);
					return vdmToolsBuilder.typeCheck();					
				default:
					return null;
			}
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	public IStatus buildResources(IScriptProject project, List resources, IProgressMonitor monitor, int status) {
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

	public void endBuild(IScriptProject project, IProgressMonitor monitor) {
		
	}
	
}
