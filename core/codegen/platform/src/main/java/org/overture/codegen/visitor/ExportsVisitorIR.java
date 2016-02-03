package org.overture.codegen.visitor;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleExports;
import org.overture.ast.modules.PExport;
import org.overture.codegen.ir.SExportIR;
import org.overture.codegen.ir.SExportsIR;
import org.overture.codegen.ir.declarations.AModuleExportsIR;
import org.overture.codegen.ir.IRInfo;

public class ExportsVisitorIR extends AbstractVisitorIR<IRInfo,SExportsIR>
{
	@Override
	public SExportsIR caseAModuleExports(AModuleExports node, IRInfo question)
			throws AnalysisException
	{
		AModuleExportsIR moduleExportsCg = new AModuleExportsIR();
		
		for(List<PExport> export : node.getExports())
		{
			List<SExportIR> exportCg = new LinkedList<SExportIR>();
			
			for(PExport exportItem : export)
			{
				SExportIR exportItemCg = exportItem.apply(question.getExportVisitor(), question);
				
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
