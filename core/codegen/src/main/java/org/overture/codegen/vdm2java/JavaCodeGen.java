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
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRClassDeclStatus;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRExpStatus;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateStructure;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;

public class JavaCodeGen extends CodeGenBase
{
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = "JavaTemplates";

	public static final String[] JAVA_RESERVED_TYPE_NAMES = {
			// Classes used from the Java standard library
			"Utils", "Record", "Long", "Double", "Character", "String", "List",
			"Set" };
	
	private JavaFormat javaFormat;
	private TemplateStructure javaTemplateStructure;
	
	public JavaCodeGen()
	{
		super(null);
		init();
	}
	
	public void setJavaTemplateStructure(TemplateStructure javaTemplateStructure)
	{
		this.javaTemplateStructure = javaTemplateStructure;
	}
	
	public TemplateStructure getJavaTemplateStructure()
	{
		return javaTemplateStructure;
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
		super(log);
		init();
	}

	private void init()
	{
		initVelocity();

		this.javaTemplateStructure = new TemplateStructure(JAVA_TEMPLATES_ROOT_FOLDER);
		
		this.generator.getIRInfo().registerQuoteValue(QUOTE_START);
		this.generator.getIRInfo().registerQuoteValue(QUOTE_APPEND);
		
		this.transAssistant = new TransAssistantCG(generator.getIRInfo(), varPrefixes);
		
		this.javaFormat = new JavaFormat(varPrefixes, javaTemplateStructure, generator.getIRInfo());
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
			
			JavaQuoteValueCreator quoteValueCreator = new JavaQuoteValueCreator(generator.getIRInfo(), transAssistant);
			
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
			if (generator.getIRInfo().getAssistantManager().getDeclAssistant().classIsLibrary(classDef))
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
		
		FunctionValueAssistant functionValueAssistant = new FunctionValueAssistant();

		DepthFirstAnalysisAdaptor[] analyses = new JavaTransSeries(this).consAnalyses(classes, functionValueAssistant);

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
		javaFormat.setFunctionValueAssistant(functionValueAssistant);

		for (IRClassDeclStatus status : canBeGenerated)
		{
			StringWriter writer = new StringWriter();
			AClassDeclCG classCg = status.getClassCg();
			String className = status.getClassName();

			javaFormat.init();

			try
			{
				SClassDefinition vdmClass = (SClassDefinition) status.getClassCg().getSourceNode().getVdmNode();
				if (shouldBeGenerated(vdmClass, generator.getIRInfo().getAssistantManager().getDeclAssistant()))
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

		List<AInterfaceDeclCG> funcValueInterfaces = functionValueAssistant.getFunctionValueInterfaces();

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

		Set<Violation> reservedWordViolations = analysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(IJavaCodeGenConstants.RESERVED_WORDS, generator.getIRInfo(), INVALID_NAME_PREFIX));
		Set<Violation> typenameViolations = analysis.usesIllegalNames(mergedParseLists, new TypenameComparison(JAVA_RESERVED_TYPE_NAMES, generator.getIRInfo(), INVALID_NAME_PREFIX));

		String[] generatedTempVarNames = GeneralUtils.concat(IRConstants.GENERATED_TEMP_NAMES, varPrefixes.GENERATED_TEMP_NAMES);

		Set<Violation> tempVarViolations = analysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(generatedTempVarNames, generator.getIRInfo(), INVALID_NAME_PREFIX));

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
