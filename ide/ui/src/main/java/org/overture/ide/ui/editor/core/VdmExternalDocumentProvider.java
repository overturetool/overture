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
package org.overture.ide.ui.editor.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.FileUtility;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.partitioning.IVdmPartitions;
import org.overture.ide.ui.editor.partitioning.VdmDocumentPartitioner;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;

public class VdmExternalDocumentProvider extends FileDocumentProvider
{
	
	protected boolean isExternalAssociated(IFile file)
	{
		return IVdmProject.externalFileContentType.isAssociatedWith(file.getName());
	}

	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{
		IDocument document = null;

		if (element instanceof FileEditorInput)
		{
			IFile file = ((FileEditorInput) element).getFile();
			if (isExternalAssociated(file))
			{
				document = new VdmExternalDocument();
				if (setDocumentContent(document, (IEditorInput) element, getEncoding(element)))
				{
					setupDocument(element, document);
				}
			}
		}

		if (document == null)
		{
			document = super.createDocument(element);
		}

		if (element instanceof FileEditorInput)
		{
			if (document instanceof VdmDocument)
			{
				IFile file = ((FileEditorInput) element).getFile();

				IVdmProject vdmProject = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);

				Assert.isNotNull(vdmProject, "Project of file: "
						+ file.getName() + " is not VDM");

				if (vdmProject != null)
				{

					IVdmSourceUnit source = vdmProject.findSourceUnit(file);
					if (source != null)
					{
						((VdmDocument) document).setSourceUnit(source);
					} else
					{
						// throw new CoreException(new Status(IStatus.ERROR, IVdmUiConstants.PLUGIN_ID,
						// "Error source file not found in build path: "+ file));
					}
				}

			}
		}

		if (document instanceof IDocumentExtension3)
		{
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = new VdmDocumentPartitioner(VdmUIPlugin.getDefault().getPartitionScanner(), VdmPartitionScanner.PARTITION_TYPES);
			extension3.setDocumentPartitioner(IVdmPartitions.VDM_PARTITIONING, partitioner);
			partitioner.connect(document);
		}

		return document;
	}

	@Override
	protected IDocument createEmptyDocument()
	{
		return new VdmExternalDocument();
	}

	@Override
	protected boolean setDocumentContent(IDocument document,
			IEditorInput editorInput, String encoding) throws CoreException
	{
		if (document instanceof VdmExternalDocument)
		{
			IFile file = null;
			if (editorInput instanceof FileEditorInput)
			{
				file = ((FileEditorInput) editorInput).getFile();
			}
			if (editorInput instanceof IStorageEditorInput)
			{
				document.set(getExternalContent(file));
				return true;
			}
		}
		return false;
	}

	protected String getExternalContent(IFile file)
	{
		return FileUtility.getContentExternalText(file);
	}
}
