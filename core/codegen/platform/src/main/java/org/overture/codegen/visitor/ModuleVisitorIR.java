package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExportsIR;
import org.overture.codegen.ir.SImportsIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.AModuleExportsIR;
import org.overture.codegen.ir.declarations.AModuleImportsIR;
import org.overture.codegen.ir.declarations.ANamedTraceDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.declarations.ATypeDeclIR;
import org.overture.codegen.ir.IRInfo;

public class ModuleVisitorIR extends AbstractVisitorIR<IRInfo, AModuleDeclIR>
{
	@Override
	public AModuleDeclIR caseAModuleModules(AModuleModules node, IRInfo question)
			throws AnalysisException
	{
		String name = node.getName().getName();
		AModuleImports imports = node.getImports();
		AModuleExports exports = node.getExports();
		Boolean isDlModule = node.getIsDLModule();
		Boolean isFlat = node.getIsFlat();

		AModuleDeclIR moduleCg = new AModuleDeclIR();
		moduleCg.setName(name);

		if (imports != null)
		{
			SImportsIR importsCg = imports.apply(question.getImportsVisitor(), question);

			if (importsCg instanceof AModuleImportsIR)
			{
				moduleCg.setImport((AModuleImportsIR) importsCg);
			} else
			{
				log.error("Expected imports to be of type '"
						+ AModuleImportsIR.class.getSimpleName() + "'. Got: "
						+ importsCg);
			}
		}

		if (exports != null)
		{
			SExportsIR exportsCg = exports.apply(question.getExportsVisitor(), question);

			if (exportsCg instanceof AModuleExportsIR)
			{
				moduleCg.setExports((AModuleExportsIR) exportsCg);
			} else
			{
				log.error("Expected export to be of type '"
						+ AModuleExportsIR.class.getSimpleName() + "'. Got: "
						+ exportsCg);
			}
		}

		moduleCg.setIsDLModule(isDlModule);
		moduleCg.setIsFlat(isFlat);

		for (PDefinition def : node.getDefs())
		{
			SDeclIR declCg = def.apply(question.getDeclVisitor(), question);

			if (declCg == null)
			{
				// Unspported stuff returns null by default
				continue;
				
			} else if (declCg instanceof AMethodDeclIR
					|| declCg instanceof AFuncDeclIR
					|| declCg instanceof ATypeDeclIR
					|| declCg instanceof AStateDeclIR
					|| declCg instanceof ANamedTraceDeclIR
					|| declCg instanceof AFieldDeclIR)
			{
				moduleCg.getDecls().add(declCg);
			} else
			{
				log.error("Unexpected definition in module: " + def);
			}
		}

		question.addModule(moduleCg);
		return moduleCg;
	}
}
