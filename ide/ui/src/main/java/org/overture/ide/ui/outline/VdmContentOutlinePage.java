package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.ast.IVdmElement;
import org.overture.ide.ui.editor.core.VdmEditor;

public class VdmContentOutlinePage extends ContentOutlinePage implements IContentOutlinePage {

	private VdmEditor vdmEditor;
	private IEditorInput editorInput;
	private TreeViewer viewer;
	
	public VdmContentOutlinePage(VdmEditor vdmEditor) {
		this.vdmEditor = vdmEditor;		
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
	       viewer= getTreeViewer();
	      viewer.setContentProvider(new VdmOutlineTreeContentProvider());
	      viewer.setLabelProvider(new VdmOutlineLabelProvider());
	      viewer.addSelectionChangedListener(this);
	      viewer.setInput(editorInput);

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Control getControl() {
		return null;
	}

	public void setActionBars(IActionBars actionBars) {
		// TODO Auto-generated method stub

	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public void setInput(IVdmElement editorInput) {
		
		viewer.setInput(editorInput);
		
	}

}
