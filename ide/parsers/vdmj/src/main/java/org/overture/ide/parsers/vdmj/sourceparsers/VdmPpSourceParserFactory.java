package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmjSourceParser;
import org.overture.ide.vdmpp.core.VdmPpProjectNature;
import org.overturetool.vdmj.lex.Dialect;

public class VdmPpSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmjSourceParser(Dialect.VDM_PP,VdmPpProjectNature.VDM_PP_NATURE);
	}

}
