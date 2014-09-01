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
package org.overture.ide.ui.quickfix;

import java.util.HashSet;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.VdmPluginImages;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.wizard.pages.LibraryUtil;

/**
 * Resolution for missing libraries. This adds a missing library to the vdm project
 * 
 * @author kel
 */
public class ImportStandardLibraryMarkerResolution implements
		IMarkerResolution, IMarkerResolution2
{
	private String libraryName;

	public ImportStandardLibraryMarkerResolution(String libraryName)
	{
		this.libraryName = libraryName;
	}

	@Override
	public String getLabel()
	{
		return "Import Standard library " + libraryName;
	}

	@Override
	public void run(IMarker marker)
	{
		IProject p = marker.getResource().getProject();

		IVdmProject vp = (IVdmProject) p.getAdapter(IVdmProject.class);
		if (vp != null)
		{
			final HashSet<String> importLibraries = new HashSet<String>();
			importLibraries.add(libraryName);
			try
			{
				LibraryUtil.createSelectedLibraries(vp, importLibraries);
			} catch (CoreException e)
			{
				VdmUIPlugin.log("Marker resolution failed to import "
						+ libraryName, e);
			}
		}
	}

	@Override
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage()
	{
		return VdmPluginImages.DESC_OBJS_VDM_LIBRARY.createImage();
	}

}
