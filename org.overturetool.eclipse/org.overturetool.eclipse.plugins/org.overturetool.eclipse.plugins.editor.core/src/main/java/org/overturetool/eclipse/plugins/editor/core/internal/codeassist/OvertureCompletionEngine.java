package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import java.util.ArrayList;

import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.env.ISourceModule;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IType;
import org.overturetool.eclipse.plugins.editor.core.OvertureKeywords;

public class OvertureCompletionEngine extends ScriptCompletionEngine{
	private AssistUtils.PositionCalculator calculator;
	
	@Override
	protected int getEndOfEmptyToken() {
		return 0;
	}

	@Override
	protected String processFieldName(IField field, String token) {
		return field.getElementName();
	}

	@Override
	protected String processMethodName(IMethod method, String token) {
		return null;
	}

	@Override
	protected String processTypeName(IType method, String token) {
		return null;
	}

	@Override
	public IAssistParser getParser() {
		return null;
	}

	public void complete(ISourceModule module, int position, int i) {
		this.actualCompletionPosition = position;
		this.requestor.beginReporting();
		final String content = module.getSourceContents();
		if (position < 0 || position > content.length()) {
			return;
		}
		
		String startPart = getWordStarting(content,position, 10);
		this.setSourceRange(position  - startPart.length(), position); // todo startpart.lengt
 		doCompletionOnKeyword(this.startPosition, this.endPosition, startPart); //startPart
		this.requestor.endReporting();
	}
	
//	private void completeFromMap(int position, String completionPart, ArrayList<String> completionList) {
//		for (String string : completionList) {
//			CompletionProposal createProposal = this.createProposal(
//					CompletionProposal.METHOD_REF, 
//					this.actualCompletionPosition
//				);
//			createProposal.setName(string.toCharArray());
//			createProposal.setCompletion(string.toCharArray());
//			// createProposal.setSignature(name);
//			createProposal.extraInfo = string;
//			// createProposal.setDeclarationSignature(cm.getDeclarationSignature());
//			// createProposal.setSignature(cm.getSignature());
//			createProposal.setReplaceRange(
//								this.startPosition - this.offset,
//								this.endPosition - this.offset
//							);
//			
//			this.requestor.accept(createProposal);
//		}
//	}
	
	private void doCompletionOnKeyword(int position, int pos, String startPart) {
		String[] keywords = OvertureKeywords.getReservedwords();
		char[][] keyWordsArray = new char[keywords.length][];
		for (int a = 0; a < keywords.length; a++) {
			keyWordsArray[a] = keywords[a].toCharArray();
		}
		findKeywords(startPart.toCharArray(), keyWordsArray, true);
	}
	
	private String getWordStarting(String content, int position, int maxLen) {
		if (position <= 0 || position > content.length())
			return Util.EMPTY_STRING;
		final int original = position;
		while (position > 0
				&& maxLen > 0
				&& ((content.charAt(position - 1) == ':')
						|| (content.charAt(position - 1) == '\'')
						|| (content.charAt(position - 1) == '"')
			|| isLessStrictIdentifierCharacter(content.charAt(position - 1)))){
			--position;
			--maxLen;
		}
		return content.substring(position, original);
	}
	
	private boolean isLessStrictIdentifierCharacter(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9') || ch == '_' || ch == '@' || ch == '$';
	}
}
