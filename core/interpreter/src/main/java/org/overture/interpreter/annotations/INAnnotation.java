/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.interpreter.annotations;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.runtime.Context;

public interface INAnnotation
{
	public void inBefore(PStm node, Context ctxt);
	public void inAfter(PStm node, Context ctxt);
	public void inBefore(PExp node, Context ctxt);
	public void inAfter(PExp node, Context ctxt);
}
