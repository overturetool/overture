/*
 * #%~
 * org.overture.ide.builders.vdmj
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
package org.overture.ide.builders.vdmj;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.overture.ide.core.builder.AbstractVdmBuilder;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;

public abstract class VdmjBuilder extends AbstractVdmBuilder
{
	private List<VDMError> errors = new ArrayList<VDMError>();
	private List<VDMWarning> warnings = new ArrayList<VDMWarning>();
	private IVdmProject project = null;

	protected IStatus buildModel(IVdmProject project)
	{
		this.project = project;
		ExitStatus typeCheckStatus = null;

		typeCheckStatus = typeCheck();
		if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
		{
			for (VDMError error : errors)
			{
				addErrorMarker(error);
			}
		}
		if (!getProject().hasSuppressWarnings())
			for (VDMWarning warning : warnings)
			{
				
				
				addWarningMarker(project, warning);
			}
		IStatus typeChecked = null;

		if (typeCheckStatus == ExitStatus.EXIT_ERRORS)
		{
			typeChecked = new Status(IStatus.ERROR, IBuilderVdmjConstants.PLUGIN_ID, 0, "not typechecked", null);

		} else
		{

			typeChecked = new Status(IStatus.OK, IBuilderVdmjConstants.PLUGIN_ID, 0, "Type Checked", null);
			//FIXME fire build complete event
		}
		return typeChecked;

	}

	private void addErrorMarker(VDMError error)
	{
		addErrorMarker(error.location.getFile(), error.toProblemString(), error.location, IBuilderVdmjConstants.PLUGIN_ID);
	}

	private void addWarningMarker(IVdmProject project, VDMWarning error)
	{
		addWarningMarker(error.location.getFile(), error.toProblemString(), error.location, IBuilderVdmjConstants.PLUGIN_ID);
	}

	/**
	 * Handle Errors
	 * 
	 * @param list
	 *            encountered during a parse or type check
	 */
	protected void processErrors(List<VDMError> errors)
	{
		this.errors.addAll(errors);
	};

	/**
	 * Handle Warnings
	 * 
	 * @param errors
	 *            encountered during a parse or type check
	 */
	protected void processWarnings(List<VDMWarning> warnings)
	{
		this.warnings.addAll(warnings);
	};

	protected void processInternalError(Throwable e)
	{
		e.printStackTrace();
		System.err.println(e.toString());
		try
		{
			if (project.getSpecFiles().size() > 0)
			{
				addErrorMarker(project.getSpecFiles().get(0).getFile(), e.toString(), 0);
			}
		} catch (CoreException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	};

	public abstract ExitStatus typeCheck();
}
