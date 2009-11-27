package org.overture.ide.parsers.vdmj.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.IAstManager;
import org.overture.ide.ast.RootNode;
import org.overture.ide.utility.ProjectUtility;
import org.overture.ide.utility.SourceLocationConverter;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

/***
 * Used to parse VDM files with VDMJ based on the dialect
 * 
 * @author kela
 * 
 */
public abstract class VdmjSourceParser extends AbstractSourceParser {

	String nature;
	private List<VDMError> errors = new ArrayList<VDMError>();
	private List<VDMWarning> warnings = new ArrayList<VDMWarning>();

	

	public VdmjSourceParser(String nature) {

		this.nature = nature;
	}

	public ModuleDeclaration parse(char[] fileName, char[] source,
			IProblemReporter reporter) {
		String fileNameString = new String(fileName);
		// find project
		Path path = new Path(fileNameString);

		IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
		IProject project = res.getProject();

		SourceLocationConverter converter = new SourceLocationConverter(source);

		errors.clear();
		warnings.clear();
		
		
		ExitStatus status = parse(new String(source), ProjectUtility.getFile(project, path));// project.getFile(path.removeFirstSegments(1)).getLocation().toFile()//parse(new
		// String(source));

		if (reporter != null) {
			if (status == ExitStatus.EXIT_ERRORS) {
				for (VDMError error : errors) {
					DefaultProblem defaultProblem = new DefaultProblem(
							fileNameString, error.message, error.number,
							new String[] {}, ProblemSeverities.Error, converter
									.convert(error.location.startLine,
											error.location.startPos - 1),
							converter.convert(error.location.endLine,
									error.location.endPos - 1),
							error.location.startLine);
					reporter.reportProblem(defaultProblem);
				}
			}
			if (warnings.size() > 0) {
				for (VDMWarning warning : warnings) {
					DefaultProblem defaultProblem = new DefaultProblem(
							fileNameString, warning.message, warning.number,
							new String[] {}, ProblemSeverities.Warning,
							converter.convert(warning.location.startLine,
									warning.location.startPos - 1), converter
									.convert(warning.location.endLine,
											warning.location.endPos - 1),
							warning.location.startLine);
					reporter.reportProblem(defaultProblem);
				}
			}
		}

		// AstManager.instance().setAst(
		// project,
		// VdmSlProjectNature.VDM_SL_NATURE,
		// eclipseParser.getModules());
	
		IAstManager astManager = AstManager.instance();
		ModuleDeclaration moduleDeclaration = astManager.addAstModuleDeclaration(project, nature, fileName, source, getModelElements());
		RootNode rootNode = astManager.getRootNode(project, nature);
		if (rootNode != null) {
			if (status == ExitStatus.EXIT_ERRORS){			
				rootNode.setParseCorrect(false);
			}
			else {
				rootNode.setParseCorrect(true);
			}
		}
		return moduleDeclaration; 
	}

	// public abstract ExitStatus typeCheck();

	/**
	 * Parse the content of a file and set the file parsed as the file in the
	 * token locations. The value returned is the number of syntax errors
	 * encountered.
	 * 
	 * @param content
	 *            The content of the file to parse.
	 * @param file
	 *            The file to set in token locations.
	 * @return The number of syntax errors.
	 */

	public abstract ExitStatus parse(String content, File file);

	/**
	 * Handle Errors
	 * 
	 * @param list
	 *            encountered during a parse or type check
	 */
	protected void processErrors(List<VDMError> errors) {
		this.errors.addAll(errors);
	};

	/**
	 * Handle Warnings
	 * 
	 * @param errors
	 *            encountered during a parse or type check
	 */
	protected void processWarnings(List<VDMWarning> warnings) {
		this.warnings.addAll(warnings);
	};
	
	protected void processInternalError(Throwable e) {
		System.out.println(e.toString());
	};

	@SuppressWarnings("unchecked")
	public abstract List getModelElements();

}