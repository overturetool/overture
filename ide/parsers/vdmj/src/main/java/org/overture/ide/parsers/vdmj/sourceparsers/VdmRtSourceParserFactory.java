package org.overture.ide.parsers.vdmj.sourceparsers;


import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.parsers.vdmj.internal.VdmjSourceParser;
import org.overture.ide.vdmrt.core.VdmRtProjectNature;
import org.overturetool.vdmj.lex.Dialect;

public class VdmRtSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmjSourceParser(Dialect.VDM_RT,VdmRtProjectNature.VDM_RT_NATURE);
	}

}
