package org.overture.codegen.vdm2java;

import java.io.File;
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
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.ir.IRClassDeclStatus;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRExpStatus;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.TransformationVisitor;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.funcvalues.FunctionValueVisitor;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.patterns.IgnorePatternTransformation;
import org.overture.codegen.trans.uniontypes.UnionTypeTransformation;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedModule;

public class JavaCodeGen
{
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = "JavaTemplates";
	
	public static final String[] CLASSES_NOT_TO_BE_GENERATED = IRConstants.CLASS_NAMES_USED_IN_VDM;
	
	public static final TemplateStructure JAVA_TEMPLATE_STRUCTURE = new TemplateStructure(JAVA_TEMPLATES_ROOT_FOLDER);
	
	public static final String[] RESERVED_TYPE_NAMES = {
		//Classes used from the Java standard library
		"Utils", "Record","Long", "Double", "Character", "String", "List", "Set"
	};
	
	public final static TempVarPrefixes varPrefixes = new TempVarPrefixes();
	
	private IRGenerator generator;
	private IRInfo irInfo;
	private JavaFormat javaFormat;
	
	public static final String IGNORE_PATTERN_NAME_PREFIX = "ignore_";
	public static final String INTERFACE_NAME_PREFIX = "Func_";
	public static final String TEMPLATE_TYPE_PREFIX = "T_";
	public static final String EVAL_METHOD_PREFIX = "eval";
	public static final String PARAM_NAME_PREFIX = "param_";
	
	private static final String QUOTES = "quotes";
	
	public JavaCodeGen()
	{
		init(null);
	}
	
	public void setJavaSettings(JavaSettings javaSettings)
	{
		this.javaFormat.setJavaSettings(javaSettings);
	}

	public JavaCodeGen(ILogger log)
	{
		init(log);
	}

	private void init(ILogger log)
	{
		initVelocity();
		this.generator = new IRGenerator(log);
		this.irInfo = generator.getIRInfo();
		this.javaFormat = new JavaFormat(varPrefixes, irInfo);
	}
	
	public void setSettings(IRSettings settings)
	{
		irInfo.setSettings(settings);
	}

	private void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class" , "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}
	
	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}

	public GeneratedModule generateJavaFromVdmQuotes()
	{
		try
		{
			StringWriter writer = new StringWriter();

			AInterfaceDeclCG quotesInterface = generator.getQuotes();
			quotesInterface.setPackage(QUOTES);

			if (quotesInterface.getFields().isEmpty())
				return null; // Nothing to generate

			javaFormat.init();
			quotesInterface.apply(javaFormat.getMergeVisitor(), writer);
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
			List<SClassDefinition> mergedParseLists) throws AnalysisException,
			InvalidNamesException, UnsupportedModelingException
	{
		List<SClassDefinition> toBeGenerated = new LinkedList<SClassDefinition>();
		
		for (SClassDefinition classDef : mergedParseLists)
		{
			if (shouldBeGenerated(classDef))
			{
				toBeGenerated.add(classDef);
			}
			else
			{
				String className = classDef.getName().getFullName();
				
				if (!classIsLibrary(classDef))
				{
					Logger.getLog().println("Skipping class based on library class: " + className);
				}
			}
		}
		
		validateVdmModelNames(toBeGenerated);
		validateVdmModelingConstructs(toBeGenerated);

		List<IRClassDeclStatus> statuses = new ArrayList<IRClassDeclStatus>();

		for (SClassDefinition classDef : toBeGenerated)
		{
			if (!shouldBeGenerated(classDef))
			{
				continue;
			}

			statuses.add(generator.generateFrom(classDef));
		}

		javaFormat.setClasses(getClassDecls(statuses));
		
		TransformationAssistantCG transformationAssistant = new TransformationAssistantCG(irInfo, varPrefixes);
		FunctionValueAssistant functionValueAssistant = new FunctionValueAssistant();
		
		IgnorePatternTransformation ignoreTransformation = new IgnorePatternTransformation(transformationAssistant, IGNORE_PATTERN_NAME_PREFIX);
		UnionTypeTransformation unionTypeTransformation = new UnionTypeTransformation(transformationAssistant, irInfo);
		FunctionValueVisitor funcValVisitor = new FunctionValueVisitor(transformationAssistant, functionValueAssistant, INTERFACE_NAME_PREFIX, TEMPLATE_TYPE_PREFIX, EVAL_METHOD_PREFIX, PARAM_NAME_PREFIX);
		ILanguageIterator langIterator = new JavaLanguageIterator(transformationAssistant, irInfo.getTempVarNameGen(), varPrefixes);
		TransformationVisitor transVisitor = new TransformationVisitor(irInfo, varPrefixes, transformationAssistant, langIterator);
		
		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();
		for (IRClassDeclStatus status : statuses)
		{
			try
			{
				AClassDeclCG classCg = status.getClassCg();
				String className = status.getClassName();
				
				if (status.canBeGenerated())
				{
					classCg.apply(ignoreTransformation);
					classCg.apply(funcValVisitor);
					classCg.apply(transVisitor);
					classCg.apply(unionTypeTransformation);
				}
				else
				{
					generated.add(new GeneratedModule(className, status.getUnsupportedNodes()));					
				}

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for class "
						+ status.getClassName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
		}
		
		MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();
		FunctionValueAssistant functionValue = funcValVisitor.getFunctionValueAssistant();
		javaFormat.setFunctionValueAssistant(functionValue);
		
		for (IRClassDeclStatus status : statuses)
		{
			if(!status.canBeGenerated())
				continue;
			
			StringWriter writer = new StringWriter();
			AClassDeclCG classCg = status.getClassCg();
			String className = status.getClassName();

			javaFormat.init();
			
			try
			{
				classCg.apply(mergeVisitor, writer);

				if (mergeVisitor.hasMergeErrors())
				{
					generated.add(new GeneratedModule(className, mergeVisitor.getMergeErrors()));
				}else
				{
					String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
					generated.add(new GeneratedModule(className, formattedJavaCode));
				}
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error generating code for class "
						+ status.getClassName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
		}
		
		List<AInterfaceDeclCG> funcValueInterfaces = functionValue.getFunctionValueInterfaces();
		
		for(AInterfaceDeclCG funcValueInterface : funcValueInterfaces)
		{
			StringWriter writer = new StringWriter();
			
			try
			{
				funcValueInterface.apply(mergeVisitor, writer);
				String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
				generated.add(new GeneratedModule(funcValueInterface.getName(), formattedJavaCode));
				
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error generating code for function value interface "
						+ funcValueInterface.getName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping interface..");
				e.printStackTrace();
			}
		}
		
		javaFormat.clearFunctionValueAssistant();
		javaFormat.clearClasses();

		return generated;
	}

	private List<AClassDeclCG> getClassDecls(List<IRClassDeclStatus> statuses)
	{
		List<AClassDeclCG> classDecls = new LinkedList<AClassDeclCG>();

		for (IRClassDeclStatus status : statuses)
		{
			classDecls.add(status.getClassCg());
		}

		return classDecls;
	}

	public Generated generateJavaFromVdmExp(PExp exp) throws AnalysisException
	{
		// There is no name validation here.

		IRExpStatus expStatus = generator.generateFrom(exp);

		StringWriter writer = new StringWriter();

		try
		{
			SExpCG expCg = expStatus.getExpCg();

			if (expStatus.canBeGenerated())
			{
				javaFormat.init();
				MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();
				expCg.apply(mergeVisitor, writer);
				
				if(mergeVisitor.hasMergeErrors())
				{
					return new Generated(mergeVisitor.getMergeErrors());
				}
				else
				{
					String code = writer.toString();
					
					return new Generated(code); 
				}
			} else
			{

				return new Generated(expStatus.getUnsupportedNodes());
			}

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Could not generate expression: "
					+ exp);
			e.printStackTrace();
			return null;
		}
	}

	public void generateJavaSourceFile(File outputFolder, GeneratedModule generatedModule)
	{
		if(generatedModule != null && generatedModule.canBeGenerated() && !generatedModule.hasMergeErrors())
		{
			JavaCodeGenUtil.saveJavaClass(outputFolder, generatedModule.getName() + IJavaCodeGenConstants.JAVA_FILE_EXTENSION, generatedModule.getContent());
		}
	}
	
	public void generateJavaSourceFiles(File outputFolder, List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			generateJavaSourceFile(outputFolder, classCg);
		}
	}
	
	private void validateVdmModelNames(List<? extends INode> mergedParseLists) throws AnalysisException, InvalidNamesException
	{
		AssistantManager assistantManager = generator.getIRInfo().getAssistantManager();
		VdmAstAnalysis analysis = new VdmAstAnalysis(assistantManager);
		
		Set<Violation> reservedWordViolations = analysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(IJavaCodeGenConstants.RESERVED_WORDS, assistantManager));
		Set<Violation> typenameViolations = analysis.usesIllegalNames(mergedParseLists, new TypenameComparison(RESERVED_TYPE_NAMES, assistantManager));
		
		String[] generatedTempVarNames = GeneralUtils.concat(IRConstants.GENERATED_TEMP_NAMES, varPrefixes.GENERATED_TEMP_NAMES);
		
		Set<Violation> tempVarViolations = analysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(generatedTempVarNames, assistantManager));
		
		if(!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty() || !tempVarViolations.isEmpty())
			throw new InvalidNamesException("The model either uses words that are reserved by Java, declares VDM types that uses Java type names or uses variable names that potentially conflicts with code generated temporary variable names", reservedWordViolations, typenameViolations, tempVarViolations);
	}
	
	private void validateVdmModelingConstructs(List<? extends INode> mergedParseLists) throws AnalysisException, UnsupportedModelingException
	{
		VdmAstAnalysis analysis = new VdmAstAnalysis(generator.getIRInfo().getAssistantManager());
		
		Set<Violation> violations = analysis.usesUnsupportedModelingConstructs(mergedParseLists);
		
		if(!violations.isEmpty())
			throw new UnsupportedModelingException("The model uses modeling constructs that are not supported for Java code Generation", violations);
	}
	
	private boolean shouldBeGenerated(SClassDefinition classDef)
	{
		if(classIsLibrary(classDef))
		{
			return false;
		}
		
		for(SClassDefinition superDef : classDef.getSuperDefs())
		{
			if(classIsLibrary(superDef))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private boolean classIsLibrary(SClassDefinition classDef)
	{
		String className = classDef.getName().getName();
		
		for(int i = 0; i < CLASSES_NOT_TO_BE_GENERATED.length; i++)
		{
			if(CLASSES_NOT_TO_BE_GENERATED[i].equals(className))
			{
				return true;
			}
		}
		
		return false;
	}
}
