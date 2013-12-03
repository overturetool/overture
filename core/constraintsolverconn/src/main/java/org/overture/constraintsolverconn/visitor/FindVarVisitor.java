package org.overture.constraintsolverconn.visitor;

import java.util.LinkedHashSet;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptorAnswer;
import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.AIntLiteralExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;

public class FindVarVisitor extends DepthFirstAnalysisAdaptorAnswer<INode> {

    public LinkedHashSet<String> getVars(PExp node) {
	LinkedHashSet<String> result = new LinkedHashSet<String>();

	if(node instanceof APlusNumericBinaryExp) {
	    result.add(((APlusNumericBinaryExp)node).getLeft().toString());
	    result.add(((APlusNumericBinaryExp)node).getRight().toString());
	} else if(node instanceof ASubtractNumericBinaryExp) {
	    result.add(((ASubtractNumericBinaryExp)node).getLeft().toString());
	    result.add(((ASubtractNumericBinaryExp)node).getRight().toString());
	} else if(node instanceof ATimesNumericBinaryExp) {
	    result.add(((ATimesNumericBinaryExp)node).getLeft().toString());
	    result.add(((ATimesNumericBinaryExp)node).getRight().toString());
	} else {
	    result.add(node.toString());
	}
	return result;

    }

    @Override
      public INode createNewReturnValue(INode node) //throws AnalysisException
      {
	return null;
      }

    @Override
      public INode createNewReturnValue(Object node) //throws AnalysisException
      {
	return null;
      }

    @Override
      public INode mergeReturns(INode original, INode new_)
      {
	return null;
      }

}
