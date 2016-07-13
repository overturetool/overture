/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
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
package org.overture.ast.util.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.types.SSetType;

/**
 * Used to check if a given type is having a hash.
 * 
 * @author gkanos
 */
public class HashChecker extends AnswerAdaptor<Integer>
{
	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public HashChecker(IAstAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Integer caseABracketType(ABracketType type) throws AnalysisException
	{
		return type.getType().apply(THIS);
	}

	@Override
	public Integer caseAClassType(AClassType type) throws AnalysisException
	{
		return type.getName().apply(THIS);
	}

	@Override
	public Integer caseAFunctionType(AFunctionType type)
			throws AnalysisException
	{
		return af.createPTypeAssistant().hashCode(type.getParameters())
				+ af.createPTypeAssistant().hashCode(type.getResult());
	}

	@Override
	public Integer caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return type.getName().apply(THIS);
	}

	@Override
	public Integer caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{

		return type.getName().apply(THIS);
	}

	@Override
	public Integer defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return type.getClass().hashCode();
	}

	@Override
	public Integer defaultSMapType(SMapType type) throws AnalysisException
	{
		SMapType mtype = type;
		return mtype.getFrom().apply(THIS) + mtype.getTo().apply(THIS);

	}

	@Override
	public Integer caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		AOperationType otype = type;
		return af.createPTypeAssistant().hashCode(otype.getParameters())
				+ af.createPTypeAssistant().hashCode(otype.getResult());

	}

	@Override
	public Integer caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return type.getType().apply(THIS);

	}

	@Override
	public Integer caseAParameterType(AParameterType type)
			throws AnalysisException
	{
		return type.getName().apply(THIS);

	}

	@Override
	public Integer caseAProductType(AProductType type) throws AnalysisException
	{
		return af.createPTypeAssistant().hashCode(type.getTypes());
	}

	@Override
	public Integer caseAQuoteType(AQuoteType type) throws AnalysisException
	{
		return type.getValue().hashCode();
	}

	@Override
	public Integer defaultSSeqType(SSeqType type) throws AnalysisException
	{
		SSeqType stype = type;
		return stype.getEmpty() ? 0 : stype.getSeqof().apply(THIS); // hashCode(stype.getSeqof());
	}

	@Override
	public Integer defaultSSetType(SSetType type) throws AnalysisException
	{
		return type.getEmpty() ? 0 : type.getSetof().apply(THIS);
	}

	@Override
	public Integer caseAUnionType(AUnionType type) throws AnalysisException
	{
		return af.createPTypeAssistant().hashCode(type.getTypes());
	}

	@Override
	public Integer caseAUnresolvedType(AUnresolvedType type)
			throws AnalysisException
	{
		return type.getName().hashCode();
	}

	@Override
	public Integer defaultPType(PType type) throws AnalysisException
	{
		return type.getClass().hashCode();
	}

	@Override
	public Integer createNewReturnValue(INode type) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return type.getClass().hashCode();
	}

	@Override
	public Integer createNewReturnValue(Object type) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return type.getClass().hashCode();
	}
}
