package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SObjectDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AFieldObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ANewObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ASelfObjectDesignatorCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.SourceNode;

public class ObjectDesignatorToExpCG extends AnswerAdaptor<SExpCG>
{
	private IRInfo info;
	private List<AClassDeclCG> classes;
	
	public ObjectDesignatorToExpCG(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.classes = classes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SExpCG caseAApplyObjectDesignatorCG(AApplyObjectDesignatorCG node)
			throws AnalysisException
	{
		SObjectDesignatorCG object = node.getObject();
		SourceNode sourceNode = node.getSourceNode();
		LinkedList<SExpCG> args = node.getArgs();
		
		SExpCG root = object.apply(this);

		STypeCG rootType = root.getType();
		STypeCG applyType = null;
		
		if(rootType instanceof SSeqTypeCG)
		{
			applyType = ((SSeqTypeCG) rootType).getSeqOf();
		}
		else if(rootType instanceof SMapTypeCG)
		{
			applyType = ((SMapTypeCG) rootType).getTo();
		}
		else if(rootType instanceof AMethodTypeCG)
		{
			applyType = ((AMethodTypeCG) rootType).getResult();
		}
		
		applyType = applyType.clone();

		
		AApplyExpCG applyExp = new AApplyExpCG();
		applyExp.setArgs((List<? extends SExpCG>) args.clone());
		applyExp.setRoot(root);
		applyExp.setType(applyType);
		applyExp.setSourceNode(sourceNode);
		
		return applyExp;
	}
	
	@Override
	public SExpCG caseAFieldObjectDesignatorCG(AFieldObjectDesignatorCG node)
			throws AnalysisException
	{
		String fieldName = node.getFieldName();
		String fieldModule = node.getFieldModule();
		SObjectDesignatorCG obj = node.getObject();
		SourceNode sourceNode = node.getSourceNode();
		
		STypeCG fieldExpType = null;
		
		TypeAssistantCG typeAssistant = info.getAssistantManager().getTypeAssistant();

		INode parent = node.parent();
		
		if(parent instanceof AApplyObjectDesignatorCG)
		{
			AApplyObjectDesignatorCG apply = (AApplyObjectDesignatorCG) parent;
			LinkedList<SExpCG> args = apply.getArgs();
			fieldExpType = typeAssistant.getMethodType(info, classes, fieldModule, fieldName, args);
		}
		else 
		{
			fieldExpType = typeAssistant.getFieldType(classes, fieldModule, fieldName);
		}
		
		SExpCG objExp = obj.apply(this);
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(fieldName);
		fieldExp.setType(fieldExpType);
		fieldExp.setObject(objExp);
		fieldExp.setSourceNode(sourceNode);
		
		return fieldExp;
	}

	@Override
	public SExpCG caseAIdentifierObjectDesignatorCG(
			AIdentifierObjectDesignatorCG node) throws AnalysisException
	{
		return node.getExp().clone();
	}
	
	@Override
	public SExpCG caseANewObjectDesignatorCG(ANewObjectDesignatorCG node)
			throws AnalysisException
	{
		return node.getExp().clone();
	}
	
	@Override
	public SExpCG caseASelfObjectDesignatorCG(ASelfObjectDesignatorCG node)
			throws AnalysisException
	{
		AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);
		
		String className = enclosingClass.getName();
		SourceNode sourceNode = node.getSourceNode();
		
		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(className);

		ASelfExpCG self = new ASelfExpCG();
		self.setType(classType);
		self.setSourceNode(sourceNode);
		
		return self;
	}
	
	@Override
	public SExpCG createNewReturnValue(INode node) throws AnalysisException
	{
		return null;
	}

	@Override
	public SExpCG createNewReturnValue(Object node) throws AnalysisException
	{
		return null;
	}
}
