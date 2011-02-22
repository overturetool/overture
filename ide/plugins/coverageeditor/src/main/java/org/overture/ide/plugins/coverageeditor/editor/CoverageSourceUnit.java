package org.overture.ide.plugins.coverageeditor.editor;

import org.eclipse.core.resources.IFile;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnit;
import org.overture.ide.core.resources.VdmSourceUnitWorkingCopy;

public class CoverageSourceUnit extends VdmSourceUnit implements IVdmSourceUnit
{
	public CoverageSourceUnit(IVdmProject project, IFile file)
	{
		super(project, file);
	}

	public VdmSourceUnitWorkingCopy getWorkingCopy()
	{
		return null;
	}
}
