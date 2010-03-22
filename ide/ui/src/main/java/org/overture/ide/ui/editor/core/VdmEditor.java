package org.overture.ide.ui.editor.core;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.SourceReferenceManager;
import org.overture.ide.core.VdmProject;
import org.overture.ide.core.parser.SourceParserManager;
import org.overture.ide.ui.outline.VdmContentOutlinePage;
import org.overturetool.vdmj.ast.IAstNode;

public abstract class VdmEditor extends TextEditor
{
	/**
	 * Updates the Java outline page selection and this editor's range indicator.
	 * 
	 * @since 3.0
	 */
	private class EditorSelectionChangedListener extends
			AbstractSelectionChangedListener
	{

		/*
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.
		 * SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event)
		{
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
	SourceReferenceManager sourceReferenceManager = null;

	public VdmEditor() {
		super();
		setDocumentProvider(new VdmDocumentProvider());

	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles)
	{

		ISourceViewer viewer = new VdmSourceViewer(parent,
				ruler,
				getOverviewRuler(),
				isOverviewRulerVisible(),
				styles);

		getSourceViewerDecorationSupport(viewer);

		return viewer;

	}

	@Override
	protected void initializeEditor()
	{
		super.initializeEditor();
		setSourceViewerConfiguration(getVdmSourceViewerConfiguration());
	}

	protected abstract VdmSourceViewerConfiguration getVdmSourceViewerConfiguration();

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class required)
	{
		if (IContentOutlinePage.class.equals(required))
		{
			if (fOutlinePage == null)
			{
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
	protected VdmContentOutlinePage createOutlinePage()
	{
		// VdmContentOutlinePage page= new VdmContentOutlinePage(fOutlinerContextMenuId, this);
		VdmContentOutlinePage page = new VdmContentOutlinePage(this);
		setOutlinePageInput(page, getEditorInput());
		page.addSelectionChangedListener(new ISelectionChangedListener() {

			@SuppressWarnings("unchecked")
			public void selectionChanged(SelectionChangedEvent event)
			{
				ISelection s = event.getSelection();
				if (s instanceof IStructuredSelection)
				{
					IStructuredSelection ss = (IStructuredSelection) s;
					List elements = ss.toList();
					if (!elements.isEmpty())
					{
						IAstNode node = (IAstNode) elements.get(0);
//						IVdmElement vdmElement = getInputVdmElement();
						if (sourceReferenceManager!=null)
						{
							//IVdmSourceUnit unit = (IVdmSourceUnit) vdmElement;

							int endPos = 0;
							
							// fix for VDMJ endPos == 0 when rest of the line is marked as location
							if (node.getLocation().endPos == 0)
							{
								endPos = sourceReferenceManager.getLineOffset(node.getLocation().endLine)- node.getLocation().startPos;
							} else
							{
								endPos = node.getLocation().endPos
										- node.getLocation().startPos;
							}
								selectAndReveal(sourceReferenceManager.getLineOffset(node.getLocation().startLine)
										+ node.getLocation().startPos - 1,
										endPos);
							
						}
					}
				}

			}
		});
		return page;
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		fEditorSelectionChangedListener = new EditorSelectionChangedListener();
		fEditorSelectionChangedListener.install(getSelectionProvider());

		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		if (doc instanceof VdmDocument)
		{
			VdmDocument vdmDoc = (VdmDocument) doc;
			/* IVdmProject project = */vdmDoc.getProject();
			try
			{
				SourceParserManager.parseFile(VdmProject.getVdmSourceUnit(vdmDoc.getFile()));
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// try
		// {
		// doSetInput(getEditorInput());
		// } catch (CoreException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// fEditorSelectionChangedListener= new EditorSelectionChangedListener();
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
	protected void doSetInput(IEditorInput input) throws CoreException
	{
		ISourceViewer sourceViewer = getSourceViewer();
		if (!(sourceViewer instanceof ISourceViewerExtension2))
		{
			// setPreferenceStore(createCombinedPreferenceStore(input));
			internalDoSetInput(input);
			return;
		}

		// uninstall & unregister preference store listener
		getSourceViewerDecorationSupport(sourceViewer).uninstall();
		((ISourceViewerExtension2) sourceViewer).unconfigure();

		// setPreferenceStore(createCombinedPreferenceStore(input));

		// install & register preference store listener
		sourceViewer.configure(getSourceViewerConfiguration());
		getSourceViewerDecorationSupport(sourceViewer).install(getPreferenceStore());

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
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#doSetSelection(ISelection)
	 */
	protected void doSetSelection(ISelection selection)
	{
		super.doSetSelection(selection);
		synchronizeOutlinePageSelection();
	}

	/*
	 * @see org.eclipse.ui.part.WorkbenchPart#getOrientation()
	 * 
	 * @since 3.1
	 */
	public int getOrientation()
	{
		return SWT.LEFT_TO_RIGHT; // Java editors are always left to right by default
	}

	private void internalDoSetInput(IEditorInput input) throws CoreException
	{
		ISourceViewer sourceViewer = getSourceViewer();
		VdmSourceViewer vdmSourceViewer = null;
		if (sourceViewer instanceof VdmSourceViewer)
			vdmSourceViewer = (VdmSourceViewer) sourceViewer;

		// IPreferenceStore store = getPreferenceStore();

		// if (vdmSourceViewer != null && isFoldingEnabled() &&(store == null ||
		// !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
		// vdmSourceViewer.prepareDelayedProjection();

		super.doSetInput(input);
		
		
	IVdmElement inputElement  =	getInputVdmElement();
		if(inputElement instanceof IVdmSourceUnit)
		{
			if(sourceReferenceManager!=null)
			{
				sourceReferenceManager.shutdown(null);	
			}
			sourceReferenceManager = new SourceReferenceManager((IVdmSourceUnit) inputElement);
			sourceReferenceManager.startup(null);
		}

		if (vdmSourceViewer != null && vdmSourceViewer.getReconciler() == null)
		{
			IReconciler reconciler = getSourceViewerConfiguration().getReconciler(vdmSourceViewer);
			if (reconciler != null)
			{
				reconciler.install(vdmSourceViewer);
				vdmSourceViewer.setReconciler(reconciler);

			}
		}

		if (fEncodingSupport != null)
			fEncodingSupport.reset();

		if (fOutlinePage == null)
		{
			fOutlinePage = createOutlinePage();
		}
		setOutlinePageInput(fOutlinePage, input);

		// if (isShowingOverrideIndicators())
		// installOverrideIndicator(false);
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
			IEditorInput input)
	{
		if (page == null)
			return;

		IVdmElement je = getInputVdmElement();
		if (je != null && je.exists())
			page.setInput(je);
		else
			page.setInput(null);

	}

	private IVdmElement getInputVdmElement()
	{
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		if (doc instanceof VdmDocument)
		{
			VdmDocument vdmDoc = (VdmDocument) doc;
			IVdmProject project = vdmDoc.getProject();
			IVdmModel model = project.getModel();
			IVdmSourceUnit sourceUnit = model.getVdmSourceUnit(vdmDoc.getFile());

			if (sourceUnit != null)
				return sourceUnit;// return rootNode.filter(vdmDoc.getFile());
			else
				System.err.println("No root parsed for: " + vdmDoc.getFile());

		}

		return null;
	}

	/**
	 * Informs the editor that its outliner has been closed.
	 */
	public void outlinePageClosed()
	{
		if (fOutlinePage != null)
		{
			fOutlinePage = null;
			resetHighlightRange();
		}
	}

	/**
	 * React to changed selection.
	 * 
	 * @since 3.0
	 */
	protected void selectionChanged()
	{
		if (getSelectionProvider() == null)
			return;
		IAstNode element = computeHighlightRangeSourceReference();
		// if (getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
		synchronizeOutlinePage(element);
		// if (fIsBreadcrumbVisible && fBreadcrumb != null && !fBreadcrumb.isActive())
		// setBreadcrumbInput(element);
		setSelection(element, false);
		// if (!fSelectionChangedViaGotoAnnotation)
		// updateStatusLine();
		// fSelectionChangedViaGotoAnnotation= false;
	}

	protected void setSelection(IAstNode reference, boolean moveCursor)
	{
		if (getSelectionProvider() == null)
			return;

		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection)
		{
			ITextSelection textSelection = (ITextSelection) selection;
			// PR 39995: [navigation] Forward history cleared after going back in navigation history:
			// mark only in navigation history if the cursor is being moved (which it isn't if
			// this is called from a PostSelectionEvent that should only update the magnet)
			if (moveCursor
					&& (textSelection.getOffset() != 0 || textSelection.getLength() != 0))
				markInNavigationHistory();
		}

		if (reference != null)
		{

			StyledText textWidget = null;

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null)
				textWidget = sourceViewer.getTextWidget();

			if (textWidget == null)
				return;

			// try {
			// ISourceRange range= null;
			// if (reference instanceof ILocalVariable || reference instanceof ITypeParameter || reference
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
			// offset= range.getOffset() + content.indexOf(name, packageKeyWordIndex + 7);
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
	 * Synchronizes the outliner selection with the given element position in the editor.
	 * 
	 * @param element
	 *            the java element to select
	 */
	protected void synchronizeOutlinePage(IAstNode element)
	{
		synchronizeOutlinePage(element, false);// true
	}

	/**
	 * Synchronizes the outliner selection with the given element position in the editor.
	 * 
	 * @param element
	 *            the java element to select
	 * @param checkIfOutlinePageActive
	 *            <code>true</code> if check for active outline page needs to be done
	 */
	protected void synchronizeOutlinePage(IAstNode element,
			boolean checkIfOutlinePageActive)
	{
		if (fOutlinePage != null && element != null
				&& !(checkIfOutlinePageActive))
		{// && isJavaOutlinePageActive()
			fOutlinePage.select(element);
		}
	}

	/**
	 * Synchronizes the outliner selection with the actual cursor position in the editor.
	 */
	public void synchronizeOutlinePageSelection()
	{
		synchronizeOutlinePage(computeHighlightRangeSourceReference());
	}

	/**
	 * Computes and returns the source reference that includes the caret and serves as provider for the
	 * outline page selection and the editor range indication.
	 * 
	 * @return the computed source reference
	 * @since 3.0
	 */
	protected IAstNode computeHighlightRangeSourceReference()
	{
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return null;

		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
			return null;

		int caret = 0;
		if (sourceViewer instanceof ITextViewerExtension5)
		{
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			caret = extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
		} else
		{
			int offset = sourceViewer.getVisibleRegion().getOffset();
			caret = offset + styledText.getCaretOffset();
		}

		IAstNode element = getElementAt(caret, false);

		if (!(element instanceof IAstNode))
			return null;

		// if (element.getElementType() == IJavaElement.IMPORT_DECLARATION) {
		//
		// IImportDeclaration declaration= (IImportDeclaration) element;
		// IImportContainer container= (IImportContainer) declaration.getParent();
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

		return (IAstNode) element;
	}

	/**
	 * Returns the most narrow java element including the given offset.
	 * 
	 * @param offset
	 *            the offset inside of the requested element
	 * @param reconcile
	 *            <code>true</code> if editor input should be reconciled in advance
	 * @return the most narrow java element
	 * @since 3.0
	 */
	protected IAstNode getElementAt(int offset, boolean reconcile)
	{
		return getElementAt(offset);
	}

	private IAstNode getElementAt(int offset)
	{

		
		if (sourceReferenceManager != null )
		{
			IAstNode node = sourceReferenceManager.getNodeAt(offset);
			if (node != null)
			{
				System.out.println("Element hit: " + node.getName());

				return node;
			}

		}
		return null;
	}
}
