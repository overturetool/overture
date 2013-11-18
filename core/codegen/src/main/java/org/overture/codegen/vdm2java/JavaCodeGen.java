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
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.DependencyAnalysis;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.utils.InvalidNamesException;
import org.overture.codegen.utils.NameViolation;
import org.overture.codegen.utils.ReservedWordsComparison;
import org.overture.codegen.utils.TypenameComparison;
import org.overture.codegen.utils.VdmAstAnalysis;
import org.overture.codegen.visitor.OoAstGenerator;

public class JavaCodeGen
{
	public static final TemplateStructure JAVA_TEMPLATE_STRUCTURE = new TemplateStructure(IJavaCodeGenConstants.JAVA_TEMPLATES_ROOT_FOLDER);
	
	private OoAstGenerator generator;
	
	public static final String[] RESERVED_WORDS = {
		
			//Java Keywords
			"abstract", "continue", "for", "new",
			"switch", "assert", "default", "goto", "package", "synchronized",
			"boolean", "do", "if", "private", "this", "break", "double",
			"implements", "protected", "throw", "byte", "else", "import",
			"public", "throws", "case", "enum", "instanceof", "return",
			"transient", "catch", "extends", "int", "short", "try", "char",
			"final", "interface", "static", "void", "class", "finally", "long",
			"strictfp", "volatile", "const", "float", "native", "super",
			"while"
	};
	
	public static final String[] RESERVED_TYPE_NAMES = {
		//Classes used from the Java standard library
		"Long", "Double", "Character", "String", "List", "Set"
	};
	
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
			MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.TEMPLATE_CALLABLES);
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
	
	public GeneratedModule generateJavaCodeGenUtils() throws IOException
	{
		StringBuffer utilsContent = GeneralUtils.readFromFile(IJavaCodeGenConstants.JAVA_UTILS_ROOT_FOLDER
				+ IText.SEPARATOR_CHAR + IJavaCodeGenConstants.UTILS_FILE + IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
		
		if (utilsContent != null)
		{
			StringBuffer utilsGenerated = new StringBuffer();
			utilsGenerated.append(IJavaCodeGenConstants.UTILS_PACKAGE + IText.NEW_LINE + IText.NEW_LINE);
			
			utilsGenerated.append(utilsContent);
			
			return new GeneratedModule(IJavaCodeGenConstants.UTILS_FILE, utilsGenerated.toString());
		}
		
		return null;
	}

	public List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists) throws AnalysisException, InvalidNamesException
	{
		validateVdmModelNames(mergedParseLists);
		
		List<AClassDeclCG> classes = new ArrayList<AClassDeclCG>();

		for (SClassDefinition classDef : mergedParseLists)
		{
			classes.add(generator.generateFrom(classDef));
		}

		MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.TEMPLATE_CALLABLES);

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
	
	public String generateJavaFromVdmExp(PExp exp) throws AnalysisException
	{
		//There is no name validation here.
		
		PExpCG expCg = generator.generateFrom(exp);

		MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, JavaCodeGen.TEMPLATE_CALLABLES);
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

	public void generateJavaSourceFile(File file, GeneratedModule generatedModule)
	{
		JavaCodeGenUtil.saveJavaClass(file, generatedModule.getName() + IJavaCodeGenConstants.JAVA_FILE_EXTENSION, generatedModule.getContent());
	}
	
	public void generateJavaSourceFiles(File file, List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			generateJavaSourceFile(file, classCg);
		}
	}
	
	private static void validateVdmModelNames(List<? extends INode> mergedParseLists) throws AnalysisException, InvalidNamesException
	{
		List<NameViolation> reservedWordViolations = VdmAstAnalysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(RESERVED_WORDS));
		List<NameViolation> typenameViolations = VdmAstAnalysis.usesIllegalNames(mergedParseLists, new TypenameComparison(RESERVED_TYPE_NAMES));
		
		if(!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty())
			throw new InvalidNamesException("The model either uses words that are reserved by Java or declares VDM types that uses Java type names", reservedWordViolations, typenameViolations);
	}
}
