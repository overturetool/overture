package org.overture.codegen.trans;

import java.util.LinkedList;

import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class PrePostTrans extends DepthFirstAnalysisAdaptor {
	
	private IRInfo info;
	
	public PrePostTrans(IRInfo info)
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
		
		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);
		
		if(enclosingClass == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing class for method: " + node);
			return;
		}
		
		SDeclCG preCond = node.getPreCond();
		if(preCond instanceof AMethodDeclCG)
		{
			AMethodDeclCG preCondMethod = (AMethodDeclCG) preCond;			
			enclosingClass.getMethods().add(preCondMethod);

			if(node.getStatic() != null && !node.getStatic())
			{
				preCondMethod.setStatic(false);
				
				//No need to pass self as the last argument
				LinkedList<STypeCG> paramTypes = preCondMethod.getMethodType().getParams();
				paramTypes.remove(paramTypes.size() - 1);
				
				LinkedList<AFormalParamLocalParamCG> formalParams = preCondMethod.getFormalParams();
				formalParams.remove(formalParams.size() - 1);
			}
		}

		SDeclCG postCond = node.getPostCond();
		if(postCond instanceof AMethodDeclCG)
		{
			AMethodDeclCG postCondMethod = (AMethodDeclCG) postCond;

			// Generation of a post condition is only supported for static operations
			// where no 'self' and '~self' are being passed
			if(node.getStatic() != null && node.getStatic())
			{
				enclosingClass.getMethods().add(postCondMethod);
			}
			else
			{
				info.addTransformationWarning(postCondMethod, "Generation of a post condition is only supported for static operations");
			}
		}
	}
}
