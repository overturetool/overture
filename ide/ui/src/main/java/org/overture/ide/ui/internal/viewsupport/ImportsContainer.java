/*
 * #%~
 * org.overture.ide.ui
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
package org.overture.ide.ui.internal.viewsupport;

import java.util.List;
import java.util.Map;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleImports;
import org.overture.ast.node.INode;

public class ImportsContainer implements INode {

	
	private AModuleImports imports = null;
	private List<PDefinition> importDefs = null;
	
	public ImportsContainer(AModuleImports imports, List<PDefinition> importDefs){
		this.imports = imports;
		this.importDefs = importDefs;
	}
	
	public List<PDefinition> getImportDefs() {
		return importDefs;
	}
	
	public AModuleImports getImports() {
		return imports;
	}

	@Override
	public void apply(IAnalysis arg0) throws AnalysisException
	{
	}

	@Override
	public <A> A apply(IAnswer<A> arg0) throws AnalysisException
	{
		return null;
	}

	@Override
	public <Q> void apply(IQuestion<Q> arg0, Q arg1) throws AnalysisException
	{
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> arg0, Q arg1)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public Object clone()
	{
		return null;
	}

	@Override
	public INode clone(Map<INode, INode> arg0)
	{
		return null;
	}

	@Override
	public void removeChild(INode arg0)
	{
	}

	@Override
	public <T extends INode> T getAncestor(Class<T> arg0)
	{
		return null;
	}

	@Override
	public Map<String, Object> getChildren(Boolean arg0)
	{
		return null;
	}

	@Override
	public INode parent()
	{
		return null;
	}

	@Override
	public void parent(INode arg0)
	{
	}

	@Override
	public void replaceChild(INode arg0, INode arg1)
	{
		
	}
	
}
