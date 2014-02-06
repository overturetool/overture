package org.overture.codegen.vdm2java;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.GeneratedVarComparison;
import org.overture.codegen.analysis.violations.InvalidNamesException;
import org.overture.codegen.analysis.violations.ReservedWordsComparison;
import org.overture.codegen.analysis.violations.TypenameComparison;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.VdmAstAnalysis;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.IText;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.ooast.ClassDeclStatus;
import org.overture.codegen.ooast.ExpStatus;
import org.overture.codegen.ooast.OoAstAnalysis;
import org.overture.codegen.ooast.OoAstGenerator;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedModule;

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
		"Utils", "Record","Long", "Double", "Character", "String", "List", "Set"
	};
	
	public static final String GENERATED_TEMP_VAR_NAME_PREFIX = "temp_";
	
	private static final String JAVA_FORMAT_KEY = "JavaFormat";
	private static final String OO_AST_ANALYSIS_KEY = "OoAstAnalysis";
	
	public final static TemplateCallable[] DEFAULT_TEMPLATE_CALLABLES = constructTemplateCallables(new JavaFormat(), OoAstAnalysis.class);
	
	public final static TemplateCallable[] constructTemplateCallables(Object javaFormat, Object ooAstAnalysis)
	{
		return new TemplateCallable[]{new TemplateCallable(JAVA_FORMAT_KEY, javaFormat), new TemplateCallable(OO_AST_ANALYSIS_KEY, ooAstAnalysis)};
	}
	
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
			MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, DEFAULT_TEMPLATE_CALLABLES);
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
	
	public List<GeneratedModule> generateJavaCodeGenUtils() throws IOException
	{
		List<GeneratedModule> utils = new LinkedList<GeneratedModule>();
		
		String utilsRoot = IJavaCodeGenConstants.JAVA_UTILS_ROOT_FOLDER + IText.SEPARATOR_CHAR;
		String fileExt = IJavaCodeGenConstants.JAVA_FILE_EXTENSION;
		
		String utilPath = utilsRoot + IJavaCodeGenConstants.UTILS_FILE + fileExt;
		String mathPath = utilsRoot + IJavaCodeGenConstants.MATH_FILE + fileExt;
		String ioPath = utilsRoot + IJavaCodeGenConstants.IO_FILE + fileExt;
		
		String[] paths = {utilPath, mathPath, ioPath};
		String[] filenames = {IJavaCodeGenConstants.UTILS_FILE, IJavaCodeGenConstants.MATH_FILE, IJavaCodeGenConstants.IO_FILE};
		
		for (int i = 0; i < paths.length; i++)
		{
			StringBuffer fileContent = GeneralUtils.readFromFile(paths[i]);

			if (fileContent != null)
			{
				StringBuffer generated = new StringBuffer();
				generated.append(IJavaCodeGenConstants.UTILS_PACKAGE
						+ IText.NEW_LINE + IText.NEW_LINE);

				generated.append(fileContent);

				utils.add(new GeneratedModule(filenames[i], generated.toString()));
			}
		}
		
		return utils;
	}
	
	public List<GeneratedModule> generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists) throws AnalysisException,
			InvalidNamesException, UnsupportedModelingException
	{
		List<SClassDefinition> toBeGenerated = new LinkedList<SClassDefinition>();
		for (SClassDefinition classDef : mergedParseLists)
			if (shouldBeGenerated(classDef.getName().getName()))
				toBeGenerated.add(classDef);

		validateVdmModelNames(toBeGenerated);
		
		validateVdmModelingConstructs(toBeGenerated);

		List<ClassDeclStatus> statuses = new ArrayList<ClassDeclStatus>();

		for (SClassDefinition classDef : toBeGenerated)
		{
			String className = classDef.getName().getName();

			if (!shouldBeGenerated(className))
				continue;

			statuses.add(generator.generateFrom(classDef));
		}

		MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, constructTemplateCallables(new JavaFormat(getClassDecls(statuses)), new OoAstAnalysis()));

		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();
		for (ClassDeclStatus status : statuses)
		{
			StringWriter writer = new StringWriter();
			try
			{
				AClassDeclCG classCg = status.getClassCg();

				classCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				String formattedJavaCode = "";

				if (status.canBeGenerated())
					formattedJavaCode = JavaCodeGenUtil.formatJavaCode(code);

				generated.add(new GeneratedModule(classCg.getName(), formattedJavaCode, status.getUnsupportedNodes()));

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for class "
						+ status.getClassCg().getName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
		}

		return generated;
	}

	private List<AClassDeclCG> getClassDecls(List<ClassDeclStatus> statuses)
	{
		List<AClassDeclCG> classDecls = new LinkedList<AClassDeclCG>();

		for (ClassDeclStatus status : statuses)
		{
			classDecls.add(status.getClassCg());
		}

		return classDecls;
	}

	public Generated generateJavaFromVdmExp(PExp exp) throws AnalysisException
	{
		// There is no name validation here.

		ExpStatus expStatus = generator.generateFrom(exp);

		MergeVisitor mergeVisitor = new MergeVisitor(JAVA_TEMPLATE_STRUCTURE, DEFAULT_TEMPLATE_CALLABLES);
		StringWriter writer = new StringWriter();

		try
		{
			PExpCG expCg = expStatus.getExpCg();

			if (expCg != null)
			{
				expCg.apply(mergeVisitor, writer);
				String code = writer.toString();

				return new Generated(code, expStatus.getUnsupportedNodes());
			} else
			{

				return new Generated("", expStatus.getUnsupportedNodes());
			}

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
		Set<Violation> reservedWordViolations = VdmAstAnalysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(RESERVED_WORDS));
		Set<Violation> typenameViolations = VdmAstAnalysis.usesIllegalNames(mergedParseLists, new TypenameComparison(RESERVED_TYPE_NAMES));
		Set<Violation> tempVarViolations = VdmAstAnalysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(new String[]{GENERATED_TEMP_VAR_NAME_PREFIX}));
		
		if(!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty() || !tempVarViolations.isEmpty())
			throw new InvalidNamesException("The model either uses words that are reserved by Java, declares VDM types that uses Java type names or uses variable names that potentially conflicts with code generated temporary variable names", reservedWordViolations, typenameViolations, tempVarViolations);
	}
	
	private static void validateVdmModelingConstructs(List<? extends INode> mergedParseLists) throws AnalysisException, UnsupportedModelingException
	{
		Set<Violation> violations = VdmAstAnalysis.usesUnsupportedModelingConstructs(mergedParseLists);
		
		if(!violations.isEmpty())
			throw new UnsupportedModelingException("The model uses modeling constructs that are not supported for Java code Generation", violations);
	}
	
	private static boolean shouldBeGenerated(String className)
	{
		for(int i = 0; i < IJavaCodeGenConstants.CLASSES_NOT_TO_BE_GENERATED.length; i++)
			if(IJavaCodeGenConstants.CLASSES_NOT_TO_BE_GENERATED[i].equals(className))
				return false;
		
		return true;
	}
}
