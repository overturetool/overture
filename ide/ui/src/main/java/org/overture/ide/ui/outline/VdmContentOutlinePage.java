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
package org.overture.ide.ui.outline;

import java.util.List;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ast.node.INode;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmElementDelta;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.editor.core.VdmEditor;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;

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

			if (getControl() == null || getControl().isDisposed())
				return;

			Display d = getControl().getDisplay();
			if (d != null)
			{
				d.asyncExec(new Runnable()
				{
					public void run()
					{
						if (fOutlineViewer != null
								&& !fOutlineViewer.getControl().isDisposed())
						{
							fOutlineViewer.refresh(true);
							fOutlineViewer.expandToLevel(AUTO_EXPAND_LEVEL);
						}
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

	/**
	 * Constant indicating that all levels of the tree should be expanded or collapsed.
	 * 
//	 * @see #expandToLevel(int)
//	 * @see #collapseToLevel(Object, int)
	 */
	public static final int ALL_LEVELS = -1;

	private VdmEditor vdmEditor;

	private VdmOutlineViewer fOutlineViewer;
	private IVdmElement fInput;
	private ElementChangedListener fListener;

	private ListenerList fSelectionChangedListeners = new ListenerList(ListenerList.IDENTITY);

	boolean inExternalSelectionMode = false;

	private static final int AUTO_EXPAND_LEVEL = 2;

	private MemberFilterActionGroup fMemberFilterActionGroup;

	private VdmUILabelProvider uiLabelProvider;

	public VdmContentOutlinePage(VdmEditor vdmEditor)
	{
		this.vdmEditor = vdmEditor;
	}

	@Override
	public void createControl(Composite parent)
	{

		fOutlineViewer = new VdmOutlineViewer(this, parent);
		fOutlineViewer.setAutoExpandLevel(AUTO_EXPAND_LEVEL);
		fOutlineViewer.setContentProvider(new VdmOutlineTreeContentProvider());
		// fOutlineViewer.setLabelProvider(new VdmOutlineLabelProvider());
		uiLabelProvider = new VdmUILabelProvider();
		fOutlineViewer.setLabelProvider(new DecorationgVdmLabelProvider(uiLabelProvider));
		fOutlineViewer.addSelectionChangedListener(this);

		Object[] listeners = fSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			fSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer.addSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		// addSelectionChangedListener(new VdmSelectionListener());

		registerToolBarActions();

		fOutlineViewer.setInput(fInput);
	}

	private void registerToolBarActions()
	{

		IPageSite site = getSite();
		IActionBars actionBars = site.getActionBars();

		IToolBarManager toolBarManager = actionBars.getToolBarManager();
		toolBarManager.add(new LexicalSortingAction(fOutlineViewer));

		
		
		fMemberFilterActionGroup = new MemberFilterActionGroup(fOutlineViewer, IVdmUiConstants.OUTLINE_ID); //$NON-NLS-1$
		fMemberFilterActionGroup.contributeToToolBar(toolBarManager);

		// fCustomFiltersActionGroup.fillActionBars(actionBars);
		//
		// IMenuManager viewMenuManager= actionBars.getMenuManager();
		//			viewMenuManager.add(new Separator("EndFilterGroup")); //$NON-NLS-1$
		//
		// fToggleLinkingAction= new ToggleLinkingAction();
		// fToggleLinkingAction.setActionDefinitionId(IWorkbenchCommandConstants.NAVIGATE_TOGGLE_LINK_WITH_EDITOR);
		// viewMenuManager.add(new ClassOnlyAction());
		// viewMenuManager.add(fToggleLinkingAction);
		//
		//			fCategoryFilterActionGroup= new CategoryFilterActionGroup(fOutlineViewer, "org.eclipse.jdt.ui.JavaOutlinePage", new IJavaElement[] {fInput}); //$NON-NLS-1$
		// fCategoryFilterActionGroup.contributeToViewMenu(viewMenuManager);

	}

	@Override
	public void dispose()
	{
		if (vdmEditor != null)
		{

			vdmEditor.outlinePageClosed();
			vdmEditor = null;
		}
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

		if (fOutlineViewer != null)
		{
			fOutlineViewer.dispose();
		}

		if (fMemberFilterActionGroup != null)
		{
			fMemberFilterActionGroup.dispose();
		}

		if (uiLabelProvider != null)
		{
			uiLabelProvider.dispose();
		}

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

	public void select(INode reference)
	{

		if (fOutlineViewer != null)
		{

			inExternalSelectionMode = true;
			ISelection s = fOutlineViewer.getSelection();
			if (s instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) s;
				@SuppressWarnings("rawtypes")
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
