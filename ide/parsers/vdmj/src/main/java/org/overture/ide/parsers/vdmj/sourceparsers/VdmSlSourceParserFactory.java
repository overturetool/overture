package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmjSourceParser;
import org.overture.ide.vdmsl.core.VdmSlProjectNature;
import org.overturetool.vdmj.lex.Dialect;

public class VdmSlSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmjSourceParser(Dialect.VDM_SL,VdmSlProjectNature.VDM_SL_NATURE);
	}

}
