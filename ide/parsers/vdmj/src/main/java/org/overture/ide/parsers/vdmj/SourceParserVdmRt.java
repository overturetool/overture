package org.overture.ide.parsers.vdmj;

import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;

public class SourceParserVdmRt extends SourceParserVdmPp
{

	@Override
	protected ParseResult startParse(IVdmSourceUnit file, String source,
			String charset)
	{
		Settings.dialect = Dialect.VDM_RT;
		return startParseFile(file, source, charset);
	}

}
