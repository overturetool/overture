/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.pog.annotations;

import org.overture.ast.node.INode;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;

public interface POAnnotation
{
	public IProofObligationList poBefore(INode node, IPOContextStack question);
	public void poAfter(INode node, IProofObligationList list, IPOContextStack question);
}
