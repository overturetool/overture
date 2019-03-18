/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.typechecker.annotations;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.typechecker.TypeCheckInfo;

public interface TCAnnotation
{
	public void tcBefore(PDefinition node, TypeCheckInfo question);
	public void tcBefore(PExp node, TypeCheckInfo question);
	public void tcBefore(PStm node, TypeCheckInfo question);
	public void tcBefore(AModuleModules node, TypeCheckInfo question);
	public void tcBefore(SClassDefinition node, TypeCheckInfo question);

	public void tcAfter(PDefinition node, TypeCheckInfo question);
	public void tcAfter(PExp node, TypeCheckInfo question);
	public void tcAfter(PStm node, TypeCheckInfo question);
	public void tcAfter(AModuleModules node, TypeCheckInfo question);
	public void tcAfter(SClassDefinition node, TypeCheckInfo question);
}
