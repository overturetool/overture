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
package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.ast.util.PTypeSet;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SObjectDesignatorIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.declarations.SClassDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.SBinaryExpIR;
import org.overture.codegen.ir.statements.AApplyObjectDesignatorIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpIR;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeAssistantIR extends AssistantBase
{
	public TypeAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void removeIllegalQuoteTypes(List<PType> types)
	{
		List<Integer> illegalIndices = new LinkedList<>();

		for (int i = 0; i < types.size(); i++)
		{
			PType t = types.get(i);

			if (t instanceof AQuoteType
					&& ((AQuoteType) t).getValue().getValue().equals(IRConstants.ILLEGAL_QUOTE_VALUE))
			{
				illegalIndices.add(i);
			}
		}

		for (int i = illegalIndices.size() - 1; i >= 0; i--)
		{
			types.remove(i);
		}
	}

	public STypeIR getFieldExpType(IRInfo info, String fieldName,
			String fieldModule, SObjectDesignatorIR obj, INode parent)
			throws AnalysisException,
			org.overture.codegen.ir.analysis.AnalysisException
	{
		if (parent instanceof AApplyObjectDesignatorIR)
		{
			AApplyObjectDesignatorIR apply = (AApplyObjectDesignatorIR) parent;
			LinkedList<SExpIR> args = apply.getArgs();

			if (fieldModule != null)
			{
				// It is a class
				SClassDeclIR clazz = info.getDeclAssistant().findClass(info.getClasses(), fieldModule);
				AFieldDeclIR field = info.getDeclAssistant().getFieldDecl(clazz, fieldModule);

				if (field != null)
				{
					return field.getType().clone();
				} else
				{
					// It must be a method
					return info.getTypeAssistant().getMethodType(info, fieldModule, fieldName, args);
				}
			}
		}
		return getFieldType(info, fieldName, fieldModule, obj);
	}

	private STypeIR getFieldType(IRInfo info, String fieldName,
			String fieldModule, SObjectDesignatorIR obj)
	{
		if (fieldModule != null)
		{
			// It is a class
			return info.getTypeAssistant().getFieldType(info.getClasses(), fieldModule, fieldName);
		} else
		{
			// It is a record
			try
			{
				ObjectDesignatorToExpIR converter = new ObjectDesignatorToExpIR(info);
				SExpIR objExp = obj.apply(converter);

				if (objExp.getType() instanceof ARecordTypeIR)
				{
					STypeIR fieldExpType = info.getTypeAssistant().getFieldType(info.getClasses(), (ARecordTypeIR) objExp.getType(), fieldName);

					if (fieldExpType == null)
					{
						log.error("Could not find field type");
					}

					return fieldExpType;
				}
			} catch (org.overture.codegen.ir.analysis.AnalysisException e)
			{
			}
		}

		log.error("Could not determine field type");

		return new AUnknownTypeIR();
	}

	public AMethodTypeIR getMethodType(IRInfo info, String fieldModule,
			String fieldName, List<SExpIR> args)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		SClassDeclIR classDecl = assistantManager.getDeclAssistant().findClass(info.getClasses(), fieldModule);
		
		if(classDecl == null)
		{
			return null;
		}

		List<AMethodDeclIR> methods = assistantManager.getDeclAssistant().getAllMethods(classDecl, info.getClasses());

		for (AMethodDeclIR method : methods)
		{
			if (method.getName().equals(fieldName))
			{
				LinkedList<STypeIR> params = method.getMethodType().getParams();

				if (assistantManager.getTypeAssistant().checkArgTypes(info, args, params))
				{
					return method.getMethodType().clone();
				}
			}
		}

		// Union type transformations may ask for the method type of a field to find out
		// that it does not exist. Consider for example the (legal) snippet below where
		// class A has an operation 'op()' and B is a completely empty class definition
		//
		// let xs = [new A(), new B()]
		// in
		// for x in xs do
		// x.op();

		// If the field does not exist then the method type does not exist
		return null;
	}

	public STypeIR getFieldType(SClassDeclIR classDecl, String fieldName,
			List<SClassDeclIR> classes)
	{
		for (AFieldDeclIR field : assistantManager.getDeclAssistant().getAllFields(classDecl, classes))
		{
			if (field.getName().equals(fieldName))
			{
				return field.getType().clone();
			}
		}

		return null;
	}

	public STypeIR getFieldType(List<SClassDeclIR> classes,
			ARecordTypeIR recordType, String memberName)
	{
		AFieldDeclIR field = assistantManager.getDeclAssistant().getFieldDecl(classes, recordType, memberName);

		if (field != null)
		{
			return field.getType().clone();
		}
		return null;
	}

	public List<STypeIR> getFieldTypes(ARecordDeclIR record)
	{
		List<STypeIR> fieldTypes = new LinkedList<STypeIR>();

		for (AFieldDeclIR field : record.getFields())
		{
			fieldTypes.add(field.getType());
		}

		return fieldTypes;
	}

	public STypeIR getFieldType(List<SClassDeclIR> classes, String moduleName,
			String fieldName)
	{
		SClassDeclIR classDecl = assistantManager.getDeclAssistant().findClass(classes, moduleName);
		return getFieldType(classDecl, fieldName, classes);
	}

	public boolean compatible(IRInfo info, STypeIR left, STypeIR right)
	{
		SourceNode leftSource = left.getSourceNode();
		SourceNode rightSource = right.getSourceNode();

		if (leftSource == null || rightSource == null)
		{
			return false;
		}

		org.overture.ast.node.INode leftType = leftSource.getVdmNode();
		org.overture.ast.node.INode rightType = rightSource.getVdmNode();

		if (!(leftType instanceof PType) || !(rightType instanceof PType))
		{
			return false;
		}

		TypeComparator typeComparator = info.getTcFactory().getTypeComparator();

		if (!typeComparator.compatible((PType) leftType, (PType) rightType))
		{
			return false;
		}

		return true;
	}

	public boolean checkArgTypes(IRInfo info, List<SExpIR> args,
			List<STypeIR> paramTypes)
			throws org.overture.codegen.ir.analysis.AnalysisException
	{
		for (int i = 0; i < paramTypes.size(); i++)
		{
			STypeIR paramType = paramTypes.get(i);
			STypeIR argType = args.get(i).getType();

			if (!compatible(info, paramType, argType))
			{
				return false;
			}
		}

		return true;
	}

	public PDefinition getTypeDef(ILexNameToken nameToken,
			PDefinitionAssistantTC defAssistant)
	{
		PDefinition def = (PDefinition) nameToken.getAncestor(PDefinition.class);

		if (def == null)
		{
			return null;
		}

		SClassDefinition enclosingClass = nameToken.getAncestor(SClassDefinition.class);

		if (enclosingClass == null)
		{
			return null;
		}

		PDefinition typeDef = defAssistant.findType(def, nameToken, enclosingClass.getName().getModule());

		return typeDef;
	}

	public STypeIR constructSeqType(SSeqTypeBase node, IRInfo question)
			throws AnalysisException
	{
		STypeIR seqOfCg = node.getSeqof().apply(question.getTypeVisitor(), question);
		boolean emptyCg = node.getEmpty();

		boolean isSeq1 = node instanceof ASeq1SeqType;

		// This is a special case since sequence of characters are strings
		if (seqOfCg instanceof ACharBasicTypeIR
				&& question.getSettings().getCharSeqAsString())
		{
			AStringTypeIR stringTypeCg = new AStringTypeIR();
			stringTypeCg.setSourceNode(new SourceNode(node));

			return stringTypeCg;
		}

		ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
		seqType.setSeqOf(seqOfCg);
		seqType.setEmpty(emptyCg);
		seqType.setSeq1(isSeq1);

		return seqType;
	}

	public boolean isBasicType(STypeIR type)
	{
		return type instanceof SBasicTypeIR;
	}

	public boolean isUnionOfNonCollectionTypes(AUnionTypeIR union)
	{
		LinkedList<STypeIR> types = union.getTypes();
		
		for(STypeIR t : types)
		{	
			if(t instanceof AUnionTypeIR)
			{
				if(!isUnionOfNonCollectionTypes((AUnionTypeIR) t))
				{
					return false;
				}
			}
			else if(!(isBasicType(t) || t instanceof AQuoteTypeIR || t instanceof ARecordTypeIR || t instanceof ATupleTypeIR))
			{
				return false;
			}
		}
		
		
		return true;
	}
	
	public STypeIR getWrapperType(SBasicTypeIR basicType)
	{

		if (basicType instanceof AIntNumericBasicTypeIR)
		{
			return new AIntBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ANat1NumericBasicTypeIR)
		{
			return new ANat1BasicTypeWrappersTypeIR();
		} else if (basicType instanceof ANatNumericBasicTypeIR)
		{
			return new ANatBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ARatNumericBasicTypeIR)
		{
			return new ARatBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ARealNumericBasicTypeIR)
		{
			return new ARealBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ACharBasicTypeIR)
		{
			return new ACharBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ABoolBasicTypeIR)
		{
			return new ABoolBasicTypeWrappersTypeIR();
		} else if (basicType instanceof ATokenBasicTypeIR)
		{
			return basicType;
		} else
		{
			return null;
		}

	}

	public AMethodTypeIR consMethodType(PType node, List<PType> paramTypes,
			PType resultType, IRInfo question) throws AnalysisException
	{
		AMethodTypeIR methodType = new AMethodTypeIR();

		methodType.setEquivalent(node.clone());

		STypeIR resultCg = resultType.apply(question.getTypeVisitor(), question);

		methodType.setResult(resultCg);

		LinkedList<STypeIR> paramsCg = methodType.getParams();
		for (PType paramType : paramTypes)
		{
			paramsCg.add(paramType.apply(question.getTypeVisitor(), question));
		}

		return methodType;
	}

	public boolean isUnionOfType(AUnionType unionType,
			Class<? extends PType> type, PTypeAssistantTC typeAssistant)
	{
		try
		{
			for (PType t : unionType.getTypes())
			{
				if (!typeAssistant.isType(t, type))
				{
					return false;
				}
			}
		} catch (Error t)// Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}

	public boolean isUnionOfType(AUnionTypeIR unionType,
								 Class<? extends STypeIR> type)
	{
		try
		{
			for (STypeIR t : unionType.getTypes())
			{
				if(t instanceof AUnionTypeIR)
				{
					if(!isUnionOfType((AUnionTypeIR) t, type))
					{
						return false;
					}
				}
				else if (!(t instanceof AQuoteTypeIR))
				{
					return false;
				}
			}
		} catch (Error t)// Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}

	public boolean isProductOfSameSize(AUnionType unionType,
			PTypeAssistantTC typeAssistant)
	{
		final int NOT_SET = -1;
		int commonSize = NOT_SET;
		try
		{
			for (PType t : unionType.getTypes())
			{
				if (!typeAssistant.isType(t, AProductType.class))
				{
					return false;
				} else
				{
					AProductType productType = typeAssistant.getProduct(t, null);
					int currentSize = productType.getTypes().size();

					if (commonSize == NOT_SET)
					{
						commonSize = currentSize;
					} else
					{
						if (commonSize != currentSize)
						{
							return false;
						}
					}
				}
			}
		} catch (Error t)// Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}

	public PType getType(IRInfo question, AUnionType unionType,
			PPattern pattern)
	{
		PTypeSet possibleTypes = new PTypeSet(question.getTcFactory());
		PType patternType = question.getTcFactory().createPPatternAssistant(null).getPossibleType(pattern);
		TypeComparator comp = question.getTcFactory().getTypeComparator();

		for (PType t : unionType.getTypes())
		{
			if (comp.compatible(patternType, t))
			{
				possibleTypes.add(t);
			}
		}

		if (possibleTypes.isEmpty())
		{
			log.error("Could not find any possible types for pattern: "
					+ pattern);
			return null;
		} else if (possibleTypes.size() == 1)
		{
			return possibleTypes.pollFirst();
		} else
		// More than one possible type
		{
			unionType.getTypes().clear();
			unionType.getTypes().addAll(possibleTypes);

			PTypeAssistantTC typeAssistant = question.getTcFactory().createPTypeAssistant();

			if (question.getTypeAssistant().isUnionOfType(unionType, AProductType.class, typeAssistant))
			{
				List<PType> fieldsTypes = new LinkedList<PType>();
				int noOfFields = ((ATuplePattern) pattern).getPlist().size();
				for (int i = 0; i < noOfFields; i++)
				{
					List<PType> currentFieldTypes = new LinkedList<PType>();

					for (PType currentPossibleType : possibleTypes)
					{
						AProductType currentProductType = (AProductType) currentPossibleType;
						currentFieldTypes.add(currentProductType.getTypes().get(i).clone());
					}

					fieldsTypes.add(AstFactory.newAUnionType(pattern.getLocation(), currentFieldTypes));
				}

				return AstFactory.newAProductType(pattern.getLocation(), fieldsTypes);
			} else
			{
				return unionType;
			}
		}
	}

	public List<STypeIR> clearObjectTypes(List<STypeIR> types)
	{
		types = new LinkedList<STypeIR>(types);

		List<AObjectTypeIR> objectTypes = new LinkedList<AObjectTypeIR>();

		for (STypeIR type : types)
		{
			if (type instanceof AObjectTypeIR)
			{
				objectTypes.add((AObjectTypeIR) type);
			}
		}

		types.removeAll(objectTypes);

		return types;
	}

	public List<STypeIR> clearDuplicates(List<STypeIR> types)
	{
		List<STypeIR> filtered = new LinkedList<STypeIR>();

		for (STypeIR type : types)
		{
			if (!containsType(filtered, type))
			{
				filtered.add(type);
			}
		}

		return filtered;
	}

	public boolean isStringType(STypeIR type)
	{
		return type instanceof AStringTypeIR;
	}

	public boolean isStringType(SExpIR exp)
	{
		return exp.getType() instanceof AStringTypeIR;
	}

	public boolean isMapType(SExpIR exp)
	{
		return exp.getType() instanceof SMapTypeIR;
	}

	public boolean isSeqType(SExpIR exp)
	{
		return exp.getType() instanceof SSeqTypeIR;
	}

	public boolean isMapApplication(AApplyExpIR applyExp)
	{
		return isMapType(applyExp.getRoot()) && applyExp.getArgs().size() == 1;
	}

	public boolean isSeqApplication(AApplyExpIR applyExp)
	{
		return isSeqType(applyExp.getRoot()) && applyExp.getArgs().size() == 1;
	}

	public boolean isCharRead(AApplyExpIR applyExp)
	{
		return isStringType(applyExp.getRoot())
				&& applyExp.getArgs().size() == 1;
	}

	public STypeIR findElementType(STypeIR type)
	{
		if (type instanceof SSetTypeIR)
		{
			SSetTypeIR setType = (SSetTypeIR) type;

			return setType.getSetOf();
		} else if (type instanceof SSeqTypeIR)
		{
			SSeqTypeIR seqType = (SSeqTypeIR) type;

			return seqType.getSeqOf();
		}

		log.error("Expected set or sequence type in findElementType. Got: "
				+ type);

		return null;
	}

	public PType resolve(PType type)
	{
		while (type instanceof ABracketType
				|| type instanceof ANamedInvariantType
				|| type instanceof AOptionalType)
		{
			if (type instanceof ABracketType)
			{
				type = ((ABracketType) type).getType();
			}

			if (type instanceof ANamedInvariantType)
			{
				type = ((ANamedInvariantType) type).getType();
			}

			if (type instanceof AOptionalType)
			{
				type = ((AOptionalType) type).getType();
			}
		}

		return type;
	}
	
	public STypeIR reduceToSeq(STypeIR type)
	{
		if(type instanceof AUnionTypeIR)
		{
			AUnionTypeIR union = (AUnionTypeIR) type;
			List<STypeIR> types = new LinkedList<>();
			
			if(!union.getTypes().isEmpty())
			{
				for(STypeIR t : union.getTypes())
				{
					if(t instanceof SSeqTypeIR)
					{
						types.add(t);
					}
				}
				
				if(types.size() == 1)
				{
					return types.get(0);
				}
				
				AUnionTypeIR newType = union.clone();
				newType.setTypes(types);
				return newType;
			}
			else
			{
				log.error("Received union empty union types: " + type);
				return type;
			}
		}
		else
		{
			return type;
		}
	}

	public SSeqTypeIR getSeqType(AUnionTypeIR unionType)
	{
		AUnionTypeIR seqOf = new AUnionTypeIR();
		seqOf.setTypes(findElementTypes(unionType, new CollectionTypeStrategy()
		{
			@Override
			public boolean isCollectionType(STypeIR type)
			{
				return type instanceof SSeqTypeIR;
			}

			@Override
			public STypeIR getElementType(STypeIR type)
			{
				return ((SSeqTypeIR) type).getSeqOf();
			}
		}));

		ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
		seqType.setEmpty(false);
		seqType.setSeqOf(seqOf);

		return seqType;
	}

	public SSetTypeIR getSetType(AUnionTypeIR unionType)
	{
		AUnionTypeIR setOf = new AUnionTypeIR();
		setOf.setTypes(findElementTypes(unionType, new CollectionTypeStrategy()
		{
			@Override
			public boolean isCollectionType(STypeIR type)
			{
				return type instanceof SSetTypeIR;
			}

			@Override
			public STypeIR getElementType(STypeIR type)
			{
				return ((SSetTypeIR) type).getSetOf();
			}
		}));

		ASetSetTypeIR setType = new ASetSetTypeIR();
		setType.setEmpty(false);
		setType.setSetOf(setOf);

		return setType;
	}

	public boolean usesUnionType(SBinaryExpIR node)
	{
		return node.getLeft().getType() instanceof AUnionTypeIR
				|| node.getRight().getType() instanceof AUnionTypeIR;
	}

	public List<STypeIR> findElementTypes(AUnionTypeIR unionType,
			CollectionTypeStrategy strategy)
	{
		List<STypeIR> elementTypes = new LinkedList<STypeIR>();

		for (int i = 0; i < unionType.getTypes().size(); i++)
		{
			STypeIR type = unionType.getTypes().get(i);

			if (type instanceof AUnionTypeIR)
			{
				elementTypes.addAll(findElementTypes((AUnionTypeIR) type, strategy));
			} else if (strategy.isCollectionType(type))
			{
				elementTypes.add(strategy.getElementType(type));
			}
		}

		return elementTypes;
	}

	public boolean containsType(List<STypeIR> types, STypeIR searchedType)
	{
		for (STypeIR currentType : types)
		{
			if (typesEqual(currentType, searchedType))
			{
				return true;
			}
		}

		return false;
	}

	private boolean typesEqual(STypeIR left, STypeIR right)
	{
		if (left instanceof AClassTypeIR && right instanceof AClassTypeIR)
		{
			AClassTypeIR currentClassType = (AClassTypeIR) left;
			AClassTypeIR searchedClassType = (AClassTypeIR) right;

			return currentClassType.getName().equals(searchedClassType.getName());
		}

		if (left instanceof ARecordTypeIR && right instanceof ARecordTypeIR)
		{
			ARecordTypeIR recordType = (ARecordTypeIR) left;
			ARecordTypeIR searchedRecordType = (ARecordTypeIR) right;

			return recordType.getName().equals(searchedRecordType.getName());
		}

		if (left instanceof ATupleTypeIR && right instanceof ATupleTypeIR)
		{
			ATupleTypeIR currentTupleType = (ATupleTypeIR) left;
			ATupleTypeIR searchedTupleType = (ATupleTypeIR) right;

			if (currentTupleType.getTypes().size() != searchedTupleType.getTypes().size())
			{
				return false;
			}

			LinkedList<STypeIR> leftTypes = currentTupleType.getTypes();
			LinkedList<STypeIR> rightTypes = searchedTupleType.getTypes();

			for (int i = 0; i < leftTypes.size(); i++)
			{
				STypeIR currentLeftFieldType = leftTypes.get(i);
				STypeIR currentRightFieldType = rightTypes.get(i);

				if (!typesEqual(currentLeftFieldType, currentRightFieldType))
				{
					return false;
				}
			}

			return true;
		}

		if (left.getClass() == right.getClass())
		{
			return true;
		}

		return false;
	}

	public boolean isNumericType(STypeIR type)
	{
		return isInt(type) || isRealOrRat(type);
	}

	public boolean isRealOrRat(STypeIR type)
	{
		return type instanceof ARatNumericBasicTypeIR
				|| type instanceof ARatBasicTypeWrappersTypeIR
				|| type instanceof ARealNumericBasicTypeIR
				|| type instanceof ARealBasicTypeWrappersTypeIR;
	}

	public boolean isInt(STypeIR type)
	{
		return type instanceof AIntNumericBasicTypeIR
				|| type instanceof AIntBasicTypeWrappersTypeIR
				|| type instanceof ANat1NumericBasicTypeIR
				|| type instanceof ANat1BasicTypeWrappersTypeIR
				|| type instanceof ANatNumericBasicTypeIR
				|| type instanceof ANatBasicTypeWrappersTypeIR;
	}

	public boolean isWrapperType(STypeIR type)
	{
		return type instanceof ANatBasicTypeWrappersTypeIR
				|| type instanceof ANat1BasicTypeWrappersTypeIR
				|| type instanceof ARatBasicTypeWrappersTypeIR
				|| type instanceof ARealBasicTypeWrappersTypeIR
				|| type instanceof ACharBasicTypeWrappersTypeIR
				|| type instanceof ABoolBasicTypeWrappersTypeIR;
	}

	public boolean isOptional(STypeIR type)
	{
		return BooleanUtils.isTrue(type.getOptional());
	}

	public boolean allowsNull(STypeIR type)
	{
		if (type instanceof AUnionTypeIR)
		{
			AUnionTypeIR unionType = (AUnionTypeIR) type;

			if (BooleanUtils.isTrue(unionType.getOptional()))
			{
				return true;
			}

			for (STypeIR t : unionType.getTypes())
			{
				if (allowsNull(t))
				{
					return true;
				}
			}

			return false;
		} else
		{
			return /* type instanceof AObjectTypeIR || */type != null
					&& (type instanceof AUnknownTypeIR
							|| BooleanUtils.isTrue(type.getOptional())
							|| isWrapperType(type));
		}

	}

	public PType getVdmType(STypeIR type)
	{
		SourceNode source = type.getSourceNode();
		if (source != null)
		{
			org.overture.ast.node.INode vdmNode = source.getVdmNode();

			if (vdmNode != null)
			{
				if (vdmNode instanceof PType)
				{
					return (PType) vdmNode;
				}
			}
		}

		log.error("Could not get VDM type of " + type);
		return new AUnknownType();
	}

	public boolean isIncompleteRecType(ARecordTypeIR recType)
	{
		return recType.getName().getName().equals("?") || recType.getName().getDefiningClass().equals("?");
	}
}
