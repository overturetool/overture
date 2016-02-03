package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.PImport;
import org.overture.codegen.ir.SImportCG;
import org.overture.codegen.ir.SImportsCG;
import org.overture.codegen.ir.declarations.AFromModuleImportsCG;
import org.overture.codegen.ir.declarations.AModuleImportsCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class ImportsVisitorCG  extends AbstractVisitorCG<IRInfo, SImportsCG> {

	@Override
	public SImportsCG caseAModuleImports(AModuleImports node,
			IRInfo question) throws AnalysisException {
		
		String name = node.getName() != null ? node.getName().getName() : null;

		AModuleImportsCG moduleImportsCg = new AModuleImportsCG();
		moduleImportsCg.setName(name);
		
		for(AFromModuleImports fromModuleImport : node.getImports())
		{
			SImportsCG fromModuleImportCg = fromModuleImport.apply(question.getImportsVisitor(), question);
			
			if(fromModuleImportCg instanceof AFromModuleImportsCG)
			{
				moduleImportsCg.getImports().add((AFromModuleImportsCG) fromModuleImportCg);
			}
			else
			{
				Logger.getLog().printErrorln(
						"Expected fromModuleImportCg to be of type '"
								+ AFromModuleImportsCG.class.getSimpleName()
								+ "'. Got: " + fromModuleImportCg + " in " + this.getClass().getSimpleName());
				return null;
			}
		}
		
		return moduleImportsCg;
	}
	
	@Override
	public SImportsCG caseAFromModuleImports(AFromModuleImports node,
			IRInfo question) throws AnalysisException {
		
		String name = node.getName() != null ? node.getName().getName() : null;
		
		AFromModuleImportsCG fromImportCg = new AFromModuleImportsCG();
		fromImportCg.setName(name);
		
		for(List<PImport> sig : node.getSignatures())
		{
			List<SImportCG> sigCg = new LinkedList<SImportCG>();
			
			for(PImport imp : sig)
			{
				SImportCG impCg = imp.apply(question.getImportVisitor(), question);
				
				if (impCg != null)
				{
					sigCg.add(impCg);
				}
				else
				{
					return null;
				}
			}
			
			fromImportCg.getSignatures().add(sigCg);
		}

		return fromImportCg;
	}
}
