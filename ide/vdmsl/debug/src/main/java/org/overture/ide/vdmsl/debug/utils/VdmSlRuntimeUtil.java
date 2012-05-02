package org.overture.ide.vdmsl.debug.utils;

import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.typecheck.Environment;
import org.overture.typecheck.ModuleEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.util.modules.ModuleList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.NameScope;

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
				if (m.getName().name.equals(defaultModuleName))
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

		expr = parseExpression(expression, defaultModule.getName().name, defaultModule.getName().name);

		expr.apply(tc, new TypeCheckInfo(env, NameScope.NAMESANDSTATE));

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
