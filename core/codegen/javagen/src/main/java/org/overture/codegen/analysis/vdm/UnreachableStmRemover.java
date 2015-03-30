package org.overture.codegen.analysis.vdm;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;

public class UnreachableStmRemover extends DepthFirstAnalysisAdaptor
{
	@Override
	public void caseABlockSimpleBlockStm(ABlockSimpleBlockStm node)
			throws AnalysisException
	{
		List<Integer> unreachStmIndices = new LinkedList<Integer>();
		
		boolean notreached = false;

		for (int i = 0; i <  node.getStatements().size(); i++)
		{
			PStm stmt = node.getStatements().get(i);
			stmt.apply(this);
			PType stype = stmt.getType();

			if (notreached)
			{
				unreachStmIndices.add(i);
			} else
			{
				notreached = true;

				if (stype instanceof AUnionType)
				{
					AUnionType ust = (AUnionType) stype;

					for (PType t : ust.getTypes())
					{
						if (t instanceof AVoidType || t instanceof AUnknownType)
						{
							notreached = false;
						}
					}
				} else
				{
					if (stype == null || stype instanceof AVoidType
							|| stype instanceof AUnknownType)
					{
						notreached = false;
					}
				}
			}
		}

		// Go backwards to not corrupt the stored indices
		for(int i = unreachStmIndices.size() - 1; i >= 0; i--)
		{
			node.getStatements().remove(unreachStmIndices.get(i).intValue());
		}
	}
}
