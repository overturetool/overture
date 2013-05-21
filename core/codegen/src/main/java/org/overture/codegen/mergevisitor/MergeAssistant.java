package org.overture.codegen.mergevisitor;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SBasicTypeCG;
import org.overture.codegen.lookup.OperatorInfo;
import org.overture.codegen.lookup.OperatorLookup;
import org.overture.codegen.templates.TemplateParameters;
import org.overture.codegen.templates.UtilityTemplatesId;
import org.overture.codegen.visitor.CodeGenContext;

public class MergeAssistant
{

	private MergeVisitor mergeVisitor;

	private OperatorLookup opLookup;

	public MergeAssistant(MergeVisitor mergeVisitor)
	{
		this.mergeVisitor = mergeVisitor;
		this.opLookup = OperatorLookup.GetInstance();
	}

	public void handleUnaryExp(SUnaryExpCG node, StringWriter question)
			throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();

		StringWriter value = new StringWriter();
		node.getExp().apply(mergeVisitor, value);

		context.put(TemplateParameters.UNARY_EXP_VALUE, value.toString());

		Template plusUnaryTemplate = mergeVisitor.getTemplateManager().getTemplate(node.getClass());
		plusUnaryTemplate.merge(context.getVelocityContext(), question);
	}

	public void handleBinaryExp(SBinaryExpCG node, StringWriter question)
			throws AnalysisException
	{
		handleBinaryExp(node, question, null);
	}
	
	public void handleBinaryExp(SBinaryExpCG node, StringWriter question, IContextManipulator manipulator) throws AnalysisException
	{
		CodeGenContext context = new CodeGenContext();

		StringWriter leftValue = new StringWriter();
		formatExp(node, node.getLeft(), leftValue);
		context.put(TemplateParameters.BIN_EXP_LEFT_OPERAND, leftValue.toString());

		StringWriter rightValue = new StringWriter();
		formatExp(node, node.getRight(), rightValue);
		context.put(TemplateParameters.BIN_EXP_RIGHT_OPERAND, rightValue.toString());
		
		if(manipulator != null)
			manipulator.manipulate(context);

		Template plusBinaryTemplate = mergeVisitor.getTemplateManager().getTemplate(node.getClass());
		plusBinaryTemplate.merge(context.getVelocityContext(), question);

	}

	public void handleBasicType(SBasicTypeCG node, StringWriter question)
	{
		Template classTemplate = mergeVisitor.getTemplateManager().getTemplate(node.getClass());
		classTemplate.merge(new VelocityContext(), question);
	}

	private void formatExp(SBinaryExpCG parent, PExpCG child,
			StringWriter question) throws AnalysisException
	{
		StringWriter generatedExpression = new StringWriter();
		child.apply(mergeVisitor, generatedExpression);

		if (childExpHasLowerPrecedence(parent, child))
		{
			Template expWrapTemplate = mergeVisitor.getTemplateManager().getTemplate(UtilityTemplatesId.EXPS_UTIL);
			CodeGenContext context = new CodeGenContext();
			context.put(TemplateParameters.BIN_EXP_WRAPPED, generatedExpression.toString());
			expWrapTemplate.merge(context.getVelocityContext(), question);
		} else
		{
			question.append(generatedExpression.toString());
		}
	}

	private boolean childExpHasLowerPrecedence(SBinaryExpCG parent, PExpCG child)
	{
		if (!(child instanceof SBinaryExpCG))
			return false;

		OperatorInfo parentOpInfo = opLookup.find(parent.getClass());

		SBinaryExpCG binExpChild = (SBinaryExpCG) child;
		OperatorInfo childInfo = opLookup.find(binExpChild.getClass());

		return childInfo.getPrecedence() < parentOpInfo.getPrecedence();
	}

	public boolean isIntegerType(PExpCG exp)
	{
		PTypeCG type = exp.getType();

		return type instanceof AIntNumericBasicTypeCG
				&& !(exp instanceof ARealLiteralExpCG);

		// wrt !(exp instanceof ARealLiteralExpCG)
		// Expressions like 1.0 are considered real literal
		// expressions of type NatOneNumericBasicType in astv2
	}

}
