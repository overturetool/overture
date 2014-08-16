/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.outline;

import java.util.List;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.internal.navigator.NavigatorDecoratingLabelProvider;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ast.node.INode;
import org.overture.ide.core.ElementChangedEvent;
import org.overture.ide.core.IElementChangedListener;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.VdmCore;
import org.overture.ide.core.VdmElementDelta;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.editor.core.VdmEditor;

@SuppressWarnings("restriction")
public class VdmContentOutlinePage extends Page implements IContentOutlinePage,
/* IAdaptable, */IPostSelectionProvider
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
			{
				return;
			}

			if (e.getSource() != null
					&& e.getSource() instanceof VdmElementDelta)
			{
				if (((VdmElementDelta) e.getSource()).getElement() != fInput)
				{
					return;// the change source was not the one shown here
				}
			}

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

							fOutlineViewer.setInput(fOutlineViewer.getInput());
							// fOutlineViewer.refresh(false);
							// fOutlineViewer.expandToLevel(AUTO_EXPAND_LEVEL);
						}
					}
				});
			}
		}

	}

	/**
	 * Constant indicating that all levels of the tree should be expanded or collapsed. // * @see #expandToLevel(int) //
	 * * @see #collapseToLevel(Object, int)
	 */
	public static final int ALL_LEVELS = -1;

	private VdmEditor vdmEditor;

	private VdmOutlineViewer fOutlineViewer;
	private IVdmElement fInput;
	private ElementChangedListener fListener;

	private static final int AUTO_EXPAND_LEVEL = 2;

	private ListenerList fSelectionChangedListeners = new ListenerList(ListenerList.IDENTITY);
	private ListenerList fPostSelectionChangedListeners = new ListenerList(ListenerList.IDENTITY);

	private MemberFilterActionGroup fMemberFilterActionGroup;

	public VdmContentOutlinePage(VdmEditor vdmEditor)
	{
		this.vdmEditor = vdmEditor;
	}

	@Override
	public void createControl(Composite parent)
	{

		fOutlineViewer = new VdmOutlineViewer(parent);
		fOutlineViewer.setAutoExpandLevel(AUTO_EXPAND_LEVEL);
		fOutlineViewer.setContentProvider(new WorkbenchContentProvider());
		fOutlineViewer.setLabelProvider(new NavigatorDecoratingLabelProvider(new WorkbenchLabelProvider()));

		Object[] listeners = fSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			fSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer.addSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

		listeners = fPostSelectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++)
		{
			fPostSelectionChangedListeners.remove(listeners[i]);
			fOutlineViewer.addPostSelectionChangedListener((ISelectionChangedListener) listeners[i]);
		}

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
	}

	@Override
	public void dispose()
	{
		if (vdmEditor != null)
		{

			vdmEditor.outlinePageClosed();
			vdmEditor = null;
		}

		if (fOutlineViewer != null)
		{
			fOutlineViewer.dispose();
		}

		if (fMemberFilterActionGroup != null)
		{
			fMemberFilterActionGroup.dispose();
		}

		fOutlineViewer = null;

		super.dispose();

	}

	@Override
	public Control getControl()
	{
		if (fOutlineViewer == null)
		{
			return null;
		}
		return fOutlineViewer.getControl();
	}

	@Override
	public void setFocus()
	{
		if (fOutlineViewer != null)
		{
			this.fOutlineViewer.getControl().setFocus();
		}

	}

	/*
	 * @see ISelectionProvider#setSelection(ISelection)
	 */
	@Override
	public void setSelection(ISelection selection)
	{
		if (fOutlineViewer != null)
		{
			fOutlineViewer.setSelection(selection);
		}
	}

	/*
	 * @see ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection()
	{
		if (fOutlineViewer == null)
		{
			return StructuredSelection.EMPTY;
		}
		return fOutlineViewer.getSelection();
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
		{
			VdmCore.removeElementChangedListener(fListener);
		}
		fListener = new ElementChangedListener();
		VdmCore.addElementChangedListener(fListener);
	}

	public void selectNode(INode reference)
	{
		if (fOutlineViewer != null)
		{
			ISelection s = fOutlineViewer.getSelection();
			if (s instanceof IStructuredSelection)
			{
				IStructuredSelection ss = (IStructuredSelection) s;
				if (ss.getFirstElement() == reference)// this should probably be
														// avoided by the caller
														// but if it is
														// selected then dont
														// load the UI with the
														// task of doing the
														// same again
				{
					return;
				}
				@SuppressWarnings("rawtypes")
				List elements = ss.toList();
				if (!elements.contains(reference))
				{
					s = reference == null ? StructuredSelection.EMPTY
							: new StructuredSelection(reference);
					fOutlineViewer.setSelection(s, true);
				}
			}
		}
	}

	/*
	 * @see ISelectionProvider#addSelectionChangedListener(ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
		{
			fOutlineViewer.addSelectionChangedListener(listener);
		} else
		{
			fSelectionChangedListeners.add(listener);
		}
	}

	/*
	 * @see ISelectionProvider#removeSelectionChangedListener(ISelectionChangedListener )
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
		{
			fOutlineViewer.removeSelectionChangedListener(listener);
		} else
		{
			fSelectionChangedListeners.remove(listener);
		}
	}

	/*
	 * @see org.eclipse.jface.text.IPostSelectionProvider#addPostSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addPostSelectionChangedListener(
			ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
		{
			fOutlineViewer.addPostSelectionChangedListener(listener);
		} else
		{
			fPostSelectionChangedListeners.add(listener);
		}
	}

	/*
	 * @see org.eclipse.jface.text.IPostSelectionProvider# removePostSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removePostSelectionChangedListener(
			ISelectionChangedListener listener)
	{
		if (fOutlineViewer != null)
		{
			fOutlineViewer.removePostSelectionChangedListener(listener);
		} else
		{
			fPostSelectionChangedListeners.remove(listener);
		}
	}

}
