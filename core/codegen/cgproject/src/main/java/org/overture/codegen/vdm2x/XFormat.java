package org.overture.codegen.vdm2x;

import java.io.StringWriter;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.TempVarPrefixes;

public class XFormat {
	
	private MergeVisitor mergeVisitor;

	public XFormat(TempVarPrefixes varPrefixes)
	{
		TemplateManager templateManager = new TemplateManager(new TemplateStructure("MyTemplates"));
		TemplateCallable[] templateCallables = new TemplateCallable[]{new TemplateCallable("XFormat", this)};
		this.mergeVisitor = new MergeVisitor(templateManager, templateCallables);
	}
	
	public String format(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

	public MergeVisitor GetMergeVisitor()
	{
		return mergeVisitor;
	}
}