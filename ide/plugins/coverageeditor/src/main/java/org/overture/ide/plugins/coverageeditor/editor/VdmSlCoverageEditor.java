/*
 * #%~
 * org.overture.ide.plugins.coverageeditor
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
package org.overture.ide.plugins.coverageeditor.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.overture.ide.vdmsl.ui.editor.core.VdmSlEditor;

public class VdmSlCoverageEditor extends VdmSlEditor
{
	private CoverageEditor covEditor = new CoverageEditor()
	{

		@Override
		protected ISourceViewer getEditorSourceViewer()
		{
			return getSourceViewer();
		}

		@Override
		protected void setEditorDocumentProvider(IDocumentProvider provider)
		{
			setDocumentProvider(provider);

		}

	};

	public VdmSlCoverageEditor()
	{
		super();
		covEditor.setEditorDocumentProvider();
	}

	@Override
	public void doSave(IProgressMonitor monitor)
	{

	}

	@Override
	public void doSaveAs()
	{

	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException
	{
		super.init(site, input);

		covEditor.init(site, input);

	}

	@Override
	public boolean isDirty()
	{
		return false;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);
		covEditor.createPartControl(parent);

	}

	

	@Override
	public boolean isEditable()
	{
		return false;
	}

}
