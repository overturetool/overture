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

import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.partitioning.IVdmPartitions;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.ui.editor.syntax.VdmMultiLineCommentScanner;
import org.overture.ide.ui.editor.syntax.VdmSingleLineCommentScanner;
import org.overture.ide.ui.editor.syntax.VdmStringScanner;
import org.overture.ide.ui.quickfix.VdmQuickAssistantProcessor;

public abstract class VdmSourceViewerConfiguration extends
		TextSourceViewerConfiguration
{
	private PresentationReconciler reconciler = null;
	protected String[] commentingPrefix = new String[] { "--" };
	protected ITokenScanner vdmSingleLineCommentScanner;
	private ITokenScanner vdmMultiLineCommentScanner;
	private ITokenScanner vdmStringScanner;
	
	public VdmSourceViewerConfiguration()
	{
		super();
	}
	
	/**
	 * Creates a text source viewer configuration and
	 * initializes it with the given preference store.
	 *
	 * @param preferenceStore	the preference store used to initialize this configuration
	 */
	public VdmSourceViewerConfiguration(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}
	
	

	// private Object fScanner;

	@Override
	public String getConfiguredDocumentPartitioning(ISourceViewer sourceViewer)
	{
		return IVdmPartitions.VDM_PARTITIONING;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer)
	{

		return new String[] { IDocument.DEFAULT_CONTENT_TYPE,
				VdmPartitionScanner.SINGLELINE_COMMENT,
				VdmPartitionScanner.MULTILINE_COMMENT,
				VdmPartitionScanner.STRING };
	}



	@Override
	public IReconciler getReconciler(ISourceViewer sourceViewer)
	{
		if (!VdmUIPlugin.getDefault().getPreferenceStore().contains(IVdmUiConstants.ENABLE_EDITOR_RECONFILER) ||VdmUIPlugin.getDefault().getPreferenceStore().getBoolean(IVdmUiConstants.ENABLE_EDITOR_RECONFILER))
		{
			MonoReconciler reconciler = new MonoReconciler(new VdmReconcilingStrategy(), false);
			 reconciler.setDelay(500);
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
			reconciler.setDocumentPartitioning(IVdmPartitions.VDM_PARTITIONING);
			
			DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getVdmSingleLineCommentScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.SINGLELINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.SINGLELINE_COMMENT);

			dr = new DefaultDamagerRepairer(getVdmMultiLineCommentScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.MULTILINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.MULTILINE_COMMENT);

			dr = new DefaultDamagerRepairer(getVdmStringScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.STRING);
			reconciler.setRepairer(dr, VdmPartitionScanner.STRING);

			dr = new DefaultDamagerRepairer(getVdmCodeScanner());
			reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
			reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		}
		return reconciler;
	}

	private ITokenScanner getVdmStringScanner()
	{
		if(vdmStringScanner == null)
		{
			vdmStringScanner = new VdmStringScanner(new VdmColorProvider());
		}
		
		return vdmStringScanner;
	}

	protected ITokenScanner getVdmSingleLineCommentScanner()
	{
		if(vdmSingleLineCommentScanner == null)
		{
			vdmSingleLineCommentScanner = new VdmSingleLineCommentScanner(new VdmColorProvider());
		}
		
		return vdmSingleLineCommentScanner;
	}
	
	private ITokenScanner getVdmMultiLineCommentScanner()
	{
		if(vdmMultiLineCommentScanner == null)
		{
			vdmMultiLineCommentScanner = new VdmMultiLineCommentScanner(new VdmColorProvider());
		}
		
		return vdmMultiLineCommentScanner;
	}

	protected abstract ITokenScanner getVdmCodeScanner();

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType)
	{
		//IAutoEditStrategy strategy = //(IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) ? new VdmAutoEditStrategy() :
		//		 new DefaultIndentLineAutoEditStrategy();
		//return new IAutoEditStrategy[] { strategy };
		return new IAutoEditStrategy[] {new DefaultIndentLineAutoEditStrategy()};
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
	
	
	@SuppressWarnings("restriction")
	@Override
	public IQuickAssistAssistant getQuickAssistAssistant(
			ISourceViewer sourceViewer)
	{
		QuickAssistAssistant assistant= new QuickAssistAssistant();
		assistant.setRestoreCompletionProposalSize(EditorsPlugin.getDefault().getDialogSettingsSection("quick_assist_proposal_size")); //$NON-NLS-1$
		assistant.setQuickAssistProcessor(new VdmQuickAssistantProcessor());
		
		return assistant;
	}

}
