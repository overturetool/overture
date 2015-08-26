package org.overture.codegen.vdm2x;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class XCodeGen extends CodeGenBase {

	
	
	public GeneratedData generateJavaFromVdm(List<SClassDefinition> ast)
			throws AnalysisException
	{
		List<IRStatus<org.overture.codegen.cgast.INode>> statuses = new LinkedList<>();
		for(SClassDefinition node : ast)
		{	
			// Try to produce the IR
			IRStatus<org.overture.codegen.cgast.INode> status = generator.generateFrom(node);
			
			if(status != null)
			{
				statuses.add(status);
			}
			
		}
		
		// By now we have the IR in 'statuses'
		
		// Transform IR	
		
		// Generate IR to syntax (generate code)
		
		GeneratedData data = new GeneratedData();
		
		// Add generated code to 'data'
		//templateManager = new TemplateManager(new TemplateStructure("MyTemplates"));
		//MergeVisitor mergeVisitor = new MergeVisitor(templateManager, new TemplateCallable[]{new TemplateCallable("CGh", new CGHelper())});
		
		XFormat my_formatter = new XFormat(varPrefixes);
		
		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
		
		for(IRStatus<AClassDeclCG> status : IRStatus.extract(statuses,AClassDeclCG.class))
		{
			StringWriter writer = new StringWriter();
			AClassDeclCG classCg = status.getIrNode();

			try {
				classCg.apply(my_formatter.GetMergeVisitor(), writer);
				
				GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), classCg, writer.toString());
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
				generated.add(generatedModule);
				
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		data.setClasses(generated);
		
		return data;
	}
}
