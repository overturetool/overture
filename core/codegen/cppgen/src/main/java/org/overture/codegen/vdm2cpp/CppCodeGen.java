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
package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.analysis.violations.VdmAstAnalysis;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.DeclAssistantCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;
import org.overture.codegen.trans.funcvalues.FunctionValueAssistant;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2cpp.timing.inserter.TimingInjectorVisitor;
import org.overture.codegen.vdm2cpp.timing.inserter.TimingMainCreator;
import org.overture.codegen.vdm2cpp.vdmtools.CGGenHelper;

public class CppCodeGen extends CodeGenBase
{
//	public static final String[] RESERVED_TYPE_NAMES = {
//			// Classes used from the Java standard library
//			"Utils", "Record", "Long", "Double", "Character", "String", "List",
//			"Set" };
	String generator_type;
	boolean generate_timing;
	
	public CppCodeGen(String gen_type,boolean gen_timing)
	{
		super();
		generator_type = gen_type;
		generate_timing = gen_timing;
		init();
	}

	private void init()
	{
		
		this.transAssistant = new TransAssistantCG(generator.getIRInfo(), varPrefixes);
	}

	public List<GeneratedModule> generateJavaFromVdmQuotes()
	{

		return null;
	}

	public GeneratedData generateCppFromVdm(
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

		List<IRStatus<AClassDeclCG>> statuses = new ArrayList<IRStatus<AClassDeclCG>>();

		for (SClassDefinition classDef : mergedParseLists)
		{
			statuses.add((IRStatus)generator.generateFrom(classDef));
		}

		List<AClassDeclCG> classes = getClassDecls(statuses);
		//javaFormat.setClasses(classes);

		LinkedList<IRStatus<AClassDeclCG>> canBeGenerated = new LinkedList<IRStatus<AClassDeclCG>>();
		List<GeneratedModule> generated = new ArrayList<GeneratedModule>();

		for (IRStatus<AClassDeclCG> status : statuses)
		{
			if (status.canBeGenerated())
			{
				canBeGenerated.add(status);
			} else
			{
				generated.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>()));
			}
		}
		
		FunctionValueAssistant functionValueAssistant = new FunctionValueAssistant();
		DepthFirstAnalysisAdaptor[] analyses = null;
		if(generator_type.toLowerCase().equals("stdlib"))
		{
			analyses = new CppStdLibTransSeries(this).consAnalyses(classes, functionValueAssistant);
		}
		else
		{
			analyses = new CppVdmtoolsTransSeries(this).consAnalyses(classes, functionValueAssistant);
		}
		
		

		for (DepthFirstAnalysisAdaptor transformation : analyses)
		{
			for (IRStatus<AClassDeclCG> status : canBeGenerated)
			{
				try
				{
					AClassDeclCG classCg = status.getIrNode();
					classCg.apply(transformation);

				} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
				{
					Logger.getLog().printErrorln("Error when generating code for class "
							+ status.getIrNodeName() + ": " + e.getMessage());
					Logger.getLog().printErrorln("Skipping class..");
					e.printStackTrace();
				}
			}
		}
		

		List<String> skipping = new LinkedList<String>();
		TypeHierarchyAnalyser tan = new TypeHierarchyAnalyser();
		
		for (IRStatus<AClassDeclCG> status : canBeGenerated) {
			AClassDeclCG cls = status.getIrNode();
			try {
				cls.apply(tan);
			} catch (org.overture.codegen.cgast.analysis.AnalysisException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		XtendAnswerStringVisitor mergeVisitor = null;
		CGGenHelper helper = null;
		if(generator_type.toLowerCase().equals("stdlib"))
		{
			mergeVisitor = new CGcppstdlib(tan);
		}
		else
		{
			mergeVisitor = new CGvdmtools(tan);
			helper = new CGGenHelper();
		}
		 //CGNew mergeVisitor = new CGNew(tan);
		
		TimingInjectorVisitor timing = new TimingInjectorVisitor();
		
		//FunctionValueAssistant functionValue = funcValueTransformation.getFunctionValueAssistant();
		//javaFormat.setFunctionValueAssistant(functionValue);
		
		 
		for (IRStatus<AClassDeclCG> status : canBeGenerated)
		{
			StringWriter writer = new StringWriter();
			AClassDeclCG classCg = status.getIrNode();
			String className = status.getIrNodeName();
			
			

			try
			{
				SClassDefinition vdmClass = (SClassDefinition) status.getIrNode().getSourceNode().getVdmNode();
				if (shouldBeGenerated(vdmClass, generator.getIRInfo().getAssistantManager().getDeclAssistant()))
				{
//					classCg.apply(mergeVisitor, writer);
//
//					if (mergeVisitor.hasMergeErrors())
//					{
//						generated.add(new GeneratedModule(className, classCg, mergeVisitor.getMergeErrors()));
//					}
					//
					// TODO: In the Java code generator the mergeVisitor keeps track of nodes that
					// unsupported by the backend. These can be transferred to the generated module
					//
					//else if(mergeVisitor.hasUnsupportedTargLangNodes())
					//{
					//	generated.add(new GeneratedModule(className, new HashSet<VdmNodeInfo>(), mergeVisitor.getUnsupportedInTargLang()));
					//}
//					else
//					{
//						String formattedJavaCode = writer.toString();
//						generated.add(new GeneratedModule(className, classCg, formattedJavaCode));
//					}
					if(generate_timing)
					{
						classCg.apply(timing);
					}
					String code = classCg.apply(mergeVisitor);
					if(helper != null)
					{
						helper.addClass(classCg);
					}
					generated.add(new GeneratedModule(className,classCg,code));

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
						+ status.getIrNodeName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping class..");
				e.printStackTrace();
			}
		}
		if(helper != null)
		{
			generated.add(new GeneratedModule("CGBase",null,(String) helper.GenerateHelper()));
		}
		if(generate_timing)
		{
			System.out.println(TimingMainCreator.generateMainMethod( timing.getRegisteredMethods()));
		}

//		List<AInterfaceDeclCG> funcValueInterfaces = functionValue.getFunctionValueInterfaces();
//
//		for (AInterfaceDeclCG funcValueInterface : funcValueInterfaces)
//		{
//			StringWriter writer = new StringWriter();
//
//			try
//			{
//				funcValueInterface.apply(mergeVisitor, writer);
//				//String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
//				generated.add(new GeneratedModule(funcValueInterface.getName(), funcValueInterface, formattedJavaCode));
//
//			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
//			{
//				Logger.getLog().printErrorln("Error generating code for function value interface "
//						+ funcValueInterface.getName() + ": " + e.getMessage());
//				Logger.getLog().printErrorln("Skipping interface..");
//				e.printStackTrace();
//			}
//		}

		//javaFormat.clearFunctionValueAssistant();
		//javaFormat.clearClasses();

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

	private List<AClassDeclCG> getClassDecls(List<IRStatus<AClassDeclCG>> statuses)
	{
		List<AClassDeclCG> classDecls = new LinkedList<AClassDeclCG>();

		for (IRStatus<AClassDeclCG> status : statuses)
		{
			AClassDeclCG classCg = status.getIrNode();
			
			if (classCg != null)
			{
				classDecls.add(classCg);
			}
		}

		return classDecls;
	}

	public Generated generateCppFromVdmExp(PExp exp) throws AnalysisException
	{
		// There is no name validation here.

		IRStatus<SExpCG> expStatus = generator.generateFrom(exp);

		try
		{
			SExpCG expCg = expStatus.getIrNode();

			if (expStatus.canBeGenerated())
			{
				//javaFormat.init();
				CGvdmtools mergeVisitor = new CGvdmtools();//vdm2cppGen(null,null,null);//javaFormat.getMergeVisitor();
				return new Generated(expCg.apply(mergeVisitor));


				//if (mergeVisitor.hasMergeErrors())
				//{
				//	return new Generated(mergeVisitor.getMergeErrors());
				//}
				//
				// TODO: In the Java code generator the mergeVisitor keeps track of nodes that
				// unsupported by the backend. These can be transferred to the generated module
				//
				//else if(mergeVisitor.hasUnsupportedTargLangNodes())
				//{
				//	generated.add(new GeneratedModule(className, new HashSet<VdmNodeInfo>(), mergeVisitor.getUnsupportedInTargLang()));
				//}
				//else
				//{
				//	String code = writer.toString();

				//	return new Generated(code);
				//}
			} else
			{

				return new Generated(expStatus.getUnsupportedInIr(), new HashSet<IrNodeInfo>());
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
			//JavaCodeGenUtil.saveJavaClass(outputFolder, generatedModule.getName()
				//	+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION, generatedModule.getContent());
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
		//AssistantManager assistantManager = generator.getIRInfo().getAssistantManager();
		//VdmAstAnalysis analysis = new VdmAstAnalysis(assistantManager);

		//Set<Violation> reservedWordViolations = analysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(IJavaCodeGenConstants.RESERVED_WORDS, irInfo, INVALID_NAME_PREFIX));
		//Set<Violation> typenameViolations = analysis.usesIllegalNames(mergedParseLists, new TypenameComparison(RESERVED_TYPE_NAMES, generator.getIRInfo(), INVALID_NAME_PREFIX));

		//String[] generatedTempVarNames = GeneralUtils.concat(IRConstants.GENERATED_TEMP_NAMES, varPrefixes.GENERATED_TEMP_NAMES);

		//Set<Violation> tempVarViolations = analysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(generatedTempVarNames, generator.getIRInfo(), INVALID_NAME_PREFIX));

//		if (!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty()
//				|| !tempVarViolations.isEmpty())
//		{
//			return new InvalidNamesResult(reservedWordViolations, typenameViolations, tempVarViolations, INVALID_NAME_PREFIX);
//		} else
//		{		
			return new InvalidNamesResult();
//		}
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
		
//		String name = classDef.getName().getName();
//		
//		if(getJavaSettings().getClassesToSkip().contains(name))
//		{
//			return false;
//		}

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
