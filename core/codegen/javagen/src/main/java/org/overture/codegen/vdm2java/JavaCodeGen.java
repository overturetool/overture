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
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.codegen.analysis.vdm.NameCollector;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.vdm.VarRenamer;
import org.overture.codegen.analysis.vdm.VarShadowingRenameCollector;
import org.overture.codegen.analysis.violations.GeneratedVarComparison;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.ReservedWordsComparison;
import org.overture.codegen.analysis.violations.TypenameComparison;
import org.overture.codegen.analysis.violations.VdmAstAnalysis;
import org.overture.codegen.analysis.violations.Violation;
import org.overture.codegen.assistant.AssistantManager;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRStatus;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.trans.DivideTrans;
import org.overture.codegen.trans.ModuleToClassTransformation;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Settings;

public class JavaCodeGen extends CodeGenBase implements IJavaQouteEventCoordinator
{
	public static final String TRACE_IMPORT = "org.overture.codegen.runtime.traces.*";
	public static final String RUNTIME_IMPORT = "org.overture.codegen.runtime.*";
	public static final String JAVA_UTIL = "java.util.*";
	
	public static final String JAVA_TEMPLATES_ROOT_FOLDER = "JavaTemplates";

	public static final String[] JAVA_RESERVED_TYPE_NAMES = {
			// Classes used from the Java standard library
			"Utils", "Record", "Long", "Double", "Character", "String", "List",
			"Set" };
	
	/**
	 * Signatures of the java.lang.Object methods:<br>
	 * clone()<br>
	 * equals(Object obj)<br>
	 * finalize()<br>
	 * getClass()<br>
	 * hashCode()<br>
	 * notify()<br>
	 * notifyAll()<br>
	 * toString()<br>
	 * wait()<br>
	 * wait(long timeout, int nanos)<br>
	 * wait(long timeout)
	 */
	public static final String[] JAVA_LANG_OBJECT_METHODS = { "clone", "equals", "finalize", "getClass", "hashCode",
			"notify", "notifyAll", "toString", "wait" };
	
	public static final String JAVA_MAIN_CLASS_NAME = "Main";
	public static final String JAVA_QUOTES_PACKAGE = "quotes";
	
	public static final String INVALID_NAME_PREFIX = "cg_";
	
	private JavaFormat javaFormat;
	
	private IJavaQuoteEventObserver quoteObserver;
	
	private JavaVarPrefixManager varPrefixManager;
	
	private JavaTransSeries transSeries;
	
	private SClassDefinition mainClass;
	
	private List<Renaming> allRenamings;
	
	private InvalidNamesResult invalidNamesResult;
	
	private List<String> warnings;

	public JavaCodeGen()
	{
		super();
		this.varPrefixManager = new JavaVarPrefixManager();
		this.quoteObserver = null;
		this.javaFormat = new JavaFormat(varPrefixManager, JAVA_TEMPLATES_ROOT_FOLDER, generator.getIRInfo());
		this.transSeries = new JavaTransSeries(this);
		
		clearVdmAstData();
	}

	private void clearVdmAstData()
	{
		this.mainClass = null;
		this.allRenamings = new LinkedList<>();
		this.invalidNamesResult = new InvalidNamesResult();
		this.warnings = new LinkedList<>();
	}

	public JavaSettings getJavaSettings()
	{
		return this.javaFormat.getJavaSettings();
	}
	
	public void setJavaSettings(JavaSettings javaSettings)
	{
		this.javaFormat.setJavaSettings(javaSettings);
	}
	
	public JavaTransSeries getTransSeries()
	{
		return this.transSeries;
	}
	
	public void setTransSeries(JavaTransSeries transSeries)
	{
		this.transSeries = transSeries;
	}
	
	public JavaFormat getJavaFormat()
	{
		return javaFormat;
	}
	
	public void setJavaFormat(JavaFormat javaFormat)
	{
		this.javaFormat = javaFormat;
	}

	@Override
	protected void clear()
	{
		super.clear();
		javaFormat.getMergeVisitor().init();
		transSeries.clear();
		javaFormat.clearFunctionValueAssistant();
		clearVdmAstData();
	}

	@Override
	protected GeneratedData genVdmToTargetLang(List<IRStatus<PCG>> statuses)
			throws AnalysisException {
		
		List<GeneratedModule> genModules = new LinkedList<GeneratedModule>();
		
		// Event notification
		statuses = initialIrEvent(statuses);
		
		List<String> userTestCases = getUserTestCases(statuses);
		statuses = filter(statuses, genModules, userTestCases);
		
		List<IRStatus<AModuleDeclCG>> moduleStatuses = IRStatus.extract(statuses, AModuleDeclCG.class);
		List<IRStatus<PCG>> modulesAsNodes = IRStatus.extract(moduleStatuses);
			
		ModuleToClassTransformation moduleTransformation = new ModuleToClassTransformation(getInfo(),
				transAssistant, getModuleDecls(moduleStatuses));
		
		for(IRStatus<PCG> status : modulesAsNodes)
		{
			try
			{
				generator.applyTotalTransformation(status, moduleTransformation);

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error when generating code for module "
						+ status.getIrNodeName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping module..");
				e.printStackTrace();
			}
		}

		List<IRStatus<ADefaultClassDeclCG>> classStatuses = IRStatus.extract(modulesAsNodes, ADefaultClassDeclCG.class);
		classStatuses.addAll(IRStatus.extract(statuses, ADefaultClassDeclCG.class));
		
		if (getJavaSettings().getJavaRootPackage() != null)
		{
			for (IRStatus<ADefaultClassDeclCG> irStatus : classStatuses)
			{
				irStatus.getIrNode().setPackage(getJavaSettings().getJavaRootPackage());
			}
		}

		List<IRStatus<ADefaultClassDeclCG>> canBeGenerated = new LinkedList<IRStatus<ADefaultClassDeclCG>>();

		for (IRStatus<ADefaultClassDeclCG> status : classStatuses)
		{
			if (status.canBeGenerated())
			{
				canBeGenerated.add(status);
			} else
			{
				genModules.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isTestCase(status)));
			}
		}

		for (DepthFirstAnalysisAdaptor trans : transSeries.getSeries())
		{
			for (IRStatus<ADefaultClassDeclCG> status : canBeGenerated)
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
		
		// Event notification
		canBeGenerated = IRStatus.extract(finalIrEvent(IRStatus.extract(canBeGenerated)), ADefaultClassDeclCG.class);
		canBeGenerated = filter(canBeGenerated, genModules, userTestCases);
		
		List<String> skipping = new LinkedList<String>();

		MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();
		javaFormat.setFunctionValueAssistant(transSeries.getFuncValAssist());

		for (IRStatus<ADefaultClassDeclCG> status : canBeGenerated)
		{
			INode vdmClass = status.getVdmNode();

			if (vdmClass == mainClass)
			{
				status.getIrNode().setTag(new JavaMainTag(status.getIrNode()));
			}

			try
			{
				if (shouldGenerateVdmNode(vdmClass))
				{
					genModules.add(genIrModule(mergeVisitor, status));
					
				} else
				{
					if (!skipping.contains(status.getIrNodeName()))
					{
						skipping.add(status.getIrNodeName());
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

		List<AInterfaceDeclCG> funcValueInterfaces = transSeries.getFuncValAssist().getFuncValInterfaces();

		for (AInterfaceDeclCG funcValueInterface : funcValueInterfaces)
		{
			funcValueInterface.setPackage(getJavaSettings().getJavaRootPackage());
			
			try
			{
				StringWriter writer = new StringWriter();
				funcValueInterface.apply(javaFormat.getMergeVisitor(), writer);
				String formattedJavaCode = JavaCodeGenUtil.formatJavaCode(writer.toString());
				genModules.add(new GeneratedModule(funcValueInterface.getName(), funcValueInterface, formattedJavaCode, false));

			} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
			{
				Logger.getLog().printErrorln("Error generating code for function value interface "
						+ funcValueInterface.getName() + ": " + e.getMessage());
				Logger.getLog().printErrorln("Skipping interface..");
				e.printStackTrace();
			}
		}

		GeneratedData data = new GeneratedData();
		data.setClasses(genModules);
		data.setQuoteValues(generateJavaFromVdmQuotes());
		data.setInvalidNamesResult(invalidNamesResult);
		data.setSkippedClasses(skipping);
		data.setAllRenamings(allRenamings);
		data.setWarnings(warnings);

		return data;
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

			javaFormat.getMergeVisitor().init();

			JavaQuoteValueCreator creator = new JavaQuoteValueCreator(generator.getIRInfo(), transAssistant);

			List<ADefaultClassDeclCG> quoteClasses = new LinkedList<>();
			for (String quoteNameVdm : quoteValues)
			{
				String pack = getJavaSettings().getJavaRootPackage();
				ADefaultClassDeclCG quoteDecl = creator.consQuoteValue(quoteNameVdm, quoteNameVdm, pack);
				
				quoteClasses.add(quoteDecl);
			}
			
			// Event notification
			if(quoteObserver != null)
			{
				quoteObserver.quoteClassesProduced(quoteClasses);
			}
			
			List<GeneratedModule> modules = new LinkedList<GeneratedModule>();
			for (int i = 0; i < quoteClasses.size(); i++)
			{
				String quoteNameVdm = quoteValues.get(i);
				ADefaultClassDeclCG qc = quoteClasses.get(i);
				
				StringWriter writer = new StringWriter();
				qc.apply(javaFormat.getMergeVisitor(), writer);

				modules.add(new GeneratedModule(quoteNameVdm, qc, formatCode(writer), false));
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

	@Override
	public String formatCode(StringWriter writer)
	{
		String code = writer.toString();
		
		if(getJavaSettings().formatCode())
		{
			code = JavaCodeGenUtil.formatJavaCode(code); 
		}
		return code;
	}
	
	@Override
	protected void preProcessAst(List<INode> ast) throws AnalysisException
	{
		super.preProcessAst(ast);
		
		if (Settings.dialect == Dialect.VDM_PP)
		{
			if (getJavaSettings().getVdmEntryExp() != null)
			{
				try
				{
					mainClass = GeneralCodeGenUtils.consMainClass(getClasses(ast), getJavaSettings().getVdmEntryExp(), Settings.dialect, JAVA_MAIN_CLASS_NAME, getInfo().getTempVarNameGen());
					ast.add(mainClass);
				} catch (Exception e)
				{
					// It can go wrong if the VDM entry point does not type check
					warnings.add("The chosen launch configuration could not be type checked: " + e.getMessage());
					warnings.add("Skipping launch configuration..");
				}
			}
		}
		
		List<INode> userModules = getUserModules(ast);
		allRenamings = normaliseIdentifiers(userModules);
		
		// To document any renaming of variables shadowing other variables
		allRenamings.addAll(performRenaming(userModules, getInfo().getIdStateDesignatorDefs()));

		invalidNamesResult = validateVdmModelNames(userModules);
	}

	@Override
	public void preProcessVdmUserClass(INode node)
	{
		if (!getJavaSettings().genJUnit4tests())
		{
			return;
		}

		if (node instanceof SClassDefinition)
		{
			SClassDefinition clazz = (SClassDefinition) node;

			if (getInfo().getDeclAssistant().isTestCase(clazz))
			{
				List<PDefinition> toRemove = new LinkedList<>();

				for (PDefinition d : clazz.getDefinitions())
				{
					if (d instanceof AExplicitOperationDefinition)
					{
						AExplicitOperationDefinition op = (AExplicitOperationDefinition) d;

						if (op.getName().getName().equals(IRConstants.TEST_CASE_RUN_FULL_SUITE)
								&& op.getParameterPatterns().isEmpty())
						{
							toRemove.add(op);
						}
					}
				}

				clazz.getDefinitions().removeAll(toRemove);
			}
		}
	}
	
	@Override
	protected void genIrStatus(List<IRStatus<PCG>> statuses, INode node) throws AnalysisException
	{
		if (!(node instanceof ASystemClassDefinition) && !(node instanceof ACpuClassDefinition))
		{
			VdmAstJavaValidator v = validateVdmNode(node);

			if (v.hasUnsupportedNodes())
			{
				// We can tell by analysing the VDM AST that the IR generator will produce an
				// IR tree that the Java backend cannot code generate
				String nodeName = getInfo().getDeclAssistant().getNodeName(node);
				HashSet<VdmNodeInfo> nodesCopy = new HashSet<VdmNodeInfo>(v.getUnsupportedNodes());
				statuses.add(new IRStatus<PCG>(node, nodeName, /* no IR node */null, nodesCopy));
			} else
			{
				super.genIrStatus(statuses, node);
			}
		}
	}

	private VdmAstJavaValidator validateVdmNode(INode node) throws AnalysisException
	{
		VdmAstJavaValidator validator = new VdmAstJavaValidator(generator.getIRInfo());
		validator.getUnsupportedNodes().clear();
		node.apply(validator);
		
		return validator;
	}

	private <T extends PCG> List<IRStatus<T>> filter(
			List<IRStatus<T>> statuses, List<GeneratedModule> generated, List<String> userTestCases)
	{
		List<IRStatus<T>> filtered = new LinkedList<IRStatus<T>>();
		
		for(IRStatus<T> status : statuses)
		{
			if(status.canBeGenerated())
			{
				filtered.add(status);
			}
			else
			{
				boolean isUserTestCase = userTestCases.contains(status.getIrNodeName());
				generated.add(new GeneratedModule(status.getIrNodeName(), status.getUnsupportedInIr(), new HashSet<IrNodeInfo>(), isUserTestCase));
			}
		}
		
		return filtered;
	}

	private List<Renaming> normaliseIdentifiers(
			List<INode> userModules) throws AnalysisException
	{
		NameCollector collector = new NameCollector();

		for (INode node : userModules)
		{
			node.apply(collector);
		}

		Set<String> allNames = collector.namesToAvoid();

		JavaIdentifierNormaliser normaliser = new JavaIdentifierNormaliser(allNames, getInfo().getTempVarNameGen());
		
		for (INode node : userModules)
		{
			node.apply(normaliser);
		}

		VarRenamer renamer = new VarRenamer();
		
		Set<Renaming> filteredRenamings = new HashSet<Renaming>();
		
		for(Renaming r : normaliser.getRenamings())
		{
			if(!getInfo().getDeclAssistant().isLibraryName(r.getLoc().getModule()))
			{
				filteredRenamings.add(r);
			}
		}
		
		for (INode node : userModules)
		{
			renamer.rename(node, filteredRenamings);
		}

		return new LinkedList<Renaming>(filteredRenamings);
	}

	private List<Renaming> performRenaming(
			List<INode> mergedParseLists,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
			throws AnalysisException
	{
		List<Renaming> allRenamings = new LinkedList<Renaming>();

		VarShadowingRenameCollector renamingsCollector = new VarShadowingRenameCollector(generator.getIRInfo().getTcFactory(), idDefs);
		VarRenamer renamer = new VarRenamer();

		for (INode node : mergedParseLists)
		{
			Set<Renaming> currentRenamings = renamer.computeRenamings(node, renamingsCollector);

			if (!currentRenamings.isEmpty())
			{
				renamer.rename(node, currentRenamings);
				allRenamings.addAll(currentRenamings);
			}
		}

		Collections.sort(allRenamings);
		
		return allRenamings;
	}

	private List<AModuleDeclCG> getModuleDecls(List<IRStatus<AModuleDeclCG>> statuses)
	{
		List<AModuleDeclCG> modules = new LinkedList<AModuleDeclCG>();
		
		for(IRStatus<AModuleDeclCG> status : statuses)
		{
			modules.add(status.getIrNode());
		}
		
		return modules;
	}

	public Generated generateJavaFromVdmExp(PExp exp) throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException
	{
		// There is no name validation here.
		IRStatus<SExpCG> expStatus = generator.generateFrom(exp);
		
		generator.applyPartialTransformation(expStatus, new DivideTrans(getInfo()));

		MergeVisitor mergeVisitor = javaFormat.getMergeVisitor();

		try
		{
			return genIrExp(expStatus, mergeVisitor);

		} catch (org.overture.codegen.cgast.analysis.AnalysisException e)
		{
			Logger.getLog().printErrorln("Could not generate expression: "
					+ exp);
			e.printStackTrace();
			return null;
		}
	}
	
	public void genJavaSourceFiles(File root,
			List<GeneratedModule> generatedClasses)
	{
		for (GeneratedModule classCg : generatedClasses)
		{
			if(classCg.canBeGenerated())
			{
				genJavaSourceFile(root, classCg);
			}
		}
	}

	public void genJavaSourceFile(File root, GeneratedModule generatedModule)
	{
		File moduleOutputDir = JavaCodeGenUtil.getModuleOutputDir(root, this, generatedModule);
		
		if(moduleOutputDir == null)
		{
			return;
		}
		
		if (generatedModule != null && generatedModule.canBeGenerated()
				&& !generatedModule.hasMergeErrors())
		{
			String javaFileName = generatedModule.getName();

			if (JavaCodeGenUtil.isQuote(generatedModule.getIrNode(), getJavaSettings()))
			{
				javaFileName += JavaQuoteValueCreator.JAVA_QUOTE_NAME_SUFFIX;
			}

			javaFileName += IJavaConstants.JAVA_FILE_EXTENSION;

			JavaCodeGenUtil.saveJavaClass(moduleOutputDir, javaFileName, generatedModule.getContent());
		}
	}
	
	private InvalidNamesResult validateVdmModelNames(
			List<INode> mergedParseLists) throws AnalysisException
	{
		AssistantManager assistantManager = generator.getIRInfo().getAssistantManager();
		VdmAstAnalysis analysis = new VdmAstAnalysis(assistantManager);

		Set<Violation> reservedWordViolations = analysis.usesIllegalNames(mergedParseLists, new ReservedWordsComparison(IJavaConstants.RESERVED_WORDS, generator.getIRInfo(), INVALID_NAME_PREFIX));
		Set<Violation> typenameViolations = analysis.usesIllegalNames(mergedParseLists, new TypenameComparison(JAVA_RESERVED_TYPE_NAMES, generator.getIRInfo(), INVALID_NAME_PREFIX));
		Set<Violation> objectMethodViolations = analysis.usesIllegalNames(mergedParseLists, new ObjectMethodComparison(JAVA_LANG_OBJECT_METHODS, generator.getIRInfo(), INVALID_NAME_PREFIX));
			
		//TODO: needs to take all of them into account
		String[] generatedTempVarNames = varPrefixManager.getIteVarPrefixes().GENERATED_TEMP_NAMES;

		Set<Violation> tempVarViolations = analysis.usesIllegalNames(mergedParseLists, new GeneratedVarComparison(generatedTempVarNames, generator.getIRInfo(), INVALID_NAME_PREFIX));

		if (!reservedWordViolations.isEmpty() || !typenameViolations.isEmpty()
				|| !tempVarViolations.isEmpty() || !objectMethodViolations.isEmpty())
		{
			return new InvalidNamesResult(reservedWordViolations, typenameViolations, tempVarViolations, objectMethodViolations, INVALID_NAME_PREFIX);
		} else
		{
			return new InvalidNamesResult();
		}
	}

	public boolean shouldGenerateVdmNode(INode node)
	{
		if(!super.shouldGenerateVdmNode(node))
		{
			return false;
		}

		String name = null;
		
		if(node instanceof SClassDefinition)
		{
			name = ((SClassDefinition) node).getName().getName();
		}
		else if(node instanceof AModuleModules)
		{
			name = ((AModuleModules) node).getName().getName();
		}
		else
		{
			return true;
		}

		if (getJavaSettings().getModulesToSkip().contains(name))
		{
			return false;
		}

		// for (SClassDefinition superDef : classDef.getSuperDefs())
		// {
		// if (declAssistant.classIsLibrary(superDef))
		// {
		// return false;
		// }
		// }

		return true;
	}

	@Override
	public void registerJavaQuoteObs(IJavaQuoteEventObserver obs)
	{
		if(obs != null && quoteObserver == null)
		{
			quoteObserver = obs;
		}
	}

	@Override
	public void unregisterJavaQuoteObs(IJavaQuoteEventObserver obs)
	{
		if(obs != null && quoteObserver == obs)
		{
			quoteObserver = null;
		}
	}

	public JavaVarPrefixManager getVarPrefixManager()
	{
		return varPrefixManager;
	}

	public void setVarPrefixManager(JavaVarPrefixManager varPrefixManager)
	{
		this.varPrefixManager = varPrefixManager;
	}
}
