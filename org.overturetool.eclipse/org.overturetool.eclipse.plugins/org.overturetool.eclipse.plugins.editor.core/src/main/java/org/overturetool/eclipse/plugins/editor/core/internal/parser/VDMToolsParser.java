package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.OvertureSourceParserFactory.Dialect;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureModuleDeclaration;
import org.overturetool.parser.imp.OvertureParser;

public class VDMToolsParser extends AbstractSourceParser {

	public VDMToolsParser(Dialect dialect) {
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
		String content = new String(source);
		OvertureModuleDeclaration moduleDeclaration = new OvertureModuleDeclaration(content.length(), true);
		OvertureParser parser = new OvertureParser(content);
//		DLTKConverter converter = new DLTKConverter(source);
		if (fileName != null){
			try {
				parser.parseDocument();
				parser.astDocument.setFilename(new String(fileName));
				
				if (parser.errors > 0){
					System.err.println("#Errors: " + parser.errors);
				}
				else
				{
					OvertureASTTreePopulator populator = new OvertureASTTreePopulator(moduleDeclaration, new DLTKConverter(content.toCharArray()));
					return populator.populateOverture(parser.astDocument.getSpecifications().getClassList());
				}
				
				
				return moduleDeclaration;
			} catch (CGException e) {
				System.out.println("Couldn't parse the document" + e.getMessage());
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

}
