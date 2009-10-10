package org.overture.ide.vdmrt.parsers.vdmj;

import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserFactory;
import org.overture.ide.vdmrt.parsers.vdmj.internal.VdmjSourceParser;

public class VdmjSourceParserFactory implements ISourceParserFactory
{

	public ISourceParser createSourceParser()
	{
		return new VdmjSourceParser();
	}

}
