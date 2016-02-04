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
package org.overture.codegen.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.ir.VdmNodeInfo;

public class Generated
{
	protected String content;
	protected Set<VdmNodeInfo> unsupportedInIr;
	protected Set<IrNodeInfo> unsupportedInTargLang;
	protected Set<IrNodeInfo> transformationWarnings;
	protected List<Exception> mergeErrors;

	public Generated(String content, Set<VdmNodeInfo> unsupportedInIr,
			Set<IrNodeInfo> unsupportedInTargLang,
			List<Exception> mergeErrors)
	{
		this.content = content;
		this.unsupportedInIr = unsupportedInIr;
		this.unsupportedInTargLang = unsupportedInTargLang;
		this.transformationWarnings = new HashSet<IrNodeInfo>();
		this.mergeErrors = mergeErrors;
	}

	public Generated(String content)
	{
		this(content, new HashSet<VdmNodeInfo>(), new HashSet<IrNodeInfo>(),new LinkedList<Exception>());
	}

	public Generated(Set<VdmNodeInfo> unsupportedNodes, Set<IrNodeInfo> unsupportedInTargLang)
	{
		this(null, unsupportedNodes, unsupportedInTargLang,new LinkedList<Exception>());
	}

	public Generated(List<Exception> mergeErrrors)
	{
		this(null, new HashSet<VdmNodeInfo>(), new HashSet<IrNodeInfo>(),mergeErrrors);
	}

	public String getContent()
	{
		return content;
	}

	public Set<VdmNodeInfo> getUnsupportedInIr()
	{
		return unsupportedInIr;
	}
	
	public Set<IrNodeInfo> getUnsupportedInTargLang()
	{
		return unsupportedInTargLang;
	}

	public List<Exception> getMergeErrors()
	{
		return mergeErrors;
	}

	public boolean canBeGenerated()
	{
		return unsupportedInIr.isEmpty() && unsupportedInTargLang.isEmpty();
	}
	
	public boolean hasErrors()
	{
		return !unsupportedInIr.isEmpty() || !unsupportedInTargLang.isEmpty() || !mergeErrors.isEmpty();
	}
	
	public boolean hasUnsupportedIrNodes()
	{
		return !unsupportedInIr.isEmpty();
	}
	
	public boolean hasUnsupportedTargLangNodes()
	{
		return !unsupportedInTargLang.isEmpty();
	}

	public Set<IrNodeInfo> getTransformationWarnings()
	{
		return transformationWarnings;
	}

	public void setTransformationWarnings(Set<IrNodeInfo> transformationWarnings)
	{
		this.transformationWarnings = transformationWarnings;
	}

	public boolean hasMergeErrors()
	{
		return !mergeErrors.isEmpty();
	}
}
