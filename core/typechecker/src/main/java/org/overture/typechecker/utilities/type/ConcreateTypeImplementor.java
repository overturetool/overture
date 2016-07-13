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
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
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
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Implement the concreate type of a given type.
 * 
 * @author kel
 */
public class ConcreateTypeImplementor extends
		QuestionAnswerAdaptor<ConcreateTypeImplementor.Newquestion, PType>
{
	public static class Newquestion
	{
		ILexNameToken pname;
		PType actualType;

		public Newquestion(ILexNameToken pname, PType actualType)
		{
			this.actualType = actualType;
			this.pname = pname;
		}
	}

	protected ITypeCheckerAssistantFactory af;

	public ConcreateTypeImplementor(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseAParameterType(AParameterType type, Newquestion question)
			throws AnalysisException
	{
		return type.getName().equals(question.pname) ? question.actualType
				: type;
	}

	@Override
	public PType caseAFunctionType(AFunctionType type, Newquestion question)
			throws AnalysisException
	{
		// return AFunctionTypeAssistantTC.polymorph(type, question.pname, question.actualType);
		List<PType> polyparams = new Vector<PType>();

		for (PType ptype : type.getParameters())
		{
			polyparams.add(af.createPTypeAssistant().polymorph(ptype, question.pname, question.actualType));
		}

		// PType polyresult = PTypeAssistantTC.polymorph(type.getResult(), question.pname, question.actualType);
		PType polyresult = type.getResult().apply(this, question);
		AFunctionType ftype = AstFactory.newAFunctionType(type.getLocation(), false, polyparams, polyresult);
		ftype.setDefinitions(type.getDefinitions());
		ftype.setInstantiated(true);
		return ftype;
	}

	@Override
	public PType defaultSMapType(SMapType type, Newquestion question)
			throws AnalysisException
	{
		return AstFactory.newAMapMapType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getFrom(), question.pname, question.actualType), af.createPTypeAssistant().polymorph(type.getTo(), question.pname, question.actualType));
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, Newquestion question)
			throws AnalysisException
	{
		return AstFactory.newAOptionalType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getType(), question.pname, question.actualType));
	}

	@Override
	public PType caseAProductType(AProductType type, Newquestion question)
			throws AnalysisException
	{
		List<PType> polytypes = new Vector<PType>();

		for (PType ptype : ((AProductType) type).getTypes())
		{
			polytypes.add(af.createPTypeAssistant().polymorph(ptype, question.pname, question.actualType));
		}

		return AstFactory.newAProductType(type.getLocation(), polytypes);
	}

	@Override
	public PType defaultSSeqType(SSeqType type, Newquestion question)
			throws AnalysisException
	{
		return AstFactory.newASeqSeqType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getSeqof(), question.pname, question.actualType));
	}

	@Override
	public PType caseASetSetType(ASetSetType type, Newquestion question)
			throws AnalysisException
	{
		return AstFactory.newASetSetType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getSetof(), question.pname, question.actualType));
	}

	@Override
	public PType caseASet1SetType(ASet1SetType type, Newquestion question)
			throws AnalysisException
	{
		return AstFactory.newASet1SetType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getSetof(), question.pname, question.actualType));
	}

	@Override
	public PType caseAUnionType(AUnionType type, Newquestion question)
			throws AnalysisException
	{
		// return AUnionTypeAssistantTC.polymorph(type, question.pname, question.actualType);
		PTypeSet polytypes = new PTypeSet(af);

		for (PType ptype : ((AUnionType) type).getTypes())
		{
			polytypes.add(af.createPTypeAssistant().polymorph(ptype, question.pname, question.actualType));
		}

		// TODO: Types in unionType should be a SET
		PTypeList result = new PTypeList();
		result.addAll(polytypes);

		return AstFactory.newAUnionType(type.getLocation(), result);
	}

	@Override
	public PType defaultPType(PType type, Newquestion question)
			throws AnalysisException
	{
		return type;
	}

	@Override
	public PType createNewReturnValue(INode type, Newquestion question)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(Object type, Newquestion question)
			throws AnalysisException
	{
		return null;
	}

}
