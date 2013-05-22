package org.overture.codegen.merging;

import java.io.StringWriter;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;

public class CG
{
	public static String format(INode field) throws AnalysisException
	{
		MergeVisitor mergeVisitor = new MergeVisitor();
		StringWriter writer = new StringWriter();
		field.apply(mergeVisitor, writer);

		return writer.toString();
	}	
}
