/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.vdm2java;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.codegen.analysis.violations.GeneratedVarComparison;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.ReservedWordsComparison;
import org.overture.codegen.analysis.violations.TypenameComparison;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.VdmAstAnalysis;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
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
import org.overture.codegen.trans.IPostCheckCreator;
import org.overture.codegen.trans.IsExpTransformation;
import org.overture.codegen.trans.PostCheckTransformation;
import org.overture.codegen.trans.PreCheckTransformation;
import org.overture.codegen.trans.PrePostTransformation;
import org.overture.codegen.trans.SeqConversionTransformation;
import org.overture.codegen.trans.TempVarPrefixes;
import org.overture.codegen.trans.TransformationVisitor;
import org.overture.codegen.trans.assistants.TransformationAssistantCG;
import org.overture.codegen.trans.conc.MainClassConcTransformation;
import org.overture.codegen.trans.conc.MutexDeclTransformation;
import org.overture.codegen.trans.conc.SentinelTransformation;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.trans.funcvalues.FunctionValueTransformation;
import org.overture.codegen.trans.iterator.ILanguageIterator;
import org.overture.codegen.trans.iterator.JavaLanguageIterator;
import org.overture.codegen.trans.letexps.FuncTransformation;
import org.overture.codegen.trans.letexps.IfExpTransformation;
import org.overture.codegen.trans.patterns.PatternMatchConfig;
import org.overture.codegen.trans.patterns.PatternTransformation;
import org.overture.codegen.trans.uniontypes.UnionTypeTransformation;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class JavaCodeGen
{
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = "JavaTemplates";

	public static final TemplateStructure JAVA_TEMPLATE_STRUCTURE = new TemplateStructure(JAVA_TEMPLATES_ROOT_FOLDER);

	public static final String[] RESERVED_TYPE_NAMES = {
			// Classes used from the Java standard library
			"Utils", "Record", "Long", "Double", "Character", "String", "List",
			"Set" };

	public final static TempVarPrefixes varPrefixes = new TempVarPrefixes();

	private IRGenerator generator;
	private IRInfo irInfo;
	private JavaFormat javaFormat;

	public static final String INTERFACE_NAME_PREFIX = "Func_";
	public static final String TEMPLATE_TYPE_PREFIX = "T_";
	public static final String EVAL_METHOD_PREFIX = "eval";
	public static final String PARAM_NAME_PREFIX = "param_";
	public static final String APPLY_EXP_NAME_PREFIX = "apply_";
	public static final String OBJ_EXP_NAME_PREFIX = "obj_";
	public static final String TERNARY_IF_EXP_NAME_PREFIX = "ternaryIfExp_";
	public static final String CALL_STM_OBJ_NAME_PREFIX = "callStmObj_";
	public static final String CASES_EXP_RESULT_NAME_PREFIX = "casesExpResult_";
	public static final String AND_EXP_NAME_PREFIX = "andResult_";
	public static final String OR_EXP_NAME_PREFIX = "orResult_";
	public static final String WHILE_COND_NAME_PREFIX = "whileCond";
	public static final String IS_EXP_SUBJECT_NAME_PREFIX = "isExpSubject_";
	public static final String REC_MODIFIER_NAME_PREFIX = "recModifierExp_";
	
	public static final String MISSING_OP_MEMBER = "Missing operation member: ";
	public static final String MISSING_MEMBER = "Missing member: ";
	
	public static final String INVALID_NAME_PREFIX = "cg_";
	public static final String OBJ_INIT_CALL_NAME_PREFIX = "cg_init_";

	public static final String FUNC_RESULT_NAME_PREFIX = "funcResult_";
	public static final String POST_CHECK_METHOD_NAME = "postCheck";
	
	public static final String QUOTES = "quotes";
	
	public static final String QUOTE_START = "start";
	public static final String QUOTE_APPEND = "append";

	public JavaCodeGen()
	{
		init(null);
	}

	public void setJavaSettings(JavaSettings javaSettings)
	{
		this.javaFormat.setJavaSettings(javaSettings);
	}
	
	public JavaSettings getJavaSettings()
	{
		return this.javaFormat.getJavaSettings();
	}

	public JavaCodeGen(ILogger log)
	{
		init(log);
	}

	private void init(ILogger log)
	{
		initVelocity();
		this.generator = new IRGenerator(log, OBJ_INIT_CALL_NAME_PREFIX);

		this.irInfo = generator.getIRInfo();
		this.irInfo.registerQuoteValue(QUOTE_START);
		this.irInfo.registerQuoteValue(QUOTE_APPEND);
		
		this.javaFormat = new JavaFormat(varPrefixes, irInfo);
	}

	public void setSettings(IRSettings settings)
	{
		irInfo.setSettings(settings);
	}

	private void initVelocity()
	{
		Velocity.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		Velocity.init();
	}
	
	public JavaFormat getJavaFormat()
	{
		return javaFormat;
	}

	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}

	public List<GeneratedModule> generateJavaFromVdmQuotes()
	{
		try
		{
			List<String> quoteValues = generator.getQuoteValues();

			if (quoteValues.isEmpty())
			{
				return null; // Nothing to generate
			}

			javaFormat.init();
			
			JavaQuoteValueCreator quoteValueCreator = new JavaQuoteValueCreator(irInfo);
			
			List<AClassDeclCG> quoteDecls = new LinkedList<AClassDeclCG>();
			
			for(String qv : quoteValues)
			{
				quoteDecls.add(quoteValueCreator.consQuoteValue(qv));
			}

			List<GeneratedModule> modules = new LinkedList<GeneratedModule>();
			
			for (AClassDeclCG q : quoteDecls)
			{
				StringWriter writer = new StringWriter();
				q.apply(javaFormat.getMergeVisitor(), writer);
				String code = writer.toString();
				String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(code);
				
				modules.add(new GeneratedModule(q.getName(), q, formattedJavaCode));
			}


			return modules;

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Error when formatting quotes: "
					+ e.getMessage());
			e.printStackTrace();
		}

		return null;
	}

	public GeneratedData generateJavaFromVdm(
			List<SClassDefinition> mergedParseLists) throws AnalysisException,
			UnsupportedModelingException
	{
		for (SClassDefinition classDef : mergedParseLists)
		{
			if (irInfo.getAssistantManager().getDeclAssistant().classIsLibrary(classDef))
			{
				simplifyLibraryClass(classDef);
			}
		}

		InvalidNamesResult invalidNamesResult = validateVdmModelNames(mergedParseLists);
		validateVdmModelingConstructs(mergedParseLists);

		List<IRClassDeclStatus> statuses = new ArrayList<IRClassDeclStatus>();

		for (SClassDefinition classDef : mergedParseLists)
		{
			statuses.add(generator.generateFrom(classDef));
		}

		List<AClassDeclCG> classes = getClassDecls(statuses);
		javaFormat.setClasses(classes);

		LinkedList<IRClassDeclStatus> canBeGenerated = new LinkedList<IRClassDeclStatus>();
		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();

		for (IRClassDeclStatus status : statuses)
		{
			if (status.canBeGenerated())
			{
				canBeGenerated.add(status);
			} else
			{
				generated.add(new GeneratedModule(status.getClassName(), status.getUnsupportedNodes()));
			}
		}

		TransformationAssistantCG transformationAssistant = new TransformationAssistantCG(irInfo, varPrefixes);
		FunctionValueAssistant functionValueAssistant = new FunctionValueAssistant();
		IPostCheckCreator postCheckCreator = new JavaPostCheckCreator(POST_CHECK_METHOD_NAME);

		FuncTransformation funcTransformation = new FuncTransformation(transformationAssistant);
		PrePostTransformation prePostTransformation = new PrePostTransformation(irInfo);
		IfExpTransformation ifExpTransformation = new IfExpTransformation(transformationAssistant);
		FunctionValueTransformation funcValueTransformation = new FunctionValueTransformation(irInfo, transformationAssistant, functionValueAssistant, INTERFACE_NAME_PREFIX, TEMPLATE_TYPE_PREFIX, EVAL_METHOD_PREFIX, PARAM_NAME_PREFIX);
		ILanguageIterator langIterator = new JavaLanguageIterator(transformationAssistant, irInfo.getTempVarNameGen(), varPrefixes);
		TransformationVisitor transVisitor = new TransformationVisitor(irInfo, classes, varPrefixes, transformationAssistant, langIterator, TERNARY_IF_EXP_NAME_PREFIX, CASES_EXP_RESULT_NAME_PREFIX, AND_EXP_NAME_PREFIX, OR_EXP_NAME_PREFIX, WHILE_COND_NAME_PREFIX, REC_MODIFIER_NAME_PREFIX);
		PatternTransformation patternTransformation = new PatternTransformation(classes, varPrefixes, irInfo, transformationAssistant, new PatternMatchConfig());
		PreCheckTransformation preCheckTransformation = new PreCheckTransformation(irInfo, transformationAssistant, new JavaValueSemanticsTag(false));
		PostCheckTransformation postCheckTransformation = new PostCheckTransformation(postCheckCreator, irInfo, transformationAssistant, FUNC_RESULT_NAME_PREFIX, new JavaValueSemanticsTag(false));
		IsExpTransformation isExpTransformation = new IsExpTransformation(irInfo, transformationAssistant, IS_EXP_SUBJECT_NAME_PREFIX);
		SeqConversionTransformation seqConversionTransformation = new SeqConversionTransformation(transformationAssistant);
		
		// Concurrency related transformations
		SentinelTransformation concurrencytransform = new SentinelTransformation(irInfo,classes);
		MainClassConcTransformation mainclassTransform = new MainClassConcTransformation(irInfo, classes);
		MutexDeclTransformation mutexTransform = new MutexDeclTransformation(irInfo, classes);

		UnionTypeTransformation unionTypeTransformation = new UnionTypeTransformation(transformationAssistant, irInfo, classes, APPLY_EXP_NAME_PREFIX, OBJ_EXP_NAME_PREFIX, CALL_STM_OBJ_NAME_PREFIX, MISSING_OP_MEMBER, MISSING_MEMBER);
		JavaClassToStringTrans javaToStringTransformation = new JavaClassToStringTrans(irInfo);
		
		DepthFirstAnalysisAdaptor[] analyses = new DepthFirstAnalysisAdaptor[] 
		{		
				funcTransformation,
				prePostTransformation,
				ifExpTransformation,
				funcValueTransformation,
				transVisitor,
				patternTransformation,
				preCheckTransformation,
				postCheckTransformation,
				isExpTransformation,
				unionTypeTransformation,
				javaToStringTransformation,
				concurrencytransform,
				mutexTransform,
				mainclassTransform,
				seqConversionTransformation
		};

		for (DepthFirstAnalysisAdaptor transformation : analyses)
		{
			for (IRClassDeclStatus status : canBeGenerated)
			{
				try
				{
					AClassDeclCG classCg = status.getClassCg();
					classCg.apply(transformation);

				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Error when generating code for class "
							+ status.getClassName() + ": " + e.getMessage());
					Logger.getLog().printErrorln("Skipping class..");
					e.printStackTrace();
				}
			}
		}

		List<String> skipping = new LinkedList<String>();
		
		MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();
		FunctionValueAssistant functionValue = funcValueTransformation.getFunctionValueAssistant();
		javaFormat.setFunctionValueAssistant(functionValue);

		for (IRClassDeclStatus status : canBeGenerated)
		{
			StringWriter writer = new StringWriter();
			AClassDeclCG classCg = status.getClassCg();
			String className = status.getClassName();

			javaFormat.init();

			try
			{
				SClassDefinition vdmClass = (SClassDefinition) status.getClassCg().getSourceNode().getVdmNode();
				if (shouldBeGenerated(vdmClass, irInfo.getAssistantManager().getDeclAssistant()))
				{
					classCg.apply(mergeVisitor, writer);

					if (mergeVisitor.hasMergeErrors())
					{
						generated.add(new GeneratedModule(className, classCg, mergeVisitor.getMergeErrors()));
					} else
					{
						String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
						generated.add(new GeneratedModule(className, classCg, formattedJavaCode));
					}
				}
				else
				{
					if (!skipping.contains(vdmClass.getName().getName()))
					{
						skipping.add(vdmClass.getName().getName());
					}
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

		for (AInterfaceDeclCG funcValueInterface : funcValueInterfaces)
		{
			StringWriter writer = new StringWriter();

			try
			{
				funcValueInterface.apply(mergeVisitor, writer);
				String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
				generated.add(new GeneratedModule(funcValueInterface.getName(), funcValueInterface, formattedJavaCode));

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

		return new GeneratedData(generated, generateJavaFromVdmQuotes(), invalidNamesResult, skipping);
	}

	private void simplifyLibraryClass(SClassDefinition classDef)
	{
		for (PDefinition def : classDef.getDefinitions())
		{
			if (def instanceof SOperationDefinition)
			{
				SOperationDefinition op = (SOperationDefinition) def;
				
				op.setBody(new ANotYetSpecifiedStm());
				op.setPrecondition(null);
				op.setPostcondition(null);
			}
			else if (def instanceof SFunctionDefinition)
			{
				SFunctionDefinition func = (SFunctionDefinition) def;
				
				func.setBody(new ANotYetSpecifiedExp());
				func.setPrecondition(null);
				func.setPostcondition(null);
			}

		}
	}

	private List<AClassDeclCG> getClassDecls(List<IRClassDeclStatus> statuses)
	{
		List<AClassDeclCG> classDecls = new LinkedList<AClassDeclCG>();

		for (IRClassDeclStatus status : statuses)
		{
			AClassDeclCG classCg = status.getClassCg();
			
			if (classCg != null)
			{
				classDecls.add(classCg);
			}
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

				if (mergeVisitor.hasMergeErrors())
				{
					return new Generated(mergeVisitor.getMergeErrors());
				} else
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

	public void generateJavaSourceFile(File outputFolder,
			GeneratedModule generatedModule)
	{
		if (generatedModule != null && generatedModule.canBeGenerated()
				&& !generatedModule.hasMergeErrors())
		{
			JavaCodeGenUtil.saveJavaClass(outputFolder, generatedModule.getName()
					+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION, generatedModule.getContent());
		}
	}

	public void generateJavaSourceFiles(File outputFolder,
			List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			generateJavaSourceFile(outputFolder, classCg);
		}
	}

	private InvalidNamesResult validateVdmModelNames(
			List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		AssistantManager assistantManager = generator.getIRInfo().getAssistantManager();
		VdmAstAnalysis analysis = new VdmAstAnalysis(assistantManager);

		Set<Violation> reservedWordViolations = analysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(IJavaCodeGenConstants.RESERVED_WORDS, irInfo, INVALID_NAME_PREFIX));
		Set<Violation> typenameViolations = analysis.usesIllegalNames(mergedParseLists, new TypenameComparison(RESERVED_TYPE_NAMES, irInfo, INVALID_NAME_PREFIX));

		String[] generatedTempVarNames = GeneralUtils.concat(IRConstants.GENERATED_TEMP_NAMES, varPrefixes.GENERATED_TEMP_NAMES);

		Set<Violation> tempVarViolations = analysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(generatedTempVarNames, irInfo, INVALID_NAME_PREFIX));

		if (!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty()
				|| !tempVarViolations.isEmpty())
		{
			return new InvalidNamesResult(reservedWordViolations, typenameViolations, tempVarViolations, INVALID_NAME_PREFIX);
		} else
		{
			return new InvalidNamesResult();
		}
	}

	private void validateVdmModelingConstructs(
			List<? extends INode> mergedParseLists) throws AnalysisException,
			UnsupportedModelingException
	{
		VdmAstAnalysis analysis = new VdmAstAnalysis(generator.getIRInfo().getAssistantManager());

		Set<Violation> violations = analysis.usesUnsupportedModelingConstructs(mergedParseLists);

		if (!violations.isEmpty())
		{
			throw new UnsupportedModelingException("The model uses modeling constructs that are not supported for Java code Generation", violations);
		}
	}

	private boolean shouldBeGenerated(SClassDefinition classDef,
			DeclAssistantCG declAssistant)
	{
		if (declAssistant.classIsLibrary(classDef))
		{
			return false;
		}
		
		String name = classDef.getName().getName();
		
		if(getJavaSettings().getClassesToSkip().contains(name))
		{
			return false;
		}

//		for (SClassDefinition superDef : classDef.getSuperDefs())
//		{
//			if (declAssistant.classIsLibrary(superDef))
//			{
//				return false;
//			}
//		}

		return true;
	}
}
