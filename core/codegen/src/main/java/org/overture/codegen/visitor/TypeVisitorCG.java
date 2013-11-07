package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.utils.VdmTransUtil;

public class TypeVisitorCG extends AbstractVisitorCG<CodeGenInfo, PTypeCG>
{
	private static final long serialVersionUID = 8845855407070139031L;

	public TypeVisitorCG()
	{
	}

	@Override
	public PTypeCG caseAUnknownType(AUnknownType node, CodeGenInfo question)
			throws AnalysisException
	{
		return null; // Indicates an unknown type
	}
	
	@Override
	public PTypeCG caseAProductType(AProductType node, CodeGenInfo question)
			throws AnalysisException
	{	
		ATupleTypeCG tuple = new ATupleTypeCG();
		
		LinkedList<PType> types = node.getTypes();
		
		for (PType type : types)
		{
			PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			tuple.getTypes().add(typeCg);
			
		}
		
		return tuple;
	}
	
	@Override
	public PTypeCG caseAParameterType(AParameterType node, CodeGenInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		
		ATemplateTypeCG templateType = new ATemplateTypeCG();
		templateType.setName(name);
		
		return templateType;
	}

	@Override
	public PTypeCG caseAOptionalType(AOptionalType node, CodeGenInfo question)
			throws AnalysisException
	{
		PTypeCG type = node.getType().apply(question.getTypeVisitor(), question);

		if (type instanceof AIntNumericBasicTypeCG)
			return new AIntBasicTypeWrappersTypeCG();
		else if (type instanceof ARealNumericBasicTypeCG)
			return new ARealBasicTypeWrappersTypeCG();
		else if (type instanceof ABoolBasicTypeCG)
			return new ABoolBasicTypeWrappersTypeCG();
		else if (type instanceof ACharBasicTypeCG)
			return new ACharBasicTypeWrappersTypeCG();
		
		return type;
	}

	@Override
	public PTypeCG caseANamedInvariantType(ANamedInvariantType node,
			CodeGenInfo question) throws AnalysisException
	{
		PType type = node.getType();

		if (type instanceof AUnionType)
		{
			AUnionType unionType = (AUnionType) type;

			if (VdmTransUtil.isUnionOfQuotes(unionType))
				return new AIntNumericBasicTypeCG();
		}

		return null; // Currently the code generator only supports the union of quotes case
	}

	@Override
	public PTypeCG caseAQuoteType(AQuoteType node, CodeGenInfo question)
			throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseARecordInvariantType(ARecordInvariantType node,
			CodeGenInfo question) throws AnalysisException
	{
		String typeName = node.getName().getName();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(typeName);

		return classType;
	}

	@Override
	public PTypeCG caseASeqSeqType(ASeqSeqType node, CodeGenInfo question)
			throws AnalysisException
	{
		return TypeAssistantCG.constructSeqType(node, question);
	}
	
	@Override
	public PTypeCG caseASeq1SeqType(ASeq1SeqType node, CodeGenInfo question)
			throws AnalysisException
	{
		return TypeAssistantCG.constructSeqType(node, question);
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
	public PTypeCG caseACharBasicType(ACharBasicType node, CodeGenInfo question)
			throws AnalysisException
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
