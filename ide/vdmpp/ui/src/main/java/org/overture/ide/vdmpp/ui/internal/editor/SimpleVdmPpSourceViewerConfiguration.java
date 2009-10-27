package org.overture.ide.vdmpp.ui.internal.editor;

import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.information.IInformationPresenter;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.ITextEditor;

public class SimpleVdmPpSourceViewerConfiguration extends VdmPpSourceViewerConfiguration {

	private boolean fConfigureFormatter;

	
	public SimpleVdmPpSourceViewerConfiguration(IColorManager colorManager,
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning, boolean configureFormatter) {
		super(colorManager, preferenceStore, editor, partitioning);
		fConfigureFormatter = configureFormatter;
	}
	
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		if (fConfigureFormatter)
			return super.getContentFormatter(sourceViewer);
		else
			return null;
	}

	public IInformationControlCreator getInformationControlCreator(
			ISourceViewer sourceViewer) {
		return null;
	}
 
	public IInformationPresenter getInformationPresenter(
			ISourceViewer sourceViewer) {
		return null;
	}
 
	public IInformationPresenter getOutlinePresenter(
			ISourceViewer sourceViewer, boolean doCodeResolve) {
		return null;
	}
 
	public IInformationPresenter getHierarchyPresenter(
			ISourceViewer sourceViewer, boolean doCodeResolve) {
		return null;
	}
 
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		return null;
	}

	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		return null;
	}
 
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return null;
	}
 
	public IAnnotationHover getOverviewRulerAnnotationHover(
			ISourceViewer sourceViewer) {
		return null;
	}
 
	public int[] getConfiguredTextHoverStateMasks(ISourceViewer sourceViewer,
			String contentType) {
		return null;
	}
 
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		return null;
	}
 
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		return null;
	}
 


}
