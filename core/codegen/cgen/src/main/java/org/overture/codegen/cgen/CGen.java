package org.overture.codegen.cgen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AClassHeaderDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FuncValAssistant;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class CGen extends CodeGenBase
{

	public GeneratedData generateCFromVdm(List<SClassDefinition> ast,
			File outputFolder) throws AnalysisException
	{
		List<IRStatus<org.overture.codegen.cgast.INode>> statuses = new LinkedList<>();

		// This is run pr. class
		for (SClassDefinition node : ast)
		{
			// Try to produce the IR
			IRStatus<org.overture.codegen.cgast.INode> status = generator.generateFrom(node);

			// If it was successful, then status is different from null
			if (status != null)
			{
				statuses.add(status);
			}

		}

		// By now we have the IR in 'statuses'

		// List<GeneratedModule> generated = new LinkedList<GeneratedModule>();

		// Event notification
		// statuses = initialIrEvent(statuses);
		// statuses = filter(statuses, generated);

		this.transAssistant = new TransAssistantCG(generator.getIRInfo());

		List<IRStatus<AModuleDeclCG>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclCG.class);
		List<IRStatus<org.overture.codegen.cgast.INode>> modulesAsNodes = IRStatus.extract(moduleStatuses);

		List<IRStatus<ADefaultClassDeclCG>> classStatuses = IRStatus.extract(modulesAsNodes, ADefaultClassDeclCG.class);
		classStatuses.addAll(IRStatus.extract(statuses, ADefaultClassDeclCG.class));
		List<ADefaultClassDeclCG> classes = getClassDecls(classStatuses);
		FuncValAssistant functionValueAssistant = new FuncValAssistant();

		
		
		// Transform IR
		CTransSeries xTransSeries = new CTransSeries(this);
		List<DepthFirstAnalysisAdaptor> transformations = xTransSeries.consAnalyses(classes, functionValueAssistant);

		// Generate IR to syntax (generate code)

		GeneratedData data = new GeneratedData();

		// Add generated code to 'data'
		// templateManager = new TemplateManager(new TemplateStructure("MyTemplates"));
		// MergeVisitor mergeVisitor = new MergeVisitor(templateManager, new TemplateCallable[]{new
		// TemplateCallable("CGh", new CGHelper())});

		CFormat my_formatter = new CFormat(generator.getIRInfo());

		List<GeneratedModule> generated = new LinkedList<GeneratedModule>();

		for (DepthFirstAnalysisAdaptor trans : transformations)
		{
			for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(statuses, ADefaultClassDeclCG.class))
			{
				try
				{
					if (!getInfo().getDeclAssistant().isLibraryName(status.getIrNodeName()))
					{
						generator.applyPartialTransformation(status, trans);
					}

				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage());
					Logger.getLog().printErrorln("Skipping class..");
					e.printStackTrace();
				}
			}
		}

		for (IRStatus<ADefaultClassDeclCG> status : IRStatus.extract(statuses, ADefaultClassDeclCG.class))
		{
			StringWriter writer = new StringWriter();
			ADefaultClassDeclCG classCg = status.getIrNode();

			try
			{
				printClass(classCg, my_formatter, outputFolder);
				generateClassHeader(classCg, my_formatter, outputFolder);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			/*
			try
			{
				classCg.apply(my_formatter.GetMergeVisitor(), writer);

				GeneratedModule generatedModule = new GeneratedModule(status.getIrNodeName(), classCg, writer.toString());
				generatedModule.setTransformationWarnings(status.getTransformationWarnings());
				generated.add(generatedModule);
				for (IrNodeInfo m : my_formatter.GetMergeVisitor().getUnsupportedInTargLang())
				{
					System.out.println(m.toString());
				}
				for (Exception m : my_formatter.GetMergeVisitor().getMergeErrors())
				{
					System.out.println(m.toString());
				}
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/
		}

		data.setClasses(generated);

		return data;
	}

	private List<ADefaultClassDeclCG> getClassDecls(
			List<IRStatus<ADefaultClassDeclCG>> statuses)
	{
		List<ADefaultClassDeclCG> classDecls = new LinkedList<ADefaultClassDeclCG>();

		for (IRStatus<ADefaultClassDeclCG> status : statuses)
		{
			classDecls.add(status.getIrNode());
		}

		return classDecls;
	}

	@SuppressWarnings("unchecked")
	private AClassHeaderDeclCG createClassHeader(ADefaultClassDeclCG ch)
	{
		AClassHeaderDeclCG res = new AClassHeaderDeclCG();

		res.setMethods((List<? extends AMethodDeclCG>) ch.getMethods().clone());

		return res;
	}

	public boolean isNull(INode node)
	{
		return node == null;
	}

	@SuppressWarnings("unchecked")
	private void generateClassHeader(ADefaultClassDeclCG cl, CFormat my_formatter,
			File output_dir) throws IOException,
					org.overture.codegen.cgast.analysis.AnalysisException
	{

		AClassHeaderDeclCG ch = new AClassHeaderDeclCG();

		for (AFieldDeclCG fi : cl.getFields())
		{
			fi.getAccess();
			fi.getInitial();
		}

		ch.setMethods((List<? extends AMethodDeclCG>) cl.getMethods().clone());
		ch.setFields((List<? extends AFieldDeclCG>) cl.getFields().clone());
		for (AMethodDeclCG m : ch.getMethods())
		{
			m.setBody(null);
			// m.getSourceNode().getVdmNode();
		}

		ch.setName(cl.getName().toString());
		StringWriter writer = new StringWriter();
		ch.apply(my_formatter.GetMergeVisitor(), writer);

		output_dir.mkdirs();

		// Print the class
		File file = new File(output_dir, cl.getName() + ".h");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(writer.toString());
		output.close();
	}

	private void printClass(ADefaultClassDeclCG cl, CFormat my_formatter,
			File output_dir)
					throws org.overture.codegen.cgast.analysis.AnalysisException,
					IOException
	{
		StringWriter writer = new StringWriter();
		cl.apply(my_formatter.GetMergeVisitor(), writer);

		output_dir.mkdirs();

		// Print the class
		File file = new File(output_dir, cl.getName() + ".c");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		output.write(writer.toString());
		output.close();
	}

}