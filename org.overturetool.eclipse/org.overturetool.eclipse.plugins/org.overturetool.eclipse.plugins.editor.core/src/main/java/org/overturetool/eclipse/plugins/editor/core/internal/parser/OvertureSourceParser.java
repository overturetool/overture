package org.overturetool.eclipse.plugins.editor.core.internal.parser;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

public class OvertureSourceParser extends AbstractSourceParser {

	public ModuleDeclaration parse(char[] fileName, char[] source, IProblemReporter reporter) {
		OvertureParserMarcel parser = new OvertureParserMarcel();
		String sourceContent = new String(source);
		String filenamestr = "";
		if (fileName != null)
		{
			filenamestr = new String(fileName);
		}
		return parser.parseContent(sourceContent, filenamestr);
	}

}
