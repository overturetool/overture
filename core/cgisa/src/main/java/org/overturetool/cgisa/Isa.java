/*
 * #%~
 * VDM to Isabelle Code Generation
 * %%
 * Copyright (C) 2008 - 2015 Overture
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
package org.overturetool.cgisa;

import java.io.StringWriter;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.merging.MergeVisitor;
import org.overture.codegen.merging.TemplateCallable;
import org.overture.codegen.merging.TemplateStructure;

public class Isa
{
	private MergeVisitor mergeVisitor;
	
	public Isa(TemplateStructure templateStructure)
	{
		TemplateCallable[] templateCallables = new TemplateCallable[]{new TemplateCallable("Isa",this)};
		this.mergeVisitor = new MergeVisitor(new IsaTemplateManager(templateStructure), templateCallables);
	}
	
	
	
	
	public MergeVisitor getMergeVisitor()
	{
		return mergeVisitor;
	}




	public String trans(INode node) throws AnalysisException
	{
		StringWriter writer = new StringWriter();
		node.apply(mergeVisitor, writer);

		return writer.toString();
	}

}
