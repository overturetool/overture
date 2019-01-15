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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.syntax.ClassReader;
import org.overture.parser.syntax.DefinitionReader;
import org.overture.parser.syntax.ExpressionReader;
import org.overture.parser.syntax.ModuleReader;
import org.overture.parser.syntax.StatementReader;
import org.overture.parser.syntax.SyntaxReader;

public interface ASTAnnotation
{
	public void astBefore(SyntaxReader reader);

	public void astAfter(DefinitionReader reader, PDefinition def);
	public void astAfter(StatementReader reader, PStm stmt);
	public void astAfter(ExpressionReader reader, PExp exp);
	public void astAfter(ModuleReader reader, AModuleModules module);
	public void astAfter(ClassReader reader, SClassDefinition module);
}
