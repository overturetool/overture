package org.overture.typechecker.util;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactoryParser;
import org.overture.ast.modules.AModuleModules;
import org.overture.parser.lex.LexException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.util.ParserUtil;
import org.overture.parser.util.ParserUtil.ParserResult;
import org.overture.typechecker.ClassTypeChecker;
import org.overture.typechecker.ModuleTypeChecker;
import org.overture.typechecker.TypeChecker;

public class TypeCheckerUtil
{
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
	}

	public static TypeCheckResult<List<AModuleModules>> typeCheckSl(File file)
	{
		ParserResult<List<AModuleModules>> parserResult = ParserUtil.parseSl(file);
		return typeCheck(parserResult, parserResult.result, new ModuleTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckPp(File file)
	{
		ParserResult<List<SClassDefinition>> parserResult = ParserUtil.parseOo(file);
		return typeCheck(parserResult, parserResult.result, new ClassTypeChecker(parserResult.result));
	}

	public static TypeCheckResult<List<SClassDefinition>> typeCheckRt(File file)
			throws ParserException, LexException
	{
		ParserResult<List<SClassDefinition>> parserResult = ParserUtil.parseOo(file);
		List<SClassDefinition> classes = new Vector<SClassDefinition>();
		classes.addAll(parserResult.result);
		classes.add(AstFactoryParser.newACpuClassDefinition());
		classes.add(AstFactoryParser.newABusClassDefinition());
		return typeCheck(parserResult, classes, new ClassTypeChecker(classes));
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
