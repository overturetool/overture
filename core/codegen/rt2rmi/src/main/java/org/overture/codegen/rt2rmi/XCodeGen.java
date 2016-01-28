package org.overture.codegen.rt2rmi;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.utils.GeneratedData;


//FIXME: Eventually this class must be deleted
public class XCodeGen extends CodeGenBase {
	
	public LinkedList<AFieldDeclCG> generateXFromVdm(List<SClassDefinition> ast)
			throws AnalysisException
	{
		List<IRStatus<PCG>> statuses = new LinkedList<>();
		
		IRInfo ir = generator.getIRInfo();
		ir.getSettings().setCharSeqAsString(true);
		
		// This is run pr. class
		for(SClassDefinition node : ast)
		{	
			// Try to produce the IR
			IRStatus<PCG> status = generator.generateFrom(node);
			System.out.println("Running..");
			
			// If it was successful, then status is different from null
			if(status != null)
			{
				statuses.add(status);
			}
			
		}
		
		// By now we have the IR in 'statuses'
		
		//List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
		
		// Event notification
		//statuses = initialIrEvent(statuses);
		//statuses = filter(statuses, generated);
		
		//this.transAssistant = new TransAssistantCG(generator.getIRInfo(), varPrefixes);
		
		List<IRStatus<AModuleDeclCG>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclCG.class);
		List<IRStatus<PCG>> modulesAsNodes = IRStatus.extract(moduleStatuses);
		
		List<IRStatus<ASystemClassDeclCG>> classStatuses = IRStatus.extract(modulesAsNodes, ASystemClassDeclCG.class);
		classStatuses.addAll(IRStatus.extract(statuses, ASystemClassDeclCG.class));
		
		
		List<ASystemClassDeclCG> classes = getClassDecls(classStatuses);
		
		System.out.println("jj");
		
		LinkedList<AFieldDeclCG> fields = null;
		
		for(ASystemClassDeclCG sys: classes){
			fields = sys.getFields();
		}
		
		return fields;
		
//		FuncValAssistant functionValueAssistant = new FuncValAssistant();
//		
//		// Transform IR	
//		XTransSeries xTransSeries = new XTransSeries(this);
//		List<DepthFirstAnalysisAdaptor> transformations = xTransSeries.consAnalyses(classes, functionValueAssistant);
//		
//		// Generate IR to syntax (generate code)
//		
//		GeneratedData data = new GeneratedData();
//		
//		// Add generated code to 'data'
//		//templateManager = new TemplateManager(new TemplateStructure("MyTemplates"));
//		//MergeVisitor mergeVisitor = new MergeVisitor(templateManager, new TemplateCallable[]{new TemplateCallable("CGh", new CGHelper())});
//		
//		XFormat my_formatter = new XFormat(varPrefixes);
//		
//		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();
//		
//		for (DepthFirstAnalysisAdaptor trans : transformations)
//		{
//			for (IRStatus<AClassDeclCG> status : IRStatus.extract(statuses,AClassDeclCG.class))
//			{
//				try
//				{
//					if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName()))
//					{
//						generator.applyPartialTransformation(status, trans);
//					}
//
//				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
//				{
//					Logger.getLog().printErrorln("Error when generating code for class "
//							+ status.getIrNodeName() + ": " + e.getMessage());
//					Logger.getLog().printErrorln("Skipping class..");
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		for(IRStatus<AClassDeclCG> status : IRStatus.extract(statuses,AClassDeclCG.class))
//		{
//			StringWriter writer = new StringWriter();
//			AClassDeclCG classCg = status.getIrNode();
//
//			try {
//				classCg.apply(my_formatter.GetMergeVisitor(), writer);
//				
//				GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), classCg, writer.toString());
//				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
//				generated.add(generatedModule);
//				for(IrNodeInfo m: my_formatter.GetMergeVisitor().getUnsupportedInTargLang())
//				{
//					System.out.println(m.toString());
//				}
//				for(Exception m: my_formatter.GetMergeVisitor().getMergeErrors())
//				{
//					System.out.println(m.toString());
//				}
//			} catch (org.overture.codegen.cgast.analysis.AnalysisException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		//data.setClasses(generated);
		
		//return data;
	}
	
	private List<ASystemClassDeclCG> getClassDecls(
			List<IRStatus<ASystemClassDeclCG>> statuses)
	{
		List<ASystemClassDeclCG> classDecls = new LinkedList<ASystemClassDeclCG>();

		for (IRStatus<ASystemClassDeclCG> status : statuses)
		{
			classDecls.add(status.getIrNode());
		}

		return classDecls;
	}

	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PCG>> statuses) throws AnalysisException
	{
		throw new RuntimeException("Implement me!");
	}
}