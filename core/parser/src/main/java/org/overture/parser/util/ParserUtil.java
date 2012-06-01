package org.overture.parser.util;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ClassReader;
import org.overturetool.vdmj.syntax.ModuleReader;

public class ParserUtil
{
	public static class ParserResult<T>
	{
		public final T result;
		public final List<VDMWarning> warnings;
		public final List<VDMError> errors;

		public ParserResult(T result, List<VDMWarning> warnings,
				List<VDMError> errors)
		{
			this.result = result;
			this.warnings = warnings;
			this.errors = errors;
		}
	}

	public static ParserResult<List<SClassDefinition>> parseOo(File file)
	{
		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		ClassReader reader = null;
		List<SClassDefinition> result = null;

		reader = new ClassReader(ltr);
		result = reader.readClasses();

		return new ParserResult<List<SClassDefinition>>(result, reader.getWarnings(), reader.getErrors());
	}
	
	public static ParserResult<List<AModuleModules>> parseSl(File file)
	{
		LexTokenReader ltr = new LexTokenReader(file, Settings.dialect);
		ModuleReader reader = null;
		List<AModuleModules> result = null;

		reader = new ModuleReader(ltr);
		result = reader.readModules();

		return new ParserResult<List<AModuleModules>>(result, reader.getWarnings(), reader.getErrors());
	}

}
