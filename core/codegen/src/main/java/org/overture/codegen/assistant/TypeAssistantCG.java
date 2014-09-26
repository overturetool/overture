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
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.ast.util.PTypeSet;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AFieldObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.ATokenBasicTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class TypeAssistantCG extends AssistantBase
{
	public TypeAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public AMethodTypeCG getMethodType(IRInfo info, List<AClassDeclCG> classes,
			String fieldModule, String fieldName, LinkedList<SExpCG> args)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		AClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(classes, fieldModule);

		List<AMethodDeclCG> methods = assistantManager.getDeclAssistant().getAllMethods(classDecl, classes);

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

	public STypeCG getFieldType(AClassDeclCG classDecl, String fieldName,
			List<AClassDeclCG> classes)
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

	public List<STypeCG> getFieldTypes(ARecordDeclCG record)
	{
		List<STypeCG> fieldTypes = new LinkedList<STypeCG>();

		for (AFieldDeclCG field : record.getFields())
		{
			fieldTypes.add(field.getType());
		}

		return fieldTypes;
	}

	public STypeCG getFieldType(List<AClassDeclCG> classes, String moduleName,
			String fieldName)
	{
		AClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(classes, moduleName);
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

	public PDefinition getTypeDef(ILexNameToken nameToken)
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

		// FIXME factories cannot be instantiated inside code blocks
		TypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
		PDefinitionAssistantTC defAssistant = factory.createPDefinitionAssistant();

		enclosingClass.getName().getModule();
		PDefinition typeDef = defAssistant.findType(def, nameToken, enclosingClass.getName().getModule());

		return typeDef;
	}

	public STypeCG constructSeqType(SSeqTypeBase node, IRInfo question)
			throws AnalysisException
	{
		STypeCG seqOfCg = node.getSeqof().apply(question.getTypeVisitor(), question);
		boolean emptyCg = node.getEmpty();

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
			Logger.getLog().printErrorln("Unexpected basic type encountered in getWrapperType method: "
					+ basicType);
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
			Class<? extends PType> type)
	{
		TypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
		PTypeAssistantTC typeAssistant = factory.createPTypeAssistant();

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

	public STypeCG findElementType(AApplyObjectDesignatorCG designator,
			List<AClassDeclCG> classes, IRInfo info)
	{
		int appliesCount = 0;

		AApplyObjectDesignatorCG mostRecentApply = designator;
		SObjectDesignatorCG object = designator.getObject();

		while (object != null)
		{
			if (object instanceof AIdentifierObjectDesignatorCG)
			{
				AIdentifierObjectDesignatorCG id = (AIdentifierObjectDesignatorCG) object;

				STypeCG type = id.getExp().getType();

				return findElementType(appliesCount, type);
			} else if (object instanceof AApplyObjectDesignatorCG)
			{
				mostRecentApply = (AApplyObjectDesignatorCG) object;
				appliesCount++;
				object = mostRecentApply.getObject();
			} else if (object instanceof AFieldObjectDesignatorCG)
			{
				AFieldObjectDesignatorCG fieldObj = (AFieldObjectDesignatorCG) object;
				object = fieldObj.getObject();

				return findElementType(classes, info, appliesCount, mostRecentApply, fieldObj);
			} else
			{
				return null;
			}
		}

		return null;
	}

	private STypeCG findElementType(int appliesCount, STypeCG type)
	{
		int methodTypesCount = 0;

		while (type instanceof AMethodTypeCG)
		{
			methodTypesCount++;
			AMethodTypeCG methodType = (AMethodTypeCG) type;
			type = methodType.getResult();
		}

		while (type instanceof SSeqTypeCG || type instanceof SMapTypeCG)
		{
			if (type instanceof SSeqTypeCG)
			{
				type = ((SSeqTypeCG) type).getSeqOf();
			}

			if (type instanceof SMapTypeCG)
			{
				type = ((SMapTypeCG) type).getTo();
			}

			if (appliesCount == methodTypesCount)
			{
				return type;
			}

			methodTypesCount++;
		}

		return null;
	}

	private STypeCG findElementType(List<AClassDeclCG> classes, IRInfo info,
			int appliesCount, AApplyObjectDesignatorCG mostRecentApply,
			AFieldObjectDesignatorCG fieldObj)
	{
		try
		{
			STypeCG type = getFieldType(classes, fieldObj.getFieldModule(), fieldObj.getFieldName());

			if (type == null)
			{
				type = getMethodType(info, classes, fieldObj.getFieldModule(), fieldObj.getFieldName(), mostRecentApply.getArgs());
			}

			return findElementType(appliesCount, type);

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			e.printStackTrace();
			return null;
		}
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

			if (question.getTypeAssistant().isUnionOfType(unionType, AProductType.class))
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
}
