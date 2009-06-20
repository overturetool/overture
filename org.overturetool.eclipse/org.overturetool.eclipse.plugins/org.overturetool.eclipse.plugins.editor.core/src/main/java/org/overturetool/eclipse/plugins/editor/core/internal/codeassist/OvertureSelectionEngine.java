package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.references.ConstantReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.codeassist.ISelectionEngine;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.core.search.TypeNameMatch;
import org.eclipse.dltk.core.search.TypeNameMatchRequestor;
import org.eclipse.dltk.ti.BasicContext;
import org.eclipse.dltk.ti.DLTKTypeInferenceEngine;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.goals.GoalEvaluator;
import org.eclipse.dltk.ti.goals.IGoal;
import org.eclipse.dltk.ti.types.ClassType;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.VDMMethodDeclaration;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.ConstantReferenceEvaluator;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.NonTypeConstantTypeGoal;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureClassType;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinClass;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinElementInfo;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinModel;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureModelUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureTypeInferencingUtils;
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
	protected boolean DEBUG = false;
	private OvertureSelectionParser parser = new OvertureSelectionParser();
	private ASTNode[] wayToNode;
	private OvertureMixinModel mixinModel;
	private DLTKTypeInferenceEngine inferencer;

	
	public OvertureSelectionEngine() {
		inferencer = new DLTKTypeInferenceEngine();
	}
	public IModelElement[] select(org.eclipse.dltk.compiler.env.ISourceModule sourceUnit, int selectionSourceStart, int selectionSourceEnd) {
		sourceModule = (ISourceModule) sourceUnit.getModelElement();
		mixinModel = OvertureMixinModel.getInstance(sourceModule.getScriptProject());
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
				else if (node instanceof VDMMethodDeclaration) {
					selectionOnMethodDeclaration(parsedUnit, (MethodDeclaration) node);
				}
				else if (node instanceof ConstantReference) {
					selectTypes(parsedUnit, node);
				}
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
						selectOnMethod(parsedUnit, parentCall);
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
	
	private void selectTypes(ModuleDeclaration parsedUnit, ASTNode node) {
		if (node instanceof ConstantReference) {
			selectOnConstant(parsedUnit, (ConstantReference) node);
		} 
		if (selectionElements.isEmpty()) {
			TypeNameMatchRequestor requestor = new TypeNameMatchRequestor() {
				public void acceptTypeNameMatch(TypeNameMatch match) {
					selectionElements.add(match.getType());
				}
			};
			String unqualifiedName = null;
			if (node instanceof ConstantReference) {
				ConstantReference expr = (ConstantReference) node;
				unqualifiedName = expr.getName();
			}
			if (unqualifiedName != null) {
				ScriptModelUtil.searchTypeDeclarations(sourceModule
						.getScriptProject(), unqualifiedName, requestor);
			}
		}
	}
	
	private void selectOnConstant(ModuleDeclaration parsedUnit,
			ConstantReference node) {
		BasicContext basicContext = new BasicContext(sourceModule, parsedUnit);
		ConstantReferenceEvaluator evaluator = new ConstantReferenceEvaluator(new ExpressionTypeGoal(basicContext, node));
		IGoal[] init = evaluator.init();
		if (init == null || init.length == 0) {
			Object evaluatedType = evaluator.produceResult();
			if (evaluatedType instanceof OvertureClassType) {
				OvertureMixinClass mixinClass = mixinModel.createOvertureClass((OvertureClassType) evaluatedType);
				if (mixinClass != null)
					addArrayToCollection(mixinClass.getSourceTypes(), selectionElements);
			}
		} else if (init[0] instanceof NonTypeConstantTypeGoal) {
			// it'a non-type constant
			processNonTypeConstant((NonTypeConstantTypeGoal) init[0]);
		}
	}
	
	/**
	 * Uses goal info for selection on non-type goal
	 * 
	 * @param ngoal
	 */
	private void processNonTypeConstant(NonTypeConstantTypeGoal ngoal) {
		IMixinElement element = ngoal.getElement();
		if (element != null) {
			Object[] eObjects = element.getAllObjects();
			for (int i = 0; i < eObjects.length; i++) {
				if (eObjects[i] instanceof OvertureMixinElementInfo) {
					OvertureMixinElementInfo info = (OvertureMixinElementInfo) eObjects[i];
					Object obj = info.getObject();
					if (obj instanceof IModelElement) {
						this.selectionElements.add(obj);
					}
				}
			}
		}
	}
	
	/**
	 * @param src
	 * @param dest
	 */
	private static void addArrayToCollection(IMember[] src, Collection dest) {
		if (src != null) {
			for (int i = 0, size = src.length; i < size; ++i) {
				dest.add(src[i]);
			}
		}
	}
	
	private void selectOnMethod(ModuleDeclaration parsedUnit,
			CallExpression parentCall) {
		String methodName = parentCall.getName();
		ASTNode receiver = parentCall.getReceiver();

		final List availableMethods = new ArrayList();

		if (receiver == null) {
			IEvaluatedType type = OvertureTypeInferencingUtils.determineSelfClass(
					mixinModel, sourceModule, parsedUnit, 
					parentCall.sourceStart());
			if ((type != null) && "Object".equals(type.getTypeName())) { //$NON-NLS-1$
				ExpressionTypeGoal goal = new ExpressionTypeGoal(
						new BasicContext(sourceModule, parsedUnit), parsedUnit);
				IEvaluatedType type2 = inferencer.evaluateType(goal, 2000);
				if (type2 != null) {
					type = type2;
				}
			}
			IMethod[] m = OvertureModelUtils.searchClassMethodsExact(mixinModel,
					sourceModule, parsedUnit, type, methodName);
			addArrayToCollection(m, availableMethods);
		} else {
			ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(
					sourceModule, parsedUnit), receiver);
			IEvaluatedType type = inferencer.evaluateType(goal, 5000);
			IMethod[] m = OvertureModelUtils.searchClassMethodsExact(mixinModel,
					sourceModule, parsedUnit, type, methodName);
			addArrayToCollection(m, availableMethods);
			if (receiver instanceof VariableReference) {
				IMethod[] availableMethods2 = OvertureModelUtils
						.getSingletonMethods(mixinModel,
								(VariableReference) receiver, parsedUnit,
								sourceModule, methodName);
				addArrayToCollection(availableMethods2, availableMethods);
			}
		}

		if (availableMethods.isEmpty()) {
			searchMethodDeclarations(sourceModule.getScriptProject(), methodName, availableMethods);
		}

		if (!availableMethods.isEmpty()) {
			for (int i = 0, size = availableMethods.size(); i < size; ++i) {
				final IMethod m = (IMethod) availableMethods.get(i);
				if (methodName.equals(methodName)) {
					selectionElements.add(m);
				}
			}
		}
	}

	private static void searchMethodDeclarations(IScriptProject project,
			String methodName, final List availableMethods) {
		final SearchRequestor requestor = new SearchRequestor() {

			public void acceptSearchMatch(SearchMatch match)
					throws CoreException {
				IModelElement modelElement = (IModelElement) match.getElement();
				ISourceModule sm = (ISourceModule) modelElement
						.getAncestor(IModelElement.SOURCE_MODULE);
				IModelElement elementAt = sm.getElementAt(match.getOffset());
				if (elementAt.getElementType() == IModelElement.METHOD) {
					availableMethods.add(elementAt);
				}
			}

		};
		final IDLTKSearchScope scope = SearchEngine.createSearchScope(project);
		try {
			final SearchEngine engine = new SearchEngine();
			final SearchPattern pattern = SearchPattern.createPattern(
					methodName, IDLTKSearchConstants.METHOD,
					IDLTKSearchConstants.DECLARATIONS,
					SearchPattern.R_EXACT_MATCH
							| SearchPattern.R_CASE_SENSITIVE,
					DLTKLanguageManager.getLanguageToolkit(project));
			final SearchParticipant[] participants = new SearchParticipant[] { SearchEngine
					.getDefaultSearchParticipant() };
			engine.search(pattern, participants, scope, requestor, null);
		} catch (CoreException e) {
			OverturePlugin.log(e);
		}
	}
	
	
}