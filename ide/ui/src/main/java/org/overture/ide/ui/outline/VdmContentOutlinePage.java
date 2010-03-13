package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.ast.IVdmElement;
import org.overture.ide.ui.editor.core.VdmEditor;

public class VdmContentOutlinePage extends ContentOutlinePage implements
		IContentOutlinePage
{

	private VdmEditor vdmEditor;
	private IEditorInput editorInput;
	private TreeViewer fOutlineViewer;
	private IVdmElement fInput;

	public VdmContentOutlinePage(VdmEditor vdmEditor) {
		this.vdmEditor = vdmEditor;
	}

	@Override
	public void createControl(Composite parent)
	{

		fOutlineViewer = new TreeViewer(parent);
		fOutlineViewer.setContentProvider(new VdmOutlineTreeContentProvider());
		fOutlineViewer.setLabelProvider(new VdmOutlineLabelProvider());
		fOutlineViewer.addSelectionChangedListener(this);
		fOutlineViewer.setInput(fInput);

	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Control getControl()
	{
		if (fOutlineViewer != null)
			return fOutlineViewer.getControl();
		return null;
	}

	@Override
	public void setActionBars(IActionBars actionBars)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public ISelection getSelection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelection(ISelection selection)
	{
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	public void setInput(IVdmElement inputElement)
	{
		this.fInput = inputElement;
		if (fOutlineViewer != null)
		{
			fOutlineViewer.setInput(fInput);
		}
	}

}
