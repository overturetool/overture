package org.overture.codegen.trans.conc;

import java.util.List;

import org.overture.codegen.cgast.SNameCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMutexSyncDeclCG;
import org.overture.codegen.cgast.name.ATokenNameCG;
import org.overture.codegen.ir.IRInfo;

public class MutexDeclTransformation extends DepthFirstAnalysisAdaptor
{
	private IRInfo info;
	private List<AClassDeclCG> classes;
	
	public MutexDeclTransformation(IRInfo info, List<AClassDeclCG> classes)
	{
		this.info = info;
		this.classes = classes;
	}
	
	@Override
	public void caseAClassDeclCG(AClassDeclCG node) throws AnalysisException
	{
		if(!info.getSettings().generateConc())
		{
			return;
		}
		
		for(AMutexSyncDeclCG mutex : node.getMutexSyncs())
		{
			for(SNameCG operation : mutex.getOpnames())
			{
				if(operation instanceof ATokenNameCG)
				{
					((ATokenNameCG) operation).getName();
				}
				
			}
		}
	}
	
	
	

}
