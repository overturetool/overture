package org.overture.ide.core.ast;

import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnitWorkingCopy;

public class VdmModelWorkingCopy extends VdmModel implements IVdmModel
{
	IVdmModel sourceModel;

	public VdmModelWorkingCopy(IVdmModel sourceModel)
	{
		this.sourceModel = sourceModel;
		for (IVdmSourceUnit source : sourceModel.getSourceUnits())
		{
			this.addVdmSourceUnit(source.getWorkingCopy());
		}
	}

	public void commit()
	{
		for (IVdmSourceUnit sourceUnit : this.vdmSourceUnits)
		{
			if (sourceUnit instanceof VdmSourceUnitWorkingCopy)
			{
				((VdmSourceUnitWorkingCopy) sourceUnit).commit();
			}
		}
		sourceModel.setChecked(typeChecked);

	}

	@Override
	protected void fireModelCheckedEvent()
	{
		// do not fire from working copy
	}
}
