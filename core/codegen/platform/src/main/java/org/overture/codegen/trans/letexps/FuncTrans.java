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

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class FuncTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	
	public FuncTrans(TransAssistantCG transformationAssistant)
	{
		this.transAssistant = transformationAssistant;
	}

	@Override
	public void caseAFuncDeclCG(AFuncDeclCG node) throws AnalysisException
	{
		AMethodDeclCG method = transAssistant.getInfo().getDeclAssistant().funcToMethod(node);

		INode parent = node.parent();
		
		if(parent instanceof ADefaultClassDeclCG)
		{
			ADefaultClassDeclCG enclosingClass = (ADefaultClassDeclCG) parent;
			
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
