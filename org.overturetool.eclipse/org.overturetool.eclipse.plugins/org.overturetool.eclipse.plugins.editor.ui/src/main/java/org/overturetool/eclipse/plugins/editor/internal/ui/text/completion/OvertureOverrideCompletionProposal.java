/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.text.completion;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.completion.ScriptTypeCompletionProposal;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.IContextInformation;


public class OvertureOverrideCompletionProposal extends ScriptTypeCompletionProposal implements ICompletionProposalExtension4 {

	private IScriptProject fDTLKProject;
	private String fMethodName;
	private String[] fParamTypes;

	public OvertureOverrideCompletionProposal(IScriptProject jproject, ISourceModule cu, String methodName, String[] paramTypes, int start, int length, String displayName, String completionProposal) {
		super(completionProposal, cu, start, length, null, displayName, 0);
		Assert.isNotNull(jproject);
		Assert.isNotNull(methodName);
		Assert.isNotNull(paramTypes);
		Assert.isNotNull(cu);

		fParamTypes= paramTypes;
		fMethodName= methodName;

		fDTLKProject= jproject;
		
		StringBuffer buffer= new StringBuffer();
		buffer.append(completionProposal);		
		
		setReplacementString(buffer.toString());
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getPrefixCompletionText(org.eclipse.jface.text.IDocument,int)
	 */
	public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
		return fMethodName;
	}

	/*
	 * @see DLTKTypeCompletionProposal#updateReplacementString(IDocument,char,int,ImportRewrite)
	 */
	protected boolean updateReplacementString(IDocument document, char trigger, int offset) throws CoreException, BadLocationException {
		final IDocument buffer= new Document(document.get());
		int index= offset - 1;
		while (index >= 0 && Character.isJavaIdentifierPart(buffer.getChar(index)))
			index--;
		final int length= offset - index - 1;
		buffer.replace(index + 1, length, " "); //$NON-NLS-1$
		return true;
	}

	/*
	 * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
	 */
	public boolean isAutoInsertable() {
		return false;
	}
	
	public IContextInformation getContextInformation() {		
		return new ContextInformation(getDisplayString(), getDisplayString());
	}
}
