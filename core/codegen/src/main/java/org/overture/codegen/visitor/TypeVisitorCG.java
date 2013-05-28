package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.AVoidType;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class TypeVisitorCG extends QuestionAnswerAdaptor<CodeGenInfo, PTypeCG>
{
	private static final long serialVersionUID = 8845855407070139031L;

	public TypeVisitorCG()
	{
	}
	
	@Override
	public PTypeCG caseAOperationType(AOperationType node, CodeGenInfo question)
			throws AnalysisException
	{
		return node.getResult().apply(question.getTypeVisitor(), question);
	}
	
	@Override
	public PTypeCG caseAClassType(AClassType node, CodeGenInfo question)
			throws AnalysisException
	{
		System.out.println();
		String typeName = node.getClassdef().getName().getName();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(typeName);
		
		return classType;
	}
	
	@Override
	public PTypeCG caseAVoidType(AVoidType node, CodeGenInfo question)
			throws AnalysisException
	{
		return new AVoidTypeCG();
	}

	@Override
	public PTypeCG caseAIntNumericBasicType(AIntNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatNumericBasicType(ANatNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseARealNumericBasicType(ARealNumericBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseACharBasicType(ACharBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new ACharBasicTypeCG();
	}
	
	@Override
	public PTypeCG caseABooleanBasicType(ABooleanBasicType node,
			CodeGenInfo question) throws AnalysisException
	{
		return new ABoolBasicTypeCG();
	}
}
