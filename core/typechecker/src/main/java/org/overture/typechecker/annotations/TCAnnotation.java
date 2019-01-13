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

import org.overture.ast.node.INode;
import org.overture.typechecker.TypeCheckInfo;

public interface TCAnnotation
{
	public void tcBefore(INode node, TypeCheckInfo question);
	public void tcAfter(INode node, TypeCheckInfo question);
}
