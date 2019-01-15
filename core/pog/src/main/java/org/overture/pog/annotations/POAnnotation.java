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

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;

public interface POAnnotation
{
	public IProofObligationList poBefore(PDefinition node, IPOContextStack question);
	public IProofObligationList poBefore(PExp node, IPOContextStack question);
	public IProofObligationList poBefore(PStm node, IPOContextStack question);
	public IProofObligationList poBefore(AModuleModules node, IPOContextStack question);
	public IProofObligationList poBefore(SClassDefinition node, IPOContextStack question);

	public void poAfter(PDefinition node, IProofObligationList list, IPOContextStack question);
	public void poAfter(PExp node, IProofObligationList list, IPOContextStack question);
	public void poAfter(PStm node, IProofObligationList list, IPOContextStack question);
	public void poAfter(AModuleModules node, IProofObligationList list, IPOContextStack question);
	public void poAfter(SClassDefinition node, IProofObligationList list, IPOContextStack question);
}
