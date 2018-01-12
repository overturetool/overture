package org.overture.typechecker.utilities.type;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.types.*;

/**
 * Created by ldc on 31/05/17.
 */
public class IsOrderedVisitor
		extends QuestionAnswerAdaptor<ILexLocation, Boolean>
{

	@Override public Boolean caseANamedInvariantType(ANamedInvariantType node,
			ILexLocation q) throws AnalysisException
	{
		if (node.getOpaque()
				&& !q.getModule().equals(node.getLocation().getModule()))
			return false;
		if (node.getOrdDef() != null)
			return true;
		return node.getType().apply(this, q);
	}

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
		return node.getTypes().isEmpty();
	}

	@Override public Boolean caseAUnknownType(AUnknownType node, ILexLocation q)
			throws AnalysisException
	{
		return true;
	}

	@Override public Boolean caseAParameterType(AParameterType node,
			ILexLocation q) throws AnalysisException
	{
		return true;
	}

	@Override public Boolean caseAOptionalType(AOptionalType node,
			ILexLocation q) throws AnalysisException
	{
		return true;
	}

	@Override public Boolean defaultSInvariantType(SInvariantType node,
			ILexLocation question) throws AnalysisException
	{
		return node.getOrdDef() != null;
	}

	@Override public Boolean defaultSNumericBasicType(SNumericBasicType node,
			ILexLocation q) throws AnalysisException
	{
		return true;
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
