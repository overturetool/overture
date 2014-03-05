package org.overture.codegen.analysis.violations;

import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SNumericBinaryBase;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.assistant.ExpAssistantCG;

public class ModelingViolationAnalysis extends ViolationAnalysis
{
		@Override
		public void defaultInINode(INode node) throws AnalysisException
		{
			if(node instanceof AClassClassDefinition)
			{
				AClassClassDefinition classDef = (AClassClassDefinition) node;
				
				if(classDef.getSupernames().size() > 1)
					addViolation(new Violation("Multiple inheritance not supported.", classDef.getLocation()));
				
				DeclAssistantCG declAssistant = new DeclAssistantCG();
				
				Set<ILexNameToken> overloadedNameTokens = declAssistant.getOverloadedMethodNames(classDef);
				
				if(overloadedNameTokens.size() > 0)
				{
					for (ILexNameToken name : overloadedNameTokens)
					{
						addViolation(new Violation("Overloading of operation and function names is not allowed. Caused by: " + classDef.getName() + "." + name.getName(), name.getLocation()));
					}
				}
			}
			else if(node instanceof AFuncInstatiationExp)
			{
				AFuncInstatiationExp exp = (AFuncInstatiationExp) node;
				
				if(exp.getImpdef() != null)
					addViolation(new Violation("Implicit functions cannot be instantiated since they are not supported.", exp.getLocation()));
			}
			else if(node instanceof ADivNumericBinaryExp || 
					node instanceof AModNumericBinaryExp ||
					node instanceof ARemNumericBinaryExp)
			{
				SNumericBinaryBase binBinaryExp = (SNumericBinaryBase) node;

				if(operandsAreIntegerTypes(binBinaryExp))
					addViolation(new Violation("Expression requires that operands are guaranteed to be integers", binBinaryExp.getLocation()));
			}
		}
		
		private boolean operandsAreIntegerTypes(SNumericBinaryBase exp)
		{
			PExp leftExp = exp.getLeft();
			PExp rightExp = exp.getRight();

			return !ExpAssistantCG.isIntegerType(leftExp) || !ExpAssistantCG.isIntegerType(rightExp);
		}
}
