package org.overture.ide.vdmpp.debug.utils;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.util.definitions.ClassList;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.Console;
import org.overture.parser.messages.VDMErrorsException;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.Environment;
import org.overture.typechecker.PublicClassEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.visitor.TypeCheckVisitor;

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
			defaultModule = AstFactory.newAClassClassDefinition(name, null, null);
			defaultModule.setUsed(true);
				
					
				//	new AClassClassDefinition(name.getLocation(),name,NameScope.CLASSNAME,true,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null, null);
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
		Environment env = new PublicClassEnvironment(classes,null);
		PExp expr;

		expr = parseExpression(expression, defaultModule.getName().getName(), defaultModule.getName().getName(),dialect);

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

	protected static PExp parseExpression(String expression, String name,
			String defaultModuleName, Dialect dialect) throws ParserException, LexException
	{
		LexTokenReader ltr = new LexTokenReader(expression, dialect, Console.charset);
		ExpressionReader reader = new ExpressionReader(ltr);
		reader.setCurrentModule(defaultModuleName);
		return reader.readExpression();
	}
}
