package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;

/**
 * Created by ldc on 31/05/17.
 */
public class IsEqVisitor extends QuestionAnswerAdaptor<ILexLocation, Boolean>
{

	@Override public Boolean caseAUnionType(AUnionType node, ILexLocation q)
			throws AnalysisException
	{
		for (PType t : node.getTypes())
		{
			if (t.apply(this, q))
			{
				return true;
			}
		}
		return false;
	}

	@Override public Boolean caseANamedInvariantType(ANamedInvariantType node,
			ILexLocation q) throws AnalysisException
	{
		if (node.getOpaque()
				&& !q.getModule().equals(node.getLocation().getModule()))
			return false;
		if (node.getEqDef() != null)
			return true;
		return node.getType().apply(this, q);
	}

	@Override public Boolean defaultSInvariantType(SInvariantType node,
			ILexLocation question) throws AnalysisException
	{
		return node.getEqDef() != null;
	}

	@Override public Boolean defaultPType(PType node, ILexLocation q)
			throws AnalysisException
	{
		return false;
	}

	@Override public Boolean createNewReturnValue(INode node, ILexLocation q)
			throws AnalysisException
	{
		return false;
	}

	@Override public Boolean createNewReturnValue(Object node, ILexLocation q)
			throws AnalysisException
	{
		return false;
	}
}
