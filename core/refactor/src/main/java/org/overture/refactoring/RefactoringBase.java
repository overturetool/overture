package org.overture.refactoring;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.util.modules.ModuleList;

import org.overture.config.Settings;

public class RefactoringBase {
	
	public GeneratedData generate(List<INode> ast) throws AnalysisException
	{

		if (Settings.dialect == Dialect.VDM_SL)
		{
			ModuleList moduleList = new ModuleList(getModules(ast));
			moduleList.combineDefaults();
			ast = getNodes(moduleList);
		}

		//preProcessAst(ast);

		//List<IRStatus<PIR>> statuses = new LinkedList<>();

//		for (INode node : ast)
//		{
//			genIrStatus(statuses, node);
//		}
//
//		return genVdmToTargetLang(statuses);
		return null;
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
}
