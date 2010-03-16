package org.overture.ide.ui.outline;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;

public class VdmContentOutlinePage extends ContentOutlinePage implements
		IContentOutlinePage
{
	/**
	 * The element change listener of the java outline viewer.
	 * @see IElementChangedListener
	 */
	protected class ElementChangedListener implements IElementChangedListener {

	
	
		public void elementChanged(final ElementChangedEvent e) {

			if (getControl() == null)
				return;

			Display d= getControl().getDisplay();
			if (d != null) {
				d.asyncExec(new Runnable() {
					public void run() {
						IVdmSourceUnit cu= (IVdmSourceUnit) fInput;
//						IVdmElement base= cu;
//						if (fTopLevelTypeOnly) {
//							base= cu.findPrimaryType();
//							if (base == null) {
								if (fOutlineViewer != null)
									fOutlineViewer.refresh(true);
//								return;
//							}
//						}
//						IVdmElementDelta delta= findElement(base, e.getDelta());
//						if (delta != null && fOutlineViewer != null) {
//							fOutlineViewer.reconcile(delta);
//						}
					}
				});
			}
		}

		private boolean isPossibleStructuralChange(IVdmElementDelta cuDelta) {
			if (cuDelta.getKind() != IVdmElementDelta.CHANGED) {
				return true; // add or remove
			}
//			int flags= cuDelta.getFlags();
//			if ((flags & IVdmElementDelta.F_CHILDREN) != 0) {
//				return true;
//			}
//			return (flags & (IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_FINE_GRAINED)) == IJavaElementDelta.F_CONTENT;
			return true;
		}

		protected IVdmElementDelta findElement(IVdmSourceUnit unit, IVdmElementDelta delta) {

			if (delta == null || unit == null)
				return null;

			if(delta.getElement() instanceof IVdmSourceUnit)
			{
			IVdmSourceUnit element= (IVdmSourceUnit) delta.getElement();

			if (unit.equals(element)) {
				if (isPossibleStructuralChange(delta)) {
					return delta;
				}
				return null;
			}
			}

//			if (element.getElementType() > IVdmElement.CLASS_FILE)
//				return null;
//
//			IJavaElementDelta[] children= delta.getAffectedChildren();
//			if (children == null || children.length == 0)
//				return null;
//
//			for (int i= 0; i < children.length; i++) {
//				IJavaElementDelta d= findElement(unit, children[i]);
//				if (d != null)
//					return d;
//			}

			return null;
		}
	}
	
	/**
	 * Constant indicating that all levels of the tree should be expanded or
	 * collapsed.
	 *
	 * @see #expandToLevel(int)
	 * @see #collapseToLevel(Object, int)
	 */
	public static final int ALL_LEVELS = -1;

	private VdmEditor vdmEditor;
	
	private TreeViewer fOutlineViewer;
	private IVdmElement fInput;
	private ElementChangedListener fListener;

	public VdmContentOutlinePage(VdmEditor vdmEditor) {
		this.vdmEditor = vdmEditor;	
	}

	@Override
	public void createControl(Composite parent)
	{

		fOutlineViewer = new TreeViewer(parent);
		fOutlineViewer.setContentProvider(new VdmOutlineTreeContentProvider());
		//fOutlineViewer.setLabelProvider(new VdmOutlineLabelProvider());
		fOutlineViewer.setLabelProvider(new DecorationgVdmLabelProvider(new VdmUILabelProvider()) );
		fOutlineViewer.addSelectionChangedListener(this);
		fOutlineViewer.setAutoExpandLevel(ALL_LEVELS);
		fOutlineViewer.setInput(fInput);
		
		addSelectionChangedListener(new VdmSelectionListener());

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
		super.addSelectionChangedListener(listener);

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
	public void setInput(IVdmElement je)
	{
		this.fInput = je;
		if (fOutlineViewer != null)
		{
			fOutlineViewer.setInput(fInput);
			fOutlineViewer.expandAll();
			
		}
		if(fListener!=null)
			VdmCore.removeElementChangedListener(fListener);
		fListener= new ElementChangedListener();
		VdmCore.addElementChangedListener(fListener);
	}

}
