package org.overture.ide.ui.editor.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.partitioning.VdmDocumentPartitioner;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;

public class VdmDocumentProvider extends FileDocumentProvider
{

	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{

		IDocument document = super.createDocument(element);

		if (element instanceof FileEditorInput)
		{
			if (document instanceof VdmDocument)
			{
				IFile file = ((FileEditorInput) element).getFile();
				
				IVdmProject vdmProject = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
				
			Assert.isNotNull(vdmProject,"Project of file: "+ file.getName()+" is not VDM");
			
			if(vdmProject != null)
			{
				
				
				IVdmSourceUnit source = vdmProject.findSourceUnit(file);
				((VdmDocument) document).setSourceUnit(source);
			}

				
			}
		}

		if (document instanceof IDocumentExtension3)
		{
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = new VdmDocumentPartitioner(VdmUIPlugin.getDefault()
					.getPartitionScanner(),
					VdmPartitionScanner.PARTITION_TYPES);
			extension3.setDocumentPartitioner(VdmUIPlugin.VDM_PARTITIONING,
					partitioner);
			partitioner.connect(document);
		}

		return document;
	}

	@Override
	protected IDocument createEmptyDocument()
	{
		return new VdmDocument();
	}
}
