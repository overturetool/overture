/*
 * #%~
 * The VDM parser
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.parser.util;

import java.io.File;
import java.util.List;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
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

		public void combine(ParserResult<T> other, IResultCombiner<T> combiner)
		{
			this.errors.addAll(other.errors);
			this.warnings.addAll(other.warnings);
			combiner.combine(this.result, other.result);
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
		return parseOo(files, null);
	}

	public static ParserResult<List<SClassDefinition>> parseOo(
			List<File> files, String charset)
	{
		ParserResult<List<SClassDefinition>> res = null;
		for (File file : files)
		{
			if (res == null)
			{
				res = parseOo(file, charset);
			} else
			{
				res.combine(parseOo(file, charset), new ParserResult.IResultCombiner<List<SClassDefinition>>()
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
		return parseOo(file, null);
	}

	public static ParserResult<List<SClassDefinition>> parseOo(File file,
			String charset)
	{
		LexTokenReader ltr = getReader(file, Settings.dialect, charset);
		ClassReader reader;
		List<SClassDefinition> result;

		reader = new ClassReader(ltr);
		result = reader.readClasses();

		return new ParserResult<List<SClassDefinition>>(result, reader.getWarnings(), reader.getErrors());
	}

	private static LexTokenReader getReader(File file, Dialect dialect,
			String charset)
	{
		if (charset == null)
			return new LexTokenReader(file, Settings.dialect);
		else
			return new LexTokenReader(file, Settings.dialect, charset);
	}

	private static LexTokenReader getReader(String content, Dialect dialect,
			String charset)
	{
		if (charset == null)
			return new LexTokenReader(content, Settings.dialect);
		else
			return new LexTokenReader(content, Settings.dialect, charset);
	}

	public static ParserResult<List<SClassDefinition>> parseOo(String content)
	{
		return parseOo(content, null);
	}

	public static ParserResult<List<SClassDefinition>> parseOo(String content,
			String charset)
	{
		LexTokenReader ltr = getReader(content, Settings.dialect, charset);
		ClassReader reader;
		List<SClassDefinition> result;

		reader = new ClassReader(ltr);
		result = reader.readClasses();

		return new ParserResult<List<SClassDefinition>>(result, reader.getWarnings(), reader.getErrors());
	}

	public static ParserResult<List<AModuleModules>> parseSl(List<File> files)
	{
		return parseSl(files, null);
	}

	public static ParserResult<List<AModuleModules>> parseSl(List<File> files,
			String charset)
	{
		ParserResult<List<AModuleModules>> res = null;
		for (File file : files)
		{
			if (res == null)
			{
				res = parseSl(file);
			} else
			{
				res.combine(parseSl(file, charset), new ParserResult.IResultCombiner<List<AModuleModules>>()
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
		return parseSl(file, null);
	}

	public static ParserResult<List<AModuleModules>> parseSl(File file,
			String charset)
	{
		LexTokenReader ltr = getReader(file, Settings.dialect, charset);
		ModuleReader reader;
		List<AModuleModules> result;

		reader = new ModuleReader(ltr);
		result = reader.readModules();

		return new ParserResult<List<AModuleModules>>(result, reader.getWarnings(), reader.getErrors());
	}

	public static ParserResult<List<AModuleModules>> parseSl(String content)
	{
		return parseSl(content, null);
	}

	public static ParserResult<List<AModuleModules>> parseSl(String content,
			String charset)
	{
		LexTokenReader ltr = getReader(content, Settings.dialect, charset);
		ModuleReader reader;
		List<AModuleModules> result;

		reader = new ModuleReader(ltr);
		result = reader.readModules();

		return new ParserResult<List<AModuleModules>>(result, reader.getWarnings(), reader.getErrors());
	}

	public static ParserResult<PExp> parseExpression(String content)
			throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(content, Settings.dialect);
		PExp result;
		ExpressionReader reader = new ExpressionReader(ltr);
		// reader.setCurrentModule(getDefaultName());
		result = reader.readExpression();

		return new ParserResult<PExp>(result, reader.getWarnings(), reader.getErrors());
	}

}
