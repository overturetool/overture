/*
 * #%~
 * org.overture.ide.vdmsl.debug
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
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
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
		Environment env = new ModuleEnvironment(new TypeCheckerAssistantFactory(),  defaultModule);
		PExp expr;

		expr = parseExpression(expression, defaultModule.getName().getName(), defaultModule.getName().getName());

		try
		{
			expr.apply(tc, new TypeCheckInfo(new TypeCheckerAssistantFactory(),env, NameScope.NAMESANDSTATE));
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
