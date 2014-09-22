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
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.overture.ide.ui.wizard.pages.LibraryUtil;

/**
 * Marker resolution to be used for quick fixing vdm errors
 * 
 * @author kel
 */
public class MarkerResolutionGenerator implements IMarkerResolutionGenerator,
		IMarkerResolutionGenerator2
{

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker)
	{
		Set<IMarkerResolution> resolutions = new HashSet<IMarkerResolution>();

		final String message = marker.getAttribute(IMarker.MESSAGE, "");

		if (hasResolutions(marker))
		{
			if (message.contains(LibraryUtil.LIB_IO))
			{
				resolutions.add(new ImportStandardLibraryMarkerResolution(LibraryUtil.LIB_IO));
			} else if (message.contains(LibraryUtil.LIB_MATH))
			{
				resolutions.add(new ImportStandardLibraryMarkerResolution(LibraryUtil.LIB_MATH));
			} else if (message.contains(LibraryUtil.LIB_CSV))
			{
				resolutions.add(new ImportStandardLibraryMarkerResolution(LibraryUtil.LIB_CSV));
			} else if (message.contains(LibraryUtil.LIB_VDM_UTIL))
			{
				resolutions.add(new ImportStandardLibraryMarkerResolution(LibraryUtil.LIB_VDM_UTIL));
			}
		}

		return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

	@Override
	public boolean hasResolutions(IMarker marker)
	{
		final String message = marker.getAttribute(IMarker.MESSAGE, "");
		final String lowerCaseMessage = message.toLowerCase();
		return (lowerCaseMessage.contains("undefined") || lowerCaseMessage.contains("is not in scope"))
				&& (containsLib(message, LibraryUtil.LIB_IO)
						|| containsLib(message, LibraryUtil.LIB_MATH)
						|| containsLib(message, LibraryUtil.LIB_VDM_UTIL) || containsLib(message, LibraryUtil.LIB_CSV));
	}

	private boolean containsLib(String msg, String lib)
	{
		return msg.contains(" " + lib) || msg.contains("`" + lib)
				|| msg.contains(lib + "`");
	}

}
