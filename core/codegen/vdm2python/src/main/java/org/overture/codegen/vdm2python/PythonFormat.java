package org.overture.codegen.vdm2python;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;

import java.io.StringWriter;

public class PythonFormat
{
	private static final String PYTHON_FORMAT_KEY = "PythonFormat";

	private MergeVisitor codeEmitter;

	public PythonFormat(String root)
	{
		TemplateCallable[] callables = new TemplateCallable[] {
				new TemplateCallable(PYTHON_FORMAT_KEY, this) };
		this.codeEmitter = new MergeVisitor(new TemplateManager(root), callables);
	}

	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(codeEmitter, writer);

		return writer.toString();
	}

	public MergeVisitor getCodeEmitter()
	{
		return codeEmitter;
	}
}
