package org.overture.codegen.merging;

import java.io.StringWriter;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.statements.AIfThenElseStmCG;
import org.overture.codegen.cgast.statements.AIfThenStmCG;
import org.overture.codegen.cgast.statements.PStmCG;

public class CG
{
	public static String format(INode field) throws AnalysisException
	{
		MergeVisitor mergeVisitor = new MergeVisitor();
		StringWriter writer = new StringWriter();
		field.apply(mergeVisitor, writer);

		return writer.toString();
	}
	
	public static String format(List<AFormalParamLocalDeclCG> params) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(params.size() <= 0)
			return "";
		
		AFormalParamLocalDeclCG firstParam = params.get(0);
		writer.append(CG.format(firstParam.getType()) + " " + firstParam.getName());
		
		for(int i = 1; i < params.size(); i++)
		{
			AFormalParamLocalDeclCG param = params.get(i);
			writer.append(", " + CG.format(param.getType()) + " " + param.getName());
		}
		return writer.toString();
	}
	
	public static String constructBody(AIfThenElseStmCG stm) throws AnalysisException
	{
		PStmCG elseBody = stm.getElseBody();
			
		if(elseBody == null)
			return "{\r\n}";
		
		String bodyFormatted = CG.format(elseBody);
		
		if(ommitCurlyBrackets(elseBody))
			return bodyFormatted;
		else
			return "{" + bodyFormatted + "}";
	}
	
	private static boolean ommitCurlyBrackets(INode node)
	{
		return node instanceof AIfThenStmCG || node instanceof AIfThenElseStmCG;
	}
}
