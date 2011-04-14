package org.overture.ide.core.ast;

import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnitWorkingCopy;

public class VdmModelWorkingCopy extends VdmModel implements IVdmModel
{
	VdmModel sourceModel;
	boolean discarded = false;

	public VdmModelWorkingCopy(VdmModel sourceModel)
	{
		this.sourceModel = sourceModel;
		for (IVdmSourceUnit source : sourceModel.getSourceUnits())
		{
			this.addVdmSourceUnit(source.getWorkingCopy());
		}
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
