package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.overturetool.eclipse.plugins.editor.core.EditorCoreConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;

public class OvertureSourceParser extends AbstractSourceParser {

	private IInterpreterInstall getInterpreterFromName(String name) {
		IInterpreterInstallType[] types = ScriptRuntime.getInterpreterInstallTypes(OvertureNature.NATURE_ID);

		for (IInterpreterInstallType interpreterInstallType : types) {
			IInterpreterInstall[] installs = interpreterInstallType.getInterpreterInstalls();

			for (IInterpreterInstall interpreterInstall : installs) {
				if (interpreterInstall.getName().equals(name)){
					return interpreterInstall;
				}
			}

		}
		return null;
	}
	
	public ModuleDeclaration parse(char[] fileName, char[] source, IProblemReporter reporter) {
		
		try {
			// find project
			Path path = new Path(new String(fileName));
			IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path); 
			IProject project = res.getProject();
			
			// find dialect
			QualifiedName qn = new QualifiedName(EditorCoreConstants.PLUGIN_ID, EditorCoreConstants.OVERTURE_DIALECT_KEY);
			String dialect = project.getPersistentProperty(qn);
			
			
			// interpreter
			qn = new QualifiedName(EditorCoreConstants.PLUGIN_ID, EditorCoreConstants.OVERTURE_INTERPETER_KEY);
			String interpreterName = project.getPersistentProperty(qn);
			
			IInterpreterInstall interpreterInstall =  getInterpreterFromName(interpreterName);
			
			// depending on the interpreterInstall select the parser
			if (interpreterInstall.getInterpreterInstallType().getId().equals(OvertureConstants.VDMJ_INTERPRETER_ID))
			{
				VDMJSourceParser vdmjSourceParser = new VDMJSourceParser(dialect);
				return vdmjSourceParser.parse(fileName, source, reporter);			
			}
			else if (interpreterInstall.getInterpreterInstallType().getId().equals(OvertureConstants.VDMJ_INTERPRETER_ID))
			{
				VDMToolsParser vdmToolsSourceParser = new VDMToolsParser(dialect);
				return vdmToolsSourceParser.parse(fileName, source, reporter);
			}
			
		} catch (Exception e) {
			return null;
		}
		return null;
	}

}
