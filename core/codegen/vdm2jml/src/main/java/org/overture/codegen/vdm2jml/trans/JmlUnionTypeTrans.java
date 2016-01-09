package org.overture.codegen.vdm2jml.trans;

import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.runtime.traces.Pair;
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
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node) throws AnalysisException
	{
		if (node.getTarget() instanceof AFieldExpCG)
		{
			AFieldExpCG field = (AFieldExpCG) node.getTarget();

			if (field.getObject().getType() instanceof AUnionTypeCG)
			{
				if (p != null)
				{
					Logger.getLog().printErrorln("Expected no state designator data by now in '"
							+ this.getClass().getSimpleName() + "'");
				}
				
				p = this.stateDesInfo.remove(node);
				
				handAssignRighHandSide(node);
				handleAssignTarget(node);
			}
		}
		else
		{
			handAssignRighHandSide(node);
		}
		
		p = null;
	}

	Pair<List<AIdentifierVarExpCG>, List<AVarDeclCG>> p = null;

	@Override
	public AAssignToExpStmCG castFieldObj(AAssignToExpStmCG assign, AFieldExpCG target, STypeCG possibleType)
	{
		AAssignToExpStmCG ret = super.castFieldObj(assign, target, possibleType);

		if (p != null)
		{
			stateDesInfo.register(ret, p.getFirst(), p.getSecond());
		} else
		{
			Logger.getLog().printErrorln("Expected to have state designator data at this point in '"
					+ this.getClass().getSimpleName() + "'");
		}

		return ret;
	}
}
