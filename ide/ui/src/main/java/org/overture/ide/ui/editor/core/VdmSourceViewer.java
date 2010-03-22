package org.overture.ide.ui.editor.core;


import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.widgets.Composite;

public class VdmSourceViewer extends SourceViewer{

	//private VdmEditor editor = null;
	private IReconciler reconciler = null;
	
	public VdmSourceViewer(Composite parent, IVerticalRuler ruler,
			IOverviewRuler overviewRuler, boolean overviewRulerVisible,
			int styles) {
		super(parent,ruler,overviewRuler,overviewRulerVisible,styles);
		this.showAnnotations(true);
		
	 
	}

	
	
	public Object getReconciler() {
		return this.reconciler;
	}

	public void setReconciler(IReconciler reconciler) {
		this.reconciler = reconciler;
		
	}

	
}
