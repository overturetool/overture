package org.overture.ide.debug.ui;


import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;

/**
 * The debug perspective factory as adapted from the Debug UI.
 */
public class DebugPerspectiveFactory implements IPerspectiveFactory {

	private class IInternalDebugUIConstants {

		public static final String ID_NAVIGATOR_FOLDER_VIEW = "org.overture.ide.debug.ui.NavigatorFolderView";
		public static final String ID_TOOLS_FOLDER_VIEW = "org.overture.ide.debug.ui.ToolsFolderView";
		public static final String ID_OUTLINE_FOLDER_VIEW = "org.overture.ide.debug.ui.OutlineFolderView";

	}

	/**
	 * @see IPerspectiveFactory#createInitialLayout(IPageLayout)
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {

		String editorArea = layout.getEditorArea();

		IFolderLayout navFolder = layout.createFolder(IInternalDebugUIConstants.ID_NAVIGATOR_FOLDER_VIEW, IPageLayout.TOP, (float) 0.25, editorArea);
		navFolder.addView(IDebugUIConstants.ID_DEBUG_VIEW);

		IFolderLayout toolsFolder = layout.createFolder(IInternalDebugUIConstants.ID_TOOLS_FOLDER_VIEW, IPageLayout.BOTTOM, (float) 0.75, editorArea);
		toolsFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		toolsFolder.addView(IPageLayout.ID_PROBLEM_VIEW);
		toolsFolder.addPlaceholder(IDebugUIConstants.ID_REGISTER_VIEW);
		toolsFolder.addPlaceholder(IPageLayout.ID_BOOKMARKS);
		toolsFolder.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

		IFolderLayout outlineFolder = layout.createFolder(IInternalDebugUIConstants.ID_OUTLINE_FOLDER_VIEW, IPageLayout.RIGHT, (float) 0.33,IInternalDebugUIConstants.ID_NAVIGATOR_FOLDER_VIEW);
		outlineFolder.addView(IDebugUIConstants.ID_VARIABLE_VIEW);
		outlineFolder.addView(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		outlineFolder.addView(IDebugUIConstants.ID_EXPRESSION_VIEW);

		layout.addShowViewShortcut(IProgressConstants.PROGRESS_VIEW_ID);

		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.DEBUG_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		setContentsOfShowViewMenu(layout);
	}
	
	/**
	 * Sets the initial contents of the "Show View" menu.
	 */
	protected void setContentsOfShowViewMenu(IPageLayout layout) {
		layout.addShowViewShortcut(IDebugUIConstants.ID_DEBUG_VIEW);
		layout.addShowViewShortcut(IDebugUIConstants.ID_VARIABLE_VIEW);
		layout.addShowViewShortcut(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		layout.addShowViewShortcut(IDebugUIConstants.ID_EXPRESSION_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
	}
}
