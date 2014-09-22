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
package org.overture.ide.ui.quickfix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * Custom vdm quick fix assistant processor that uses the {@link MarkerResolutionGenerator} class to show completion
 * options
 * 
 * @author kel
 */
public class VdmQuickAssistantProcessor implements IQuickAssistProcessor
{
	private class MarkerResolutionProposal implements ICompletionProposal
	{

		private IMarkerResolution quickfix;
		private IMarker marker;

		public MarkerResolutionProposal(IMarkerResolution quickfix,
				IMarker marker)
		{
			this.quickfix = quickfix;
			this.marker = marker;
		}

		@Override
		public void apply(IDocument document)
		{
			quickfix.run(marker);
		}

		@Override
		public Point getSelection(IDocument document)
		{
			return null;
		}

		@Override
		public String getAdditionalProposalInfo()
		{
			return null;
		}

		@Override
		public String getDisplayString()
		{
			return quickfix.getLabel();
		}

		@Override
		public Image getImage()
		{
			if (quickfix instanceof IMarkerResolution2)
			{
				return ((IMarkerResolution2) quickfix).getImage();
			}
			return null;
		}

		@Override
		public IContextInformation getContextInformation()
		{
			return null;
		}

	}

	@Override
	public String getErrorMessage()
	{
		return null;
	}

	@Override
	public boolean canFix(Annotation annotation)
	{
		return annotation instanceof MarkerAnnotation
				&& !annotation.isMarkedDeleted();
	}

	@Override
	public boolean canAssist(IQuickAssistInvocationContext invocationContext)
	{
		return true;
	}

	@Override
	public ICompletionProposal[] computeQuickAssistProposals(
			IQuickAssistInvocationContext invocationContext)
	{
		/**
		 * TextInvocationContext - length, offset, and sourceviewer
		 */
		ISourceViewer viewer = invocationContext.getSourceViewer();
		int documentOffset = invocationContext.getOffset();

		Set<ICompletionProposal> proposals = new HashSet<ICompletionProposal>();

		MarkerResolutionGenerator generator = new MarkerResolutionGenerator();

		Iterator<?> iter = viewer.getAnnotationModel().getAnnotationIterator();
		while (iter.hasNext())
		{
			Annotation annotation = (Annotation) iter.next();

			if (annotation instanceof MarkerAnnotation)
			{
				MarkerAnnotation ma = (MarkerAnnotation) annotation;

				final IMarker marker = ma.getMarker();

				Integer start = marker.getAttribute(IMarker.CHAR_START, -1);
				Integer end = marker.getAttribute(IMarker.CHAR_END, -1);

				if (start > 0 && end > 0 && documentOffset <= end
						&& documentOffset >= start)
				{
					if (generator.hasResolutions(marker))
					{
						for (IMarkerResolution quickfix : generator.getResolutions(marker))
						{
							proposals.add(new MarkerResolutionProposal(quickfix, marker));
						}
					}
				}

			}
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}

}
