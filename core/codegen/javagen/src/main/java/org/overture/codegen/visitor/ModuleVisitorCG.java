package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExportsCG;
import org.overture.codegen.cgast.SImportsCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.AModuleExportsCG;
import org.overture.codegen.cgast.declarations.AModuleImportsCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class ModuleVisitorCG extends AbstractVisitorCG<IRInfo, AModuleDeclCG>
{
	@Override
	public AModuleDeclCG caseAModuleModules(AModuleModules node, IRInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		AModuleImports imports = node.getImports();
		AModuleExports exports = node.getExports();
		Boolean isDlModule = node.getIsDLModule();
		Boolean isFlat = node.getIsFlat();

		AModuleDeclCG moduleCg = new AModuleDeclCG();
		moduleCg.setName(name);

		if (imports != null)
		{
			SImportsCG importsCg = imports.apply(question.getImportsVisitor(), question);

			if (importsCg instanceof AModuleImportsCG)
			{
				moduleCg.setImport((AModuleImportsCG) importsCg);
			} else
			{
				Logger.getLog().printErrorln("Expected imports to be of type '"
						+ AModuleImportsCG.class.getSimpleName() + "'. Got: "
						+ importsCg + " in " + this.getClass().getSimpleName());
			}
		}

		if (exports != null)
		{
			SExportsCG exportsCg = exports.apply(question.getExportsVisitor(), question);

			if (exportsCg instanceof AModuleExportsCG)
			{
				moduleCg.setExports((AModuleExportsCG) exportsCg);
			} else
			{
				Logger.getLog().printErrorln("Expected export to be of type '"
						+ AModuleExportsCG.class.getSimpleName() + "'. Got: "
						+ exportsCg + " in " + this.getClass().getSimpleName());
			}
		}

		moduleCg.setIsDLModule(isDlModule);
		moduleCg.setIsFlat(isFlat);

		// TODO: other definitions like state definitions
		for (PDefinition def : node.getDefs())
		{
			SDeclCG declCg = def.apply(question.getDeclVisitor(), question);

			if (declCg == null)
			{
				continue;// Unspported stuff returns null by default
			}
			if (declCg instanceof AMethodDeclCG)
			{
				moduleCg.getDecls().add((AMethodDeclCG) declCg);
			} else if (declCg instanceof AFuncDeclCG)
			{
				moduleCg.getDecls().add((AFuncDeclCG) declCg);
			} else if (declCg instanceof ATypeDeclCG)
			{
				moduleCg.getDecls().add((ATypeDeclCG) declCg);
			} else if (declCg instanceof ANamedTraceDeclCG)
			{
				moduleCg.getDecls().add((ANamedTraceDeclCG) declCg);
			} else
			{
				Logger.getLog().printErrorln("Unexpected definition in class: "
						+ name + ": " + def.getName().getName() + " at "
						+ def.getLocation());
			}
		}

		return moduleCg;
	}
}
