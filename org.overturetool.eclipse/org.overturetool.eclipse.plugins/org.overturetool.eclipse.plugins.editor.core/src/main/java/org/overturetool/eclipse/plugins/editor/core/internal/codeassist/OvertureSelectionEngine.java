package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.codeassist.ISelectionEngine;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;
import org.overturetool.eclipse.plugins.editor.core.utils.OvertureSyntaxUtils;

/***
 * It search for structure elements from field, method references. 
 * @author Kedde
 *
 */
public class OvertureSelectionEngine implements ISelectionEngine {
	private ISourceModule sourceModule;
	protected int actualSelectionStart;
	protected int actualSelectionEnd;
	private Set selectionElements = new HashSet();
	protected boolean DEBUG = true;
	private OvertureSelectionParser parser = new OvertureSelectionParser();
	private ASTNode[] wayToNode;
	

	public IModelElement[] select(org.eclipse.dltk.compiler.env.ISourceModule sourceUnit, int selectionSourceStart, int selectionSourceEnd) {
		sourceModule = (ISourceModule) sourceUnit.getModelElement();
		String source = sourceUnit.getSourceContents();
		if (DEBUG) {
			System.out.print("SELECTION IN "); //$NON-NLS-1$
			System.out.print(sourceUnit.getFileName());
			System.out.print(" FROM "); //$NON-NLS-1$
			System.out.print(selectionSourceStart);
			System.out.print(" TO "); //$NON-NLS-1$
			System.out.println(selectionSourceEnd);
			System.out.println("SELECTION - Source :"); //$NON-NLS-1$
			System.out.println(source);
		}
		if (!checkSelection(source, selectionSourceStart, selectionSourceEnd)) {
			return new IModelElement[0];
		}
		actualSelectionEnd--; // inclusion fix
		if (DEBUG) {
			System.out.print("SELECTION - Checked : \""); //$NON-NLS-1$
			System.out.print(source.substring(actualSelectionStart, actualSelectionEnd + 1));
			System.out.println('"');
		}

		try {
			ModuleDeclaration parsedUnit = this.parser.parse(sourceUnit);

			if (parsedUnit != null) {
				if (DEBUG) {
					System.out.println("SELECTION - AST :"); //$NON-NLS-1$
					System.out.println(parsedUnit.toString());
				}

				ASTNode node = ASTUtils.findMinimalNode(parsedUnit, actualSelectionStart, actualSelectionEnd);

				if (node == null)
					return new IModelElement[0];

				this.wayToNode = ASTUtils.restoreWayToNode(parsedUnit, node);
				if (node instanceof TypeDeclaration) {
					selectionOnTypeDeclaration(parsedUnit, (TypeDeclaration) node);
				}
				else if (node instanceof MethodDeclaration) {
					selectionOnMethodDeclaration(parsedUnit, (MethodDeclaration) node);
				}
//				else if (node instanceof ConstantReference) {
//					selectTypes(parsedUnit, node);
//				}
//				else if (node instanceof VariableReference) {
//					selectionOnVariable(parsedUnit, (VariableReference) node);
//				}
//				else if (node instanceof RubyMethodArgument) {
//					selectOnMethodArgument(parsedUnit,
//							(RubyMethodArgument) node);
//				} 
				//else if (node instanceof RubySuperExpression) {
//					selectOnSuper(parsedUnit, (RubySuperExpression) node);
//				} 
				else {
					CallExpression parentCall = this.getEnclosingCallNode(node);
					if (parentCall != null) {
						// TODO kedde
						//selectOnMethod(parsedUnit, parentCall);
					} else { // parentCall == null
					}
				}
			}
		} catch (IndexOutOfBoundsException e) { // work-around internal failure
			OverturePlugin.log(e);
		}
		return (IModelElement[]) selectionElements.toArray(new IModelElement[selectionElements.size()]);
	}
	
	
	public void setOptions(Map options) {

	}
	
	private CallExpression getEnclosingCallNode(ASTNode node) {
		return ASTUtils.getEnclosingCallNode(wayToNode, node, true);
	}
	
	private void selectionOnTypeDeclaration(ModuleDeclaration parsedUnit,
			TypeDeclaration typeDeclaration) {
		// if (typeDeclaration instanceof RubyClassDeclaration) {
		// RubyClassDeclaration rcd = (RubyClassDeclaration) typeDeclaration;
		// IType[] types = getSourceTypesForClass(parsedUnit, rcd
		// .getClassName());
		// selectionElements.addAll(Arrays.asList(types));
		// }
		IModelElement elementAt = null;
		try {
			elementAt = sourceModule
					.getElementAt(typeDeclaration.sourceStart() + 1);
		} catch (ModelException e) {
			OverturePlugin.log(e);
		}
		if (elementAt != null)
			selectionElements.add(elementAt);
	}
	
	/**
	 * Checks, whether giver selection is correct selection, or can be expanded
	 * to correct selection region. As result will set
	 * this.actualSelection(Start|End) properly. In case of incorrect selection,
	 * will return false.
	 * 
	 * @param source
	 * @param start
	 * @param end
	 * @return
	 */
	protected boolean checkSelection(String source, int start, int end) {
		if (start > end) {
			int x = start;
			start = end;
			end = x;
		}
		if (start + 1 == end) {
			ISourceRange range = OvertureSyntaxUtils.getEnclosingName(source, end);
			if (range != null) {
				this.actualSelectionStart = range.getOffset();
				this.actualSelectionEnd = this.actualSelectionStart
						+ range.getLength();
				// return true;
			}
			ISourceRange range2 = OvertureSyntaxUtils.insideMethodOperator(source,
					end);
			if (range != null
					&& (range2 == null || range2.getLength() < range
							.getLength()))
				return true;
			if (range2 != null) {
				this.actualSelectionStart = range2.getOffset();
				this.actualSelectionEnd = this.actualSelectionStart
						+ range2.getLength();
				return true;
			}
		} else {
			if (start >= 0 && end < source.length()) {
				String str = source.substring(start, end + 1);
				if (OvertureSyntaxUtils.isOvertureName(str)) {
					this.actualSelectionStart = start;
					this.actualSelectionEnd = end + 1;
					return true;
				}
			}
		}

		return false;
	}

	private void selectionOnMethodDeclaration(ModuleDeclaration parsedUnit, MethodDeclaration methodDeclaration) {
		IModelElement elementAt = null;
		try {
			elementAt = sourceModule.getElementAt(methodDeclaration.sourceStart() + 1);
		} catch (ModelException e) {
			OverturePlugin.log(e);
		}
		if (elementAt != null)
		{
			selectionElements.add(elementAt);
		}
	}
}
