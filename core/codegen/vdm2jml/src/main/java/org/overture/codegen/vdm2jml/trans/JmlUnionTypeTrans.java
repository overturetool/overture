package org.overture.codegen.vdm2jml.trans;

import java.util.List;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.runtime.traces.Pair;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.overture.codegen.trans.uniontypes.UnionTypeTrans;
import org.overture.codegen.trans.uniontypes.UnionTypeVarPrefixes;
import org.overture.codegen.vdm2jml.data.StateDesInfo;

public class JmlUnionTypeTrans extends UnionTypeTrans
{
	private StateDesInfo stateDesInfo;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public JmlUnionTypeTrans(TransAssistantIR transAssistant,
			UnionTypeVarPrefixes unionTypePrefixes, List<INode> cloneFreeNodes,
			StateDesInfo stateDesInfo)
	{
		super(transAssistant, unionTypePrefixes, cloneFreeNodes);

		this.stateDesInfo = stateDesInfo;
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if (node.getTarget() instanceof AFieldExpIR)
		{
			AFieldExpIR field = (AFieldExpIR) node.getTarget();

			if (field.getObject().getType() instanceof AUnionTypeIR)
			{
				if (p != null)
				{
					log.error("Expected no state designator data by now");
				}

				p = this.stateDesInfo.remove(node);

				handAssignRighHandSide(node);
				handleAssignTarget(node);
			}
		} else
		{
			handAssignRighHandSide(node);
		}

		p = null;
	}

	Pair<List<AIdentifierVarExpIR>, List<AVarDeclIR>> p = null;

	@Override
	public AAssignToExpStmIR castFieldObj(AAssignToExpStmIR assign,
			AFieldExpIR target, STypeIR possibleType)
	{
		AAssignToExpStmIR ret = super.castFieldObj(assign, target, possibleType);

		if (p != null)
		{
			stateDesInfo.register(ret, p.getFirst(), p.getSecond());
		} else
		{
			log.error("Expected to have state designator data at this point");
		}

		return ret;
	}
}
