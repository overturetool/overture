/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.resources;


public class VdmSourceUnitWorkingCopy extends VdmSourceUnit implements
		IVdmSourceUnit
{

	IVdmSourceUnit sourceUnit = null;

	public VdmSourceUnitWorkingCopy(VdmSourceUnit vdmSourceUnit)
	{
		super(vdmSourceUnit.getProject(), vdmSourceUnit.getFile());
		this.sourceUnit = vdmSourceUnit;
		this.parseList.addAll(this.sourceUnit.getParseList());
	}

	public void commit()
	{
		this.sourceUnit.reconcile(this.parseList, this.parseErrors);
		fireChangedEvent();
	}

	@Override
	public String toString()
	{
		return super.toString() + " - Working copy";
	}

	public IVdmSourceUnit getSource()
	{
		return this.sourceUnit;
	}

}
