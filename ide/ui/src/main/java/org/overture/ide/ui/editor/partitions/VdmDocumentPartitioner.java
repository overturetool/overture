package org.overture.ide.ui.editor.partitions;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.overture.ide.ui.VdmUIPlugin;

public class VdmDocumentPartitioner extends FastPartitioner implements
		IDocumentPartitioner {
	
	public VdmDocumentPartitioner(IPartitionTokenScanner scanner,
			String[] legalContentTypes) {
		super(scanner, legalContentTypes);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void connect(IDocument document, boolean delayInitialise)
	{
	    super.connect(document, delayInitialise);
	    printPartitions(document);
	}

	
	public void printPartitions(IDocument document)
	{
	    StringBuffer buffer = new StringBuffer();

	    ITypedRegion[] partitions = computePartitioning(0, document.getLength());
	    for (int i = 0; i < partitions.length; i++)
	    {
	        try
	        {
	            buffer.append("Partition type: " 
	              + partitions[i].getType() 
	              + ", offset: " + partitions[i].getOffset()
	              + ", length: " + partitions[i].getLength());
	            buffer.append("\n");
	            buffer.append("Text:\n");
	            buffer.append(document.get(partitions[i].getOffset(), 
	             partitions[i].getLength()));
	            buffer.append("\n---------------------------\n\n\n");
	        }
	        catch (BadLocationException e)
	        {
	            VdmUIPlugin.printe(e);
	        }
	    }
	    VdmUIPlugin.println(buffer.toString());
	}

}
