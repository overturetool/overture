package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.type.ABracketTypeAssistantTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.AFunctionTypeAssistantTC;
import org.overture.typechecker.assistant.type.ANamedInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOperationTypeAssistantTC;
import org.overture.typechecker.assistant.type.AOptionalTypeAssistantTC;
import org.overture.typechecker.assistant.type.AParameterTypeAssistantTC;
import org.overture.typechecker.assistant.type.AProductTypeAssistantTC;
import org.overture.typechecker.assistant.type.ARecordInvariantTypeAssistantTC;
import org.overture.typechecker.assistant.type.ASetTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnionTypeAssistantTC;
import org.overture.typechecker.assistant.type.AUnresolvedTypeAssistantTC;
import org.overture.typechecker.assistant.type.SMapTypeAssistantTC;
import org.overture.typechecker.assistant.type.SSeqTypeAssistantTC;

public class PTypeResolver extends QuestionAnswerAdaptor<PTypeResolver.Newquestion, PType>
{
	public static class Newquestion
	{
		ATypeDefinition root;
		IQuestionAnswer<TypeCheckInfo, PType> rootVisitor;
		TypeCheckInfo question;
		
		public Newquestion(ATypeDefinition root, 
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
		{
			this.question = question;
			this.root = root;
			this.rootVisitor = rootVisitor;
		}
		
		
	}
	private static final long serialVersionUID = 1L;

	protected ITypeCheckerAssistantFactory af;

	public PTypeResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseABracketType(ABracketType type, Newquestion question)
			throws AnalysisException
	{
		return ABracketTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAClassType(AClassType type, Newquestion question)
			throws AnalysisException
	{
		return AClassTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAFunctionType(AFunctionType type, Newquestion question)
			throws AnalysisException
	{
		
		return AFunctionTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	@Override
	public PType caseANamedInvariantType(ANamedInvariantType type,
			Newquestion question) throws AnalysisException
	{
		
		return ANamedInvariantTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	@Override
	public PType caseARecordInvariantType(ARecordInvariantType type,
			Newquestion question) throws AnalysisException
	{
		return ARecordInvariantTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	@Override
	public PType defaultSInvariantType(SInvariantType type, Newquestion question)
			throws AnalysisException
	{
		type.setResolved(true);
		return type;
	}
	@Override
	public PType defaultSMapType(SMapType type, Newquestion question)
			throws AnalysisException
	{
		return SMapTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAOperationType(AOperationType type, Newquestion question)
			throws AnalysisException
	{
		return AOperationTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAOptionalType(AOptionalType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return AOptionalTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAParameterType(AParameterType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return AParameterTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAProductType(AProductType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return AProductTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType defaultSSeqType(SSeqType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return SSeqTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseASetType(ASetType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return ASetTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAUnionType(AUnionType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return AUnionTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType caseAUnresolvedType(AUnresolvedType type, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return AUnresolvedTypeAssistantTC.typeResolve(type, question.root, question.rootVisitor, question.question);
	}
	
	@Override
	public PType defaultPType(PType type, Newquestion question)
			throws AnalysisException
	{
		type.setResolved(true);
		return type;
	}
//	PType result = null;
//	return result;
	
	@Override
	public PType createNewReturnValue(INode node, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, Newquestion question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
