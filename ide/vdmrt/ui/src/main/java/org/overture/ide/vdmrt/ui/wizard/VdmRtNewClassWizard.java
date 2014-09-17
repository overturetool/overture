/*
 * #%~
 * org.overture.ide.vdmrt.ui
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
package org.overture.ide.vdmrt.ui.wizard;

import org.overture.ide.vdmpp.ui.wizard.VdmPpNewClassWizard;

public class VdmRtNewClassWizard extends VdmPpNewClassWizard {

	@Override
	protected String getPageDescription() {		
		return "Chose new file name and location";
	}

	@Override
	protected String getPageName() {		
		return "VDM-RT New File Wizard";
	}

	@Override
	protected String getPageTitle() {		
		return "VDM-RT New File Wizard";
	}

	@Override
	protected String getFileExtension()
	{
		return "vdmrt";
	}

}
