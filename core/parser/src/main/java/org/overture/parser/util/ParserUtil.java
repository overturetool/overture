package org.overture.parser.util;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.ParserException;

public class ParserUtil
{
	public static class ParserResult<T>
	{
		public static interface IResultCombiner<T>
		{
			public void combine(T source, T other);
		}
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
		
		public void combine(ParserResult<T> other,IResultCombiner<T> combiner)
		{
			this.errors.addAll(other.errors);
			this.warnings.addAll(other.warnings);
			combiner.combine(this.result,other.result);
		}

		public String getErrorString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("\tErrors:");
			for (VDMError err : errors)
			{
				sb.append("\n\t" + err);
			}

			return sb.toString();
		}

		public String getWarningString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("\tWarnings:");
			for (VDMWarning err : warnings)
			{
				sb.append("\n\t" + err);
			}
			return sb.toString();
		}
	}
	
	
	public static ParserResult<List<SClassDefinition>> parseOo(List<File> files)
	{
		ParserResult<List<SClassDefinition>> res = null;
		for (File file : files)
		{
			if(res==null)
			{
				res = parseOo(file);
			}else
			{
				res.combine(parseOo(file), new ParserResult.IResultCombiner<List<SClassDefinition>>()
				{

					@Override
					public void combine(List<SClassDefinition> source,
							List<SClassDefinition> other)
					{
						source.addAll(other);
					}
				});
			}
		}
		
		return res;
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

	public static ParserResult<List<SClassDefinition>> parseOo(String content)
	{
		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		ClassReader reader = null;
		List<SClassDefinition> result = null;

		reader = new ClassReader(ltr);
		result = reader.readClasses();

		return new ParserResult<List<SClassDefinition>>(result, reader.getWarnings(), reader.getErrors());
	}
	
	public static ParserResult<List<AModuleModules>> parseSl(List<File> files)
	{
		ParserResult<List<AModuleModules>> res = null;
		for (File file : files)
		{
			if(res==null)
			{
				res = parseSl(file);
			}else
			{
				res.combine(parseSl(file), new ParserResult.IResultCombiner<List<AModuleModules>>()
				{

					@Override
					public void combine(List<AModuleModules> source,
							List<AModuleModules> other)
					{
						source.addAll(other);
					}
				});
			}
		}
		
		return res;
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

	public static ParserResult<List<AModuleModules>> parseSl(String content)
	{
		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		ModuleReader reader = null;
		List<AModuleModules> result = null;

		reader = new ModuleReader(ltr);
		result = reader.readModules();

		return new ParserResult<List<AModuleModules>>(result, reader.getWarnings(), reader.getErrors());
	}

	public static ParserResult<PExp> parseExpression(String content)
			throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		PExp result = null;
		ExpressionReader reader = new ExpressionReader(ltr);
		// reader.setCurrentModule(getDefaultName());
		result = reader.readExpression();

		return new ParserResult<PExp>(result, reader.getWarnings(), reader.getErrors());
	}

}
