package org.overturetool.eclipse.plugins.debug.ui.internal.console.ui;

import java.io.IOException;
import java.util.List;

import org.eclipse.dltk.console.IScriptConsoleShell;
import org.eclipse.dltk.console.ScriptConsoleCompletionProposal;
import org.eclipse.dltk.console.ui.IScriptConsoleViewer;
import org.eclipse.dltk.console.ui.ScriptConsoleCompletionProcessor;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.swt.graphics.Image;


public class OvertureConsoleCompletionProcessor extends
		ScriptConsoleCompletionProcessor {

	protected static class Validator implements IContextInformationValidator,
			IContextInformationPresenter {

		protected int installOffset;

		public boolean isContextInformationValid(int offset) {
			return Math.abs(installOffset - offset) < 5;
		}

		public void install(IContextInformation info, ITextViewer viewer,
				int offset) {
			installOffset = offset;
		}

		public boolean updatePresentation(int documentPosition,
				TextPresentation presentation) {
			return false;
		}
	}

	protected IProposalDecorator tclDecorator = new IProposalDecorator() {
		public String formatProposal(ScriptConsoleCompletionProposal c) {
			return c.getDisplay();
		}

		public Image getImage(ScriptConsoleCompletionProposal c) {
			String type = c.getType();
			if (type.equals("var")){
				return DLTKPluginImages
				.get(DLTKPluginImages.IMG_OBJS_LOCAL_VARIABLE); 
			} else if (type.equals("proc")){
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PUBLIC);
			} else if (type.equals("command")){
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PRIVATE);
			} else if (type.equals("func")){
				return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_FIELD);
			}
			
			return null; 
		}
	};
	
	private IContextInformationValidator validator;
	
	public OvertureConsoleCompletionProcessor(
			IScriptConsoleShell interpreterShell) {
		super(interpreterShell);
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '$' };
	}

	protected ICompletionProposal[] computeCompletionProposalsImpl(
			IScriptConsoleViewer viewer, int offset) {

		try {
			String commandLine = viewer.getCommandLine();
			int cursorPosition = offset - viewer.getCommandLineOffset();

			List list = getInterpreterShell().getCompletions(commandLine,
					cursorPosition);

			List proposals = createProposalsFromString(list, offset, tclDecorator);

			return (ICompletionProposal[]) proposals
					.toArray(new ICompletionProposal[proposals.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ICompletionProposal[] {};
	}

	protected IContextInformation[] computeContextInformationImpl(
			ITextViewer viewer, int offset) {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		if (validator == null) {
			validator = new Validator();
		}

		return validator;
	}
}
