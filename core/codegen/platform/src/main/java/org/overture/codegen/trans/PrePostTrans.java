package org.overture.codegen.trans;

import java.util.LinkedList;

import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class PrePostTrans extends DepthFirstAnalysisAdaptor {
	
	private IRInfo info;
	
	public PrePostTrans(IRInfo info)
	{
		this.info = info;
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node)
			throws org.overture.codegen.ir.analysis.AnalysisException {
		
		if(!info.getSettings().generatePreConds())
		{
			return;
		}
		
		ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);
		
		if(enclosingClass == null)
		{
			Logger.getLog().printErrorln("Could not find enclosing class for method: " + node);
			return;
		}
		
		SDeclIR preCond = node.getPreCond();
		if(preCond instanceof AMethodDeclIR)
		{
			AMethodDeclIR preCondMethod = (AMethodDeclIR) preCond;			
			
			if(info.getSettings().makePreCondsPublic())
			{
				preCondMethod.setAccess(IRConstants.PUBLIC);
			}
			
			enclosingClass.getMethods().add(preCondMethod);

			if(node.getStatic() != null && !node.getStatic())
			{
				preCondMethod.setStatic(false);
				
				//No need to pass self as the last argument
				LinkedList<STypeIR> paramTypes = preCondMethod.getMethodType().getParams();
				paramTypes.remove(paramTypes.size() - 1);
				
				LinkedList<AFormalParamLocalParamIR> formalParams = preCondMethod.getFormalParams();
				formalParams.remove(formalParams.size() - 1);
			}
		}

		SDeclIR postCond = node.getPostCond();
		if(postCond instanceof AMethodDeclIR)
		{
			AMethodDeclIR postCondMethod = (AMethodDeclIR) postCond;
			
			if(info.getSettings().makePostCondsPublic())
			{
				postCondMethod.setAccess(IRConstants.PUBLIC);
			}

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
