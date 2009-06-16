package org.overturetool.eclipse.plugins.editor.core.internal.parser.ast;

import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.DLTKConverter;
import org.overturetool.vdmj.statements.CallStatement;

public class VDMJASTUtil {

	public static CallExpression createCallExpression(CallStatement callStatement, DLTKConverter converter)
	{
		String name = callStatement.name.name;
		int start = converter.convert(callStatement.location.startLine, callStatement.location.startPos) - 1;
		int end = converter.convert(callStatement.location.endLine, callStatement.location.endPos) - 1;
		// TODO receiver???
		if (callStatement.args.size() > 0)
		{
			CallArgumentsList argList = processArgumentList(callStatement, converter);
			return new CallExpression(start, end, null, name, argList);
		}	
		else
		{
			return new CallExpression(start, end, null, name, null);			
		}
	}
	
	private static CallArgumentsList processArgumentList(CallStatement callStatement, DLTKConverter converter)
	{
		int startPos = converter.convert(
				callStatement.args.get(0).location.startLine, 
				callStatement.args.get(0).location.startPos) -1;
		int endPos = converter.convert(
				callStatement.args.get(callStatement.args.size()-1).location.endLine,
				callStatement.args.get(callStatement.args.size()-1).location.endPos) - 1;
		
		// TODO add args
		CallArgumentsList argList = new CallArgumentsList(startPos, endPos);
		return argList;
	}
}

