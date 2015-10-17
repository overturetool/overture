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
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AUnresolvedType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.ATemplateTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRNamedTypeInvariantTag;
import org.overture.codegen.logging.Logger;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeVisitorCG extends AbstractVisitorCG<IRInfo, STypeCG>
{
	@Override
	public STypeCG caseAUnresolvedType(AUnresolvedType node, IRInfo question)
			throws AnalysisException
	{
		Logger.getLog().printErrorln("Found unresolved type in the VDM AST");
		//To guard against unresolved type in the type checker
		return new AUnknownTypeCG();
	}
	
	@Override
	public STypeCG caseAUnionType(AUnionType node, IRInfo question)
			throws AnalysisException
	{
		List<PType> types = node.getTypes();

		PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();

		if (question.getTypeAssistant().isUnionOfType(node, ASetType.class, typeAssistant))
		{
			ASetType setType = typeAssistant.getSet(node);
			
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
				AUnionTypeCG unionTypeCg = new AUnionTypeCG();

				for (PType type : types)
				{
					STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
					
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
	public STypeCG caseABracketType(ABracketType node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		
		return type.apply(question.getTypeVisitor(), question);
	}

	@Override
	public STypeCG caseAUnknownType(AUnknownType node, IRInfo question)
			throws AnalysisException
	{
		return new AUnknownTypeCG(); // '?' Indicates an unknown type
	}

	@Override
	public STypeCG caseATokenBasicType(ATokenBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ATokenBasicTypeCG();
	}

	@Override
	public STypeCG caseASetType(ASetType node, IRInfo question)
			throws AnalysisException
	{
		PType setOf = node.getSetof();
		STypeCG typeCg = setOf.apply(question.getTypeVisitor(), question);
		boolean empty = node.getEmpty();

		ASetSetTypeCG setType = new ASetSetTypeCG();
		setType.setSetOf(typeCg);
		setType.setEmpty(empty);

		return setType;
	}

	@Override
	public STypeCG caseAMapMapType(AMapMapType node, IRInfo question)
			throws AnalysisException
	{
		return question.getExpAssistant().handleMapType(node, question, false);
	}

	@Override
	public STypeCG caseAInMapMapType(AInMapMapType node, IRInfo question) throws AnalysisException
	{
		return question.getExpAssistant().handleMapType(node, question, true);
	}
	
	@Override
	public STypeCG caseAProductType(AProductType node, IRInfo question)
			throws AnalysisException
	{
		ATupleTypeCG tuple = new ATupleTypeCG();

		LinkedList<PType> types = node.getTypes();

		for (PType type : types)
		{
			STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
			
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
	public STypeCG caseAParameterType(AParameterType node, IRInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();

		ATemplateTypeCG templateType = new ATemplateTypeCG();
		templateType.setName(name);

		return templateType;
	}

	@Override
	public STypeCG caseAOptionalType(AOptionalType node, IRInfo question)
			throws AnalysisException
	{
		STypeCG typeCg = node.getType().apply(question.getTypeVisitor(), question);
		
		if(typeCg != null)
		{
			typeCg.setOptional(true);
		}
		
		return typeCg;
	}

	@Override
	public STypeCG caseANamedInvariantType(ANamedInvariantType node,
			IRInfo question) throws AnalysisException
	{
		PType type = node.getType();
		STypeCG underlyingType  = type.apply(question.getTypeVisitor(), question);
		
		// TODO: Morten initially requested some way of knowing whether a type originates
		// from a named invariant type. With the NamedInvTypeInfo being introduced, using
		// IR tags for this is redundant. Check if the IR tagging can be removed.
		underlyingType.setTag(new IRNamedTypeInvariantTag(node.getName().getName()));
		
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(node.getName().getModule());
		typeName.setName(node.getName().getName());
		
		ANamedTypeDeclCG typeDecl = new ANamedTypeDeclCG();
		typeDecl.setName(typeName);

		if(underlyingType != null)
		{
			typeDecl.setType(underlyingType.clone());
		}
		
		underlyingType.setNamedInvType(typeDecl);
		
		return underlyingType;
	}

	@Override
	public STypeCG caseAQuoteType(AQuoteType node, IRInfo question)
			throws AnalysisException
	{
		String value = node.getValue().getValue();

		AQuoteTypeCG quoteTypeCg = new AQuoteTypeCG();
		quoteTypeCg.setValue(value);

		question.registerQuoteValue(value);
		
		return quoteTypeCg;
	}

	@Override
	public STypeCG caseARecordInvariantType(ARecordInvariantType node,
			IRInfo question) throws AnalysisException
	{
		ILexNameToken name = node.getName();

		ARecordTypeCG recordType = new ARecordTypeCG();

		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setName(name.getName());
		typeName.setDefiningClass(name.getModule());

		recordType.setName(typeName);

		return recordType;
	}

	@Override
	public STypeCG caseASeqSeqType(ASeqSeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}

	@Override
	public STypeCG caseASeq1SeqType(ASeq1SeqType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().constructSeqType(node, question);
	}

	@Override
	public STypeCG caseAOperationType(AOperationType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}

	@Override
	public STypeCG caseAFunctionType(AFunctionType node, IRInfo question)
			throws AnalysisException
	{
		return question.getTypeAssistant().consMethodType(node, node.getParameters(), node.getResult(), question);
	}

	@Override
	public STypeCG caseAClassType(AClassType node, IRInfo question)
			throws AnalysisException
	{
		String typeName = node.getClassdef().getName().getName();

		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(typeName);

		return classType;
	}

	@Override
	public STypeCG caseAVoidType(AVoidType node, IRInfo question)
			throws AnalysisException
	{
		return new AVoidTypeCG();
	}

	@Override
	public STypeCG caseAIntNumericBasicType(AIntNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new AIntNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ANat1NumericBasicTypeCG();
	}

	@Override
	public STypeCG caseANatNumericBasicType(ANatNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ANatNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseARealNumericBasicType(ARealNumericBasicType node,
			IRInfo question) throws AnalysisException
	{
		return new ARealNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseARationalNumericBasicType(
			ARationalNumericBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ARatNumericBasicTypeCG();
	}

	@Override
	public STypeCG caseACharBasicType(ACharBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ACharBasicTypeCG();
	}

	@Override
	public STypeCG caseABooleanBasicType(ABooleanBasicType node, IRInfo question)
			throws AnalysisException
	{
		return new ABoolBasicTypeCG();
	}
}
