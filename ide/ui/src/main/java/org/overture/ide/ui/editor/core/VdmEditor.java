package org.overture.ide.ui.editor.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.overture.ide.core.IVdmElement;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.VdmProject;
import org.overture.ide.core.parser.SourceParserManager;

import org.overture.ide.ui.outline.VdmContentOutlinePage;

public abstract class VdmEditor extends TextEditor
{

	VdmContentOutlinePage fOutlinePage = null;

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
				styles,
				this);

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
		return page;
	}

	/*
	 * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl(Composite parent)
	{
		super.createPartControl(parent);

		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		if (doc instanceof VdmDocument)
		{
			VdmDocument vdmDoc = (VdmDocument) doc;
			IVdmProject project = vdmDoc.getProject();
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

	private void internalDoSetInput(IEditorInput input) throws CoreException
	{
		ISourceViewer sourceViewer = getSourceViewer();
		VdmSourceViewer vdmSourceViewer = null;
		if (sourceViewer instanceof VdmSourceViewer)
			vdmSourceViewer = (VdmSourceViewer) sourceViewer;

		IPreferenceStore store = getPreferenceStore();

		// if (vdmSourceViewer != null && isFoldingEnabled() &&(store == null ||
		// !store.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
		// vdmSourceViewer.prepareDelayedProjection();

		super.doSetInput(input);

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

	@SuppressWarnings("unchecked")
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
}
