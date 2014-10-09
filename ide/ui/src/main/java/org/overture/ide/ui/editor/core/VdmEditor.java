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
package org.overture.ide.ui.editor.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.DefaultCharacterPairMatcher;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ABlockSimpleBlockStm;
import org.overture.ast.types.AFieldField;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.outline.VdmContentOutlinePage;
import org.overture.ide.ui.utility.ast.AstLocationSearcher;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2;
import org.overture.ide.ui.utility.ast.AstLocationSearcher2.TextReference;

public abstract class VdmEditor extends TextEditor {

	private final static String MATCHING_BRACKETS = "matchingBrackets";
	private final static String MATCHING_BRACKETS_COLOR = "matchingBracketsColor";
	private final static String GREY_COLOR_CODE = "128,128,128";

	
	@Override
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		super.configureSourceViewerDecorationSupport(support);

		char[] matchChars = { '(', ')', '[', ']', '{', '}' };
		ICharacterPairMatcher matcher = new DefaultCharacterPairMatcher(
				matchChars, IDocumentExtension3.DEFAULT_PARTITIONING);
		support.setCharacterPairMatcher(matcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS,
				MATCHING_BRACKETS_COLOR);

		// Switch preference on. Need to use EditorsUI because getPreference() returns a read only store
		IPreferenceStore store = EditorsUI.getPreferenceStore();
		store.setDefault(MATCHING_BRACKETS, true);
		store.setDefault(MATCHING_BRACKETS_COLOR, GREY_COLOR_CODE);
	}

	public static interface ILocationSearcher {
		public INode search(List<INode> nodes, int offSet);

		public int[] getNodeOffset(INode node);
	}

	/**
	 * Updates the Java outline page selection and this editor's range
	 * indicator.
	 * 
	 * @since 3.0
	 */
	private class EditorSelectionChangedListener extends
			AbstractSelectionChangedListener {

		/*
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
		 * (org.eclipse.jface.viewers. SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			// XXX: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=56161
			VdmEditor.this.selectionChanged();
		}
	}

	/**
	 * The editor selection changed listener.
	 * 
	 * @since 3.0
	 */
	private EditorSelectionChangedListener fEditorSelectionChangedListener;
	VdmContentOutlinePage fOutlinePage = null;
	protected VdmSourceViewerConfiguration fVdmSourceViewer;
	final boolean TRACE_GET_ELEMENT_AT = false;
	ISourceViewer viewer;
	protected ILocationSearcher locationSearcher = null;

	public VdmEditor() {

		super();
		setDocumentProvider(new VdmDocumentProvider());
		this.locationSearcher = new ILocationSearcher() {

			@Override
			public INode search(List<INode> nodes, int offSet) {
				// Fix for bug #185, the location searcher did not consider the
				// file name.
				IVdmElement element = getInputVdmElement();
				if (element instanceof IVdmSourceUnit) {
					// return AstLocationSearcher.search(nodes, offSet,
					// (IVdmSourceUnit) element);
					return new AstLocationSearcher2().getNode(
							new TextReference(((IVdmSourceUnit) element)
									.getSystemFile(), offSet), nodes);
				}
				return null;
			}

			@Override
			public int[] getNodeOffset(INode node) {
				return AstLocationSearcher.getNodeOffset(node);
			}
		};

	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {

		viewer = new VdmSourceViewer(parent, ruler, getOverviewRuler(),
				isOverviewRulerVisible(), styles);
		getSourceViewerDecorationSupport(viewer);

		return viewer;

	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		fVdmSourceViewer = getVdmSourceViewerConfiguration(getPreferenceStore());
		setSourceViewerConfiguration(fVdmSourceViewer);
		setRulerContextMenuId(IVdmUiConstants.RULERBAR_ID);
	}

	protected abstract VdmSourceViewerConfiguration getVdmSourceViewerConfiguration(
			IPreferenceStore fPreferenceStore);

	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null) {
				fOutlinePage = createOutlinePage();
			}
			return fOutlinePage;
		}

		return super.getAdapter(required);
	}

	/**
	 * Creates the outline page used with this editor.
	 * 
	 * @return the created Java outline page
	 */
	protected VdmContentOutlinePage createOutlinePage() {
		// VdmContentOutlinePage page= new
		// VdmContentOutlinePage(fOutlinerContextMenuId, this);
		VdmContentOutlinePage page = new VdmContentOutlinePage(this);
		setOutlinePageInput(page, getEditorInput());
		page.addSelectionChangedListener(createOutlineSelectionChangedListener());
		return page;
	}

	protected ISelectionChangedListener createOutlineSelectionChangedListener() {
		return new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				ISelection s = event.getSelection();
				if (s instanceof IStructuredSelection) {
					IStructuredSelection ss = (IStructuredSelection) s;
					@SuppressWarnings("rawtypes")
					List elements = ss.toList();
					/*
					 * As a fix for bug #185 the selectAndReveal is changed to
					 * highlight range, and thus just highlights the line
					 * instead of selecting the text. If no selection then the
					 * selection is reset
					 */
					if (!elements.isEmpty()) {
						if (elements.get(0) instanceof INode) {
							INode node = (INode) elements.get(0);
							// selectAndReveal(node);
							if (node != computeHighlightRangeSourceReference()) {
								setHighlightRange(node);
							}
						}
					} else {
						resetHighlightRange();
					}
				}
			}
		};
	}

	/**
	 * Selects a node existing within the ast presented by the editor
	 * 
	 * @param node
	 */
	public void selectAndReveal(INode node) {
		int[] offsetLength = this.locationSearcher.getNodeOffset(node);
		selectAndReveal(offsetLength[0], offsetLength[1]);
	}

	/**
	 * highlights a node in the text editor.
	 * 
	 * @param node
	 */
	public void setHighlightRange(INode node) {
		try {
			int[] offsetLength = this.locationSearcher.getNodeOffset(node);
			// int offset = getSourceViewer().getTextWidget().getCaretOffset();
			Assert.isNotNull(offsetLength);
			Assert.isTrue(offsetLength[0] > 0, "Illegal start offset");
			Assert.isTrue(offsetLength[0] > 0, "Illegal offset length");
			super.setHighlightRange(offsetLength[0], offsetLength[1], true);
		} catch (IllegalArgumentException e) {
			super.resetHighlightRange();
		}
	}

	/*
	 * @see
	 * org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		fEditorSelectionChangedListener = new EditorSelectionChangedListener();
		fEditorSelectionChangedListener.install(getSelectionProvider());

		IEditorInput input = getEditorInput();
		IDocument doc = getDocumentProvider().getDocument(input);
		if (doc instanceof VdmDocument) {
			VdmDocument vdmDoc = (VdmDocument) doc;
			try {
				if (vdmDoc != null && vdmDoc.getSourceUnit() != null
						&& !vdmDoc.getSourceUnit().hasParseTree()) {
					SourceParserManager.parseFile(vdmDoc.getSourceUnit());
				}
			} catch (CoreException e) {
				VdmUIPlugin.log(
						"Faild to do initial parse of SourceUnit in editor", e);
			} catch (IOException e) {
				VdmUIPlugin.log(
						"Faild to do initial parse of SourceUnit in editor", e);
			}

		} else {
			if (input instanceof FileStoreEditorInput) {
				MessageDialog d = new MessageDialog(PlatformUI.getWorkbench()
						.getDisplay().getActiveShell(), "Error", null,
						"Cannot open a vdm file outside the workspace",
						MessageDialog.ERROR, new String[] { "Ok" }, 0);
				d.open();

			}
			// FileStoreEditorInput fs = (FileStoreEditorInput) input;
			// IFileStore fileStore = EFS.getLocalFileSystem().getStore(
			// fs.getURI());
			//
			// if (!fileStore.fetchInfo().isDirectory()
			// && fileStore.fetchInfo().exists())
			// {
			// IWorkbench wb = PlatformUI.getWorkbench();
			// IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			//
			// IWorkbenchPage page = win.getActivePage();
			//
			// try
			// {
			//
			// page
			// .openEditor(input,
			// EditorsUI.DEFAULT_TEXT_EDITOR_ID);
			// } catch (PartInitException e)
			// {
			// /* some code */
			// fileStore.toString();
			// }
			//
			// }
			// }

		}

		// IAnnotationModel model=
		// getDocumentProvider().getAnnotationModel(getEditorInput());
		// IDocument document =
		// getDocumentProvider().getDocument(getEditorInput());

		// if(model!=null && document !=null)
		// {
		// getSourceViewer().setDocument(document, model);
		// model.connect(document);

		// }

		// try
		// {
		// doSetInput(getEditorInput());
		// } catch (CoreException e)
		// {
		//
		// e.printStackTrace();
		// }
		// fEditorSelectionChangedListener= new
		// EditorSelectionChangedListener();
		// fEditorSelectionChangedListener.install(getSelectionProvider());
		//
		// if (isSemanticHighlightingEnabled())
		// installSemanticHighlighting();
		//
		// fBreadcrumb= createBreadcrumb();
		// fIsBreadcrumbVisible= isBreadcrumbShown();
		// if (fIsBreadcrumbVisible)
		// showBreadcrumb();
		//
		// PlatformUI.getWorkbench().addWindowListener(fActivationListener);

	}

	/*
	 * @see AbstractTextEditor#doSetInput
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {

		ISourceViewer sourceViewer = getSourceViewer();
		if (!(sourceViewer instanceof ISourceViewerExtension2)) {
			setPreferenceStore(createCombinedPreferenceStore(input));
			internalDoSetInput(input);
			return;
		}

		// uninstall & unregister preference store listener
		getSourceViewerDecorationSupport(sourceViewer).uninstall();
		((ISourceViewerExtension2) sourceViewer).unconfigure();

		setPreferenceStore(createCombinedPreferenceStore(input));

		// install & register preference store listener
		sourceViewer.configure(getSourceViewerConfiguration());
		getSourceViewerDecorationSupport(sourceViewer).install(
				getPreferenceStore());

		internalDoSetInput(input);
		// ISourceViewer sourceViewer = super.getSourceViewer();
		// // if (!(sourceViewer instanceof ISourceViewerExtension2)) {
		// // //setPreferenceStore(createCombinedPreferenceStore(input));
		// // internalDoSetInput(input);
		// // return;
		// // }
		//
		// // uninstall & unregister preference store listener
		// // getSourceViewerDecorationSupport(sourceViewer).uninstall();
		// // ((ISourceViewerExtension2)sourceViewer).unconfigure();
		//
		// // setPreferenceStore(createCombinedPreferenceStore(input));
		//
		// // install & register preference store listener
		// if (sourceViewer != null)
		// {
		// sourceViewer.configure(getSourceViewerConfiguration());
		// getSourceViewerDecorationSupport(sourceViewer).install(getPreferenceStore());
		//
		// internalDoSetInput(input);
		// }else super.doSetInput(input);
		//
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.AbstractTextEditor#doSetSelection(ISelection)
	 */
	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		synchronizeOutlinePageSelection();
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#getOrientation()
	 * 
	 * @since 3.1
	 */
	public int getOrientation() {
		return SWT.LEFT_TO_RIGHT; // Java editors are always left to right by
									// default
	}

	private void internalDoSetInput(IEditorInput input) throws CoreException {
		ISourceViewer sourceViewer = getSourceViewer();
		VdmSourceViewer vdmSourceViewer = null;
		if (sourceViewer instanceof VdmSourceViewer) {
			vdmSourceViewer = (VdmSourceViewer) sourceViewer;
		}
		// IPreferenceStore store = getPreferenceStore();

		// if (vdmSourceViewer != null && isFoldingEnabled() &&(store == null ||
		// !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
		// vdmSourceViewer.prepareDelayedProjection();

		super.doSetInput(input);

		if (vdmSourceViewer != null && vdmSourceViewer.getReconciler() == null) {
			IReconciler reconciler = getSourceViewerConfiguration()
					.getReconciler(vdmSourceViewer);
			if (reconciler != null) {
				reconciler.install(vdmSourceViewer);
				vdmSourceViewer.setReconciler(reconciler);

			}
		}

		if (fEncodingSupport != null)
			fEncodingSupport.reset();

		if (fOutlinePage == null) {
			fOutlinePage = createOutlinePage();
		}
		setOutlinePageInput(fOutlinePage, input);

		// if (isShowingOverrideIndicators())
		// installOverrideIndicator(false);

	}

	/**
	 * Creates and returns the preference store for this Java editor with the
	 * given input.
	 * 
	 * @param input
	 *            The editor input for which to create the preference store
	 * @return the preference store for this editor
	 * @since 3.0
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(3);

		// IJavaProject project= EditorUtility.getJavaProject(input);
		// if (project != null) {
		// stores.add(new EclipsePreferencesAdapter(new
		// ProjectScope(project.getProject()), JavaCore.PLUGIN_ID));
		// }

		stores.add(VdmUIPlugin.getDefault().getPreferenceStore());
		// stores.add(new
		// PreferencesAdapter(VdmCore.getDefault().getPluginPreferences()));
		stores.add(EditorsUI.getPreferenceStore());
		stores.add(PlatformUI.getPreferenceStore());
		// stores.get(0).setValue(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_OVERVIEW_RULER,true);

		return new ChainedPreferenceStore(
				(IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores
						.size()]));
	}

	/**
	 * Sets the input of the editor's outline page.
	 * 
	 * @param page
	 *            the Java outline page
	 * @param input
	 *            the editor input
	 */
	protected void setOutlinePageInput(VdmContentOutlinePage page,
			IEditorInput input) {
		if (page == null)
			return;

		IVdmElement je = getInputVdmElement();
		if (je != null && je.exists())
			page.setInput(je);
		else
			page.setInput(null);

	}

	public IVdmElement getInputVdmElement() {
		IDocumentProvider docProvider = getDocumentProvider();
		if (docProvider != null) {
			IDocument doc = docProvider.getDocument(getEditorInput());
			if (doc instanceof VdmDocument) {
				VdmDocument vdmDoc = (VdmDocument) doc;
				IVdmSourceUnit sourceUnit = vdmDoc.getSourceUnit();
				if (sourceUnit != null) {
					return sourceUnit;
				}
			}
		}
		return null;
	}

	/**
	 * Informs the editor that its outliner has been closed.
	 */
	public void outlinePageClosed() {
		if (fOutlinePage != null) {
			fOutlinePage = null;
			resetHighlightRange();
		}
	}

	/**
	 * React to changed selection.
	 * 
	 * @since 3.0
	 */
	protected void selectionChanged() {
		if (getSelectionProvider() == null)
			return;
		INode element = computeHighlightRangeSourceReference();
		// if
		// (getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
		synchronizeOutlinePage(element);
		// if (fIsBreadcrumbVisible && fBreadcrumb != null &&
		// !fBreadcrumb.isActive())
		// setBreadcrumbInput(element);
		setSelection(element, false);
		// if (!fSelectionChangedViaGotoAnnotation)
		// updateStatusLine();
		// fSelectionChangedViaGotoAnnotation= false;

	}

	protected void setSelection(INode reference, boolean moveCursor) {
		if (getSelectionProvider() == null)
			return;

		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			// PR 39995: [navigation] Forward history cleared after going back
			// in navigation history:
			// mark only in navigation history if the cursor is being moved
			// (which it isn't if
			// this is called from a PostSelectionEvent that should only update
			// the magnet)
			if (moveCursor
					&& (textSelection.getOffset() != 0 || textSelection
							.getLength() != 0))
				markInNavigationHistory();
		}

		if (reference != null) {

			StyledText textWidget = null;

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null)
				textWidget = sourceViewer.getTextWidget();

			if (textWidget == null)
				return;

			// try {
			// ISourceRange range= null;
			// if (reference instanceof ILocalVariable || reference instanceof
			// ITypeParameter || reference
			// instanceof IAnnotation) {
			// IJavaElement je= ((IJavaElement)reference).getParent();
			// if (je instanceof ISourceReference)
			// range= ((ISourceReference)je).getSourceRange();
			// } else
			// range= reference.getSourceRange();
			//
			// if (range == null)
			// return;
			//
			// int offset= range.getOffset();
			// int length= range.getLength();
			//
			// if (offset < 0 || length < 0)
			// return;
			//
			// setHighlightRange(offset, length, moveCursor);
			//
			// if (!moveCursor)
			// return;
			//
			// offset= -1;
			// length= -1;
			//
			// if (reference instanceof IMember) {
			// range= ((IMember) reference).getNameRange();
			// if (range != null) {
			// offset= range.getOffset();
			// length= range.getLength();
			// }
			// } else if (reference instanceof ITypeParameter) {
			// range= ((ITypeParameter) reference).getNameRange();
			// if (range != null) {
			// offset= range.getOffset();
			// length= range.getLength();
			// }
			// } else if (reference instanceof ILocalVariable) {
			// range= ((ILocalVariable)reference).getNameRange();
			// if (range != null) {
			// offset= range.getOffset();
			// length= range.getLength();
			// }
			// } else if (reference instanceof IAnnotation) {
			// range= ((IAnnotation)reference).getNameRange();
			// if (range != null) {
			// offset= range.getOffset();
			// length= range.getLength();
			// }
			// } else if (reference instanceof IImportDeclaration) {
			// String content= reference.getSource();
			// if (content != null) {
			//						int start= content.indexOf("import") + 6; //$NON-NLS-1$
			// while (start < content.length() && content.charAt(start) == ' ')
			// start++;
			//
			// int end= content.indexOf(';');
			// do {
			// end--;
			// } while (end >= 0 && content.charAt(end) == ' ');
			//
			// offset= range.getOffset() + start;
			// length= end - start + 1;
			// } else {
			// // fallback
			// offset= range.getOffset();
			// length= range.getLength();
			// }
			// } else if (reference instanceof IPackageDeclaration) {
			// String name= ((IPackageDeclaration) reference).getElementName();
			// if (name != null && name.length() > 0) {
			// String content= reference.getSource();
			// if (content != null) {
			//							int packageKeyWordIndex = content.lastIndexOf("package"); //$NON-NLS-1$
			// if (packageKeyWordIndex != -1) {
			// offset= range.getOffset() + content.indexOf(name,
			// packageKeyWordIndex + 7);
			// length= name.length();
			// }
			// }
			// }
			// }
			//
			// if (offset > -1 && length > 0) {
			//
			// try {
			// textWidget.setRedraw(false);
			// sourceViewer.revealRange(offset, length);
			// sourceViewer.setSelectedRange(offset, length);
			// } finally {
			// textWidget.setRedraw(true);
			// }
			//
			// markInNavigationHistory();
			// }
			//
			// } catch (JavaModelException x) {
			// } catch (IllegalArgumentException x) {
			// }
			//
			// } else if (moveCursor) {
			// resetHighlightRange();
			// markInNavigationHistory();
		}
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            the java element to select
	 */
	protected void synchronizeOutlinePage(INode element) {
		// TODO: don't search for mutexes
		if (element instanceof AMutexSyncDefinition)
			return;
		if (element instanceof ABlockSimpleBlockStm)
			return;

		try {
			synchronizeOutlinePage(element, false);// true
		} catch (Exception e) {

		}
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            the java element to select
	 * @param checkIfOutlinePageActive
	 *            <code>true</code> if check for active outline page needs to be
	 *            done
	 */
	protected void synchronizeOutlinePage(INode element,
			boolean checkIfOutlinePageActive) {
		if (fOutlinePage != null && element != null
				&& !(checkIfOutlinePageActive)) {// && isJavaOutlinePageActive()

			// if added for bug #185, it prevents the outline from being update
			// if the selection from the searcher determine that the node is the
			// same.
			if (fOutlinePage.getSelection() != null
					&& fOutlinePage.getSelection() instanceof IStructuredSelection
					&& element == ((IStructuredSelection) fOutlinePage
							.getSelection()).getFirstElement()) {
				// skip
			} else {
				fOutlinePage.selectNode(element);
			}
		}
	}

	/**
	 * Synchronizes the outliner selection with the actual cursor position in
	 * the editor.
	 */
	public void synchronizeOutlinePageSelection() {
		synchronizeOutlinePage(computeHighlightRangeSourceReference());
	}

	/**
	 * Computes and returns the source reference that includes the caret and
	 * serves as provider for the outline page selection and the editor range
	 * indication.
	 * 
	 * @return the computed source reference
	 * @since 3.0
	 */
	protected INode computeHighlightRangeSourceReference() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return null;

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
			return null;

		int caret = 0;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			caret = extension.widgetOffset2ModelOffset(styledText
					.getCaretOffset());
		} else {
			int offset = sourceViewer.getVisibleRegion().getOffset();
			caret = offset + styledText.getCaretOffset();
		}
		// System.out.println("Compute element at "+caret);
		INode element = getElementAt(caret, false);

		// if (!(element instanceof INode))
		// return null;

		// if (element.getElementType() == IJavaElement.IMPORT_DECLARATION) {
		//
		// IImportDeclaration declaration= (IImportDeclaration) element;
		// IImportContainer container= (IImportContainer)
		// declaration.getParent();
		// ISourceRange srcRange= null;
		//
		// try {
		// srcRange= container.getSourceRange();
		// } catch (JavaModelException e) {
		// }
		//
		// if (srcRange != null && srcRange.getOffset() == caret)
		// return container;
		// }

		return element;
	}

	/**
	 * Returns the most narrow java element including the given offset.
	 * 
	 * @param offset
	 *            the offset inside of the requested element
	 * @param reconcile
	 *            <code>true</code> if editor input should be reconciled in
	 *            advance
	 * @return the most narrow java element
	 * @since 3.0
	 */
	protected INode getElementAt(int offset, boolean reconcile) {
		return getElementAt(offset);
	}

	public INode getElementAt(int offset) {
		IVdmElement element = getInputVdmElement();
		List<INode> nodes = null;
		INode node = null;
		if (element instanceof IVdmSourceUnit) {
			nodes = ((IVdmSourceUnit) element).getParseList();

			long startTime = System.currentTimeMillis();
			node = this.locationSearcher.search(nodes, offset);

			if (TRACE_GET_ELEMENT_AT) {
				System.out.println("Search Time for offset " + offset + " in "
						+ element + " is "
						+ (System.currentTimeMillis() - startTime) + " found: "
						+ node);

				System.out.println("Node offset is: "
						+ getSourceViewer().getTextWidget().getLineAtOffset(
								offset));
				System.out.println("This thread is: "
						+ Thread.currentThread().getName());
			}

		}

		// Get a definition to sync with outline, where only definitions are
		// shown. If not a definition the search up
		// the tree until one is found.
		INode def = null;
		if (node instanceof PDefinition || node instanceof AFieldField) {
			def = node;
		} else if (node != null) {
			def = node.getAncestor(PDefinition.class);
		}
		return def;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (fOutlinePage != null) {
			fOutlinePage.dispose();
		}

		// fEditorSelectionChangedListener
	}

	public SourceViewerConfiguration getNewSourceViewerConfiguration() {
		return getVdmSourceViewerConfiguration(getPreferenceStore());
	}

	// @Override
	// protected void createActions() {
	// super.createActions();
	// ResourceBundle bla = new ResourceBundle() {
	//
	// @Override
	// protected Object handleGetObject(String key) {
	// if(key.equals("ToggleComment.label")){
	// return "Toggle Comment";
	// }
	// if(key.equals("ToggleComment.tooltip")){
	// return "Toggle Comment Tooltip";
	// }
	// if(key.equals("ToggleComment.description")){
	// return "Toggle Comment Description";
	// }
	// if(key.equals("ToggleComment.image")){
	// return null;
	// }
	//
	//
	//
	// return null;
	// }
	//
	// @Override
	// public Enumeration<String> getKeys() {
	//
	// return null;
	// }
	// };
	//		Action action= new ToggleCommentAction(bla, "ToggleComment.", this); //$NON-NLS-1$
	// action.setActionDefinitionId(IVdmActionDefinitionIds.TOGGLE_COMMENT);
	//		setAction("ToggleComment", action); //$NON-NLS-1$
	//		markAsStateDependentAction("ToggleComment", true); //$NON-NLS-1$
	// configureToggleCommentAction();
	// }

	// /**
	// * Configure actions
	// */
	// private void configureToggleCommentAction() {
	//		IAction action = getAction("ToggleComment"); //$NON-NLS-1$
	// if (action instanceof ToggleCommentAction) {
	// ISourceViewer sourceViewer = getSourceViewer();
	// SourceViewerConfiguration configuration = getSourceViewerConfiguration();
	// ((ToggleCommentAction) action).configure(sourceViewer,
	// configuration);
	// }
	// }
}
