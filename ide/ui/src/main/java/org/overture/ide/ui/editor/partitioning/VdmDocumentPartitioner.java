/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.editor.partitioning;


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
	    document.setDocumentPartitioner(this);
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
