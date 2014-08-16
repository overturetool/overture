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

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.plugins.externaleditor.VdmExternalEditorImpl;
import org.overture.ide.ui.editor.core.VdmExternalDocumentProvider;
import org.overture.parser.lex.BacktrackInputReader.ReaderType;

public class ExternalCoverageEditor extends VdmExternalEditorImpl
{

	private static final String DOCXCOV = "docxcov";
	private static final String DOCCOV = "doccov";
	private static final String ODTCOV = "odfcov";

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

		protected ReaderType getReaderType(File file)
		{
			if (file.getName().endsWith(DOCXCOV))
			{
				return ReaderType.Docx;
			} else if (file.getName().endsWith(DOCCOV))
			{
				return ReaderType.Doc;
			} else if (file.getName().endsWith(ODTCOV))
			{
				return ReaderType.Odf;
			}

			return ReaderType.Latex;
		};

	};

	private class VdmExternalCoverageDocumentProvider extends
			VdmExternalDocumentProvider
	{

		@Override
		protected boolean isExternalAssociated(IFile file)
		{
			return super.isExternalAssociated(file)
					|| file.getName().endsWith(DOCXCOV)
					|| file.getName().endsWith(DOCCOV)
					|| file.getName().endsWith(ODTCOV);
		}

		@Override
		protected String getExternalContent(IFile file)
		{
			InputStreamReader reader = null;
			try
			{
				reader = FileUtility.getReader(covEditor.getReaderType(file.getLocation().toFile()), file);

				if (reader != null)
				{
					return FileUtility.getContentExternalText(file, reader);
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return super.getExternalContent(file);
		}

	}

	public ExternalCoverageEditor()
	{
		super();
		setDocumentProvider(new VdmExternalCoverageDocumentProvider());
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
