package org.overture.pog.visitors;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.pog.utility.Substitution;

public interface IVariableSubVisitor extends
		IQuestionAnswer<Substitution, PExp>
{

}
