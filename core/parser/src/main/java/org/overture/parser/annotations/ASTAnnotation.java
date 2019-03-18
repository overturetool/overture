/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.parser.annotations;

import java.util.List;

import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.ParserException;
import org.overture.parser.syntax.StatementReader;

public interface ASTAnnotation
{
	public void setAST(PAnnotation ast);
	public List<PExp> parse(LexTokenReader ltr) throws LexException, ParserException;

	public void astBefore(DefinitionReader reader);
	public void astBefore(StatementReader reader);
	public void astBefore(ExpressionReader reader);
	public void astBefore(ModuleReader reader);
	public void astBefore(ClassReader reader);

	public void astAfter(DefinitionReader reader, PDefinition def);
	public void astAfter(StatementReader reader, PStm stmt);
	public void astAfter(ExpressionReader reader, PExp exp);
	public void astAfter(ModuleReader reader, AModuleModules module);
	public void astAfter(ClassReader reader, SClassDefinition module);
}
