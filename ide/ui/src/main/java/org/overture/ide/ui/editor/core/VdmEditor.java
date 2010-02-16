package org.overture.ide.ui.editor.core;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;


public abstract class VdmEditor extends TextEditor {

	
	public VdmEditor()
	{
		super();
		setDocumentProvider(new VdmDocumentProvider());
	}
	
	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		
		ISourceViewer viewer = new VdmSourceViewer(parent,ruler, getOverviewRuler(), isOverviewRulerVisible(), styles, this);
		
		getSourceViewerDecorationSupport(viewer);
		
		return viewer;
		
	}
	
	@Override
	protected void initializeEditor() {		
		super.initializeEditor();
		setSourceViewerConfiguration(getVdmSourceViewerConfiguration());
	}

	protected abstract VdmSourceViewerConfiguration getVdmSourceViewerConfiguration();
	
	
	
}
