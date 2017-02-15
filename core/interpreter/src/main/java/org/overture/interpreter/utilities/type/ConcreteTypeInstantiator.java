/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2017 Overture
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
package org.overture.interpreter.utilities.type;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ASet1SetType;
import org.overture.ast.types.ASetSetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.VdmRuntimeError;
import org.overture.interpreter.values.ParameterValue;
import org.overture.interpreter.values.Value;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Implement the concrete type of a given type, given a Context with @T parameter definitions.
 * 
 * @author ncb
 */
public class ConcreteTypeInstantiator extends QuestionAnswerAdaptor<Context, PType>
{
	protected ITypeCheckerAssistantFactory af;

	public ConcreteTypeInstantiator(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseAParameterType(AParameterType type, Context ctxt) throws AnalysisException
	{
		Value t = ctxt.lookup(type.getName());

		if (t == null)
		{
			VdmRuntimeError.abort(type.getLocation(), 4008,
				"No such type parameter @" + type.getName() + " in scope", ctxt);
		}
		else if (t instanceof ParameterValue)
		{
			ParameterValue tv = (ParameterValue)t;
			return tv.type;
		}
		else
		{
			VdmRuntimeError.abort(type.getLocation(), 4009,
				"Type parameter/local variable name clash, @" + type.getName(), ctxt);
		}
		
		return null;
	}

	@Override
	public PType caseAFunctionType(AFunctionType type, Context ctxt) throws AnalysisException
	{
		type = AstFactory.newAFunctionType(type.getLocation(), type.getPartial(),
			instantiate(type.getParameters(), ctxt), type.getResult().apply(this, ctxt));
		type.setInstantiated(true);
		return type;
	}

	@Override
	public PType defaultSMapType(SMapType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newAMapMapType(type.getLocation(),
			type.getFrom().apply(this, ctxt),
			type.getTo().apply(this, ctxt));
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newAOptionalType(type.getLocation(), type.getType().apply(this, ctxt));
	}

	@Override
	public PType caseAProductType(AProductType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newAProductType(type.getLocation(), instantiate(type.getTypes(), ctxt));
	}

	@Override
	public PType defaultSSeqType(SSeqType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newASeqSeqType(type.getLocation(), type.getSeqof().apply(this, ctxt));
	}

	@Override
	public PType caseASetSetType(ASetSetType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newASetSetType(type.getLocation(), type.getSetof().apply(this, ctxt));
	}

	@Override
	public PType caseASet1SetType(ASet1SetType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newASet1SetType(type.getLocation(), type.getSetof().apply(this, ctxt));
	}

	@Override
	public PType caseAUnionType(AUnionType type, Context ctxt) throws AnalysisException
	{
		return AstFactory.newAUnionType(type.getLocation(), instantiate(type.getTypes(), ctxt));
	}

	@Override
	public PType defaultPType(PType type, Context ctxt)	throws AnalysisException
	{
		return type;
	}

	private List<PType> instantiate(LinkedList<PType> types, Context ctxt) throws AnalysisException
	{
		List<PType> instantiated = new Vector<PType>();
		
		for (PType type: types)
		{
			instantiated.add(type.apply(this, ctxt));
		}
		
		return instantiated;
	}

	@Override
	public PType createNewReturnValue(INode node, Context question)	throws AnalysisException
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, Context question) throws AnalysisException
	{
		return null;
	}
}
