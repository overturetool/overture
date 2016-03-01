/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.trans.letexps;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class FuncTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;
	
	public FuncTrans(TransAssistantIR transformationAssistant)
	{
		this.transAssistant = transformationAssistant;
	}

	@Override
	public void caseAFuncDeclIR(AFuncDeclIR node) throws AnalysisException
	{
		AMethodDeclIR method = transAssistant.getInfo().getDeclAssistant().funcToMethod(node);

		INode parent = node.parent();
		
		if(parent instanceof ADefaultClassDeclIR)
		{
			ADefaultClassDeclIR enclosingClass = (ADefaultClassDeclIR) parent;
			
			if(enclosingClass.getInvariant() == node)
			{
				transAssistant.replaceNodeWith(node, method);
			}
			else
			{
				enclosingClass.getFunctions().remove(node);
				enclosingClass.getMethods().add(method);
				
				if(method.getPreCond() != null)
				{
					method.getPreCond().apply(this);
				}
				
				if(method.getPostCond() != null)
				{
					method.getPostCond().apply(this);
				}
			}
		}
		else
		{
			// The node's parent is not a class so it must be a pre/post condition
			transAssistant.replaceNodeWith(node, method);
		}
	}
}
