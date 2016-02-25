package org.overture.codegen.trans;

import java.util.LinkedList;

import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ABlockStmIR;

public class BlockCleanupTrans extends DepthFirstAnalysisAdaptor
{
	@Override
	public void caseABlockStmIR(ABlockStmIR node) throws org.overture.codegen.ir.analysis.AnalysisException
	{
		for (SStmIR s : new LinkedList<>(node.getStatements()))
		{
			s.apply(this);
		}

		if (node.parent() instanceof ABlockStmIR && isEmpty(node))
		{
			/**
			 * Only remove empty blocks from blocks, otherwise we can get into a situation where we destroy the tree.
			 * For example, 'if exp then () else ()'
			 */
			node.parent().removeChild(node);
			return;
		}

		if (singleBlockWrapsBlock(node))
		{
			SStmIR enclosedStm = node.getStatements().get(0);

			if (node.parent() != null)
			{
				node.parent().replaceChild(node, enclosedStm);
			}
		}
	}

	private boolean singleBlockWrapsBlock(ABlockStmIR node)
	{
		return node.getLocalDefs().isEmpty() && node.getStatements().size() == 1
				&& node.getStatements().get(0) instanceof ABlockStmIR;
	}

	public boolean isEmpty(SStmIR target)
	{
		if (!(target instanceof ABlockStmIR))
		{
			return false;
		}

		ABlockStmIR block = (ABlockStmIR) target;

		if (!block.getLocalDefs().isEmpty())
		{
			return false;
		}

		if (block.getStatements().isEmpty())
		{
			return true;
		} else
		{
			for (SStmIR s : block.getStatements())
			{
				if (!isEmpty(s))
				{
					return false;
				}
			}

			return true;
		}
	}
}
