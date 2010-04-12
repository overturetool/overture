package org.overture.ide.parsers.vdmj;

import java.util.List;
import java.util.Vector;

import org.overture.ide.core.parser.AbstractParserParticipant;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.syntax.ClassReader;

public class SourceParserVdmPp extends AbstractParserParticipant
{

	@Override
	protected ParseResult startParse(IVdmSourceUnit file, String source,
			String charset)
	{
		Settings.dialect = Dialect.VDM_PP;
		return startParseFile(file, source, charset);
	}

	protected ParseResult startParseFile(IVdmSourceUnit file, String source,
			String charset)
	{
		file.setType(IVdmSourceUnit.VDM_CLASS_SPEC);
		LexTokenReader.TABSTOP = 1;
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
					file.getSystemFile(),
					charset);
			reader = new ClassReader(ltr);
			classes.addAll(reader.readClasses());
			List<IAstNode> nodes = new Vector<IAstNode>();
			for (ClassDefinition classDefinition : classes)
			{
				nodes.add(classDefinition);
			}
			if (nodes.size() > 0)
			{
				result.setAst(nodes);
			} else
			{
				perrs++;
				result.setFatalError(new Exception("No VDM source in file"));
			}

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

		for (ClassDefinition classDefinition : classes)
		{
			classDefinition.getDefinitions();
		}

		result.setAllLocation(LexLocation.getAllLocations());
		result.setLocationToAstNodeMap(LexLocation.getLocationToAstNodeMap());

		return result;
	}

}
