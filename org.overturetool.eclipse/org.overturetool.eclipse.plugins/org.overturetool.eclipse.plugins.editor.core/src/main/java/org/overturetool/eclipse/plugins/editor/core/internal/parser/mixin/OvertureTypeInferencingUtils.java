/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.IMixinRequestor;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.core.search.TypeNameMatch;
import org.eclipse.dltk.core.search.TypeNameMatchRequestor;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.evaluation.types.AmbiguousType;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.IContext;
import org.eclipse.dltk.ti.IInstanceContext;
import org.eclipse.dltk.ti.ISourceModuleContext;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.dltk.ti.types.RecursionTypeCall;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureAssignment;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureBlock;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureDAssgnExpression;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureMethodArgument;

public class OvertureTypeInferencingUtils {

	/**
	 * Searches all top level types, which starts with prefix
	 */
	public static IType[] getAllTypes(ISourceModule module, String prefix) {
		final List types = new ArrayList();

		TypeNameMatchRequestor requestor = new TypeNameMatchRequestor() {

			public void acceptTypeNameMatch(TypeNameMatch match) {
				IType type = match.getType();
				if (type.getParent() instanceof ISourceModule) {
					types.add(type);
				}
			}

		};

		ScriptModelUtil.searchTypeDeclarations(module.getScriptProject(), prefix + "*", requestor); //$NON-NLS-1$

		return (IType[]) types.toArray(new IType[types.size()]);
	}

	public static ASTNode[] getAllStaticScopes(ModuleDeclaration rootNode, final int requestedOffset) {
		final Collection scopes = new ArrayList();
		ASTVisitor visitor = new OffsetTargetedASTVisitor(requestedOffset) {
			public boolean visitInteresting(MethodDeclaration s) {
				scopes.add(s);
				return true;
			}

			public boolean visitInteresting(ModuleDeclaration s) {
				scopes.add(s);
				return true;
			}

			public boolean visitInteresting(TypeDeclaration s) {
				scopes.add(s);
				return true;
			}

			protected boolean visitInteresting(OvertureBlock b) {
				scopes.add(b);
				return true;
			}

			protected boolean visitGeneralInteresting(ASTNode s) {
				if (ASTUtils.isNodeScoping(s)) {
					scopes.add(s);
					return true;
				}
				return super.visitGeneralInteresting(s);
			}

		};
		try {
			rootNode.traverse(visitor);
		} catch (Exception e) {
			OverturePlugin.log(e);
		}
		if (scopes.size() == 0)
			scopes.add(rootNode);
		return (ASTNode[]) scopes.toArray(new ASTNode[scopes.size()]);
	}

	public static IMixinElement[] getModelStaticScopes(MixinModel model,
			ModuleDeclaration rootNode, final int requestedOffset) {
		String[] modelStaticScopesKeys = getModelStaticScopesKeys(model,
				rootNode, requestedOffset);
		IMixinElement[] result = new IMixinElement[modelStaticScopesKeys.length];
		for (int i = 1; i < modelStaticScopesKeys.length; i++) { // XXX-fourdman:
			// removed
			// Object
			// resulution
			result[i] = model.get(modelStaticScopesKeys[i]);
			// if (result[i] == null)
			// throw new RuntimeException("getModelStaticScopes(): Failed to get
			// element from mixin-model: " + modelStaticScopesKeys[i]);
		}
		return result;
	}

	public static String[] getModelStaticScopesKeys(MixinModel model, ModuleDeclaration rootNode, final int requestedOffset) {
		ASTNode[] allStaticScopes = OvertureTypeInferencingUtils.getAllStaticScopes(rootNode, requestedOffset);
		return OvertureMixinBuildVisitor.restoreScopesByNodes(allStaticScopes);
	}

	public static IEvaluatedType determineSelfClass(
			OvertureMixinModel mixinModel, IContext context, int keyOffset) {
		if (context instanceof IInstanceContext) {
			IInstanceContext instanceContext = (IInstanceContext) context;
			return instanceContext.getInstanceType();
		} else {
			ISourceModuleContext basicContext = (ISourceModuleContext) context;
			return determineSelfClass(mixinModel, basicContext
					.getSourceModule(), basicContext.getRootNode(), keyOffset);
		}
	}

	/**
	 * Determines a fully-qualified names of the class scope that the given
	 * offset is statically enclosed in.
	 * 
	 * @param sourceModule
	 *            a module containing the given offsets
	 * @param rootNode
	 *            the root of AST corresponding to the given source module
	 * @param keyOffset
	 *            the offset
	 * @return The type of <code>self</code> at the given offset (never null)
	 */
	public static OvertureClassType determineSelfClass(
			OvertureMixinModel overtureModel, final ISourceModule sourceModule,
			ModuleDeclaration rootNode, final int keyOffset) {
		String[] keys = getModelStaticScopesKeys(overtureModel.getRawModel(),
				rootNode, keyOffset);
		if (keys != null && keys.length > 0) {
			String inner = keys[keys.length - 1];
			IOvertureMixinElement overtureElement = overtureModel
					.createOvertureElement(inner);
			// ssanders: Fallback to locating the immediate parent
			if ((overtureElement == null)
					&& (inner.indexOf(IIndexConstants.SEPARATOR) != -1)) {
				overtureElement = overtureModel.createOvertureElement(inner
						.substring(0, inner
								.lastIndexOf(IIndexConstants.SEPARATOR)));
			}
			if (overtureElement instanceof OvertureMixinMethod) {
				OvertureMixinMethod method = (OvertureMixinMethod) overtureElement;
				OvertureMixinClass selfType = method.getSelfType();
				if (selfType != null) {
					return new OvertureClassType(selfType.getKey());
				}
			} else if (overtureElement instanceof OvertureMixinClass) {
				OvertureMixinClass overtureMixinClass = (OvertureMixinClass) overtureElement;
				return new OvertureClassType(overtureMixinClass.getKey());
			}
		}
		return null;
	}

	 public static OvertureAssignment[] findLocalVariableAssignments(final ASTNode scope, final ASTNode nextScope, final String varName) {
		final Collection assignments = new ArrayList();
		ASTVisitor visitor = new ASTVisitor() {

			public boolean visit(MethodDeclaration s) throws Exception {
				if (s == scope)
					return true;
				return false;
			}

			public boolean visit(TypeDeclaration s) throws Exception {
				if (s == scope)
					return true;
				return false;
			}

			public boolean visit(ASTNode node) throws Exception {
				if (node instanceof OvertureAssignment) {
					OvertureAssignment assignment = (OvertureAssignment) node;
					ASTNode lhs = assignment.getLeft();
					if (lhs instanceof VariableReference) {
						VariableReference varRef = (VariableReference) lhs;
						if (varName.equals(varRef.getName())) {
							assignments.add(assignment);
						}
					}
				}
				if (node == nextScope)
					return false;
				return true;
			}
		};
		try {
			scope.traverse(visitor);
		} catch (Exception e) {
			OverturePlugin.log(e);
		}
		return (OvertureAssignment[]) assignments.toArray(new OvertureAssignment[assignments.size()]);
	}

	public static boolean isRootLocalScope(ASTNode node) {
		return node instanceof ModuleDeclaration
				|| node instanceof TypeDeclaration
				|| node instanceof MethodDeclaration;
	}

	public static IEvaluatedType combineTypes(Collection evaluaedTypes) {
		Set types = new HashSet(evaluaedTypes);
		types.remove(null);
		if (types.size() > 1 && types.contains(RecursionTypeCall.INSTANCE))
			types.remove(RecursionTypeCall.INSTANCE);
		return combineUniqueTypes((IEvaluatedType[]) types
				.toArray(new IEvaluatedType[types.size()]));
	}

	private static IEvaluatedType combineUniqueTypes(IEvaluatedType[] types) {
		if (types.length == 0)
			return UnknownType.INSTANCE;
		if (types.length == 1)
			return types[0];
		return new AmbiguousType(types);
	}

	public static IEvaluatedType combineTypes(IEvaluatedType[] evaluaedTypes) {
		return combineTypes(Arrays.asList(evaluaedTypes));
	}

	public static ModuleDeclaration parseSource(ISourceModule module) {
		return SourceParserUtil.getModuleDeclaration(module);
	}

	public static IEvaluatedType getAmbiguousMetaType(IEvaluatedType receiver) {
		if (receiver instanceof AmbiguousType) {
			Set possibleReturns = new HashSet();
			AmbiguousType ambiguousType = (AmbiguousType) receiver;
			IEvaluatedType[] possibleTypes = ambiguousType.getPossibleTypes();
			for (int i = 0; i < possibleTypes.length; i++) {
				IEvaluatedType type = possibleTypes[i];
				IEvaluatedType possibleReturn = getAmbiguousMetaType(type);
				possibleReturns.add(possibleReturn);
			}
			return OvertureTypeInferencingUtils.combineTypes(possibleReturns);
		}
		return null;
	}

	public static String searchConstantElement(MixinModel mixinModel,
			ModuleDeclaration module, int calculationOffset, String constantName) {
		String[] modelStaticScopes = getModelStaticScopesKeys(mixinModel,
				module, calculationOffset);

		String resultKey = null;

		for (int i = modelStaticScopes.length - 1; i >= 0; i--) {
			String possibleKey = modelStaticScopes[i]
					+ IMixinRequestor.MIXIN_NAME_SEPARATOR + constantName;
			if (mixinModel.keyExists(possibleKey)) {
				resultKey = possibleKey;
				break;
			}
		}

		// check top-most scope
		if (resultKey == null) {
			if (mixinModel.keyExists(constantName)) {
				resultKey = constantName;
			}
		}
		return resultKey;
	}

	private static class LocalVariablesSearchVisitor extends ASTVisitor {

		private class VariableAssignment {
			public OvertureAssignment assignment;
			public int level;

			public VariableAssignment(OvertureAssignment assignenment, int level) {
				super();
				this.assignment = assignenment;
				this.level = level;
			}

		}

		private List assignements;
		private Stack level = new Stack();
		private int maxLevel;

		private final String name;
		private final int offset;
		private final ASTNode root;

		public LocalVariablesSearchVisitor(String name, ASTNode root, int offset) {
			super();
			this.name = name;
			this.root = root;
			this.offset = offset;
			this.maxLevel = 0;
			this.assignements = new ArrayList();
			this.level.clear();
		}

		public boolean visitGeneral(ASTNode node) throws Exception {
			if (node == root)
				return true;
			if (node instanceof MethodDeclaration
					|| node instanceof TypeDeclaration)
				return false;
			if (node instanceof OvertureAssignment) {
				if (!(node.sourceEnd() <= offset))
					return false;
				OvertureAssignment overtureAssignment = (OvertureAssignment) node;
				ASTNode lhs = overtureAssignment.getLeft();
				if (lhs instanceof VariableReference) {
					VariableReference varRef = (VariableReference) lhs;
					if (name.equals(varRef.getName())) {
						assignements.add(new VariableAssignment(overtureAssignment, level.size()));
					}
				}
			} else if (
			/*
			 * node instanceof OvertureIfStatement || node instanceof
			 * OvertureForStatement2 || node instanceof OvertureWhileStatement
			 * ||
			 */
			node instanceof OvertureBlock
			/*
			 * || node instanceof OvertureUntilStatement || node instanceof
			 * OvertureUnlessStatement
			 */) {
				if (node.sourceStart() >= offset)
					return false;
				level.push(node);
				if (node.sourceEnd() >= offset && level.size() > maxLevel)
					maxLevel = level.size();
			}
			return true;
		}

		public void endvisitGeneral(ASTNode node) throws Exception {
			if (level.size() > 0 && level.peek().equals(node)) {
				level.pop();
			}
		}

		public OvertureAssignment getUnconditionalAssignment() {
			VariableAssignment[] array = (VariableAssignment[]) assignements
					.toArray(new VariableAssignment[assignements.size()]);
			for (int i = array.length - 1; i >= 0; i--) {
				VariableAssignment a = array[i];
				if (a.level <= maxLevel) {
					return a.assignment;
				}
			}
			return null;
		}

		public List getConditionals() {
			OvertureAssignment unconditionalAssignment = getUnconditionalAssignment();
			List result = new ArrayList();
			for (Iterator iter = assignements.iterator(); iter.hasNext();) {
				VariableAssignment assign = (VariableAssignment) iter.next();
				if (unconditionalAssignment == null
						|| assign.assignment.sourceStart() > unconditionalAssignment
								.sourceStart())
					result.add(assign.assignment);

			}
			return result;

		}

	}

	private static OvertureAssignment findAssignments(String variableName,
			ASTNode scopeNode, int tillOffset, List conditionals) {
		LocalVariablesSearchVisitor visitor = new LocalVariablesSearchVisitor(
				variableName, scopeNode, tillOffset);
		try {
			scopeNode.traverse(visitor);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println();
		if (conditionals != null)
			conditionals.addAll(visitor.getConditionals());
		return visitor.getUnconditionalAssignment();
	}

	public static LocalVariableInfo inspectLocalVariable(ModuleDeclaration module, int offset, String name) {
		LocalVariableInfo info = new LocalVariableInfo();
		ASTNode[] scopes = getAllStaticScopes(module, offset);
		int i = -1;
		for (i = scopes.length - 1; i >= 0; i--) {
			ASTNode scope = scopes[i];
			if (scope instanceof TypeDeclaration) {
				info.setDeclaringScope(scope);
				List conditionals = new ArrayList();
				OvertureAssignment last = findAssignments(name, scope, offset,
						conditionals);
				info.setLastAssignment(last);
				info.setDeclaringScope(scope);
				info.setConditionalAssignments(conditionals);
				break;
			} else if (scope instanceof MethodDeclaration) {
				MethodDeclaration method = (MethodDeclaration) scope;
				boolean isArgument = false;
				List arguments = method.getArguments();
				for (Iterator iterator = arguments.iterator(); iterator.hasNext();) {
					OvertureMethodArgument arg = (OvertureMethodArgument) iterator.next();
					String argName = arg.getName();
					if (argName.equals(name)) {
						isArgument = true;
						break;
					}
				}
				if (isArgument
						&& (info.getKind() == LocalVariableInfo.KIND_DEFAULT))
					info.setKind(LocalVariableInfo.KIND_METHOD_ARG);
				List conditionals = new ArrayList();
				OvertureAssignment last = findAssignments(name, scope, offset, conditionals);
				info.setLastAssignment(last);
				info.setDeclaringScope(scope);
				info.setConditionalAssignments(conditionals);
				break;
			} else if (scope instanceof OvertureBlock) {
				boolean isArgument = false;
				OvertureBlock block = (OvertureBlock) scope;
				Set vars = block.getVars();
				for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
					ASTNode vnode = (ASTNode) iterator.next();
					if (vnode instanceof OvertureDAssgnExpression) {
						OvertureDAssgnExpression v = (OvertureDAssgnExpression) vnode;
						if (v.getName().equals(name)) {
							isArgument = true;
							break;
						}
					}
				}
				if (isArgument) {
					if (info.getKind() == LocalVariableInfo.KIND_DEFAULT)
						info.setKind(LocalVariableInfo.KIND_BLOCK_ARG);
					// List conditionals = new ArrayList();
					// RubyAssignment last = findAssignments(name, scope,
					// offset,
					// conditionals);
					// info.setLastAssignment(last);
					// info.setDeclaringScope(scope);
					// info.setConditionalAssignments(conditionals);
					// break;
				}
			} 
			/*else if (scope instanceof RubyForStatement2) {

				RubyForStatement2 forStatement = (RubyForStatement2) scope;
				ASTNode var = forStatement.getTarget();

				if (var instanceof OvertureAssignment) {

					OvertureAssignment rubyAssignment = (OvertureAssignment) var;
					if (rubyAssignment.getLeft() instanceof VariableReference) {
						VariableReference ref = (VariableReference) rubyAssignment
								.getLeft();
						if (ref.getName().equals(name)) {
							if (info.getKind() == LocalVariableInfo.KIND_DEFAULT)
								info.setKind(LocalVariableInfo.KIND_LOOP_VAR);
							// List conditionals = new ArrayList();
							// RubyAssignment last = findAssignments(name,
							// scope,
							// offset, conditionals);
							// info.setLastAssignment(last);
							// info.setDeclaringScope(scope);
							// info.setConditionalAssignments(conditionals);
							// break;
						}
					}
				}

			}*/
		}
		if (i < 0) {
			// consider the whole module
			List conditionals = new ArrayList();
			OvertureAssignment last = findAssignments(name, module, offset,
					conditionals);
			info.setLastAssignment(last);
			info.setDeclaringScope(module);
			info.setConditionalAssignments(conditionals);
		}
		return info;
	}

//	public static LocalVariableInfo searchLocalVars(ModuleDeclaration module, int offset, String name) {
//		ASTNode[] scopes = getAllStaticScopes(module, offset);
//		int i = -1;
//		loop: for (i = scopes.length - 1; i >= 0; i--) {
//			if (scopes[i] instanceof MethodDeclaration
//					|| scopes[i] instanceof TypeDeclaration) {
//				break;
//			} else if (scopes[i] instanceof OvertureBlock) {
//				OvertureBlock rubyBlock = (OvertureBlock) scopes[i];
//				Set vars = rubyBlock.getVars();
//				for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
//					ASTNode vnode = (ASTNode) iterator.next();
//					if (vnode instanceof OvertureDAssgnExpression) {
//						OvertureDAssgnExpression v = (OvertureDAssgnExpression) vnode;
//						if (v.getName().equals(name)) {
//							break loop;
//						}
//					}
//				}
//			} 
//			/*else if (scopes[i] instanceof RubyForStatement2) {
//				RubyForStatement2 forst = (RubyForStatement2) scopes[i];
//				ASTListNode vars = forst.getList();
//				for (Iterator iterator = vars.getChilds().iterator(); iterator
//						.hasNext();) {
//					ASTNode vnode = (ASTNode) iterator.next();
//					if (vnode instanceof OvertureAssignment) {
//						OvertureAssignment assign = (OvertureAssignment) vnode;
//						if (assign.getLeft() instanceof VariableReference) {
//							VariableReference ref = (VariableReference) assign
//									.getLeft();
//							if (ref.getName().equals(name))
//								break loop;
//						}
//					}
//				}
//			}*/
//		}
//		if (i < 0)
//			i = 0;
//		LocalVarSearchVisitor visitor = new LocalVarSearchVisitor(name, scopes[i], offset);
//		try {
//			scopes[i].traverse(visitor);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		List conds = visitor.getConditionalAssignments();
//		OvertureAssignment[] c = (OvertureAssignment[]) conds.toArray(new OvertureAssignment[conds.size()]);
//		return new LocalVariableInfo(scopes[i], c, visitor.getLast());
//
//	}

}
