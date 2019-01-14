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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;

public interface INAnnotation
{
	public void inBefore(PStm node, Context ctxt) throws AnalysisException;
	public void inBefore(PExp node, Context ctxt) throws AnalysisException;

	public void inAfter(PStm node, Value value, Context ctxt) throws AnalysisException;
	public void inAfter(PExp node, Value value, Context ctxt) throws AnalysisException;
}
