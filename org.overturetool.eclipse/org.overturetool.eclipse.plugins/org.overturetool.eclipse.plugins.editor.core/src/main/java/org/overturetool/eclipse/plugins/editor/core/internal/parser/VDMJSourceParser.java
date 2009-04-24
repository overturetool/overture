package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.overturetool.eclipse.plugins.editor.core.internal.builder.EclipseVDMJPP;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.OvertureSourceParserFactory.Dialect;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;

public class VDMJSourceParser extends AbstractSourceParser {

		
	public VDMJSourceParser(Dialect dialect) {
		//TODO VDM++ / VDM-SL
		switch (dialect)
		{
			case VDM_PP:
				break;
			
			case VDM_SL:
				break;
		
			case VDM_RT:
				break;
			default:
				
		}
	}
	
	public ModuleDeclaration parse(char[] fileName, char[] source, IProblemReporter reporter) {
		// test
//		OverturePlugin.getDefault().getBundle().
		
		// 
		OvertureModuleDeclaration moduleDeclaration = new OvertureModuleDeclaration(source.length, true);
		DLTKConverter converter = new DLTKConverter(source);
		
		EclipseVDMJPP vdmPpParser = new EclipseVDMJPP();
		
		ExitStatus status = vdmPpParser.parse(new String(source));
		OvertureASTTreePopulator populator = new OvertureASTTreePopulator(moduleDeclaration,converter);
		moduleDeclaration = populator.populateVDMJ(vdmPpParser.classes);
		
		if (status == ExitStatus.EXIT_ERRORS)
		{
			for (VDMError error : vdmPpParser.getParseErrors()) {
				DefaultProblem defaultProblem = new DefaultProblem(
						new String(fileName),
						error.message,
						error.number,
						new String[] {},
						ProblemSeverities.Error, 
						converter.convert(error.location.startLine, error.location.startPos),
						converter.convert(error.location.endLine, error.location.endPos),
						error.location.startLine);
				reporter.reportProblem(defaultProblem);
			}
		}
		return moduleDeclaration;
	}

}
