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
package org.overture.ide.vdmrt.ui.editor.core;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.overture.ide.ui.editor.core.VdmSourceViewerConfiguration;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.vdmrt.ui.editor.contentAssist.VdmRtContentAssistent;
import org.overture.ide.vdmrt.ui.editor.syntax.VdmRtCodeScanner;


public class VdmRtSourceViewerConfiguration extends
		VdmSourceViewerConfiguration {

	public VdmRtSourceViewerConfiguration(IPreferenceStore preferenceStore)
	{
		super(preferenceStore);
	}

	@Override
	protected ITokenScanner getVdmCodeScanner() {
		return new VdmRtCodeScanner(new VdmColorProvider());
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant  assistant = new VdmRtContentAssistent();
		assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return assistant;
	}

}
