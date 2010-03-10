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
	protected ParseResult startParse(IFile file, String source,
			String contentType)
	{
		Settings.dialect = Dialect.VDM_PP;
		ClassList classes = new ClassList();
		classes.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		int pwarn = 0;

		ClassReader reader = null;

		try {

			LexTokenReader ltr = new LexTokenReader(source, Settings.dialect,
					file.getLocation().toFile(),contentType);
			reader = new ClassReader(ltr);
			classes.addAll(reader.readClasses());

		} catch (InternalException e) {
			//processInternalError(e);
			perrs++;
		} catch (Throwable e) {
			//processInternalError(e);
			perrs++;
		}

		if (reader != null && reader.getErrorCount() > 0) {
			perrs += reader.getErrorCount();

			processErrors(reader.getErrors());
		}

		if (reader != null && reader.getWarningCount() > 0) {
			pwarn += reader.getWarningCount();

			processWarnings(reader.getWarnings());
		}

		return new ParseResult( perrs != 0,classes );
	}

	

}
