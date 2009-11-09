package org.overture.ide.vdmsl.ui.internal.selection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.codeassist.ISelectionEngine;
import org.eclipse.dltk.codeassist.ScriptSelectionEngine;
import org.eclipse.dltk.compiler.env.ISourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;

public class VdmSlSelectionEngine extends ScriptSelectionEngine implements
		ISelectionEngine {

	private org.eclipse.dltk.core.ISourceModule sourceModule;
	protected int actualSelectionStart;
	protected int actualSelectionEnd;
	private ASTNode[] wayToNode;
	private Set<IModelElement> selectionElements = new HashSet();
	

	@Override
	public IAssistParser getParser() {
		// TODO Auto-generated method stub
		return null;
	}

	public IModelElement[] select(ISourceModule module,
			int selectionSourceStart, int selectionSourceEnd) {

		sourceModule = (org.eclipse.dltk.core.ISourceModule) module.getModelElement();
		String source = module.getSourceContents();

		if (!checkSelection(source, selectionSourceStart, selectionSourceEnd)) {
			return new IModelElement[0];
		}
		actualSelectionEnd--;
		// if (true) {
		//			System.out.print("SELECTION - Checked : \""); //$NON-NLS-1$
		// System.out.print(source.substring(actualSelectionStart,
		// actualSelectionEnd + 1));
		// System.out.println('"');
		// }
		//		
		ModuleDeclaration parsedUnit = SourceParserUtil
				.getModuleDeclaration((org.eclipse.dltk.core.ISourceModule) module
						.getModelElement());

		ASTNode node = ASTUtils.findMinimalNode(parsedUnit,
				actualSelectionStart, actualSelectionEnd);
		// System.out.println(node.toString());

		if (node == null)
			return new IModelElement[0];

		this.wayToNode = ASTUtils.restoreWayToNode(parsedUnit, node);

		if (node instanceof CallExpression) {
			CallExpression parentCall = this.getEnclosingCallNode(node);
			if (parentCall != null) {
				selectOnMethod(parsedUnit, parentCall);
			}
		}

		return (IModelElement[]) selectionElements
		.toArray(new IModelElement[selectionElements.size()]);
	}

	private void selectOnMethod(ModuleDeclaration parsedUnit,
			CallExpression parentCall) {
		String methodName = parentCall.getName();
		ASTNode receiver = parentCall.getReceiver();

		final List availableMethods = new ArrayList();

		List<ASTNode> nodes = parsedUnit.getChilds();
		for (ASTNode node : nodes) {
			List childs = node.getChilds();
			for (Object child : childs) {
				TypeDeclaration b = (TypeDeclaration) child;
				List stList = b.getStatements();
				for (Object method : stList) {
					MethodDeclaration m = (MethodDeclaration) method;
					if (m.getName().equals(methodName)) {
						System.out.println("METHOD MATCH");
						try {
							IModelElement elem = sourceModule.getElementAt(m.sourceStart());
							
							selectionElements.add(elem);
							System.out.println(elem);
						} catch (ModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
//						SourceMethod sm = new SourceMethod();
					}
				}

			}
		}

		if (receiver == null) {
			// TODO:
		} else {

		}

	}

	protected boolean checkSelection(String source, int start, int end) {
		if (start > end) {
			int x = start;
			start = end;
			end = x;
		}
		// if (start + 1 == end) {
		// ISourceRange range = RubySyntaxUtils.getEnclosingName(source, end);
		// if (range != null) {
		// this.actualSelectionStart = range.getOffset();
		// this.actualSelectionEnd = this.actualSelectionStart
		// + range.getLength();
		// // return true;
		// }
		// ISourceRange range2 = RubySyntaxUtils.insideMethodOperator(source,
		// end);
		// if (range != null
		// && (range2 == null || range2.getLength() < range
		// .getLength()))
		// return true;
		// if (range2 != null) {
		// this.actualSelectionStart = range2.getOffset();
		// this.actualSelectionEnd = this.actualSelectionStart
		// + range2.getLength();
		// return true;
		// }
		// }
		else {
			if (start >= 0 && end < source.length()) {
				String str = source.substring(start, end + 1);
				// if (RubySyntaxUtils.isRubyName(str)) {
				this.actualSelectionStart = start;
				this.actualSelectionEnd = end + 1;
				return true;
				// }
			}
		}

		return false;
	}

	private CallExpression getEnclosingCallNode(ASTNode node) {
		return ASTUtils.getEnclosingCallNode(wayToNode, node, true);
	}
}
