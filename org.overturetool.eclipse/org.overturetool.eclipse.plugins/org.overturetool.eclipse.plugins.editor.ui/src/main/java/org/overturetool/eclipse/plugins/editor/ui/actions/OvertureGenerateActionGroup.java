/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.ui.actions;

import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.actions.GenerateActionGroup;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.TextOperationAction;

public class OvertureGenerateActionGroup extends GenerateActionGroup {

	public OvertureGenerateActionGroup(ScriptEditor editor, String groupName) {
		super(editor, groupName);

		Action action = new AddBlockCommentAction(DLTKEditorMessages
				.getBundleForConstructedKeys(), "AddBlockComment.", editor); //$NON-NLS-1$
		action
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.ADD_BLOCK_COMMENT);
		editor.setAction(DLTKActionConstants.ADD_BLOCK_COMMENT, action);
		editor.markAsStateDependentAction(
				DLTKActionConstants.ADD_BLOCK_COMMENT, true);
		editor.markAsSelectionDependentAction(
				DLTKActionConstants.ADD_BLOCK_COMMENT, true);

		action = new RemoveBlockCommentAction(DLTKEditorMessages
				.getBundleForConstructedKeys(), "RemoveBlockComment.", editor); //$NON-NLS-1$
		action
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.REMOVE_BLOCK_COMMENT);
		editor.setAction(DLTKActionConstants.REMOVE_BLOCK_COMMENT, action);
		editor.markAsStateDependentAction(
				DLTKActionConstants.REMOVE_BLOCK_COMMENT, true);
		editor.markAsSelectionDependentAction(
				DLTKActionConstants.REMOVE_BLOCK_COMMENT, true);

		action = new TextOperationAction(DLTKEditorMessages
				.getBundleForConstructedKeys(),
				"Format.", editor, ISourceViewer.FORMAT); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.FORMAT);
		editor.setAction(DLTKActionConstants.FORMAT, action);
		editor.markAsStateDependentAction(DLTKActionConstants.FORMAT, true);
		editor.markAsSelectionDependentAction(DLTKActionConstants.FORMAT, true);

		action = new IndentAction(DLTKEditorMessages
				.getBundleForConstructedKeys(), "Indent.", editor, false); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.INDENT);
		editor.setAction(DLTKActionConstants.INDENT, action);
		editor.markAsStateDependentAction(DLTKActionConstants.INDENT, true);
		editor.markAsSelectionDependentAction(DLTKActionConstants.INDENT, true);
	}

}
