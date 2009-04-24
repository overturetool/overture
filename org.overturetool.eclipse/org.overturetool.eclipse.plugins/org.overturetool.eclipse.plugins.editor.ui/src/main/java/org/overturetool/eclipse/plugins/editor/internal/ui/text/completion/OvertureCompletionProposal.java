/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.completion;

import org.eclipse.dltk.ui.text.completion.ProposalInfo;
import org.eclipse.dltk.ui.text.completion.ScriptCompletionProposal;
import org.eclipse.swt.graphics.Image;

public class OvertureCompletionProposal extends ScriptCompletionProposal {

	public OvertureCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance);
		;
		ProposalInfo proposalInfo = new ProposalInfo(null);
		this.setProposalInfo(proposalInfo);
	}

	public OvertureCompletionProposal(String replacementString,
			int replacementOffset, int replacementLength, Image image,
			String displayString, int relevance, boolean isInDoc) {
		super(replacementString, replacementOffset, replacementLength, image,
				displayString, relevance, isInDoc);
		ProposalInfo proposalInfo = new ProposalInfo(null);
		this.setProposalInfo(proposalInfo);
	}

	protected boolean isSmartTrigger(char trigger) {
		if (trigger == '$') {
			return true;
		}
		return false;
	}
}
