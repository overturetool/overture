package org.overture.ide.parsers.vdmj;

import org.eclipse.core.resources.IFile;
import org.overture.ide.core.parser.AbstractParserParticipant;
import org.overturetool.vdmj.*;
import org.overturetool.vdmj.definitions.*;
import org.overturetool.vdmj.lex.*;
import org.overturetool.vdmj.messages.*;
import org.overturetool.vdmj.syntax.*;

public class SourceParserVdmPp extends AbstractParserParticipant
{

	@Override
	protected ParseResult startParse(IFile file, String source, String charset)
	{
		Settings.dialect = Dialect.VDM_PP;
		ClassList classes = new ClassList();
		classes.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		int pwarn = 0;

		ClassReader reader = null;
		ParseResult result = new ParseResult();
		try
		{

			LexTokenReader ltr = new LexTokenReader(source,
					Settings.dialect,
					file.getLocation().toFile(),
					charset);
			reader = new ClassReader(ltr);
			classes.addAll(reader.readClasses());
			result.setAst(classes);

		} catch (InternalException e)
		{

			perrs++;
			result.setFatalError(e);
		} catch (Throwable e)
		{

			perrs++;
			result.setFatalError(e);
		}

		if (reader != null && reader.getErrorCount() > 0)
		{
			perrs += reader.getErrorCount();

			result.setErrors(reader.getErrors());
		}

		if (reader != null && reader.getWarningCount() > 0)
		{
			pwarn += reader.getWarningCount();

			result.setWarnings(reader.getWarnings());
		}

		return result;
	}

}
