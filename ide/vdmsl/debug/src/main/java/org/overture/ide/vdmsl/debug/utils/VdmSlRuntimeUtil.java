package org.overture.ide.vdmsl.debug.utils;

import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.modules.ModuleList;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.Environment;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.visitor.TypeCheckVisitor;

public class VdmSlRuntimeUtil
{
	protected final static String STATIC_CALL_SEPERATOR = "`";

	public static boolean typeCheck(ModuleList modules, String expression)
			throws VDMErrorsException, ParserException, LexException
	{
		//modules.combineDefaults();
		
		String defaultModuleName = null;
		if (expression.contains(STATIC_CALL_SEPERATOR))
		{
			defaultModuleName = expression.substring(0, expression.indexOf(STATIC_CALL_SEPERATOR)); // needed for static
																									// fn/op check
		}

		AModuleModules defaultModule = null;

		if (defaultModuleName == null || (modules.isEmpty()))
		{
			defaultModule = new AModuleModules();
		} else
		{
			for (AModuleModules m : modules)
			{
				if (m.getName().getName().equals(defaultModuleName))
				{
					defaultModule = m;
				}
			}
		}

		TypeCheckVisitor tc = new TypeCheckVisitor();
		TypeChecker.clearErrors();
		if(defaultModule==null)
		{
			return false;//FIXME throw approiate error
		}
		Environment env = new ModuleEnvironment(defaultModule);
		PExp expr;

		expr = parseExpression(expression, defaultModule.getName().getName(), defaultModule.getName().getName());

		try
		{
			expr.apply(tc, new TypeCheckInfo(env, NameScope.NAMESANDSTATE));
		} catch (Throwable e)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());//FIXME: this might not has any errors if it goes wrong
		}

		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
			// return false;
		}

		return true;
	}

	private static PExp parseExpression(String expression, String name,
			String defaultModuleName) throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(expression, Dialect.VDM_SL, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(defaultModuleName);
		return reader.readExpression();
	}
}
