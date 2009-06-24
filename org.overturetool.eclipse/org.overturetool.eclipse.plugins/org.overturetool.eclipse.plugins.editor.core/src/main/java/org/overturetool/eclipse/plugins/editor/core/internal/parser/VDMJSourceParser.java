package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.overturetool.eclipse.plugins.editor.core.internal.builder.EclipseVDMJ;
import org.overturetool.eclipse.plugins.editor.core.internal.builder.EclipseVDMJPP;
import org.overturetool.eclipse.plugins.editor.core.internal.builder.EclipseVDMJSL;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public class VDMJSourceParser{
	
	String dialect;
	
	VDMJSourceParser(String dialect){
		this.dialect = dialect;
	}
	
	public ModuleDeclaration parse(char[] fileName, char[] source, IProblemReporter reporter) {
		// test
		
		// 
		OvertureModuleDeclaration moduleDeclaration = new OvertureModuleDeclaration(source.length, true);
		DLTKConverter converter = new DLTKConverter(source);
		
		EclipseVDMJ eclipseParser = null;
		if (dialect.equals("VDM_PP")){			
			eclipseParser = new EclipseVDMJPP();
		}
		else if (dialect.equals("VDM_SL")) {
			eclipseParser = new EclipseVDMJSL();
		}
		else if (dialect.equals("VDM_RT)")) {
			eclipseParser = null; // TODO
		}
		
		ExitStatus status = eclipseParser.parse(new String(source));
		OvertureASTTreePopulator populator = new OvertureASTTreePopulator(moduleDeclaration,converter);

		moduleDeclaration = populator.populateVDMJ(eclipseParser);
		if (reporter != null){
			if (status == ExitStatus.EXIT_ERRORS)
			{
				for (VDMError error : eclipseParser.getParseErrors()) {
					DefaultProblem defaultProblem = new DefaultProblem(
							new String(fileName),
							error.message,
							error.number,
							new String[] {},
							ProblemSeverities.Error, 
							converter.convert(error.location.startLine, error.location.startPos -1),
							converter.convert(error.location.endLine, error.location.endPos -1),
							error.location.startLine);
					reporter.reportProblem(defaultProblem);
				}
			}
			if (eclipseParser.getParseWarnings().size() > 0)
			{
				for (VDMWarning warning : eclipseParser.getParseWarnings()) {
					DefaultProblem defaultProblem = new DefaultProblem(
							new String(fileName),
							warning.message,
							warning.number,
							new String[] {},
							ProblemSeverities.Warning, 
							converter.convert(warning.location.startLine, warning.location.startPos -1),
							converter.convert(warning.location.endLine, warning.location.endPos -1),
							warning.location.startLine);
					reporter.reportProblem(defaultProblem);
				}			
			}
		}
		return moduleDeclaration;
	}

}
