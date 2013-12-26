package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
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
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class TypeVisitorCG extends AbstractVisitorCG<OoAstInfo, PTypeCG>
{
	@Override
	public PTypeCG caseAUnknownType(AUnknownType node, OoAstInfo question)
			throws AnalysisException
	{
		return new AObjectTypeCG(); // '?' Indicates an unknown type
	}
	
	@Override
	public PTypeCG caseASetType(ASetType node, OoAstInfo question)
			throws AnalysisException
	{
		PType setOf = node.getSetof();
		PTypeCG typeCg = setOf.apply(question.getTypeVisitor(), question);
		
		ASetSetTypeCG setType = new ASetSetTypeCG();
		setType.setSetOf(typeCg);

		return setType;
	}
	
	@Override
	public PTypeCG caseAProductType(AProductType node, OoAstInfo question)
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
	public PTypeCG caseAParameterType(AParameterType node, OoAstInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		
		ATemplateTypeCG templateType = new ATemplateTypeCG();
		templateType.setName(name);
		
		return templateType;
	}

	@Override
	public PTypeCG caseAOptionalType(AOptionalType node, OoAstInfo question)
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
			OoAstInfo question) throws AnalysisException
	{
		PType type = node.getType();

		if (type instanceof AUnionType)
		{
			AUnionType unionType = (AUnionType) type;

			if (TypeAssistantCG.isUnionOfQuotes(unionType))
				return new AIntNumericBasicTypeCG();
		}

		return null; // Currently the code generator only supports the union of quotes case
	}

	@Override
	public PTypeCG caseAQuoteType(AQuoteType node, OoAstInfo question)
			throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseARecordInvariantType(ARecordInvariantType node,
			OoAstInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();
		
		ARecordTypeCG recordType = new ARecordTypeCG();
		
		//TODO: Could consider doing this using a visitor at some point..
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setName(name.getName());
		typeName.setDefiningClass(name.getModule());

		recordType.setName(typeName);
		
		return recordType;
	}

	@Override
	public PTypeCG caseASeqSeqType(ASeqSeqType node, OoAstInfo question)
			throws AnalysisException
	{
		return TypeAssistantCG.constructSeqType(node, question);
	}
	
	@Override
	public PTypeCG caseASeq1SeqType(ASeq1SeqType node, OoAstInfo question)
			throws AnalysisException
	{
		return TypeAssistantCG.constructSeqType(node, question);
	}

	@Override
	public PTypeCG caseAOperationType(AOperationType node, OoAstInfo question)
			throws AnalysisException
	{
		return node.getResult().apply(question.getTypeVisitor(), question);
	}

	@Override
	public PTypeCG caseAFunctionType(AFunctionType node, OoAstInfo question)
			throws AnalysisException
	{
		return node.getResult().apply(question.getTypeVisitor(), question);
	}
	
	@Override
	public PTypeCG caseAClassType(AClassType node, OoAstInfo question)
			throws AnalysisException
	{
		String typeName = node.getClassdef().getName().getName();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(typeName);

		return classType;
	}

	@Override
	public PTypeCG caseAVoidType(AVoidType node, OoAstInfo question)
			throws AnalysisException
	{
		return new AVoidTypeCG();
	}

	@Override
	public PTypeCG caseAIntNumericBasicType(AIntNumericBasicType node,
			OoAstInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			OoAstInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseANatNumericBasicType(ANatNumericBasicType node,
			OoAstInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseARealNumericBasicType(ARealNumericBasicType node,
			OoAstInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}

	@Override
	public PTypeCG caseACharBasicType(ACharBasicType node, OoAstInfo question)
			throws AnalysisException
	{
		return new ACharBasicTypeCG();
	}

	@Override
	public PTypeCG caseABooleanBasicType(ABooleanBasicType node,
			OoAstInfo question) throws AnalysisException
	{
		return new ABoolBasicTypeCG();
	}
}
