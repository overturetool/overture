package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqTypeBase;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
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
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
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
	
	public STypeCG getMethodType(IRInfo info, List<AClassDeclCG> classes,
			String fieldModule, String fieldName, LinkedList<SExpCG> args)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		AClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(classes, fieldModule);
		LinkedList<AMethodDeclCG> methods = classDecl.getMethods();

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

		return null;
	}
	
	public STypeCG getFieldType(AClassDeclCG classDecl, String fieldName)
	{
		for(AFieldDeclCG field : classDecl.getFields())
		{
			if(field.getName().equals(fieldName))
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
	
	public STypeCG getFieldType(List<AClassDeclCG> classes, String moduleName, String fieldName)
	{
		AClassDeclCG classDecl = assistantManager.getDeclAssistant().findClass(classes, moduleName);
		return getFieldType(classDecl, fieldName);
	}
	
	public boolean checkArgTypes(IRInfo info, List<SExpCG> args, List<STypeCG> paramTypes)
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		if(args.size() != paramTypes.size())
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

			if (!(paramTypeNode instanceof PType) || !(argTypeNode instanceof PType))
			{
				return false;
			}
			
			TypeComparator typeComparator = new TypeComparator(info.getTcFactory());
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
			return null;

		SClassDefinition enclosingClass = nameToken.getAncestor(SClassDefinition.class);

		if (enclosingClass == null)
			return null;

		//FIXME factories cannot be instantiated inside code blocks
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
		if (seqOfCg instanceof ACharBasicTypeCG && question.getSettings().getCharSeqAsString())
		{
			return new AStringTypeCG();
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

	public SBasicTypeWrappersTypeCG getWrapperType(
			SBasicTypeCG basicType)
	{

		if (basicType instanceof AIntNumericBasicTypeCG)
			return new AIntBasicTypeWrappersTypeCG();
		else if (basicType instanceof ARealNumericBasicTypeCG)
			return new ARealBasicTypeWrappersTypeCG();
		else if (basicType instanceof ACharBasicTypeCG)
			return new ACharBasicTypeWrappersTypeCG();
		else if (basicType instanceof ABoolBasicTypeCG)
			return new ABoolBasicTypeWrappersTypeCG();
		else
		{
			Logger.getLog().printErrorln("Unexpected basic type encountered in getWrapperType method: "
					+ basicType);
			return null;
		}

	}
	
	public AMethodTypeCG consMethodType(PType node, List<PType> paramTypes, PType resultType, IRInfo question) throws AnalysisException
	{
		AMethodTypeCG methodType = new AMethodTypeCG();
		
		methodType.setEquivalent(node.clone());
		
		STypeCG resultCg = resultType.apply(question.getTypeVisitor(), question);
		
		methodType.setResult(resultCg);
		
		LinkedList<STypeCG> paramsCg = methodType.getParams();
		for(PType paramType : paramTypes)
		{
			paramsCg.add(paramType.apply(question.getTypeVisitor(), question));
		}
		
		return methodType;
	}
	
	public boolean isUnionOfType(AUnionType unionType, Class<? extends PType> type)
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
		} catch (Error t)//Hack for stackoverflowError
		{
			return false;
		}

		return true;
	}
	
	public STypeCG findElementType(AApplyObjectDesignatorCG designator)
	{
		int appliesCount = 0;
		
		SObjectDesignatorCG object = designator.getObject();

		while(object != null)
		{
			if(object instanceof AIdentifierObjectDesignatorCG)
			{
				AIdentifierObjectDesignatorCG id = (AIdentifierObjectDesignatorCG) object;
			
				STypeCG type = id.getExp().getType();
				
				int methodTypesCount = 0;
				
				while (type instanceof AMethodTypeCG)
				{
					methodTypesCount++;
					AMethodTypeCG methodType = (AMethodTypeCG) type;
					type = methodType.getResult();
				}
				
				while(type instanceof SSeqTypeCG || type instanceof SMapTypeCG)
				{
					if(type instanceof SSeqTypeCG)
					{
						type = ((SSeqTypeCG) type).getSeqOf();
					}

					if(type instanceof SMapTypeCG)
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
			else if(object instanceof AApplyObjectDesignatorCG)
			{
				AApplyObjectDesignatorCG applyObj = (AApplyObjectDesignatorCG) object;
				appliesCount++;
				object = applyObj.getObject();
			}
			else
			{
				return null;
			}
		}
		
		return null;
	}
}
