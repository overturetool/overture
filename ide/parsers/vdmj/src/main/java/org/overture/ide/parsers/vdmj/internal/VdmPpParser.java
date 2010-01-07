package org.overture.ide.parsers.vdmj.internal;

import java.io.File;
import java.util.List;

import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.syntax.ClassReader;

public class VdmPpParser extends VdmjSourceParser  {

	public VdmPpParser(String nature) {
		super(nature);
		Settings.dialect = Dialect.VDM_PP;
	}

	private ClassList classes = new ClassList();

	@SuppressWarnings("unchecked")
	@Override
	public List getModelElements() {
		return classes;
	}

	/**
	 * @see org.overturetool.vdmj.VDMJ#parse(String,File)
	 */
	@Override
	public ExitStatus parse(String content, File file,String charset) {

		classes.clear();
		LexLocation.resetLocations();
		int perrs = 0;
		int pwarn = 0;

		ClassReader reader = null;

		try {

			LexTokenReader ltr = new LexTokenReader(content, Settings.dialect,
					file,charset);
			reader = new ClassReader(ltr);
			classes.addAll(reader.readClasses());

		} catch (InternalException e) {
			processInternalError(e);
			perrs++;
		} catch (Throwable e) {
			processInternalError(e);
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

		return perrs == 0 ? ExitStatus.EXIT_OK : ExitStatus.EXIT_ERRORS;
	}

}
