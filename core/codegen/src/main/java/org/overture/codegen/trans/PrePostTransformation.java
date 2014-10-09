package org.overture.codegen.trans;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class PrePostTransformation extends DepthFirstAnalysisAdaptor {
	
	private IRInfo info;
	
	public PrePostTransformation(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node)
			throws org.overture.codegen.cgast.analysis.AnalysisException {
		
		if(!info.getSettings().generatePreConds())
		{
			return;
		}
		
		AClassDeclCG enclosingClass = node.getAncestor(AClassDeclCG.class);
		
		if(enclosingClass == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing class for method: " + node);
			return;
		}
		
		SDeclCG preCond = node.getPreCond();
		handleCond(enclosingClass, preCond);
		
		SDeclCG postCond = node.getPostCond();
		handleCond(enclosingClass, postCond);
	}

	private void handleCond(AClassDeclCG enclosingClass, SDeclCG cond) {
		if(cond != null)
		{
			if(!(cond instanceof AMethodDeclCG))
			{
				Logger.getLog().printErrorln("Expected pre/post condition to be a method declaration at this point. Got: " + cond);
				return;
			}
			
			enclosingClass.getMethods().add((AMethodDeclCG) cond);			
		}
	}
}
