//package org.overture.ide.ui.editor.partitioning;
//
//import org.eclipse.jface.text.DocumentEvent;
//import org.eclipse.jface.text.IRegion;
//import org.eclipse.jface.text.ITypedRegion;
//import org.eclipse.jface.text.Region;
//import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
//import org.eclipse.jface.text.rules.ITokenScanner;
//
//public class VdmDamagerRepairer extends DefaultDamagerRepairer
//{
//
//	public VdmDamagerRepairer(ITokenScanner scanner)
//	{
//		super(scanner);
//	}
//	
//	@Override
//	public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e,
//			boolean documentPartitioningChanged) {
//		return new Region(0, e.getDocument().getLength());
//	}
//
//}
