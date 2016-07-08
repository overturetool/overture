/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.utilities.type;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.SSetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUndefinedType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.ast.util.Utils;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class displays a string representation of the Type.
 * 
 * @author kel
 */
public class TypeDisplayer extends AnswerAdaptor<String>
{

	protected ITypeCheckerAssistantFactory af;

	public TypeDisplayer(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public String caseABooleanBasicType(ABooleanBasicType type)
			throws AnalysisException
	{
		return "bool";
	}

	@Override
	public String caseACharBasicType(ACharBasicType type)
			throws AnalysisException
	{
		return "char";
	}

	@Override
	public String caseAIntNumericBasicType(AIntNumericBasicType type)
			throws AnalysisException
	{
		return "int";
	}

	@Override
	public String caseANatNumericBasicType(ANatNumericBasicType type)
			throws AnalysisException
	{
		return "nat";
	}

	@Override
	public String caseANatOneNumericBasicType(ANatOneNumericBasicType node)
			throws AnalysisException
	{
		return "nat1";
	}

	@Override
	public String caseARationalNumericBasicType(ARationalNumericBasicType type)
			throws AnalysisException
	{
		return "rat";
	}

	@Override
	public String caseARealNumericBasicType(ARealNumericBasicType node)
			throws AnalysisException
	{
		return "real";
	}

	@Override
	public String defaultSNumericBasicType(SNumericBasicType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public String caseATokenBasicType(ATokenBasicType type)
			throws AnalysisException
	{
		return "token";
	}

	@Override
	public String defaultSBasicType(SBasicType node) throws AnalysisException
	{
		return null;
	}

	@Override
	public String caseABracketType(ABracketType type) throws AnalysisException
	{
		return "(" + type.getType() + ")";
	}

	@Override
	public String caseAClassType(AClassType type) throws AnalysisException
	{
		return type.getClassdef().getName().getName();
	}

	@Override
	public String caseAFunctionType(AFunctionType type)
			throws AnalysisException
	{
		List<PType> parameters = type.getParameters();
		String params = parameters.isEmpty() ? "()"
				: Utils.listToString(parameters, " * ");
		return "(" + params + (type.getPartial() ? " -> " : " +> ")
				+ type.getResult() + ")";
	}

	@Override
	public String caseANamedInvariantType(ANamedInvariantType type)
			throws AnalysisException
	{
		return type.getName().toString();
	}

	@Override
	public String caseARecordInvariantType(ARecordInvariantType type)
			throws AnalysisException
	{
		return type.getName().toString();
	}

	@Override
	public String defaultSInvariantType(SInvariantType type)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public String caseAInMapMapType(AInMapMapType type)
			throws AnalysisException
	{
		return "inmap of (" + type.getFrom() + ") to (" + type.getTo() + ")";
	}

	@Override
	public String caseAMapMapType(AMapMapType type) throws AnalysisException
	{
		return "map (" + type.getFrom() + ") to (" + type.getTo() + ")";
	}

	@Override
	public String defaultSMapType(SMapType type) throws AnalysisException
	{
		return null;
	}

	@Override
	public String caseAOperationType(AOperationType type)
			throws AnalysisException
	{
		List<PType> parameters = type.getParameters();
		String params = parameters.isEmpty() ? "()"
				: Utils.listToString(parameters, " * ");
		return "(" + params + " ==> " + type.getResult() + ")";
	}

	@Override
	public String caseAOptionalType(AOptionalType type)
			throws AnalysisException
	{
		return "[" + type.getType() + "]";
	}

	@Override
	public String caseAParameterType(AParameterType type)
			throws AnalysisException
	{
		return "@" + type.getName();
	}

	@Override
	public String caseAProductType(AProductType type) throws AnalysisException
	{
		return Utils.listToString("(", type.getTypes(), " * ", ")");
	}

	@Override
	public String caseAQuoteType(AQuoteType type) throws AnalysisException
	{
		return "<" + type.getValue() + ">";
	}

	@Override
	public String caseASeqSeqType(ASeqSeqType type) throws AnalysisException
	{
		return type.getEmpty() ? "[]" : "seq of (" + type.getSeqof() + ")";
	}

	@Override
	public String caseASeq1SeqType(ASeq1SeqType type) throws AnalysisException
	{
		return type.getEmpty() ? "[]" : "seq1 of (" + type.getSeqof() + ")";
	}

	@Override
	public String defaultSSeqType(SSeqType node) throws AnalysisException
	{
		return null;
	}

	@Override
	public String defaultSSetType(SSetType type) throws AnalysisException
	{
		return type.getEmpty() ? "{}" : "set of (" + type.getSetof() + ")";
	}

	@Override
	public String caseAUndefinedType(AUndefinedType type)
			throws AnalysisException
	{
		return "(undefined)";
	}

	@Override
	public String caseAUnionType(AUnionType type) throws AnalysisException
	{
		List<PType> types = type.getTypes();

		if (types.size() == 1)
		{
			return types.iterator().next().toString();
		} else
		{
			return Utils.setToString(new PTypeSet(types, af), " | ");
		}
	}

	@Override
	public String caseAUnknownType(AUnknownType type) throws AnalysisException
	{
		return "?";
	}

	@Override
	public String caseAUnresolvedType(AUnresolvedType type)
			throws AnalysisException
	{
		return "(unresolved " + type.getName().getExplicit(true) + ")";
	}

	@Override
	public String caseAVoidType(AVoidType type) throws AnalysisException
	{
		return "()";
	}

	@Override
	public String caseAVoidReturnType(AVoidReturnType type)
			throws AnalysisException
	{
		return "(return)";
	}

	@Override
	public String defaultPType(PType type) throws AnalysisException
	{
		assert false : "PType default method should not hit this case";
		return null;
	}

	@Override
	public String createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
