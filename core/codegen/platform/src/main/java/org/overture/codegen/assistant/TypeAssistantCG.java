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
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.ast.util.PTypeSet;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.SClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ANat1BasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.cgast.types.ARatBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.conv.ObjectDesignatorToExpCG;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeAssistantCG extends AssistantBase
{
	public TypeAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public STypeCG getFieldExpType(IRInfo info, String fieldName, String fieldModule,
			SObjectDesignatorCG obj, INode parent)
			throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException
	{
		if (parent instanceof AApplyObjectDesignatorCG)
		{
			AApplyObjectDesignatorCG apply = (AApplyObjectDesignatorCG) parent;
			LinkedList<SExpCG> args = apply.getArgs();

			if (fieldModule != null)
			{
				// It is a class
				SClassDeclCG clazz = info.getDeclAssistant().findClass(info.getClasses(), fieldModule);
				AFieldDeclCG field = info.getDeclAssistant().getFieldDecl(clazz, fieldModule);
				
				if(field != null)
				{
					return field.getType().clone();
				}
				else
				{
					// It must be a method
					return info.getTypeAssistant().getMethodType(info, fieldModule, fieldName, args);
				}
			}
		}
		return getFieldType(info, fieldName, fieldModule, obj);
	}
	
	private STypeCG getFieldType(IRInfo info, String fieldName, String fieldModule, SObjectDesignatorCG obj)
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
				ObjectDesignatorToExpCG converter = new ObjectDesignatorToExpCG(info);
				SExpCG objExp = obj.apply(converter);

				if (objExp.getType() instanceof ARecordTypeCG)
				{
					STypeCG fieldExpType = info.getTypeAssistant().getFieldType(info.getClasses(), (ARecordTypeCG) objExp.getType(), fieldName);

					if (fieldExpType == null)
					{
						Logger.getLog().printErrorln("Lookup of field type gave nothing in 'TypeAssistantCG'");
					}

					return fieldExpType;
				}
			} 
			catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
			}
		}

		Logger.getLog().printErrorln("Could not determine field type of field expression in 'TypeAssistantCG'");
		return new AUnknownTypeCG();
	}
	
	public AMethodTypeCG getMethodType(IRInfo info,
			String fieldModule, String fieldName, List<SExpCG> args)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		SClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(info.getClasses(), fieldModule);

		List<AMethodDeclCG> methods = assistantManager.getDeclAssistant().getAllMethods(classDecl, info.getClasses());

		for (AMethodDeclCG method : methods)
		{
			if (method.getName().equals(fieldName))
			{
				LinkedList<STypeCG> params = method.getMethodType().getParams();

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
		//	for x in xs do
		// 		x.op();
		
		//If the field does not exist then the method type does not exist
		return null;
	}

	public STypeCG getFieldType(SClassDeclCG classDecl, String fieldName,
			List<SClassDeclCG> classes)
	{
		for (AFieldDeclCG field : assistantManager.getDeclAssistant().getAllFields(classDecl, classes))
		{
			if (field.getName().equals(fieldName))
			{
				return field.getType().clone();
			}
		}

		return null;
	}
	
	public STypeCG getFieldType(List<SClassDeclCG> classes,
			ARecordTypeCG recordType, String memberName)
	{
		AFieldDeclCG field = assistantManager.getDeclAssistant().getFieldDecl(classes, recordType, memberName);

		if (field != null)
		{
			return field.getType().clone();
		}
		return null;
	}

	public List<STypeCG> getFieldTypes(ARecordDeclCG record)
	{
		List<STypeCG> fieldTypes = new LinkedList<STypeCG>();

		for (AFieldDeclCG field : record.getFields())
		{
			fieldTypes.add(field.getType());
		}

		return fieldTypes;
	}

	public STypeCG getFieldType(List<SClassDeclCG> classes, String moduleName,
			String fieldName)
	{
		SClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(classes, moduleName);
		return getFieldType(classDecl, fieldName, classes);
	}

	public boolean checkArgTypes(IRInfo info, List<SExpCG> args,
			List<STypeCG> paramTypes)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if (args.size() != paramTypes.size())
		{
			return false;
		}

		for (int i = 0; i < paramTypes.size(); i++)
		{
			SourceNode paramSourceNode = paramTypes.get(i).getSourceNode();
			SourceNode argTypeSourceNode = args.get(i).getType().getSourceNode();

			if (paramSourceNode == null || argTypeSourceNode == null)
			{
				return false;
			}

			org.overture.ast.node.INode paramTypeNode = paramSourceNode.getVdmNode();
			org.overture.ast.node.INode argTypeNode = argTypeSourceNode.getVdmNode();

			if (!(paramTypeNode instanceof PType)
					|| !(argTypeNode instanceof PType))
			{
				return false;
			}

			TypeComparator typeComparator = info.getTcFactory().getTypeComparator();
			if (!typeComparator.compatible((PType) paramTypeNode, (PType) argTypeNode))
			{
				return false;
			}
		}

		return true;
	}

	public PDefinition getTypeDef(ILexNameToken nameToken, PDefinitionAssistantTC defAssistant)
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

		enclosingClass.getName().getModule();
		PDefinition typeDef = defAssistant.findType(def, nameToken, enclosingClass.getName().getModule());

		return typeDef;
	}

	public STypeCG constructSeqType(SSeqTypeBase node, IRInfo question)
			throws AnalysisException
	{
		STypeCG seqOfCg = node.getSeqof().apply(question.getTypeVisitor(), question);
		boolean emptyCg = node.getEmpty();
		
		boolean isSeq1 = node instanceof ASeq1SeqType;

		// This is a special case since sequence of characters are strings
		if (seqOfCg instanceof ACharBasicTypeCG
				&& question.getSettings().getCharSeqAsString())
		{
			AStringTypeCG stringTypeCg = new AStringTypeCG();
			stringTypeCg.setSourceNode(new SourceNode(node));

			return stringTypeCg;
		}

		ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
		seqType.setSeqOf(seqOfCg);
		seqType.setEmpty(emptyCg);
		seqType.setSeq1(isSeq1);

		return seqType;
	}

	public boolean isBasicType(STypeCG type)
	{
		return type instanceof SBasicTypeCG;
	}

	public STypeCG getWrapperType(SBasicTypeCG basicType)
	{

		if (basicType instanceof AIntNumericBasicTypeCG)
		{
			return new AIntBasicTypeWrappersTypeCG();
		} else if(basicType instanceof ANat1NumericBasicTypeCG)
		{
			return new ANat1BasicTypeWrappersTypeCG();
		} else if(basicType instanceof ANatNumericBasicTypeCG)
		{
			return new ANatBasicTypeWrappersTypeCG();
		} else if (basicType instanceof ARealNumericBasicTypeCG)
		{
			return new ARealBasicTypeWrappersTypeCG();
		} else if (basicType instanceof ACharBasicTypeCG)
		{
			return new ACharBasicTypeWrappersTypeCG();
		} else if (basicType instanceof ABoolBasicTypeCG)
		{
			return new ABoolBasicTypeWrappersTypeCG();
		} else if (basicType instanceof ATokenBasicTypeCG)
		{
			return basicType;
		} else
		{
			return null;
		}

	}

	public AMethodTypeCG consMethodType(PType node, List<PType> paramTypes,
			PType resultType, IRInfo question) throws AnalysisException
	{
		AMethodTypeCG methodType = new AMethodTypeCG();

		methodType.setEquivalent(node.clone());

		STypeCG resultCg = resultType.apply(question.getTypeVisitor(), question);

		methodType.setResult(resultCg);

		LinkedList<STypeCG> paramsCg = methodType.getParams();
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
					AProductType productType = typeAssistant.getProduct(t);
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
	
	public PType getType(IRInfo question, AUnionType unionType, PPattern pattern)
	{
		PTypeSet possibleTypes = new PTypeSet(question.getTcFactory());
		PType patternType = question.getTcFactory().createPPatternAssistant().getPossibleType(pattern);
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
			Logger.getLog().printError("Could not find any possible types for pattern: "
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
	
	public List<STypeCG> clearObjectTypes(List<STypeCG> types)
	{
		types = new LinkedList<STypeCG>(types);
		
		List<AObjectTypeCG> objectTypes = new LinkedList<AObjectTypeCG>();
		
		for(STypeCG type : types)
		{
			if(type instanceof AObjectTypeCG)
			{
				objectTypes.add((AObjectTypeCG) type);
			}
		}
		
		types.removeAll(objectTypes);
		
		return types;
	}
	
	public List<STypeCG> clearDuplicates(List<STypeCG> types)
	{
		List<STypeCG> filtered = new LinkedList<STypeCG>();

		for(STypeCG type : types)
		{
			if(!containsType(filtered, type))
			{
				filtered.add(type);
			}
		}
		
		return filtered;
	}
	
	public boolean isStringType(STypeCG type)
	{
		return type instanceof AStringTypeCG;
	}
	
	public boolean isStringType(SExpCG exp)
	{
		return exp.getType() instanceof AStringTypeCG;
	}
	
	public boolean isMapType(SExpCG exp)
	{
		return exp.getType() instanceof SMapTypeCG;
	}
	
	public boolean isSeqType(SExpCG exp)
	{
		return exp.getType() instanceof SSeqTypeCG;
	}
	
	public boolean isMapApplication(AApplyExpCG applyExp)
	{
		return isMapType(applyExp.getRoot()) && applyExp.getArgs().size() == 1;
	}
	
	public boolean isSeqApplication(AApplyExpCG applyExp)
	{
		return isSeqType(applyExp.getRoot()) && applyExp.getArgs().size() == 1;
	}
	
	public boolean isCharRead(AApplyExpCG applyExp)
	{
		return isStringType(applyExp.getRoot()) && applyExp.getArgs().size() == 1;
	}

	public STypeCG findElementType(STypeCG type)
	{
		if (type instanceof SSetTypeCG)
		{
			SSetTypeCG setType = (SSetTypeCG) type;

			return setType.getSetOf();
		} else if (type instanceof SSeqTypeCG)
		{
			SSeqTypeCG seqType = (SSeqTypeCG) type;

			return seqType.getSeqOf();
		}

		Logger.getLog().printErrorln("Expected set or sequence type in findElementType. Got: " + type);
		
		return null;
	}
	
	public PType resolve(PType type)
	{
		while (type instanceof ABracketType || type instanceof ANamedInvariantType || type instanceof AOptionalType)
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
	
	public SSeqTypeCG getSeqType(AUnionTypeCG unionType)
	{
		AUnionTypeCG seqOf = new AUnionTypeCG();
		seqOf.setTypes(findElementTypes(unionType, new CollectionTypeStrategy()
		{
			@Override
			public boolean isCollectionType(STypeCG type)
			{
				return type instanceof SSeqTypeCG;
			}
			
			@Override
			public STypeCG getElementType(STypeCG type)
			{
				return ((SSeqTypeCG) type).getSeqOf();
			}
		}));
		
		ASeqSeqTypeCG seqType = new ASeqSeqTypeCG();
		seqType.setEmpty(false);
		seqType.setSeqOf(seqOf);
		
		return seqType;
	}
	
	public SSetTypeCG getSetType(AUnionTypeCG unionType)
	{
		AUnionTypeCG setOf = new AUnionTypeCG();
		setOf.setTypes(findElementTypes(unionType, new CollectionTypeStrategy()
		{
			@Override
			public boolean isCollectionType(STypeCG type)
			{
				return type instanceof SSetTypeCG;
			}
			
			@Override
			public STypeCG getElementType(STypeCG type)
			{
				return ((SSetTypeCG) type).getSetOf();
			}
		}));
		
		ASetSetTypeCG setType = new ASetSetTypeCG();
		setType.setEmpty(false);
		setType.setSetOf(setOf);
		
		return setType;
	}
	
	public boolean usesUnionType(SBinaryExpCG node)
	{
		return node.getLeft().getType() instanceof AUnionTypeCG || node.getRight().getType() instanceof AUnionTypeCG;
	}
	
	public List<STypeCG> findElementTypes(AUnionTypeCG unionType, CollectionTypeStrategy strategy)
	{
		List<STypeCG> elementTypes = new LinkedList<STypeCG>();
		
		for (int i = 0; i < unionType.getTypes().size(); i++)
		{
			STypeCG type = unionType.getTypes().get(i);

			if (type instanceof AUnionTypeCG)
			{
				elementTypes.addAll(findElementTypes((AUnionTypeCG) type, strategy));
			} else if (strategy.isCollectionType(type))
			{
				elementTypes.add(strategy.getElementType(type));
			}
		}
		
		return elementTypes;
	}
	
	public boolean containsType(List<STypeCG> types, STypeCG searchedType)
	{
		for (STypeCG currentType : types)
		{
			if (typesEqual(currentType, searchedType))
			{
				return true;
			}
		}

		return false;
	}
	
	private boolean typesEqual(STypeCG left, STypeCG right)
	{
		if (left instanceof AClassTypeCG
				&& right instanceof AClassTypeCG)
		{
			AClassTypeCG currentClassType = (AClassTypeCG) left;
			AClassTypeCG searchedClassType = (AClassTypeCG) right;

			return currentClassType.getName().equals(searchedClassType.getName());
		}

		if (left instanceof ARecordTypeCG
				&& right instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) left;
			ARecordTypeCG searchedRecordType = (ARecordTypeCG) right;

			return recordType.getName().equals(searchedRecordType.getName());
		}

		if (left instanceof ATupleTypeCG
				&& right instanceof ATupleTypeCG)
		{
			ATupleTypeCG currentTupleType = (ATupleTypeCG) left;
			ATupleTypeCG searchedTupleType = (ATupleTypeCG) right;
			
			if(currentTupleType.getTypes().size() != searchedTupleType.getTypes().size())
			{
				return false;
			}
			
			LinkedList<STypeCG> leftTypes = currentTupleType.getTypes();
			LinkedList<STypeCG> rightTypes = searchedTupleType.getTypes();
			
			for(int i = 0; i < leftTypes.size(); i++)
			{
				STypeCG currentLeftFieldType = leftTypes.get(i);
				STypeCG currentRightFieldType = rightTypes.get(i);
				
				if(!typesEqual(currentLeftFieldType, currentRightFieldType))
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
	
	public boolean isNumericType(STypeCG type)
	{
		return isInt(type) || isRealOrRat(type);
	}

	public boolean isRealOrRat(STypeCG type)
	{
		return type instanceof ARatNumericBasicTypeCG
				|| type instanceof ARatBasicTypeWrappersTypeCG
				|| type instanceof ARealNumericBasicTypeCG
				|| type instanceof ARealBasicTypeWrappersTypeCG;
	}

	public boolean isInt(STypeCG type)
	{
		return type instanceof AIntNumericBasicTypeCG
				|| type instanceof AIntBasicTypeWrappersTypeCG
				|| type instanceof ANat1NumericBasicTypeCG
				|| type instanceof ANat1BasicTypeWrappersTypeCG
				|| type instanceof ANatNumericBasicTypeCG
				|| type instanceof ANatBasicTypeWrappersTypeCG;
	}
	
	public boolean isWrapperType(STypeCG type)
	{
		return type instanceof ANatBasicTypeWrappersTypeCG
				|| type instanceof ANat1BasicTypeWrappersTypeCG
				|| type instanceof ARatBasicTypeWrappersTypeCG
				|| type instanceof ARealBasicTypeWrappersTypeCG
				|| type instanceof ACharBasicTypeWrappersTypeCG
				|| type instanceof ABoolBasicTypeWrappersTypeCG;
	}
	
	public boolean allowsNull(STypeCG type)
	{
		if(type instanceof AUnionTypeCG)
		{
			AUnionTypeCG unionType = (AUnionTypeCG) type;
			
			if(BooleanUtils.isTrue(unionType.getOptional()))
			{
				return true;
			}
			
			for(STypeCG t : unionType.getTypes())
			{
				if(allowsNull(t))
				{
					return true;
				}
			}
			
			return false;
		}
		else
		{
			return /* type instanceof AObjectTypeCG || */type != null
					&& (type instanceof AUnknownTypeCG || BooleanUtils.isTrue(type.getOptional()) || isWrapperType(type));
		}
		
	}
}
