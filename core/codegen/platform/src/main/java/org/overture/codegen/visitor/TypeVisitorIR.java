/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
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
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRNamedTypeInvariantTag;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeVisitorIR extends AbstractVisitorIR<IRInfo, STypeIR>
{
	@Override
	public STypeIR caseAUnresolvedType(AUnresolvedType node, IRInfo question)
			throws AnalysisException
	{
		log.error("Found unresolved type in the VDM AST");
		//To guard against unresolved type in the type checker
		return new AUnknownTypeIR();
	}
	
	@Override
	public STypeIR caseAUnionType(AUnionType node, IRInfo question)
			throws AnalysisException
	{
		List<PType> types = node.getTypes();
		
		question.getTypeAssistant().removeIllegalQuoteTypes(types);
		
		PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();

		if (question.getTypeAssistant().isUnionOfType(node, SSetType.class, typeAssistant))
		{
			SSetType setType = typeAssistant.getSet(node);
			
			return setType.apply(question.getTypeVisitor(), question);

		} else if (question.getTypeAssistant().isUnionOfType(node, SSeqType.class, typeAssistant))
		{
			SSeqType seqType = typeAssistant.getSeq(node);
			
			return seqType.apply(question.getTypeVisitor(), question);

		} else if (question.getTypeAssistant().isUnionOfType(node, SMapType.class, typeAssistant))
		{
			SMapType mapType = typeAssistant.getMap(node);
			
			return mapType.apply(question.getTypeVisitor(), question);
		} else if(question.getTypeAssistant().isProductOfSameSize(node, typeAssistant)) 
		{
			AProductType productType = typeAssistant.getProduct(node);
			
			return productType.apply(question.getTypeVisitor(), question);
		} else
		{
			if(types.size() <= 1)
			{
				return types.get(0).apply(question.getTypeVisitor(), question);
			}
			else
			{
				AUnionTypeIR unionTypeCg = new AUnionTypeIR();

				for (PType type : types)
				{
					STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
					
					if(typeCg != null)
					{
						unionTypeCg.getTypes().add(typeCg);	
					}
					else
					{
						return null;
					}
				}

				return unionTypeCg;
			}
		}
	}
	
	@Override
	public STypeIR caseABracketType(ABracketType node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		return type.apply(question.getTypeVisitor(), question);
	}

	@Override
	public STypeIR caseAUnknownType(AUnknownType node, IRInfo question)
			throws AnalysisException
	{
		return new AUnknownTypeIR(); // '?' Indicates an unknown type
	}

	@Override
	public STypeIR caseATokenBasicType(ATokenBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ATokenBasicTypeIR();
	}

	@Override
	public STypeIR defaultSSetType(SSetType node, IRInfo question)
			throws AnalysisException
	{
		PType setOf = node.getSetof();
		STypeIR typeCg = setOf.apply(question.getTypeVisitor(), question);
		boolean empty = node.getEmpty();

		ASetSetTypeIR setType = new ASetSetTypeIR();
		setType.setSetOf(typeCg);
		setType.setEmpty(empty);

		return setType;
	}

	@Override
	public STypeIR caseAMapMapType(AMapMapType node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleMapType(node, question, false);
	}

	@Override
	public STypeIR caseAInMapMapType(AInMapMapType node, IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleMapType(node, question, true);
	}
	
	@Override
	public STypeIR caseAProductType(AProductType node, IRInfo question)
			throws AnalysisException
	{
		ATupleTypeIR tuple = new ATupleTypeIR();

		LinkedList<PType> types = node.getTypes();

		for (PType type : types)
		{
			STypeIR typeCg = type.apply(question.getTypeVisitor(), question);
			
			if (typeCg != null)
			{
				tuple.getTypes().add(typeCg);
			}
			else
			{
				return null;
			}
		}

		return tuple;
	}

	@Override
	public STypeIR caseAParameterType(AParameterType node, IRInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();

		ATemplateTypeIR templateType = new ATemplateTypeIR();
		templateType.setName(name);

		return templateType;
	}

	@Override
	public STypeIR caseAOptionalType(AOptionalType node, IRInfo question)
			throws AnalysisException
	{
		STypeIR typeCg = node.getType().apply(question.getTypeVisitor(), question);
		
		if(typeCg != null)
		{
			typeCg.setOptional(true);
		}
		
		return typeCg;
	}

	@Override
	public STypeIR caseANamedInvariantType(ANamedInvariantType node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		STypeIR underlyingType  = type.apply(question.getTypeVisitor(), question);
		
		// TODO: Morten initially requested some way of knowing whether a type originates
		// from a named invariant type. With the NamedInvTypeInfo being introduced, using
		// IR tags for this is redundant. Check if the IR tagging can be removed.
		underlyingType.setTag(new IRNamedTypeInvariantTag(node.getName().getName()));
		
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(node.getName().getModule());
		typeName.setName(node.getName().getName());
		
		ANamedTypeDeclIR typeDecl = new ANamedTypeDeclIR();
		typeDecl.setName(typeName);

		if(underlyingType != null)
		{
			typeDecl.setType(underlyingType.clone());
		}
		
		underlyingType.setNamedInvType(typeDecl);
		
		return underlyingType;
	}

	@Override
	public STypeIR caseAQuoteType(AQuoteType node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();

		AQuoteTypeIR quoteTypeCg = new AQuoteTypeIR();
		quoteTypeCg.setValue(value);

		question.registerQuoteValue(value);
		
		return quoteTypeCg;
	}

	@Override
	public STypeIR caseARecordInvariantType(ARecordInvariantType node,
			IRInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();

		ARecordTypeIR recordType = new ARecordTypeIR();

		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setName(name.getName());
		typeName.setDefiningClass(name.getModule());

		recordType.setName(typeName);

		return recordType;
	}

	@Override
	public STypeIR caseASeqSeqType(ASeqSeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}

	@Override
	public STypeIR caseASeq1SeqType(ASeq1SeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}

	@Override
	public STypeIR caseAOperationType(AOperationType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}

	@Override
	public STypeIR caseAFunctionType(AFunctionType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}

	@Override
	public STypeIR caseAClassType(AClassType node, IRInfo question)
			throws AnalysisException
	{
		String typeName = node.getClassdef().getName().getName();

		AClassTypeIR classType = new AClassTypeIR();
		classType.setName(typeName);

		return classType;
	}

	@Override
	public STypeIR caseAVoidType(AVoidType node, IRInfo question)
			throws AnalysisException
	{
		return new AVoidTypeIR();
	}

	@Override
	public STypeIR caseAIntNumericBasicType(AIntNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeIR();
	}

	@Override
	public STypeIR caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ANat1NumericBasicTypeIR();
	}

	@Override
	public STypeIR caseANatNumericBasicType(ANatNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ANatNumericBasicTypeIR();
	}

	@Override
	public STypeIR caseARealNumericBasicType(ARealNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeIR();
	}

	@Override
	public STypeIR caseARationalNumericBasicType(
			ARationalNumericBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ARatNumericBasicTypeIR();
	}

	@Override
	public STypeIR caseACharBasicType(ACharBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ACharBasicTypeIR();
	}

	@Override
	public STypeIR caseABooleanBasicType(ABooleanBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ABoolBasicTypeIR();
	}
}
