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
package org.overture.ide.plugins.showtrace.viewer;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorActionBarContributor;

public class VdmRtEditorActionBarContributor extends EditorActionBarContributor
{
	private VdmRtLogEditor editor;
	private Action exportDiagramAction;
	private Action moveHorizontalAction;
	private Action openValidationAction;
	private Action moveNextHorizontalAction;
	private Action movePreviousHorizontalAction;

	/**
	 * Creates a multi-page contributor.
	 */
	public VdmRtEditorActionBarContributor()
	{
		super();
		createActions();
	}

	private void createActions()
	{
		// sampleAction = new Action()
		// {
		// public void run()
		// {
		// MessageDialog.openInformation(null, "MultipageEditor", "Sample Action Executed");
		// }
		// };
		// sampleAction.setText("Sample Action");
		// sampleAction.setToolTipText("Sample Action tool tip");
		// sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJS_TASK_TSK));
		//		

		openValidationAction = new Action("Show failed conjectures")
		{
			@Override
			public void run()
			{
				if (editor != null)
				{
					editor.openValidationConjectures();
				}
			}
		};
		openValidationAction.setToolTipText("Open the validation conjecture file");
		openValidationAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor("IMG_OBJS_WARN_TSK"));
		openValidationAction.setEnabled(false);

		exportDiagramAction = new Action("Export to JPG")
		{
			@Override
			public void run()
			{
				if (editor != null)
				{
					editor.diagramExportAction();
				}
			}
		};
		exportDiagramAction.setToolTipText("Save all diagrams as JPG");
		exportDiagramAction.setImageDescriptor(TracefileViewerPlugin.getImageDescriptor((new StringBuilder("icons")).append(File.separator).append("print.gif").toString()));
		exportDiagramAction.setEnabled(false);

		moveHorizontalAction = new Action("Move time")
		{
			@Override
			public void run()
			{
				if (editor != null)
				{
					editor.moveHorizontal();
				}
			}
		};
		moveHorizontalAction.setToolTipText("Move time in the views");
		moveHorizontalAction.setImageDescriptor(TracefileViewerPlugin.getImageDescriptor((new StringBuilder("icons")).append(File.separator).append("panhor.gif").toString()));
		moveHorizontalAction.setEnabled(false);

		moveNextHorizontalAction = new Action("Move next")
		{
			@Override
			public void run()
			{
				if (editor != null)
				{
					editor.moveNextHorizontal();
				}
			}
		};
		moveNextHorizontalAction.setToolTipText("Move time to next time");
		moveNextHorizontalAction.setEnabled(false);

		movePreviousHorizontalAction = new Action("Move Previous")
		{
			@Override
			public void run()
			{
				if (editor != null)
				{
					editor.movePreviousHorizontal();
				}
			}
		};
		movePreviousHorizontalAction.setToolTipText("Move time to Previous time");
		movePreviousHorizontalAction.setEnabled(false);
	}

	public void contributeToMenu(IMenuManager manager)
	{
		IMenuManager menu = new MenuManager("&RealTime-Log");
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		// menu.add(sampleAction);

		menu.add(moveHorizontalAction);
		menu.add(moveNextHorizontalAction);
		menu.add(movePreviousHorizontalAction);

		menu.add(new Separator());
		menu.add(exportDiagramAction);

		menu.add(new Separator());
		menu.add(openValidationAction);

	}

	public void contributeToToolBar(IToolBarManager manager)
	{
		manager.add(new Separator());

		manager.add(moveHorizontalAction);
		manager.add(movePreviousHorizontalAction);
		manager.add(moveNextHorizontalAction);

		manager.add(new Separator());
		manager.add(exportDiagramAction);

		manager.add(new Separator());
		manager.add(openValidationAction);
		manager.add(new Separator());
	}

	@Override
	public void setActiveEditor(IEditorPart targetEditor)
	{
		if (targetEditor instanceof VdmRtLogEditor)
		{
			this.editor = (VdmRtLogEditor) targetEditor;
			
			moveHorizontalAction.setEnabled(editor.canMoveHorizontal());
			moveNextHorizontalAction.setEnabled(editor.canMoveHorizontal());
			movePreviousHorizontalAction.setEnabled(editor.canMoveHorizontal());
			
			exportDiagramAction.setEnabled(editor.canExportJpg());
			
			openValidationAction.setEnabled(editor.canOpenValidation());
			
		}
	}

}
