/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.util.modules.ModuleList;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.ModuleTypeChecker;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.visitor.TypeCheckVisitor;

public class TypeCheckerUtil
{
	private TypeCheckerUtil() {
	}

	public static class TypeCheckResult<T>
	{
		public final ParserResult<T> parserResult;
		public final List<VDMWarning> warnings;
		public final List<VDMError> errors;
		public final T result;

		public TypeCheckResult(ParserResult<T> parserResult, T result,
				List<VDMWarning> warnings, List<VDMError> errors)
		{
			this.parserResult = parserResult;
			this.result = result;
			this.warnings = warnings;
			this.errors = errors;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Parse result:\n" + parserResult);
			sb.append("\n\n\n");
			sb.append("TypeCheck result:\n");
			sb.append("\tErrors:");
			sb.append(getErrorString());
			sb.append("\tWarnings:");
			sb.append(getWarningString());
			return sb.toString();
		}

		public String getErrorString()
		{
			StringBuilder sb = new StringBuilder();

			for (VDMError err : errors)
			{
				sb.append("\n\t" + err);
			}

			return sb.toString();
		}

		public String getWarningString()
		{
			StringBuilder sb = new StringBuilder();

			for (VDMWarning err : warnings)
			{
				sb.append("\n\t" + err);
			}
			return sb.toString();
		}
	}

	public static class ExpressionTypeChecker extends TypeChecker
	{
		PExp expression;
		Environment env;
		public PType type;

		public ExpressionTypeChecker(PExp expression, Environment env)
		{
			this.expression = expression;
			this.env = env;
		}

		public ExpressionTypeChecker(PExp expression)
		{
			this(expression, new FlatEnvironment(new TypeCheckerAssistantFactory(), new Vector<PDefinition>()));// new
																												// ModuleEnvironment(""));
		}

		@Override
		public void typeCheck()
		{
			try
			{
				type = expression.apply(new TypeCheckVisitor(), new TypeCheckInfo(assistantFactory, env, NameScope.NAMESANDSTATE));
			} catch (AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(File file)
	{
		return typeCheckSl(file, null);
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(File file,
			String charset)
	{
		ParserResult<List<AModuleModules>> parserResult = ParserUtil.parseSl(file, charset);

		ModuleList modules = new ModuleList(parserResult.result);

		return typeCheck(parserResult, parserResult.result, new ModuleTypeChecker(modules));
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(
			List<File> file)
	{
		return typeCheckSl(file, null);
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(
			List<File> file, String charset)
	{
		ParserResult<List<AModuleModules>> parserResult = ParserUtil.parseSl(file, charset);

		ModuleList modules = new ModuleList(parserResult.result);
		modules.combineDefaults();
		return typeCheck(parserResult, parserResult.result, new ModuleTypeChecker(modules));
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(
			String content)
	{
		return typeCheckSl(content, null);
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(
			String content, String charset)
	{
		ParserResult<List<AModuleModules>> parserResult = ParserUtil.parseSl(content, charset);
		return typeCheck(parserResult, parserResult.result, new ModuleTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(File file)
	{
		return typeCheckPp(file, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(
			File file, String charset)
	{
		ParserResult<List<SClassDefinition>> parserResult = ParserUtil.parseOo(file, charset);
		return typeCheck(parserResult, parserResult.result, new ClassTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(
			List<File> file)
	{
		return typeCheckPp(file, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(
			List<File> file, String charset)
	{
		ParserResult<List<SClassDefinition>> parserResult = ParserUtil.parseOo(file, charset);
		return typeCheck(parserResult, parserResult.result, new ClassTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(
			String content)
	{
		return typeCheckPp(content, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(
			String content, String charset)
	{
		ParserResult<List<SClassDefinition>> parserResult = ParserUtil.parseOo(content, charset);
		return typeCheck(parserResult, parserResult.result, new ClassTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			List<File> file) throws ParserException, LexException
	{
		return typeCheckRt(file, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			List<File> file, String charset) throws ParserException,
			LexException
	{
		return typeCheckRt(ParserUtil.parseOo(file, charset));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(File file)
			throws ParserException, LexException
	{
		return typeCheckRt(file, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			File file, String charset) throws ParserException, LexException
	{
		return typeCheckRt(ParserUtil.parseOo(file, charset));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			String content) throws ParserException, LexException
	{
		return typeCheckRt(content, null);
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			String content, String charset) throws ParserException,
			LexException
	{
		return typeCheckRt(ParserUtil.parseOo(content, charset));
	}

	protected static TypeCheckResult<List<SClassDefinition>> typeCheckRt(
			ParserResult<List<SClassDefinition>> parserResult)
			throws ParserException, LexException
	{
		final ITypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();
		List<SClassDefinition> classes = new Vector<SClassDefinition>();
		classes.addAll(parserResult.result);
		classes.add(AstFactoryTC.newACpuClassDefinition(af));
		classes.add(AstFactoryTC.newABusClassDefinition(af));
		return typeCheck(parserResult, classes, new ClassTypeChecker(classes, af));
	}

	public static TypeCheckResult<PExp> typeCheckExpression(String content)
			throws ParserException, LexException
	{
		ParserResult<PExp> parserResult = ParserUtil.parseExpression(content);
		return typeCheck(parserResult, parserResult.result, new ExpressionTypeChecker(parserResult.result));
	}

	public static <P> TypeCheckResult<P> typeCheck(
			ParserResult<P> parserResult, P tcList, TypeChecker tc)
	{
		if (parserResult.errors.isEmpty())
		{
			TypeChecker.clearErrors();
			tc.typeCheck();
			return new TypeCheckResult<P>(parserResult, parserResult.result, TypeChecker.getWarnings(), TypeChecker.getErrors());
		}
		return new TypeCheckResult<P>(parserResult, null, new Vector<VDMWarning>(), new Vector<VDMError>());
	}

}
