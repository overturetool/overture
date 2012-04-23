package com.lausdahl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;

public class DestecsDocumentProvider extends FileDocumentProvider
{
	@Override
	protected IDocument createDocument(Object element) throws CoreException
	{

		IDocument document = super.createDocument(element);

		if (element instanceof FileEditorInput)
		{
			if (document instanceof AstcDocument)
			{
				IFile file = ((FileEditorInput) element).getFile();
				((AstcDocument)document).setFile(file);
//
//				IVdmProject vdmProject = (IVdmProject) file.getProject().getAdapter(IVdmProject.class);
//
//				Assert.isNotNull(vdmProject, "Project of file: "
//						+ file.getName() + " is not VDM");
//
//				if (vdmProject != null)
//				{
//
//					IVdmSourceUnit source = vdmProject.findSourceUnit(file);
//					((VdmDocument) document).setSourceUnit(source);
//				}
//
			}
		}

//		if (document instanceof IDocumentExtension3)
//		{
//			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
//			IDocumentPartitioner partitioner = new VdmDocumentPartitioner(VdmUIPlugin.getDefault().getPartitionScanner(), VdmPartitionScanner.PARTITION_TYPES);
//			extension3.setDocumentPartitioner(VdmUIPlugin.VDM_PARTITIONING, partitioner);
//			partitioner.connect(document);
//		}

		return document;
	}

	@Override
	protected IDocument createEmptyDocument()
	{
		return new AstcDocument();
	}
}
