/*
 * #%~
 * org.overture.ide.plugins.externaleditor
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
package org.overture.ide.plugins.externaleditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.editor.core.VdmExternalEditor;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.vdmpp.ui.editor.core.VdmPpSourceViewerConfiguration;
import org.overture.ide.vdmrt.ui.editor.core.VdmRtSourceViewerConfiguration;
import org.overture.ide.vdmsl.ui.editor.core.VdmSlSourceViewerConfiguration;

public class VdmExternalEditorImpl extends VdmExternalEditor
{
	@Override
	public VdmSourceViewerConfiguration getVdmSourceViewerConfiguration(IPreferenceStore preferenceStore)
	{
		return new VdmSlSourceViewerConfiguration(preferenceStore);
	}
	
	
	@Override
	protected boolean isPrefQuickDiffAlwaysOn()
	{
		return false;
	}
	
	@Override
	protected void doSetInput(IEditorInput input) throws CoreException
	{

		if (input instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) input).getFile();
			
			IProject p =file.getProject();
			
			IVdmProject project = (IVdmProject) p.getAdapter(IVdmProject.class);
			
			VdmSourceViewerConfiguration config = null;
			switch (project.getDialect())
			{
				case VDM_PP:
					config = new VdmPpSourceViewerConfiguration(getPreferenceStore());
					break;
				case VDM_RT:
					config = new VdmRtSourceViewerConfiguration(getPreferenceStore());
					break;
				case VDM_SL:
					config = new VdmSlSourceViewerConfiguration(getPreferenceStore());
					break;
				case CML:
					break;
				
			}
			super.fVdmSourceViewer = config;
			super.setSourceViewerConfiguration(config);
		}
		
		super.doSetInput(input);
	}
}
