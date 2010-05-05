package org.overture.ide.ui.outline;

import java.util.List;

import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;

import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;
import org.overturetool.vdmj.ast.IAstNode;
import org.overturetool.vdmj.definitions.ThreadDefinition;

@SuppressWarnings("deprecation")
public class VdmContentOutlinePage extends ContentOutlinePage implements
		IContentOutlinePage
{
	/**
	 * The element change listener of the java outline viewer.
	 * 
	 * @see IElementChangedListener
	 */
	protected class ElementChangedListener implements IElementChangedListener
	{

		public void elementChanged(final ElementChangedEvent e)
		{

			if (getControl() == null)
				return;

			Display d = getControl().getDisplay();
			if (d != null)
			{
				d.asyncExec(new Runnable() {
					public void run()
					{
						// IVdmSourceUnit cu= (IVdmSourceUnit) fInput;
						// IVdmElement base= cu;
						// if (fTopLevelTypeOnly) {
						// base= cu.findPrimaryType();
						// if (base == null) {
						if (fOutlineViewer != null)
						{
							fOutlineViewer.refresh(true);
							fOutlineViewer.expandToLevel(AUTO_EXPAND_LEVEL);
						}
						// return;
						// }
						// }
						// IVdmElementDelta delta= findElement(base, e.getDelta());
						// if (delta != null && fOutlineViewer != null) {
						// fOutlineViewer.reconcile(delta);
						// }
					}
				});
			}
		}

		private boolean isPossibleStructuralChange(IVdmElementDelta cuDelta)
		{
			if (cuDelta.getKind() != IVdmElementDelta.CHANGED)
			{
				return true; // add or remove
			}
			// int flags= cuDelta.getFlags();
			// if ((flags & IVdmElementDelta.F_CHILDREN) != 0) {
			// return true;
			// }
			// return (flags & (IJavaElementDelta.F_CONTENT | IJavaElementDelta.F_FINE_GRAINED)) ==
			// IJavaElementDelta.F_CONTENT;
			return true;
		}

		protected IVdmElementDelta findElement(IVdmSourceUnit unit,
				IVdmElementDelta delta)
		{

			if (delta == null || unit == null)
				return null;

			if (delta.getElement() instanceof IVdmSourceUnit)
			{
				IVdmSourceUnit element = (IVdmSourceUnit) delta.getElement();

				if (unit.equals(element))
				{
					if (isPossibleStructuralChange(delta))
					{
						return delta;
					}
					return null;
				}
			}

			// if (element.getElementType() > IVdmElement.CLASS_FILE)
			// return null;
			//
			// IJavaElementDelta[] children= delta.getAffectedChildren();
			// if (children == null || children.length == 0)
			// return null;
			//
			// for (int i= 0; i < children.length; i++) {
			// IJavaElementDelta d= findElement(unit, children[i]);
			// if (d != null)
			// return d;
			// }

			return null;
		}
	}

	public class VdmOutlineViewer extends TreeViewer
	{

		public VdmOutlineViewer(Composite parent) {
			super(parent);
			setAutoExpandLevel(2);
			setUseHashlookup(true);
		}

		@Override
		protected void fireSelectionChanged(SelectionChangedEvent event)
		{
			if (!inExternalSelectionMode)
			{
				super.fireSelectionChanged(event);
			}
		}

	}

	/**
	 * Constant indicating that all levels of the tree should be expanded or collapsed.
	 * 
	 * @see #expandToLevel(int)
	 * @see #collapseToLevel(Object, int)
	 */
	public static final int ALL_LEVELS = -1;

	private VdmEditor vdmEditor;

	private TreeViewer fOutlineViewer;
	private IVdmElement fInput;
	private ElementChangedListener fListener;

	private ListenerList fSelectionChangedListeners = new ListenerList(ListenerList.IDENTITY);

	private boolean inExternalSelectionMode = false;

	private final int AUTO_EXPAND_LEVEL = 2;

	public VdmContentOutlinePage(VdmEditor vdmEditor) {
		this.vdmEditor = vdmEditor;
	}

	@Override
	public void createControl(Composite parent)
	{

		fOutlineViewer = new VdmOutlineViewer(parent);
		fOutlineViewer.setAutoExpandLevel(AUTO_EXPAND_LEVEL);
		fOutlineViewer.setContentProvider(new VdmOutlineTreeContentProvider());
		// fOutlineViewer.setLabelProvider(new VdmOutlineLabelProvider());
		fOutlineViewer.setLabelProvider(new DecorationgVdmLabelProvider(new VdmUILabelProvider()));
		fOutlineViewer.addSelectionChangedListener(this);
	

		Object[] listeners = fSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			fSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer.addSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		// addSelectionChangedListener(new VdmSelectionListener());

		fOutlineViewer.setInput(fInput);
	}

	@Override
	public void dispose()
	{
		if (vdmEditor == null)
			return;

		vdmEditor.outlinePageClosed();
		vdmEditor = null;

		// fListener.clear();
		// fListener = null;

		// fPostSelectionChangedListeners.clear();
		// fPostSelectionChangedListeners= null;

		// if (fPropertyChangeListener != null) {
		// JavaPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(fPropertyChangeListener);
		// fPropertyChangeListener= null;
		// }

		// if (fMenu != null && !fMenu.isDisposed()) {
		// fMenu.dispose();
		// fMenu= null;
		// }

		// if (fActionGroups != null)
		// fActionGroups.dispose();

		// fTogglePresentation.setEditor(null);

		fOutlineViewer = null;

		super.dispose();

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

	/*
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
			fOutlineViewer.addSelectionChangedListener(listener);
		else
			fSelectionChangedListeners.add(listener);
	}

	/*
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection)
	{
		if (fOutlineViewer != null)
			fOutlineViewer.setSelection(selection);
	}

	/*
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection()
	{
		if (fOutlineViewer == null)
			return StructuredSelection.EMPTY;
		return fOutlineViewer.getSelection();
	}

	/*
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
			fOutlineViewer.removeSelectionChangedListener(listener);
		else
			fSelectionChangedListeners.remove(listener);
	}

	public void setInput(IVdmElement je)
	{
		this.fInput = je;
		if (fOutlineViewer != null)
		{
			fOutlineViewer.setInput(fInput);
			fOutlineViewer.expandToLevel(AUTO_EXPAND_LEVEL);

		}
		if (fListener != null)
			VdmCore.removeElementChangedListener(fListener);
		fListener = new ElementChangedListener();
		VdmCore.addElementChangedListener(fListener);
	}

	@SuppressWarnings("unchecked")
	public void select(IAstNode reference)
	{
		
		
		if (fOutlineViewer != null)
		{

			inExternalSelectionMode = true;
			ISelection s = fOutlineViewer.getSelection();
			if (s instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) s;
				List elements = ss.toList();
				if (!elements.contains(reference))
				{
					s = (reference == null ? StructuredSelection.EMPTY
							: new StructuredSelection(reference));
					fOutlineViewer.setSelection(s, true);
				}
			}
			inExternalSelectionMode = false;
		}
	}

}
