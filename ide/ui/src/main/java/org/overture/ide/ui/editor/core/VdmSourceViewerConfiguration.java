package org.overture.ide.ui.editor.core;

import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.autoedit.VdmAutoEditStrategy;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;

public abstract class VdmSourceViewerConfiguration extends
		SourceViewerConfiguration
{
	private ITokenScanner vdmCodeScanner = null;
	PresentationReconciler reconciler = null;
	private String[] commentingPrefix = new String[] { "--" };

	// private Object fScanner;

	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer)
	{
		return VdmUIPlugin.VDM_PARTITIONING;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{

		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				VdmPartitionScanner.SINGLELINE_COMMENT,
				VdmPartitionScanner.MULTILINE_COMMENT,
				VdmPartitionScanner.STRING };
	}

	// @Override
	// public IUndoManager getUndoManager(ISourceViewer sourceViewer) {
	// return new DefaultUndoManager(25);
	// }

	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		if (VdmUIPlugin.getDefault().getPreferenceStore().getBoolean(IVdmUiConstants.ENABLE_EDITOR_RECONFILER))
		{
			MonoReconciler reconciler = new MonoReconciler(new VdmReconcilingStrategy(), false);
			// reconciler.setDelay(500);
			reconciler.install(sourceViewer);

			return reconciler;
		} else
		{
			return null;
		}
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(
			ISourceViewer sourceViewer)
	{
		if (reconciler == null)
		{
			reconciler = new PresentationReconciler();

			if (vdmCodeScanner == null)
			{
				vdmCodeScanner = getVdmCodeScanner();
			}

			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(vdmCodeScanner);
			reconciler.setDamager(dr, VdmPartitionScanner.SINGLELINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.SINGLELINE_COMMENT);

			dr = new DefaultDamagerRepairer(getVdmCodeScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.MULTILINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.MULTILINE_COMMENT);

			dr = new DefaultDamagerRepairer(getVdmCodeScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.STRING);
			reconciler.setRepairer(dr, VdmPartitionScanner.STRING);

			dr = new DefaultDamagerRepairer(getVdmCodeScanner());
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		return reconciler;
	}

	protected abstract ITokenScanner getVdmCodeScanner();

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType)
	{
		IAutoEditStrategy strategy = (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) ? new VdmAutoEditStrategy()
				: new DefaultIndentLineAutoEditStrategy();
		return new IAutoEditStrategy[] { strategy };
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType)
	{
		return new ITextHover()
		{

			public IRegion getHoverRegion(ITextViewer textViewer, int offset)
			{
				return new Region(offset, 5);
			}

			public String getHoverInfo(ITextViewer textViewer,
					IRegion hoverRegion)
			{
				return null;
			}
		};
	}

	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer)
	{

		return new VdmAnnotationHover(false);
	}

	@Override
	abstract public IContentAssistant getContentAssistant(
			ISourceViewer sourceViewer);

	// return new VdmContentAssistant();

	@Override
	public String[] getDefaultPrefixes(ISourceViewer sourceViewer,
			String contentType)
	{
		if (contentType.equals(IDocument.DEFAULT_CONTENT_TYPE))
		{
			return commentingPrefix;
		}
		if (contentType.equals(VdmPartitionScanner.SINGLELINE_COMMENT))
		{
			return commentingPrefix;
		}

		return super.getDefaultPrefixes(sourceViewer, contentType);
	}

}
