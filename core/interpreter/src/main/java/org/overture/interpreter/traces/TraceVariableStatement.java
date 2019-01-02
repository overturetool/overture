/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter.traces;

import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.PStmBase;
import org.overture.ast.typechecker.NameScope;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.VoidValue;
import org.overture.typechecker.FlatEnvironment;

public class TraceVariableStatement extends PStmBase
{
	private static final long serialVersionUID = 1L;
	public final TraceVariable var;

	@SuppressWarnings("deprecation")
	public TraceVariableStatement(TraceVariable var)
	{
		super(var.name.getLocation(), null);
		this.var = var;
	}

	public void typeCheck(FlatEnvironment env, NameScope scope)
	{
		env.add(AstFactoryTC.newALocalDefinition(var.name.getLocation(), var.name, scope, var.type));
	}

	public static Value eval(TraceVariableStatement stmt, Context ctxt)
	{
		stmt.getLocation().hit();
		Value val = stmt.var.value;

		if (stmt.var.clone)
		{
			val = (Value) stmt.var.value.clone(); // To allow updates to objects
		}

		ctxt.put(stmt.var.name, val);
		return new VoidValue();
	}

	// @Override
	public String toString()
	{
		return var.toString();
	}

	@Override
	public PStm clone()
	{
		return null;
	}

	@Override
	public PStm clone(Map<INode, INode> oldToNewMap)
	{
		return null;
	}

	@Override
	public void apply(IAnalysis analysis) throws AnalysisException
	{

	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws AnalysisException
	{
		return null;
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
			throws AnalysisException
	{

	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
			throws AnalysisException
	{
		return null;
	}

}
