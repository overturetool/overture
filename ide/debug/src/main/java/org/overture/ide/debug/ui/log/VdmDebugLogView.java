/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

public class VdmDebugLogView extends ViewPart
{
	public static final String VIEW_ID = "org.overture.ide.debug.ui.log.dbgpLogView"; //$NON-NLS-1$
	public static final String THEME_ID = "org.eclipse.dltk.debug.ui.dbgpLogView.txtViewFont"; //$NON-NLS-1$

	private final List<VdmDebugLogItem> items = new ArrayList<VdmDebugLogItem>();
	private TableViewer viewer;
	private TextViewer textViewer;
	private IDocument textDocument;
	private IPropertyChangeListener fontRegistryChangeListener;
	private IAction executionFilterAction;

	public VdmDebugLogView()
	{
		super();
	}

	public void setFocus()
	{
		viewer.getControl().setFocus();
	}

	public void createPartControl(Composite parent)
	{
		final SashForm sashForm = new SashForm(parent, SWT.HORIZONTAL);
		viewer = new TableViewer(sashForm, SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.MULTI | SWT.FULL_SELECTION | SWT.VIRTUAL);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		// addColumn(Messages.Column_Date, 100, true);

		addColumn(Messages.Column_Time, 100, true);
		addColumn(Messages.Column_Type, 80, true);
		addColumn(Messages.Column_Session, 60, true);
		addColumn(Messages.Column_Message, 500, false);
		viewer.getTable().addListener(SWT.Resize, new Listener()
		{

			public void handleEvent(Event event)
			{
				final Table table = (Table) event.widget;
				final int columnCount = table.getColumnCount();
				int w = table.getClientArea().width;
				for (int i = 0; i < columnCount - 1; ++i)
				{
					w -= table.getColumn(i).getWidth();
				}
				if (w > 0)
				{
					table.getColumn(columnCount - 1).setWidth(w);
				}
			}

		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{

			public void selectionChanged(SelectionChangedEvent event)
			{
				if (event.getSelection() instanceof IStructuredSelection)
				{
					final Object first = ((IStructuredSelection) event.getSelection()).getFirstElement();
					if (first instanceof VdmDebugLogItem)
					{
						textDocument.set(((VdmDebugLogItem) first).getMessage());
						return;
					}
				}
				textDocument.set(""); //$NON-NLS-1$
			}

		});
		viewer.setContentProvider(new VdmDebugLogContentProvider());
		viewer.setLabelProvider(new VdmDebugLogLabelProvider());
		viewer.setInput(items);
		textDocument = new Document();
		textViewer = new TextViewer(sashForm, SWT.V_SCROLL | SWT.H_SCROLL
				| SWT.WRAP | SWT.READ_ONLY);
		textViewer.setDocument(textDocument);
		fontRegistryChangeListener = new IPropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				handlePropertyChangeEvent(event);
			}
		};
		JFaceResources.getFontRegistry().addListener(fontRegistryChangeListener);

		updateFont();
		sashForm.setWeights(new int[] { 75, 25 });
		createActions();
		createMenu();
		createToolbar();
		createContextMenu();
	}

	public void dispose()
	{
		if (fontRegistryChangeListener != null)
		{
			JFaceResources.getFontRegistry().removeListener(fontRegistryChangeListener);
			fontRegistryChangeListener = null;
		}
		super.dispose();
	}

	/**
	 * @param event
	 */
	protected void handlePropertyChangeEvent(PropertyChangeEvent event)
	{
		final String key = event.getProperty();
		if (key.equals(THEME_ID))
		{
			updateFont();
		}
	}

	private void updateFont()
	{
		textViewer.getTextWidget().setFont(JFaceResources.getFont(THEME_ID));
	}

	/**
	 * @param caption
	 * @param width
	 */
	private void addColumn(String caption, int width, boolean center)
	{
		final TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
		column.setText(caption);
		column.setWidth(width);
	}

	public void append(final VdmDebugLogItem item)
	{
		synchronized (items)
		{
			items.add(item);
		}
		final Table table = viewer.getTable();
		if (table.isDisposed())
		{
			return;
		}
		final Display display = table.getDisplay();
		if (display.isDisposed())
		{
			return;
		}
		display.asyncExec(new Runnable()
		{

			public void run()
			{
				viewer.refresh(false, false);
				if (table.isDisposed() || table.getDisplay().isDisposed())
				{
					return;
				}
				final int itemCount = table.getItemCount();
				if (itemCount > 0)
				{
					table.showItem(table.getItem(itemCount - 1));
				}
			}

		});
	}

	private IAction copyAction;
	private IAction clearAction;

	public void clear()
	{
		Display.getDefault().syncExec(new Runnable()
		{
			public void run()
			{
				clearAction.run();
			}

		});

	}

	public void createActions()
	{
		copyAction = new VdmDebugLogCopyAction(viewer);
		clearAction = new Action(Messages.VdmDebugLogView_clear)
		{
			public void run()
			{
				synchronized (items)
				{
					items.clear();
				}
				viewer.refresh();
			}
		};

		executionFilterAction = new Action("Execution filter", SWT.TOGGLE)
		{
			public void run()
			{
				synchronized (viewer)
				{
					List<ViewerFilter> filters = new ArrayList<ViewerFilter>();
					filters.addAll(Arrays.asList(viewer.getFilters()));
					if (executionFilterAction.isChecked())
					{
						filters.add(new VdmDebugLogExecutionFilter());
					} else
					{
						for (int i = 0; i < filters.size(); i++)
						{
							if (filters.get(i) instanceof VdmDebugLogExecutionFilter)
							{
								filters.remove(i);
								break;
							}
						}
					}
					viewer.setFilters(filters.toArray(new ViewerFilter[filters.size()]));
					viewer.refresh();
				}
			}
		};
	}

	private void createMenu()
	{
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
		manager.add(copyAction);
		manager.add(clearAction);
		manager.add(executionFilterAction);
	}

	private void createToolbar()
	{
		IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
		manager.add(copyAction);
		manager.add(clearAction);
	}

	private void createContextMenu()
	{
		// Create menu manager.
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new IMenuListener()
		{
			public void menuAboutToShow(IMenuManager manager)
			{
				fillContextMenu(manager);
			}
		});

		// Create menu.
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);

		// Register menu for extension.
		getSite().registerContextMenu(menuManager, viewer);
	}

	private void fillContextMenu(IMenuManager manager)
	{
		manager.add(copyAction);
		manager.add(clearAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
