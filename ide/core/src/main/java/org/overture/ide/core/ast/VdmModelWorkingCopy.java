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
package org.overture.ide.core.ast;

import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnitWorkingCopy;

public class VdmModelWorkingCopy extends VdmModel implements IVdmModel
{
	VdmModel sourceModel;
	boolean discarded = false;
//FIXME: state!!
	public VdmModelWorkingCopy(VdmModel sourceModel)
	{
		this.sourceModel = sourceModel;
		for (IVdmSourceUnit source : sourceModel.getSourceUnits())
		{
			this.addVdmSourceUnit(source.getWorkingCopy());
		}
		
		
		this.isTypeChecked = sourceModel.isTypeChecked;
		this.isTypeCorrect = sourceModel.isTypeCorrect;
		this.workingCopyNotCommitedCount = 0;

		this.checkedTime= sourceModel.checkedTime;

		
	}

	public synchronized void commit()
	{
		if(discarded)
		{
			return;
		}
		for (IVdmSourceUnit sourceUnit : this.vdmSourceUnits)
		{
			if (sourceUnit instanceof VdmSourceUnitWorkingCopy)
			{
				((VdmSourceUnitWorkingCopy) sourceUnit).commit();
			}
		}
		sourceModel.setTypeCheckedStatus(isTypeCorrect());
		synchronized (sourceModel)
		{
			sourceModel.workingCopyNotCommitedCount--;
		}
	}
	
	public synchronized void discard()
	{
		this.discarded = true;
		synchronized (sourceModel)
		{
			sourceModel.workingCopyNotCommitedCount--;
		}
	}

	@Override
	protected void fireModelCheckedEvent()
	{
		// do not fire from working copy
	}
}
