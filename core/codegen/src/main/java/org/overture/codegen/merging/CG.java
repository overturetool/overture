package org.overture.codegen.merging;

import java.io.StringWriter;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFormalParamLocalDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;

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
	
	public static String formatArgs(List<PExpCG> exps) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		
		if(exps.size() <= 0)
			return "";
		
		PExpCG firstExp = exps.get(0);
		writer.append(CG.format(firstExp));
		
		for(int i = 1; i < exps.size(); i++)
		{
			PExpCG exp = exps.get(i);
			writer.append(", " + CG.format(exp));
		}
		
		return writer.toString();
	}
	
	public static boolean isNull(INode node)
	{
		return node == null;
	}
	
//	public static void makeImage(File dotPath, INode node, String type,
//			File output) throws GraphVizException
//	{
//		DotGraphVisitor visitor = new DotGraphVisitor();
//		try
//		{
//			node.apply(visitor, null);
//		} catch (Throwable e)
//		{
//			// Ignore
//		}
//		GraphViz gv = new GraphViz();
//		gv.writeGraphToFile(gv.getGraph(visitor.getResultString(), type), output);
//	}
	
//	public static String constructElseBody(AIfStmCG stm) throws AnalysisException
//	{
//		PStmCG elseStm = stm.getElseStm();
//			
//		if(elseStm == null)
//			return "{\r\n}";
//		
//		String bodyFormatted = CG.format(elseBody);
//		
//		if(ommitCurlyBrackets(elseBody))
//			return bodyFormatted;
//		else
//			return "{" + bodyFormatted + "}";
//	}
//	
//	private static boolean ommitCurlyBrackets(INode node)
//	{
//		return node instanceof AIfThenStmCG || node instanceof AIfThenElseStmCG;
//	}
}
