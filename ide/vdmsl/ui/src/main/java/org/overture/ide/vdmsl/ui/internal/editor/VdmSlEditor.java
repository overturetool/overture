package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.DLTKEditorMessages;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.internal.ui.editor.ToggleCommentAction;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.actions.GenerateActionGroup;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.IUpdate;
import org.overture.ide.vdmsl.core.VdmSlLanguageToolkit;
import org.overture.ide.vdmsl.ui.UIPlugin;

public class VdmSlEditor extends ScriptEditor {

	// private org.eclipse.dltk.internal.ui.editor.BracketInserter
	// fBracketInserter = new VdmBracketInserter(
	// this);

	@Override
	public String getEditorId() {
		// TODO Auto-generated method stub
		return VdmslEditorConstants.EDITOR_ID;
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(VdmslEditorConstants.EDITOR_CONTEXT);

	}

	@Override
	public ScriptTextTools getTextTools() {
		return UIPlugin.getDefault().getTextTools();
	}

	@Override
	protected IPreferenceStore getScriptPreferenceStore() {
		return UIPlugin.getDefault().getPreferenceStore();
	}

	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		return VdmSlLanguageToolkit.getDefault();
	}

	@Override
	protected void connectPartitioningToElement(IEditorInput input,
			IDocument document) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			if (extension
					.getDocumentPartitioner(IVdmSlPartitions.VDMSL_PARTITIONING) == null) {
				VdmSlTextTools tools = UIPlugin.getDefault().getTextTools();
				tools.setupDocumentPartitioner(document,
						IVdmSlPartitions.VDMSL_PARTITIONING);
			}
		}
	}

	@Override
	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.overture.ide.vdmsl.ui.vdmSlEditorScope" }); //$NON-NLS-1$
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		ActionGroup generateActions = new GenerateActionGroup(this,
				ITextEditorActionConstants.GROUP_EDIT);
		fActionGroups.addGroup(generateActions);
		fContextMenuGroup.addGroup(generateActions);

		Action action = new ToggleCommentAction(DLTKEditorMessages
				.getBundleForConstructedKeys(), "ToggleComment", this);
		action
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.TOGGLE_COMMENT);
		setAction(DLTKActionConstants.TOGGLE_COMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.TOGGLE_COMMENT, true);
		ISourceViewer sourceViewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		((ToggleCommentAction) action).configure(sourceViewer, configuration);
	}

	protected class FormatElementAction extends Action implements IUpdate {

		/*
		 * @since 3.2
		 */
		FormatElementAction() {
			setText("Format Eleme&nt"); //$NON-NLS-1$
			setEnabled(isEditorInputModifiable());
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		@Override
		public void run() {

			final ScriptSourceViewer viewer = (ScriptSourceViewer) getSourceViewer();
			if (viewer.isEditable()) {

				final Point selection = viewer.rememberSelection();
				try {
					viewer.setRedraw(false);

					final String type = TextUtilities.getContentType(viewer
							.getDocument(),
							IVdmSlPartitions.VDMSL_PARTITIONING, selection.x,
							true);
					if (type.equals(IDocument.DEFAULT_CONTENT_TYPE)
							&& selection.y == 0) {

						try {
							final IModelElement element = getElementAt(
									selection.x, true);
							if (element != null && element.exists()) {
								final int kind = element.getElementType();
								if (kind == IModelElement.TYPE
										|| kind == IModelElement.METHOD) {

									final ISourceReference reference = (ISourceReference) element;
									final ISourceRange range = reference
											.getSourceRange();
									if (range != null) {
										viewer
												.setSelectedRange(range
														.getOffset(), range
														.getLength());
										viewer
												.doOperation(ISourceViewer.FORMAT);
									}
								}
							}
						} catch (ModelException exception) {
							// Should not happen
						}
					} else {
						viewer.setSelectedRange(selection.x, 1);
						viewer.doOperation(ISourceViewer.FORMAT);
					}
				} catch (BadLocationException exception) {
					// Can not happen
				} finally {

					viewer.setRedraw(true);
					viewer.restoreSelection();
				}
			}
		}

		/*
		 * @see org.eclipse.ui.texteditor.IUpdate#update()
		 * 
		 * @since 3.2
		 */
		public void update() {
			setEnabled(isEditorInputModifiable());
		}
	}

	// private VdmPairMatcher bracketMatcher = new VdmPairMatcher(
	// "{}[]()".toCharArray());
	//
	// public void createPartControl(Composite parent) {
	// super.createPartControl(parent);
	//
	// IPreferenceStore preferenceStore = getPreferenceStore();
	// boolean closeBrackets = preferenceStore.getBoolean(CLOSE_BRACKETS);
	// boolean closeStrings = preferenceStore.getBoolean(CLOSE_STRINGS);
	// boolean closeAngularBrackets = false;
	//
	// fBracketInserter.setCloseBracketsEnabled(closeBrackets);
	// fBracketInserter.setCloseStringsEnabled(closeStrings);
	// fBracketInserter.setCloseAngularBracketsEnabled(closeAngularBrackets);
	//
	// ISourceViewer sourceViewer = getSourceViewer();
	// if (sourceViewer instanceof ITextViewerExtension)
	// ((ITextViewerExtension)
	// sourceViewer).prependVerifyKeyListener(fBracketInserter);
	//
	// // if (isMarkingOccurrences())
	// // installOccurrencesFinder(false);
	// }

	// protected void
	// configureSourceViewerDecorationSupport(SourceViewerDecorationSupport
	// support) {
	// support.setCharacterPairMatcher(bracketMatcher);
	// support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS,
	// MATCHING_BRACKETS_COLOR);
	// super.configureSourceViewerDecorationSupport(support);
	// }

}
