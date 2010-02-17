package org.overture.ide.ui.editor.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.partitioning.VdmDocumentPartitioner;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;

public class VdmDocumentProvider extends FileDocumentProvider {

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		
		IDocument document = super.createDocument(element);
		
		if(document instanceof IDocumentExtension3)
		{
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = 
				new VdmDocumentPartitioner(VdmUIPlugin.getDefault().getPartitionScanner(), VdmPartitionScanner.PARTITION_TYPES);
			extension3.setDocumentPartitioner(VdmUIPlugin.VDM_PARTITIONING, partitioner);
			partitioner.connect(document);			
		}
		
		return document;
	}
	
	@Override
	protected IDocument createEmptyDocument() {
		return new VdmDocument();
	}
}
