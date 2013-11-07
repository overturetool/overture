package org.overture.codegen.vdm2java;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.analysis.DependencyAnalysis;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.visitor.OoAstGenerator;

public class JavaCodeGen
{
	private OoAstGenerator generator;
	
	public final static TemplateCallable[] TEMPLATE_CALLABLES = new TemplateCallable[]
	{
			new TemplateCallable("JavaFormat", JavaFormat.class),
			new TemplateCallable("DependencyAnalysis", DependencyAnalysis.class)
	};
	
	public JavaCodeGen()
	{
		init(null);
	}

	public JavaCodeGen(ILogger log)
	{
		init(log);
	}

	private void init(ILogger log)
	{
		initVelocity();
		this.generator = new OoAstGenerator(log);
	}

	private void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class" , "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}

	public GeneratedModule generateJavaFromVdmQuotes()
	{
		try
		{
			MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.TEMPLATE_CALLABLES);
			StringWriter writer = new StringWriter();

			AInterfaceDeclCG quotesInterface = generator.getQuotes();

			if (quotesInterface.getFields().size() == 0)
				return null; // Nothing to generate

			quotesInterface.apply(mergeVisitor, writer);
			String code = writer.toString();

			String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(code);
			
			return new GeneratedModule(quotesInterface.getName(), formattedJavaCode);

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Error when formatting quotes: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		List<AClassDeclCG> classes = new ArrayList<AClassDeclCG>();

		for (SClassDefinition classDef : mergedParseLists)
		{
			classes.add(generator.generateFrom(classDef));
		}

		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.TEMPLATE_CALLABLES);

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();
		for (AClassDeclCG classCg : classes)
		{
			StringWriter writer = new StringWriter();
			try
			{
				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(code);
				
				generated.add(new GeneratedModule(classCg.getName(), formattedJavaCode));

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for class "
						+ classCg.getName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
		}

		return generated;
	}

	public void generateJavaSourceFiles(List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			JavaCodeGenUtil.saveJavaClass(classCg.getName() + ".java", classCg.getContent());
		}
	}

	public void generateJavaCodeGenUtils()
	{
		String utilsPath = "src\\main\\java\\org\\overture\\codegen\\generated\\collections";
		String targetr = "target\\sources";

		try
		{
			GeneralCodeGenUtils.copyDirectory(new File(utilsPath), new File(targetr));
		} catch (IOException e)
		{
			Logger.getLog().printErrorln("Error when generating CG utils");
			e.printStackTrace();
		}
		GeneralCodeGenUtils.replaceInFile(targetr + "\\Utils.java", "package org.overture.codegen.generated.collections;", "");
	}

	public String generateJavaFromVdmExp(PExp exp) throws AnalysisException
	{
		PExpCG expCg = generator.generateFrom(exp);

		MergeVisitor mergeVisitor = new MergeVisitor(JavaCodeGen.TEMPLATE_CALLABLES);
		StringWriter writer = new StringWriter();

		try
		{
			expCg.apply(mergeVisitor, writer);
			String code = writer.toString();

			return code;
		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Could not generate expression: "
					+ exp);
			e.printStackTrace();
			return null;
		}
	}
}
