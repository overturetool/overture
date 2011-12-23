package org.overture.ide.vdmpp.debug.utils;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.typecheck.Environment;
import org.overture.typecheck.PublicClassEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeChecker;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.util.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexTokenReader;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.VDMErrorsException;
import org.overturetool.vdmj.syntax.ExpressionReader;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.NameScope;

public class VdmPpRuntimeUtil
{
	public static boolean typeCheck(ClassList classes, String expression)
			throws VDMErrorsException, ParserException, LexException
	{
		return typeCheck(classes, expression,Dialect.VDM_PP);
	}
	public static boolean typeCheck(ClassList classes, String expression, Dialect dialect)
			throws VDMErrorsException, ParserException, LexException
	{
		String defaultModuleName = null;
//		if (expression.contains(STATIC_CALL_SEPERATOR))
//		{
//			defaultModuleName = expression.substring(0, expression.indexOf(STATIC_CALL_SEPERATOR)); // needed for static
//																									// fn/op check
//		}

		SClassDefinition defaultModule = null;

		if (defaultModuleName == null || (classes.isEmpty()))
		{
			LexNameToken name =new LexNameToken("CLASS", "DEFAULT", new LexLocation());
			defaultModule = new AClassClassDefinition(name.getLocation(),name,NameScope.CLASSNAME,true,null,null,null,null,null,null,null,null);
//			classes.add(defaultModule);
		} 
//		else
//		{
//			for (SClassDefinition m : classes)
//			{
//				if (m.getName().name.equals(defaultModuleName))
//				{
//					defaultModule = m;
//				}
//			}
//		}

		TypeCheckVisitor tc = new TypeCheckVisitor();
		TypeChecker.clearErrors();
		Environment env = new PublicClassEnvironment(classes);
		PExp expr;

		expr = parseExpression(expression, defaultModule.getName().name, defaultModule.getName().name,dialect);

		expr.apply(tc, new TypeCheckInfo(env, NameScope.NAMESANDSTATE));

		if (TypeChecker.getErrorCount() > 0)
		{
			throw new VDMErrorsException(TypeChecker.getErrors());
			// return false;
		}

		return true;
	}

	protected static PExp parseExpression(String expression, String name,
			String defaultModuleName, Dialect dialect) throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(expression, dialect, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(defaultModuleName);
		return reader.readExpression();
	}
}
