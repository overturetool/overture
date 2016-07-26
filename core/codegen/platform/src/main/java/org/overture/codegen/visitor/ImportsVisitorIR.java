package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AFromModuleImports;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.modules.PImport;
import org.overture.codegen.ir.SImportIR;
import org.overture.codegen.ir.SImportsIR;
import org.overture.codegen.ir.declarations.AFromModuleImportsIR;
import org.overture.codegen.ir.declarations.AModuleImportsIR;
import org.overture.codegen.ir.IRInfo;

public class ImportsVisitorIR  extends AbstractVisitorIR<IRInfo, SImportsIR> {

	@Override
	public SImportsIR caseAModuleImports(AModuleImports node,
			IRInfo question) throws AnalysisException {
		
		String name = node.getName() != null ? node.getName().getName() : null;

		AModuleImportsIR moduleImportsCg = new AModuleImportsIR();
		moduleImportsCg.setName(name);
		
		for(AFromModuleImports fromModuleImport : node.getImports())
		{
			SImportsIR fromModuleImportCg = fromModuleImport.apply(question.getImportsVisitor(), question);
			
			if(fromModuleImportCg instanceof AFromModuleImportsIR)
			{
				moduleImportsCg.getImports().add((AFromModuleImportsIR) fromModuleImportCg);
			}
			else
			{
				log.error("Expected fromModuleImportCg to be of type '"
						+ AFromModuleImportsIR.class.getSimpleName()
						+ "'. Got: " + fromModuleImportCg);
				return null;
			}
		}
		
		return moduleImportsCg;
	}
	
	@Override
	public SImportsIR caseAFromModuleImports(AFromModuleImports node,
			IRInfo question) throws AnalysisException {
		
		String name = node.getName() != null ? node.getName().getName() : null;
		
		AFromModuleImportsIR fromImportCg = new AFromModuleImportsIR();
		fromImportCg.setName(name);
		
		for(List<PImport> sig : node.getSignatures())
		{
			List<SImportIR> sigCg = new LinkedList<SImportIR>();
			
			for(PImport imp : sig)
			{
				SImportIR impCg = imp.apply(question.getImportVisitor(), question);
				
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
