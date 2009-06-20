package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.expressions.BooleanLiteral;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.references.VariableReference;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.DLTKConverter;
import org.overturetool.vdmj.expressions.ApplyExpression;
import org.overturetool.vdmj.expressions.BooleanLiteralExpression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.IntegerLiteralExpression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.IfStatement;

public class VDMJASTUtil {

	public static CallExpression createCallExpression(CallStatement callStatement, DLTKConverter converter)
	{
		String name = callStatement.name.name;
		int start = getStartPos(callStatement.location, converter);
		int end = getEndPos(callStatement.location, converter);
		// TODO receiver???
		if (callStatement.args.size() > 0)
		{
			CallArgumentsList argList = processArgumentList(callStatement.args, converter);
			return new CallExpression(start, end, null, name, argList);
		}	
		else
		{
			return new CallExpression(start, end, null, name, null);			
		}
	}
	
	public static CallExpression createCallExpression(ApplyExpression applyExpression, DLTKConverter converter)
	{
		String name = applyExpression.root.toString();
		int start = getStartPos(applyExpression.root.location, converter);
		int end = getEndPos(applyExpression.root.location, converter);

		// TODO receiver???
		if (applyExpression.args.size() > 0)
		{
			CallArgumentsList argList = processArgumentList(applyExpression.args, converter);
			return new CallExpression(start, end, null, name, argList);
		}	
		else
		{
			return new CallExpression(start, end, null, name, null);			
		}
	}
	
	
	public static VariableReference createVariableReference(String name, LexLocation location, DLTKConverter converter){
		int start = getStartPos(location, converter);
		int end = getEndPos(location, converter);
		return new VariableReference(start, end, name);
	}
	
	private static CallArgumentsList processArgumentList(ExpressionList args, DLTKConverter converter)
	{
		int startPos = getStartPos(args.get(0).location, converter);
		int endPos = getEndPos(args.get(args.size()-1).location, converter);
		
		// TODO add args
		CallArgumentsList argList = new CallArgumentsList(startPos, endPos);
		return argList;
	}

	public static BooleanLiteral createBooleanLiteral(BooleanLiteralExpression exp, DLTKConverter converter) {
		int start = getStartPos(exp.location, converter);
		int end = getEndPos(exp.location, converter);
		return new BooleanLiteral(start, end, exp.value.value);
	}

	public static NumericLiteral createNumericLiteral(IntegerLiteralExpression exp, DLTKConverter converter) {
		int start = getStartPos(exp.location, converter);
		int end = getEndPos(exp.location, converter);
		return new NumericLiteral(start, end, exp.value.value);
	}
	
	private static int getStartPos(LexLocation loc, DLTKConverter converter)
	{
		return converter.convert(loc.startLine, loc.startPos) - 1;
	}
	
	private static int getEndPos(LexLocation loc, DLTKConverter converter)
	{
		return converter.convert(loc.endLine, loc.endPos);
	}

	public static OvertureIfStatement createIfStatement(IfStatement statement, DLTKConverter converter) {
		return null;
	}
}

