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
package org.overture.ide.debug.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.internal.VdmLineBreakpoint;
import org.overture.ide.debug.utils.ExecutableAnalysis;
import org.overture.ide.ui.editor.core.VdmEditor;

public class VdmLineBreakpointAdapter implements IToggleBreakpointsTarget
{

	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException
	{
		ITextEditor textEditor = getEditor(part);
		IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
		ITextSelection textSelection = (ITextSelection) selection;
		int lineNumber = textSelection.getStartLine() + 1;

		boolean executable = false;

		if (textEditor != null)
		{
			if (textEditor instanceof VdmEditor)
			{
				VdmEditor vEditor = (VdmEditor) textEditor;
				IVdmElement element = vEditor.getInputVdmElement();
				if (element != null && element instanceof IVdmSourceUnit)
				{
					IVdmSourceUnit sourceUnti = (IVdmSourceUnit) element;
					for (INode node : sourceUnti.getParseList())
					{
						executable = ExecutableAnalysis.isExecutable(node, lineNumber, true);

						if (executable)
						{
							break;
						}
					}
				}
			}

			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IDebugConstants.ID_VDM_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++)
			{
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource()))
				{
					if (((ILineBreakpoint) breakpoint).getLineNumber() == lineNumber)
					{
						breakpoint.delete();
						return;
					}
				}
			}

			if (!executable)
			{
				return;
			}

			IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());

			IRegion line;
			try
			{
				line = document.getLineInformation(textSelection.getStartLine());
				int start = line.getOffset();
				int end = start + line.getLength();
				String debugModelId = IDebugConstants.ID_VDM_DEBUG_MODEL;// getDebugModelId(textEditor,
				// resource);
				if (debugModelId == null)
				{
					return;
				}
				IPath location = resource.getFullPath();

				VdmLineBreakpoint lineBreakpoint = new VdmLineBreakpoint(IDebugConstants.ID_VDM_DEBUG_MODEL, resource, location, lineNumber, start, end, false);

				StringBuilder message = new StringBuilder();
				message.append("Line breakpoint:");
				message.append(location.lastSegment());
				message.append("[line:" + lineNumber + "]");

				lineBreakpoint.setMessage(message.toString());

				DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);

			} catch (BadLocationException e)
			{
				VdmDebugPlugin.log(e);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.debug.ui.actions.IToggleBreakpointsTarget# canToggleLineBreakpoints(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection)
	{
		return getEditor(part) != null;
	}

	/**
	 * Returns the editor being used to edit a PDA file, associated with the given part, or <code>null</code> if none.
	 * 
	 * @param part
	 *            workbench part
	 * @return the editor being used to edit a PDA file, associated with the given part, or <code>null</code> if none
	 */
	private ITextEditor getEditor(IWorkbenchPart part)
	{
		if (part instanceof ITextEditor)
		{
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null && resource instanceof IFile)
			{
				return editorPart;
				// IFile file = (IFile) resource;
				// try
				// {
				// String contentTypeId = file.getContentDescription().getContentType().getId();
				// if (SourceViewerEditorManager.getInstance().getContentTypeIds().contains(contentTypeId))
				// {
				// return editorPart;
				// }
				// } catch (CoreException e)
				// {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints (org.eclipse.ui.IWorkbenchPart
	 * , org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) throws CoreException
	{
	}

	// private static IWorkspaceRoot getWorkspaceRoot()
	// {
	// return ResourcesPlugin.getWorkspace().getRoot();
	// }

	// private static IResource getBreakpointResource(ITextEditor textEditor)
	// {
	// return getBreakpointResource(textEditor.getEditorInput());
	// }

	// private static IResource getBreakpointResource(
	// final IEditorInput editorInput)
	// {
	// IResource resource = (IResource) editorInput.getAdapter(IResource.class);
	// if (resource == null)
	// resource = getWorkspaceRoot();
	// return resource;
	// }

	/*
	 * (non-Javadoc)
	 * @seeorg.eclipse.debug.ui.actions.IToggleBreakpointsTarget# canToggleMethodBreakpoints(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection)
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints (org.eclipse.ui.IWorkbenchPart,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints (org.eclipse.ui.IWorkbenchPart ,
	 * org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection)
	{
		return false;
	}
}
