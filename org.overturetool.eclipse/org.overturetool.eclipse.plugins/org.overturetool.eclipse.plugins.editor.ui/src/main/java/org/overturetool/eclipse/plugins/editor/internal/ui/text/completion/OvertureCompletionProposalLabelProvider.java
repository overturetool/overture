/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.completion;

import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;

public class OvertureCompletionProposalLabelProvider extends
		CompletionProposalLabelProvider {
	protected String createMethodProposalLabel(CompletionProposal methodProposal) {
		//TODO
		return null;
	}

	protected String createOverrideMethodProposalLabel(
			CompletionProposal methodProposal) {
		StringBuffer nameBuffer = new StringBuffer();

		// method name
		nameBuffer.append(methodProposal.getName());

		// parameters
		nameBuffer.append('(');
		appendUnboundedParameterList(nameBuffer, methodProposal);
		nameBuffer.append(')'); //$NON-NLS-1$

		return nameBuffer.toString();
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createImageDescriptor(proposal);
	}

	/**
	 * @param proposal
	 * @return
	 */
	private ImageDescriptor extraImageFromProposal(CompletionProposal proposal) {
		//TODO
		return null;
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createFieldImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	protected ImageDescriptor createFieldImageDescriptor(
			CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createFieldImageDescriptor(proposal);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createLocalImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	protected ImageDescriptor createLocalImageDescriptor(
			CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createLocalImageDescriptor(proposal);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createMethodImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	public ImageDescriptor createMethodImageDescriptor(
			CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createMethodImageDescriptor(proposal);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createPackageImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	protected ImageDescriptor createPackageImageDescriptor(
			CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createPackageImageDescriptor(proposal);
	}

	/**
	 * @see org.eclipse.dltk.ui.text.completion.CompletionProposalLabelProvider#createTypeImageDescriptor(org.eclipse.dltk.core.CompletionProposal)
	 */
	protected ImageDescriptor createTypeImageDescriptor(
			CompletionProposal proposal) {
		ImageDescriptor imageDescriptor = extraImageFromProposal(proposal);
		if (imageDescriptor != null)
			return imageDescriptor;
		return super.createTypeImageDescriptor(proposal);
	}
}
