package org.overture.ide.vdmsl.ui.internal.editor;

import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.overture.ide.ui.partitioning.IVdmColorConstants;
import org.overture.ide.vdmsl.ui.internal.completion.VdmSlCompletionProcessor;
import org.overture.ide.vdmsl.ui.internal.partitioning.VdmSlCodeScanner;

public class VdmSlSourceViewerConfiguration extends
		ScriptSourceViewerConfiguration
{
	
	public VdmSlSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning)
	{
		super(colorManager, preferenceStore, editor, partitioning);
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType)
	{
		return new IAutoEditStrategy[] { new DefaultIndentLineAutoEditStrategy() };
	}

	@Override
	public String[] getIndentPrefixes(ISourceViewer sourceViewer,
			String contentType)
	{
		return new String[] { "\t", "        " };
	}

	@Override
	protected ContentAssistPreference getContentAssistPreference()
	{
		return VdmSlContentAssistPreference.getDefault();
	}

	@Override
	public IInformationPresenter getOutlinePresenter(
			ScriptSourceViewer sourceViewer, boolean doCodeResolve)
	{
		return null;
	}

	private AbstractScriptScanner fCodeScanner;
	private AbstractScriptScanner fStringScanner;
	private AbstractScriptScanner fCommentScanner;

	// This method called from base class.
	@Override
	protected void initializeScanners()
	{
		// This is our code scanner
		this.fCodeScanner = new VdmSlCodeScanner(this.getColorManager(),
				this.fPreferenceStore);
		// This is default scanners for partitions with same color.
		this.fStringScanner = new SingleTokenScriptScanner(
				this.getColorManager(), this.fPreferenceStore,
				IVdmColorConstants.VDM_STRING);
		this.fCommentScanner = new SingleTokenScriptScanner(
				this.getColorManager(), this.fPreferenceStore,
				IVdmColorConstants.VDM_COMMENT);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer)
	{
		PresentationReconciler reconciler = new ScriptPresentationReconciler();
		reconciler.setDocumentPartitioning(this.getConfiguredDocumentPartitioning(sourceViewer));

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(
				this.fCodeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(this.fStringScanner);
		reconciler.setDamager(dr, IVdmColorConstants.VDM_STRING);
		reconciler.setRepairer(dr, IVdmColorConstants.VDM_STRING);

		dr = new DefaultDamagerRepairer(this.fCommentScanner);
		reconciler.setDamager(dr, IVdmColorConstants.VDM_COMMENT);
		reconciler.setRepairer(dr, IVdmColorConstants.VDM_COMMENT);

		return reconciler;
	}

	@Override
	public void handlePropertyChangeEvent(PropertyChangeEvent event)
	{
		if (this.fCodeScanner.affectsBehavior(event))
		{
			this.fCodeScanner.adaptToPreferenceChange(event);
		}
		if (this.fStringScanner.affectsBehavior(event))
		{
			this.fStringScanner.adaptToPreferenceChange(event);
		}
	}

	@Override
	public boolean affectsTextPresentation(PropertyChangeEvent event)
	{
		return this.fCodeScanner.affectsBehavior(event)
				|| this.fStringScanner.affectsBehavior(event);
	}
	
	@Override
	protected String getCommentPrefix() {
		return "--"; //$NON-NLS-1$
	}
	
	@Override
	protected void alterContentAssistant(ContentAssistant assistant) {
		// IDocument.DEFAULT_CONTENT_TYPE
		IContentAssistProcessor scriptProcessor = new VdmSlCompletionProcessor(getEditor(), assistant, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.setContentAssistProcessor(scriptProcessor, IDocument.DEFAULT_CONTENT_TYPE);
	}

}