/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.core.resources;

import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.VdmElementDelta;


public class VdmSourceUnitWorkingCopy extends VdmSourceUnit implements
		IVdmSourceUnit
{

	IVdmSourceUnit sourceUnit = null;

	public VdmSourceUnitWorkingCopy(VdmSourceUnit vdmSourceUnit)
	{
		super(vdmSourceUnit.getProject(),vdmSourceUnit.getFile());
		this.sourceUnit = vdmSourceUnit;
		this.parseList.addAll(this.sourceUnit.getParseList());
//		this.allLocation.addAll(this.sourceUnit.);
		this.locationToAstNodeMap.putAll(this.sourceUnit.getLocationToAstNodeMap());
	}
	
	
	public void commit()
	{
		this.sourceUnit.reconcile(this.parseList, this.allLocation, this.locationToAstNodeMap, this.parseErrors);
		fireChangedEvent();
	}
	
	@Override
	protected void fireChangedEvent()
	{
		VdmCore.getDeltaProcessor().fire(this, new ElementChangedEvent(new VdmElementDelta(this.sourceUnit, IVdmElementDelta.CHANGED), ElementChangedEvent.DeltaType.POST_BUILD));
	}
	
	@Override
	public String toString()
	{
		return super.toString()+ " - Working copy";
	}

}
