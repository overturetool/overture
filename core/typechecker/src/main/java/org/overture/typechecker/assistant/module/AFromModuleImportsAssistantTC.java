package org.overture.typechecker.assistant.module;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.modules.PImport;
import org.overture.typechecker.ModuleEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.visitor.TypeCheckVisitor;

public class AFromModuleImportsAssistantTC {

	public static List<PDefinition> getDefinitions(
			AFromModuleImports ifm, AModuleModules from) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		for (List<PImport> ofType: ifm.getSignatures())
		{
			for (PImport imp: ofType)
			{
				defs.addAll(PImportAssistantTC.getDefinitions(imp,from));
			}
		}

		return defs;
	}

	public static void typeCheck(AFromModuleImports ifm, ModuleEnvironment env) throws AnalysisException {
		TypeCheckVisitor tc = new TypeCheckVisitor();
		TypeCheckInfo question = new TypeCheckInfo(env, null, null);
		
		for (List<PImport> ofType: ifm.getSignatures())
		{
			for (PImport imp: ofType)
			{
				 imp.apply(tc, question);
			}
		}
		
	}

}
