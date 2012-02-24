/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
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
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.editor.partitioning.IVdmPartitions;
import org.overture.ide.ui.editor.partitioning.VdmDamagerRepairer;
import org.overture.ide.ui.editor.partitioning.VdmPartitionScanner;
import org.overture.ide.ui.editor.syntax.VdmColorProvider;
import org.overture.ide.ui.editor.syntax.VdmMultiLineCommentScanner;
import org.overture.ide.ui.editor.syntax.VdmSingleLineCommentScanner;
import org.overture.ide.ui.editor.syntax.VdmStringScanner;

public abstract class VdmSourceViewerConfiguration extends
		SourceViewerConfiguration
{
	private ITokenScanner vdmCodeScanner = null;
	PresentationReconciler reconciler = null;
	private String[] commentingPrefix = new String[] { "--" };
	private ITokenScanner vdmSingleLineCommentScanner;
	private ITokenScanner vdmMultiLineCommentScanner;
	private ITokenScanner vdmStringScanner;

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
			
			if (vdmCodeScanner == null)
			{
				vdmCodeScanner = getVdmCodeScanner();
			}

			VdmDamagerRepairer dr = new VdmDamagerRepairer(getVdmSingleLineCommentScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.SINGLELINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.SINGLELINE_COMMENT);

			dr = new VdmDamagerRepairer(getVdmMultiLineCommentScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.MULTILINE_COMMENT);
			reconciler.setRepairer(dr, VdmPartitionScanner.MULTILINE_COMMENT);

			dr = new VdmDamagerRepairer(getVdmStringScanner());
			reconciler.setDamager(dr, VdmPartitionScanner.STRING);
			reconciler.setRepairer(dr, VdmPartitionScanner.STRING);

			dr = new VdmDamagerRepairer(getVdmCodeScanner());
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

	private ITokenScanner getVdmSingleLineCommentScanner()
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
		//The commented code is for auto insertion of quotes
//		IAutoEditStrategy strategy = (IDocument.DEFAULT_CONTENT_TYPE.equals(contentType)) ? new VdmAutoEditStrategy()
//				: new DefaultIndentLineAutoEditStrategy();
//		return new IAutoEditStrategy[] { strategy };
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

}
