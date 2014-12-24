package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class InstanceVarPPEvalTransformation extends DepthFirstAnalysisAdaptor
{
	private static final String SENTINEL_FIELD_NAME = "sentinel";
	private TransAssistantCG transAssistant;
	private IRInfo info;
	private List<AClassDeclCG> classes;
	
	public InstanceVarPPEvalTransformation(IRInfo info, TransAssistantCG transAssistant, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.transAssistant = transAssistant;
		this.classes = classes;
	}

	@Override
	public void caseAAssignmentStmCG(AAssignmentStmCG node)
			throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		AMethodDeclCG enclosingMethod = node.getAncestor(AMethodDeclCG.class);
		
		if(enclosingMethod != null)
		{
			Boolean isStatic = enclosingMethod.getStatic();
			
			if(isStatic != null && isStatic)
			{
				return;
			}
			
			if(enclosingMethod.getIsConstructor())
			{
				return;
			}
			
			if(isIRGenerated(enclosingMethod))
			{
				return;
			}
		}
		else
		{
			// Can in fact be okay since the IR construction of the thread definition skips the
			// explicit operation definition implicitly associated with the thread definition.
			//
			// Example:
			// thread
			// (x := 2;)
			//
		}
		
		
		STypeCG fieldType = getSentinelFieldType(node);
		
		AIdentifierVarExpCG sentinelVar = new AIdentifierVarExpCG();
		sentinelVar.setIsLambda(false);
		sentinelVar.setName(SENTINEL_FIELD_NAME);
		sentinelVar.setType(fieldType);
		
		AIdentifierObjectDesignatorCG sentinel = new AIdentifierObjectDesignatorCG();
		sentinel.setExp(sentinelVar);
		
		ACallObjectStmCG callSentinel = new ACallObjectStmCG();
		callSentinel.setClassName(null);
		callSentinel.setDesignator(sentinel);
		callSentinel.setFieldName("stateChanged");
		callSentinel.setType(new AVoidTypeCG());
		
		ABlockStmCG replacementBlock = new ABlockStmCG();
		
		transAssistant.replaceNodeWith(node, replacementBlock);
		
		replacementBlock.getStatements().add(node);
		replacementBlock.getStatements().add(callSentinel);
	}

	private STypeCG getSentinelFieldType(AAssignmentStmCG node)
	{
		AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);
		
		STypeCG fieldType = null;
		
		if(enclosingClass != null)
		{
			fieldType = info.getTypeAssistant().getFieldType(enclosingClass, SENTINEL_FIELD_NAME, classes);
		}
		else
		{
			Logger.getLog().printErrorln("Could not find enclosing class of assignment statement in InstanceVarPPEvalTransformation");
		}
		return fieldType;
	}
	
	private boolean isIRGenerated(AMethodDeclCG method)
	{
		return method.getTag() instanceof IRGeneratedTag;
	}
}
