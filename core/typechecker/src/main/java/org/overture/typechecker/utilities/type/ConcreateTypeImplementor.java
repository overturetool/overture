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
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

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
		return ftype;
	}

	@Override
	public PType defaultSMapType(SMapType type, Newquestion question)
			throws AnalysisException
	{
<<<<<<< HEAD
		
		return AstFactory.newAMapMapType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getFrom(), question.pname, question.actualType), af.createPTypeAssistant().polymorph(type.getTo(), question.pname, question.actualType));
=======

		return AstFactory.newAMapMapType(type.getLocation(), PTypeAssistantTC.polymorph(type.getFrom(), question.pname, question.actualType), PTypeAssistantTC.polymorph(type.getTo(), question.pname, question.actualType));
>>>>>>> origin/pvj/main
	}

	@Override
	public PType caseAOptionalType(AOptionalType type, Newquestion question)
			throws AnalysisException
	{
<<<<<<< HEAD
		
		return AstFactory.newAOptionalType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getType(), question.pname, question.actualType));
=======

		return AstFactory.newAOptionalType(type.getLocation(), PTypeAssistantTC.polymorph(type.getType(), question.pname, question.actualType));
>>>>>>> origin/pvj/main
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
<<<<<<< HEAD
	
		return AstFactory.newASeqSeqType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getSeqof(), question.pname, question.actualType));
=======

		return AstFactory.newASeqSeqType(type.getLocation(), PTypeAssistantTC.polymorph(type.getSeqof(), question.pname, question.actualType));
>>>>>>> origin/pvj/main
	}

	@Override
	public PType caseASetType(ASetType type, Newquestion question)
			throws AnalysisException
	{
<<<<<<< HEAD
		//return ASetTypeAssistantTC.polymorph(type, question.pname, question.actualType);
		return AstFactory.newASetType(type.getLocation(), af.createPTypeAssistant().polymorph(type.getSetof(), question.pname, question.actualType));
=======
		// return ASetTypeAssistantTC.polymorph(type, question.pname, question.actualType);
		return AstFactory.newASetType(type.getLocation(), PTypeAssistantTC.polymorph(type.getSetof(), question.pname, question.actualType));
>>>>>>> origin/pvj/main
	}

	@Override
	public PType caseAUnionType(AUnionType type, Newquestion question)
			throws AnalysisException
	{
		// return AUnionTypeAssistantTC.polymorph(type, question.pname, question.actualType);
		PTypeSet polytypes = new PTypeSet();

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
