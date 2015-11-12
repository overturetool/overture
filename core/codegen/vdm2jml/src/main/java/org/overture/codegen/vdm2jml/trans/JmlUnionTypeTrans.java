package org.overture.codegen.vdm2jml.trans;

import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;
import org.overture.codegen.vdm2jml.data.StateDesInfo;

public class JmlUnionTypeTrans extends UnionTypeTrans
{
	private StateDesInfo stateDesInfo;
	
	public JmlUnionTypeTrans(TransAssistantCG transAssistant, UnionTypeVarPrefixes unionTypePrefixes,
			List<INode> cloneFreeNodes, StateDesInfo stateDesInfo)
	{
		super(transAssistant, unionTypePrefixes, cloneFreeNodes);
		
		this.stateDesInfo = stateDesInfo;
	}
	
	@Override
	public AAssignToExpStmCG castFieldObj(AAssignToExpStmCG assign, AFieldExpCG target, STypeCG possibleType)
	{
		AAssignToExpStmCG ret = super.castFieldObj(assign, target, possibleType);
		stateDesInfo.duplicateStateDesOwner(assign, ret);
		
		return ret;
	}
}
