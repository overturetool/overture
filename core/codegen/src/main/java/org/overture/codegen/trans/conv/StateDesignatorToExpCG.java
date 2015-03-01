package org.overture.codegen.trans.conv;

import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AMapGetExpCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.TransAssistantCG;

/**
 * Converts a state designator into an equivalent expression. Please note that this converter assumes map sequence state
 * designators to be "map readings" and not "map modifications". More explicitly, this means that the parent of a map
 * sequence state designator is assumed to be a state designator and not an assignment statement.
 * 
 * @author pvj
 */
public class StateDesignatorToExpCG extends AnswerAdaptor<SExpCG>
{
	private IRInfo info;
	private List<AClassDeclCG> classes;
	private TransAssistantCG transAssistant;
	
	public StateDesignatorToExpCG(IRInfo info, List<AClassDeclCG> classes, TransAssistantCG transAssistant)
	{
		this.info = info;
		this.classes = classes;
		this.transAssistant = transAssistant;
	}
	
	@Override
	public SExpCG caseAIdentifierStateDesignatorCG(
			AIdentifierStateDesignatorCG node) throws AnalysisException
	{
		boolean isLocal = info.getDeclAssistant().isLocal(node, classes);
		
		if(node.getExplicit())
		{
			AClassTypeCG classType = new AClassTypeCG();
			classType.setName(node.getClassName());
			
			AExplicitVarExpCG explicitVar = new AExplicitVarExpCG();
			explicitVar.setClassType(classType);
			explicitVar.setIsLambda(false);
			explicitVar.setIsLocal(isLocal);
			explicitVar.setName(node.getName());
			explicitVar.setSourceNode(node.getSourceNode());
			explicitVar.setTag(node.getTag());
			explicitVar.setType(node.getType().clone());
			
			return explicitVar;
		}
		else
		{
			AIdentifierVarExpCG idVar = transAssistant.consIdentifierVar(node.getName(), node.getType().clone());
			idVar.setTag(node.getTag());
			idVar.setSourceNode(node.getSourceNode());
			idVar.setIsLocal(isLocal);
			
			return idVar;
		}
	}
	
	@Override
	public SExpCG caseAFieldStateDesignatorCG(AFieldStateDesignatorCG node)
			throws AnalysisException
	{
		SExpCG objExp = node.getObject().apply(this);
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(node.getField());
		fieldExp.setObject(objExp);
		fieldExp.setType(node.getType().clone());
		fieldExp.setTag(node.getTag());
		fieldExp.setSourceNode(node.getSourceNode());
		
		return fieldExp;
	}
	
	@Override
	public SExpCG caseAMapSeqStateDesignatorCG(AMapSeqStateDesignatorCG node)
			throws AnalysisException
	{
		SExpCG domValue = node.getExp();
		SExpCG mapSeq = node.getMapseq().apply(this);

		AMapGetExpCG mapGet = new AMapGetExpCG();
		mapGet.setType(node.getType().clone());
		mapGet.setDomValue(domValue.clone());
		mapGet.setMap(mapSeq);
		mapGet.setSourceNode(node.getSourceNode());
		mapGet.setTag(node.getTag());

		// e.g. ((Rec) m(true)).field := 2;
		return mapGet;
	}
	
	@Override
	public SExpCG createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public SExpCG createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
