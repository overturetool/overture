package org.overture.ide.vdmsl.ui.internal.selection;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.codeassist.ISelectionEngine;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.ISourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.SourceParserUtil;
import org.overture.ide.ast.*;

public class VdmSlSelectionEngine extends ScriptSelectionEngine implements ISelectionEngine  {

	private ISourceModule sourceModule;
	protected int actualSelectionStart;
	protected int actualSelectionEnd;
	private ASTNode[] wayToNode;
	
	@Override
	public IAssistParser getParser() {
		// TODO Auto-generated method stub
		return null;
	}

	public IModelElement[] select(ISourceModule module, int selectionSourceStart, int selectionSourceEnd) {
		
		sourceModule = (ISourceModule) module.getModelElement();
		String source = module.getSourceContents();
		
		if (!checkSelection(source, selectionSourceStart, selectionSourceEnd)) {
			return new IModelElement[0];
		}
		actualSelectionEnd--;
//		if (true) {
//			System.out.print("SELECTION - Checked : \""); //$NON-NLS-1$
//			System.out.print(source.substring(actualSelectionStart,
//					actualSelectionEnd + 1));
//			System.out.println('"');
//		}
//		
		ModuleDeclaration parsedUnit = SourceParserUtil.getModuleDeclaration((org.eclipse.dltk.core.ISourceModule) module
				.getModelElement());
		
		ASTNode node = ASTUtils.findMinimalNode(parsedUnit,
				actualSelectionStart, actualSelectionEnd);
//		System.out.println(node.toString());
		
		if (node == null)
			return new IModelElement[0];

		this.wayToNode = ASTUtils.restoreWayToNode(parsedUnit, node);
		
		return null;
	}

	protected boolean checkSelection(String source, int start, int end) {
		if (start > end) {
			int x = start;
			start = end;
			end = x;
		}
		if (start + 1 == end) {
//			ISourceRange range = RubySyntaxUtils.getEnclosingName(source, end);
//			if (range != null) {
//				this.actualSelectionStart = range.getOffset();
//				this.actualSelectionEnd = this.actualSelectionStart
//						+ range.getLength();
//				// return true;
//			}
//			ISourceRange range2 = RubySyntaxUtils.insideMethodOperator(source,
//					end);
//			if (range != null
//					&& (range2 == null || range2.getLength() < range
//							.getLength()))
//				return true;
//			if (range2 != null) {
//				this.actualSelectionStart = range2.getOffset();
//				this.actualSelectionEnd = this.actualSelectionStart
//						+ range2.getLength();
//				return true;
//			}
		} else {
			if (start >= 0 && end < source.length()) {
				String str = source.substring(start, end + 1);
//				if (RubySyntaxUtils.isRubyName(str)) {
					this.actualSelectionStart = start;
					this.actualSelectionEnd = end + 1;
					return true;
//				}
			}
		}

		return false;
	}

	
}
