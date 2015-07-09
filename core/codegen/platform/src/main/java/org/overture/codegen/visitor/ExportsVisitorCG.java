package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.PExport;
import org.overture.codegen.cgast.SExportCG;
import org.overture.codegen.cgast.SExportsCG;
import org.overture.codegen.cgast.declarations.AModuleExportsCG;
import org.overture.codegen.ir.IRInfo;

public class ExportsVisitorCG extends AbstractVisitorCG<IRInfo,SExportsCG>
{
	@Override
	public SExportsCG caseAModuleExports(AModuleExports node, IRInfo question)
			throws AnalysisException
	{
		AModuleExportsCG moduleExportsCg = new AModuleExportsCG();
		
		for(List<PExport> export : node.getExports())
		{
			List<SExportCG> exportCg = new LinkedList<SExportCG>();
			
			for(PExport exportItem : export)
			{
				SExportCG exportItemCg = exportItem.apply(question.getExportVisitor(), question);
				
				if(exportItemCg != null)
				{
					exportCg.add(exportItemCg);
				}
				else
				{
					return null;
				}
			}
			
			moduleExportsCg.getExports().add(exportCg);
		}
		
		return moduleExportsCg;
	}
}
