package org.overturetool.cgisa;

import java.io.StringWriter;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;

public class Isa
{
	private MergeVisitor mergeVisitor;
	
	public Isa(TemplateStructure templateStructure)
	{
		TemplateCallable[] templateCallables = new TemplateCallable[]{new TemplateCallable("Isa",this)};
		this.mergeVisitor = new MergeVisitor(new IsaTemplateManager(templateStructure), templateCallables);
	}
	
	
	
	
	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}




	public String trans(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

}
