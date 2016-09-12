package org.overture.refactoring;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.util.modules.ModuleList;
import org.overture.codegen.analysis.vdm.Renaming;
import org.overture.codegen.analysis.vdm.VarRenamer;
import org.overture.codegen.analysis.vdm.VarShadowingRenameCollector;
import org.overture.codegen.ir.IRGenerator;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.IRSettings;
import org.overture.config.Settings;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

public class RefactoringBase {
	
	protected IRGenerator generator;
	private List<Renaming> allRenamings;
	
	public RefactoringBase(){
		this.generator = new IRGenerator();
		
		IRSettings irSettings = new IRSettings();
		irSettings.setCharSeqAsString(true);
		irSettings.setGeneratePreConds(false);
		irSettings.setGeneratePreCondChecks(false);
		irSettings.setGeneratePostConds(false);
		irSettings.setGeneratePostCondChecks(false);
		generator.getIRInfo().setSettings(irSettings);
	}
	
	public GeneratedData generate(List<INode> ast) throws AnalysisException
	{

		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleList moduleList = new ModuleList(getModules(ast));
			moduleList.combineDefaults();
			ast = getNodes(moduleList);
		}
		
		List<INode> userModules = getUserModules(ast);
		
		allRenamings = new LinkedList<Renaming>();
//		// To document any renaming of variables shadowing other variables
		allRenamings.addAll(performRenaming(userModules, getInfo().getIdStateDesignatorDefs()));


		GeneratedData data = new GeneratedData();
		data.setAllRenamings(allRenamings);

		return data;
	}
	
	public static List<INode> getNodes(List<? extends INode> ast)
	{
		List<INode> nodes = new LinkedList<>();

		nodes.addAll(ast);

		return nodes;
	}
	
	public static List<AModuleModules> getModules(List<INode> ast)
	{
		List<AModuleModules> modules = new LinkedList<>();

		for (INode n : ast)
		{
			if (n instanceof AModuleModules)
			{
				modules.add((AModuleModules) n);
			}
		}

		return modules;
	}
	
	private List<Renaming> performRenaming(List<INode> mergedParseLists,
			Map<AIdentifierStateDesignator, PDefinition> idDefs)
			throws AnalysisException
	{

		List<Renaming> allRenamings = new LinkedList<Renaming>();

		RefactoringRenameCollector renamingsCollector = new RefactoringRenameCollector(generator.getIRInfo().getTcFactory(), idDefs);
//		VarShadowingRenameCollector renamingsCollector = new VarShadowingRenameCollector(generator.getIRInfo().getTcFactory(), idDefs);
		Renamer renamer = new Renamer();
//		VarRenamer renamer = new VarRenamer();
		for (INode node : mergedParseLists)
		{
			Set<Renaming> currentRenamings = renamer.computeRenamings(node, renamingsCollector); //TODO det er nok her det går galt

			if (!currentRenamings.isEmpty())
			{
				renamer.rename(node, currentRenamings);
				allRenamings.addAll(currentRenamings);
			}
		}

		Collections.sort(allRenamings);

		return allRenamings;
	}
	
	protected List<INode> getUserModules(List<? extends INode> ast)
	{
		List<INode> userModules = new LinkedList<INode>();

		for (INode node : ast)
		{
			if (!getInfo().getDeclAssistant().isLibrary(node))
			{
				userModules.add(node);
			}
		}

		return userModules;
	}

	public IRInfo getInfo()
	{
		return generator.getIRInfo();
	}

	
}
