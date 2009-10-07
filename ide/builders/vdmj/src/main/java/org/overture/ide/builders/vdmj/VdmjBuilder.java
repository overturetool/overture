package org.overture.ide.builders.vdmj;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.overture.ide.builders.builder.AbstractBuilder;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;

public abstract class VdmjBuilder  extends AbstractBuilder
{
	private final int adjustPosition = 1;
	
	protected IStatus buileModelElements(IProject project, IEclipseVdmj eclipseType)
	{
		
		ExitStatus typeCheckStatus = null;
		
		typeCheckStatus = eclipseType.typeCheck();
		if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
		{
			for (VDMError error : eclipseType.getTypeErrors())
			{
				addErrorMarker(error);
			}
		}
		for (VDMWarning warning : eclipseType.getTypeWarnings())
		{
			addWarningMarker(warning);
		}

		if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
		{
			IStatus typeChecked = new Status(IStatus.ERROR,
					VdmjBuilderPluginConstants.PLUGIN_ID, 0, "not typechecked",
					null);
			return typeChecked;
		} else
		{

			IStatus typeChecked = new Status(IStatus.OK,
					VdmjBuilderPluginConstants.PLUGIN_ID, 0, "Type Checked",
					null);
			return typeChecked;
		}
		
	}

	private void addErrorMarker(VDMError error)
	{
		try
		{
			this.addMarker(
					findIFile(error.location.file),
					error.message,
					error.location.startLine,
					IMarker.SEVERITY_ERROR,
					error.location.startPos - adjustPosition,
					error.location.endPos - adjustPosition);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void addWarningMarker(VDMWarning error)
	{
		try
		{
			this.addMarker(
					findIFile(error.location.file),
					error.message,
					error.location.startLine,
					IMarker.SEVERITY_WARNING,
					error.location.startPos - adjustPosition,
					error.location.endPos - adjustPosition);
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
