/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallArgumentsList;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.NumericLiteral;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.references.ConstantReference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.codeassist.IAssistParser;
import org.eclipse.dltk.codeassist.ScriptCompletionEngine;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.Flags;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.evaluation.types.AmbiguousType;
import org.eclipse.dltk.evaluation.types.IClassType;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.util.WeakHashSet;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ti.BasicContext;
import org.eclipse.dltk.ti.DLTKTypeInferenceEngine;
import org.eclipse.dltk.ti.goals.ExpressionTypeGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.overturetool.eclipse.plugins.editor.core.OvertureKeywords;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.core.OverturePlugin;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ASTUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureBlock;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureDAssgnExpression;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureDVarExpression;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.ast.OvertureSelfReference;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.IMixinSearchRequestor;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.IOvertureMixinElement;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureClassType;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinClass;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinElementInfo;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinMethod;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinModel;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureMixinVariable;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureModelUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.OvertureTypeInferencingUtils;
import org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin.PrefixNoCaseMixinSearchPattern;
import org.overturetool.eclipse.plugins.editor.core.model.FakeField;
import org.overturetool.eclipse.plugins.editor.core.utils.OvertureSyntaxUtils;

public class OvertureCompletionEngine2 extends ScriptCompletionEngine {

	/**
	 * Type inferencer timeout
	 */
	private static final int TI_TIMEOUT = 2000;

	private final static int RELEVANCE_FREE_SPACE = 100000;

	/**
	 * Relevance for keywords. Should be used only for keywords - the distinct
	 * value is required to sort templates (their relevance is much lower by
	 * default) before the matching keywords - see
	 * ScriptCompletionProposalComputer#updateTemplateProposalRelevance()
	 */
	private final static int RELEVANCE_KEYWORD = 1000000;

	private final static int RELEVANCE_TYPE = 2000000;

	private final static int RELEVANCE_METHODS = 10000000;

	private final static int RELEVANCE_VARIABLES = 100000000;

	private DLTKTypeInferenceEngine inferencer;
	private ISourceParser parser = null;
	private OvertureMixinModel mixinModel;
	private HashSet completedNames = new HashSet();
	private WeakHashSet intresting = new WeakHashSet();

	private ASTNode completionNode;

	private ISourceModule currentModule;

	public OvertureCompletionEngine2() {
		this.inferencer = new DLTKTypeInferenceEngine();
		this.parser = DLTKLanguageManager.getSourceParser(OvertureNature.NATURE_ID);
	}

	protected int getEndOfEmptyToken() {
		return 0;
	}

	protected String processMethodName(IMethod method, String token) {
		return null;
	}

	protected String processTypeName(IType method, String token) {
		return null;
	}

	public IAssistParser getParser() {
		return null;
	}

//	private boolean afterColons(String content, int position) {
//		if (position < 2)
//			return false;
//		if (content.charAt(position - 1) == ':'
//				&& content.charAt(position - 2) == ':')
//			return true;
//		return false;
//	}
//
//	private boolean afterDollar(String content, int position) {
//		if (position < 1)
//			return false;
//		if (content.charAt(position - 1) == '$')
//			return true;
//		return false;
//	}
//
//	private boolean afterAt(String content, int position) {
//		if (position < 1)
//			return false;
//		if (content.charAt(position - 1) == '@')
//			return true;
//		return false;
//	}
//
//	private boolean afterAt2(String content, int position) {
//		if (position < 2)
//			return false;
//		if (content.charAt(position - 1) == '@'
//				&& content.charAt(position - 2) == '@')
//			return true;
//		return false;
//	}

	private boolean afterDot(String content, int position) {
		return position >= 1 && content.charAt(position - 1) == '.';
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
					|| OvertureSyntaxUtils.isLessStrictIdentifierCharacter(content.charAt(position - 1)))) 
		{
			--position;
			--maxLen;
		}
		return content.substring(position, original);
	}

	public void complete(org.eclipse.dltk.compiler.env.ISourceModule module, int position, int i) {
		this.currentModule = (ISourceModule) module;
		this.mixinModel = OvertureMixinModel.getInstance(currentModule.getScriptProject());
		completedNames.clear();
		
		this.actualCompletionPosition = position;
		this.requestor.beginReporting();
		try {
			final String content = module.getSourceContents();

			String wordStarting = getWordStarting(content, position, 10);

			if (wordStarting.length() != 0) {
				this.setSourceRange(position - wordStarting.length(), position);
				String[] keywords = OvertureKeywords.findByPrefix(wordStarting);
				for (int j = 0; j < keywords.length; j++) {
					reportKeyword(keywords[j]);
				}
			}

			ModuleDeclaration moduleDeclaration = parser.parse(module.getFileName(), content.toCharArray(), null);
			
			//completeSimpleRef(moduleDeclaration, "", position); //$NON-NLS-1$
			completeSimpleRef(moduleDeclaration, wordStarting, position); //$NON-NLS-1$
			

			ASTNode node = ASTUtils.findMaximalNodeEndingAt(moduleDeclaration, position - 2);
			this.setSourceRange(position, position);
			if (node != null) {
				BasicContext basicContext = new BasicContext(currentModule, moduleDeclaration);
				ExpressionTypeGoal goal = new ExpressionTypeGoal(basicContext, node);
				IEvaluatedType type = inferencer.evaluateType(goal, TI_TIMEOUT * 3 / 2);
				reportSubElements(type, Util.EMPTY_STRING);
			} else {
				completeConstant(moduleDeclaration, Util.EMPTY_STRING, position, true);
			}
			//} else {
				ASTNode minimalNode = ASTUtils.findMinimalNode(moduleDeclaration, position, position);
				if (minimalNode != null) {
					if (minimalNode instanceof CallExpression) {
						CallExpression callExp = (CallExpression) minimalNode;
						if ((position > 0)
								&& (content.charAt(position - 1) == ' ')) {
							minimalNode = callExp.getArgs();
						}
					}
					this.completionNode = minimalNode;
					if (minimalNode instanceof CallExpression) {
						completeCall(moduleDeclaration, (CallExpression) minimalNode, position);
					} else if (minimalNode instanceof CallArgumentsList) {
						completeSimpleRef(moduleDeclaration, wordStarting, position);

						IEvaluatedType self = OvertureTypeInferencingUtils.determineSelfClass(mixinModel, currentModule,
										moduleDeclaration, position);
						if ((self != null) && "Object".equals(self.getTypeName())) { //$NON-NLS-1$
							ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(currentModule, moduleDeclaration), minimalNode);
							IEvaluatedType self2 = inferencer.evaluateType(goal, TI_TIMEOUT);
							if (self2 != null) {
								self = self2;
							}
						}
						completeClassMethods(moduleDeclaration, self, Util.EMPTY_STRING, true);
					} else if (minimalNode instanceof ConstantReference) {
						completeConstant(moduleDeclaration, (ConstantReference) minimalNode, position);
					} else if (minimalNode instanceof SimpleReference) {
						completeSimpleRef(moduleDeclaration, wordStarting, position);
					} else if (minimalNode instanceof OvertureDVarExpression) {
						if (afterDot(content, position)) {
							completeClassMethods(moduleDeclaration, minimalNode, wordStarting);
						} else {
							completeSimpleRef(moduleDeclaration, wordStarting, position);
						}
					} else if (minimalNode instanceof MethodDeclaration
							|| minimalNode instanceof TypeDeclaration
							|| minimalNode instanceof StringLiteral) {
						completeSimpleRef(moduleDeclaration, wordStarting, position);

						IEvaluatedType self = OvertureTypeInferencingUtils.determineSelfClass(mixinModel, currentModule, moduleDeclaration, position);
						if ((self != null) && "Object".equals(self.getTypeName())) { //$NON-NLS-1$
							ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(currentModule, moduleDeclaration), minimalNode);
							IEvaluatedType self2 = inferencer.evaluateType( goal, TI_TIMEOUT);
							if (self2 != null) {
								self = self2;
							}
						}
						completeClassMethods(moduleDeclaration, self, wordStarting, true);
					} else if (minimalNode instanceof ModuleDeclaration) {
						ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(currentModule, moduleDeclaration), minimalNode);
						IEvaluatedType self = inferencer.evaluateType(goal, TI_TIMEOUT);
						completeClassMethods(moduleDeclaration, self, wordStarting, true);
					} else if (minimalNode instanceof NumericLiteral
							&& position > 0
							&& position == minimalNode.sourceEnd()
							&& position > minimalNode.sourceStart()
							&& content.charAt(position - 1) == '.') {
						setSourceRange(position, position);
						completeClassMethods(moduleDeclaration, minimalNode,
								Util.EMPTY_STRING);
					} else { // worst case
						completeSimpleRef(moduleDeclaration, wordStarting, position);

						if (wordStarting.length() == 0 && !requestor.isContextInformationMode()) {
							if (!afterContentAndSpace(moduleDeclaration, content, position)) {
								reportCurrentElements(moduleDeclaration, position);
							}
						}
					}
				}
			//}
		} finally {
			this.requestor.endReporting();
		}
	}

	/**
	 * @param content
	 * @param position
	 * @return
	 */
	private boolean afterContentAndSpace(ModuleDeclaration moduleDeclaration,
			String content, int position) {
		while (position > 0) {
			final char c = content.charAt(position - 1);
			if (c == ' ' || c == '\t') {
				--position;
			} else {
				break;
			}
		}
		if (position > 0
				&& OvertureSyntaxUtils.isIdentifierCharacter(content
						.charAt(position - 1))) {
			ASTNode node = ASTUtils.findMinimalNode(moduleDeclaration,
					position, position);
			if (node instanceof CallExpression) {
				int begin = position;
				while (begin > 0 && content.charAt(begin - 1) != '\r'
						&& content.charAt(begin - 1) != '\n') {
					--begin;
				}
				ASTNode[] way = ASTUtils.restoreWayToNode(moduleDeclaration,
						node);
				for (int i = way.length - 1; --i >= 0;) {
					if (way[i] instanceof CallExpression) {
						// if multiple method calls in the same line
						return way[i].sourceStart() >= begin;
					}
				}
				return false;
			}
			return true;
		}
		return false;
	}

	private void reportCurrentElements(ModuleDeclaration moduleDeclaration, int position) {
		setSourceRange(position, position);
		completeSimpleRef(moduleDeclaration, Util.EMPTY_STRING, position);
		IClassType self = OvertureTypeInferencingUtils.determineSelfClass(
				mixinModel, currentModule, moduleDeclaration, position);
		if (self == null) {
			return;
		}
		completeClassMethods(moduleDeclaration, self, Util.EMPTY_STRING, true);
		if ("Object".equals(self.getTypeName())) { //$NON-NLS-1$
			try {
				final IModelElement[] children = currentModule.getChildren();
				if (children != null) {
					for (int i = 0; i < children.length; ++i) {
						IModelElement element = children[i];
						if (element instanceof IField) {
							reportField((IField) element, RELEVANCE_FREE_SPACE);
						} else if (element instanceof IMethod) {
							IMethod method = (IMethod) element;
							if ((method.getFlags() & Modifiers.AccStatic) == 0) {
								reportMethod(method, RELEVANCE_FREE_SPACE);
							}
						} else if (element instanceof IType) {
							if (!element.getElementName().trim().startsWith(
									"<<")) //$NON-NLS-1$
								reportType((IType) element,
										RELEVANCE_FREE_SPACE);
						}
					}
				}
			} catch (ModelException e) {
				OverturePlugin.log(e);
			}
		}
	}

	private class CompletionMixinMethodRequestor implements IMixinSearchRequestor {
		private String lastParent = null;
		private boolean lastParentIsSuperClass = true;
		private final List group = new ArrayList();
		private final OvertureMixinClass klass;
		private final boolean isSelf;

		public CompletionMixinMethodRequestor(OvertureMixinClass klass, boolean isSelf) {
			this.klass = klass;
			this.isSelf = isSelf;
		}

		/**
		 * Tests that the specified key identifies superclass of the
		 * {@link #klass} or the same class.
		 * 
		 * @param key
		 * @return
		 */
		private boolean isSuperClass(String key) {
			if (!OvertureMixinUtils.isObjectOrKernel(key)) {
				if (key.equals(klass.getKey())) {
					return true;
				}
				OvertureMixinClass superclass = klass.getSuperclass();
				while (superclass != null) {
					final String superClassKey = superclass.getKey();
					if (OvertureMixinUtils.isObjectOrKernel(superClassKey)) {
						break;
					}
					if (key.equals(superClassKey)) {
						return true;
					}
					superclass = superclass.getSuperclass();
				}
			}
			return false;
		}

		public void acceptResult(IOvertureMixinElement element) {
			if (element instanceof OvertureMixinMethod) {
				final OvertureMixinMethod method = (OvertureMixinMethod) element;
				final OvertureMixinClass selfClass = method.getSelfType();
				if (lastParent == null
						|| !lastParent.equals(selfClass.getKey())) {
					if (lastParent != null) {
						this.flush();
					}
					lastParent = selfClass.getKey();
					lastParentIsSuperClass = isSuperClass(lastParent);
				}
				int flags = 0;
				final IMethod[] methods = method.getSourceMethods();
				for (int cnt = 0, max = methods.length; cnt < max; cnt++) {
					final IMethod m = methods[cnt];
					if (m != null) {
						try {
							flags |= m.getFlags();
						} catch (ModelException mxcn) {
							// ignore
						}
					}
				}

				final boolean shouldAdd = Flags.isPublic(flags)
						|| lastParentIsSuperClass && Flags.isProtected(flags)
						|| Flags.isPrivate(flags) && isSelf
						|| OvertureMixinUtils.isKernel(selfClass.getKey());
				if (shouldAdd)
					group.add(method);
			}
		}

		public void flush() {
			if (group.size() > 0) {
				OvertureMixinMethod[] mixinMethods = (OvertureMixinMethod[]) group.toArray(new OvertureMixinMethod[group.size()]);
				final List methods = OvertureModelUtils.getAllSourceMethods(
						mixinMethods, klass);
				int skew = 0;
				if (klass.getKey().equals(lastParent)) {
					skew = 2;
				} else if (lastParentIsSuperClass) {
					// TODO calculate the distance in the hierarchy
					skew = 1;
				}
				for (int j = 0, size = methods.size(); j < size; j++) {
					reportMethod((IMethod) methods.get(j), RELEVANCE_METHODS
							+ skew);
				}
				group.clear();
			}
		}
	}

	private void completeClassMethods(ModuleDeclaration moduleDeclaration, OvertureMixinClass overtureClass, String prefix, boolean isSelf) {
		CompletionMixinMethodRequestor mixinSearchRequestor = new CompletionMixinMethodRequestor(overtureClass, isSelf);
		overtureClass.findMethods(new PrefixNoCaseMixinSearchPattern(prefix), mixinSearchRequestor);
		mixinSearchRequestor.flush();
	}

	private void completeClassMethods(ModuleDeclaration moduleDeclaration, IEvaluatedType type, String prefix, boolean isSelf) {
		if (type instanceof OvertureClassType) {
			OvertureClassType overtureClassType = (OvertureClassType) type;
			OvertureMixinClass overtureClass = mixinModel.createOvertureClass(overtureClassType);
			if (overtureClass != null) {
				completeClassMethods(moduleDeclaration, overtureClass, prefix, isSelf);
			}

		} else if (type instanceof AmbiguousType) {
			AmbiguousType type2 = (AmbiguousType) type;
			IEvaluatedType[] possibleTypes = type2.getPossibleTypes();
			for (int i = 0; i < possibleTypes.length; i++) {
				completeClassMethods(moduleDeclaration, possibleTypes[i], prefix, isSelf);
			}
		}
	}

	private void completeClassMethods(ModuleDeclaration moduleDeclaration, ASTNode receiver, String pattern) {
		ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(currentModule, moduleDeclaration), receiver);
		IEvaluatedType type = inferencer.evaluateType(goal, TI_TIMEOUT);
		completeClassMethods(moduleDeclaration, type, pattern, receiver instanceof OvertureSelfReference);
	}

//	private void completeGlobalVar(ModuleDeclaration moduleDeclaration, String prefix, int position) {
//		this.setSourceRange(position - (prefix != null ? prefix.length() : 0), position);
//
//		IMixinElement[] elements = mixinModel.getRawModel().find((prefix != null ? prefix : Util.EMPTY_STRING) + "*"); //$NON-NLS-1$
//		// String[] findKeys = RubyMixinModel.getRawInstance().findKeys(
//		// prefix + "*");
//		for (int i = 0; i < elements.length; i++) {
//			IOvertureMixinElement overtureElement = mixinModel.createOvertureElement(elements[i]);
//			if (overtureElement instanceof OvertureMixinVariable) {
//				OvertureMixinVariable variable = (OvertureMixinVariable) overtureElement;
//				IField[] sourceFields = variable.getSourceFields();
//				for (int j = 0; j < sourceFields.length; j++) {
//					if (sourceFields[j] != null) {
//						reportField(sourceFields[j], RELEVANCE_VARIABLES);
//						break;
//					}
//				}
//			}
//		}
//
//		for (int i = 0; i < globalVars.length; i++) {
//			if (prefix == null || globalVars[i].startsWith(prefix))
//				reportField(new FakeField(currentModule, globalVars[i], 0, 0),
//						RELEVANCE_VARIABLES);
//		}
//	}

	private void completeSimpleRef(ModuleDeclaration moduleDeclaration, String prefix, int position) {
		this.setSourceRange(position - prefix.length(), position);
		if (this.completionNode != null){			
			ASTNode[] wayToNode = ASTUtils.restoreWayToNode(moduleDeclaration, this.completionNode);
			for (int i = wayToNode.length - 1; i > 0; i--) {
				if (wayToNode[i] instanceof OvertureBlock) {
					OvertureBlock overtureBlock = (OvertureBlock) wayToNode[i];
					Set vars = overtureBlock.getVars();
					for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
						ASTNode n = (ASTNode) iterator.next();
						if (n instanceof OvertureDAssgnExpression) {
							OvertureDAssgnExpression rd = (OvertureDAssgnExpression) n;
							if (rd.getName().startsWith(prefix)) {
								reportField(new FakeField(currentModule, rd.getName(), 0, 0), RELEVANCE_VARIABLES);
							}
						}
					}
				}
			}
		}

		IField[] fields = OvertureModelUtils.findFields(mixinModel, currentModule, moduleDeclaration, prefix, position);
		for (int i = 0; i < fields.length; i++) {
			reportField(fields[i], RELEVANCE_VARIABLES);
		}

	}

	private void reportSubElements(IEvaluatedType type, String prefix) {
		if (!(type instanceof OvertureClassType)) {
			return;
		}
		OvertureClassType overtureClassType = (OvertureClassType) type;
		IMixinElement mixinElement = mixinModel.getRawModel().get(
				overtureClassType.getModelKey());
		if (mixinElement == null) {
			return;
		}
		List types = new ArrayList();
		List methods = new ArrayList();
		List fields = new ArrayList();
		IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			Object[] infos = children[i].getAllObjects();
			for (int j = 0; j < infos.length; j++) {
				OvertureMixinElementInfo obj = (OvertureMixinElementInfo) infos[j];
				if (obj.getObject() == null)
					continue;
				if (obj.getKind() == OvertureMixinElementInfo.K_CLASS
						|| obj.getKind() == OvertureMixinElementInfo.K_MODULE) {
					IType type2 = (IType) obj.getObject();
					if (type2 != null
							&& (prefix == null || type2.getElementName()
									.startsWith(prefix))) {
						types.add(type2);
					}
				} else if (obj.getKind() == OvertureMixinElementInfo.K_METHOD) {
					IMethod method2 = (IMethod) obj.getObject();
					if (method2 != null
							&& (prefix == null || method2.getElementName()
									.startsWith(prefix))) {
						methods.add(method2);
					}
				}
				if (obj.getKind() == OvertureMixinElementInfo.K_VARIABLE) {
					IField fff = (IField) obj.getObject();
					if (fff != null
							&& (prefix == null || fff.getElementName()
									.startsWith(prefix))) {
						fields.add(fff);
					}
				}
				break;
			}
		}

		for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
			IField t = (IField) iterator.next();
			reportField(t, RELEVANCE_VARIABLES);
		}

		for (Iterator iterator = types.iterator(); iterator.hasNext();) {
			IType t = (IType) iterator.next();
			reportType(t, RELEVANCE_TYPE);
		}

		for (Iterator iterator = methods.iterator(); iterator.hasNext();) {
			IMethod t = (IMethod) iterator.next();
			reportMethod(t, RELEVANCE_METHODS);
		}

	}

//	private void completeColonExpression(ModuleDeclaration moduleDeclaration,
//			RubyColonExpression node, int position) {
//		String content;
//		try {
//			content = currentModule.getSource();
//		} catch (ModelException e) {
//			return;
//		}
//		int pos = (node.getLeft() != null) ? (node.getLeft().sourceEnd() + 2)
//				: (node.sourceStart());
//		String starting = null;
//		try {
//			starting = content.substring(pos, position).trim();
//		} catch (IndexOutOfBoundsException e) {
//			e.printStackTrace();
//			return;
//		}
//
//		if (starting.startsWith("::")) { //$NON-NLS-1$
//			this.setSourceRange(position - starting.length() + 2, position);
//			completeConstant(moduleDeclaration, starting.substring(2),
//					position, true);
//			return;
//		}
//
//		this.setSourceRange(position - starting.length(), position);
//
//		ExpressionTypeGoal goal = new ExpressionTypeGoal(new BasicContext(
//				currentModule, moduleDeclaration), node.getLeft());
//		IEvaluatedType type = inferencer.evaluateType(goal, TI_TIMEOUT * 3 / 2);
//		reportSubElements(type, starting);
//	}

	private void completeConstant(ModuleDeclaration moduleDeclaration, String prefix, int position, boolean topLevelOnly) {
		IType[] types = OvertureTypeInferencingUtils.getAllTypes(currentModule, prefix);
		Arrays.sort(types, new ProjectTypeComparator(currentModule));
		final Set names = new HashSet();
		for (int i = 0; i < types.length; i++) {
			final String elementName = types[i].getElementName();
			if (names.add(elementName)) {
				reportType(types[i], RELEVANCE_TYPE);
			}
		}

		if (!topLevelOnly) {
			IMixinElement[] modelStaticScopes = OvertureTypeInferencingUtils
					.getModelStaticScopes(mixinModel.getRawModel(),
							moduleDeclaration, position);
			for (int i = modelStaticScopes.length - 1; i >= 0; i--) {
				IMixinElement scope = modelStaticScopes[i];
				if (scope == null)
					continue;
				reportSubElements(new OvertureClassType(scope.getKey()), prefix);
			}
		}

		if (prefix != null && prefix.length() > 0) {
			String varkey = "Object" + MixinModel.SEPARATOR + prefix; //$NON-NLS-1$
			String[] keys2 = mixinModel.getRawModel().findKeys(varkey + "*"); //$NON-NLS-1$
			for (int i = 0; i < keys2.length; i++) {
				IOvertureMixinElement element = mixinModel
						.createOvertureElement(keys2[i]);
				if (element instanceof OvertureMixinVariable) {
					OvertureMixinVariable variable = (OvertureMixinVariable) element;
					IField[] sourceFields = variable.getSourceFields();
					for (int j = 0; j < sourceFields.length; j++) {
						if (sourceFields[j] != null) {
							reportField(sourceFields[j], RELEVANCE_VARIABLES);
							break;
						}
					}
				}
			}
		}
	}

	private void completeConstant(ModuleDeclaration moduleDeclaration, ConstantReference node, int position) {
		String content;
		try {
			content = currentModule.getSource();
		} catch (ModelException e) {
			return;
		}

		String prefix = content.substring(node.sourceStart(), position);
		this.setSourceRange(position - prefix.length(), position);
		completeConstant(moduleDeclaration, prefix, position, false);
	}

	private void completeCall(ModuleDeclaration moduleDeclaration, CallExpression node, int position) {
		ASTNode receiver = node.getReceiver();

		String content;
		try {
			content = currentModule.getSource();
		} catch (ModelException e) {
			return;
		}

		int pos = (receiver != null) ? (receiver.sourceEnd() == node
				.sourceStart()) ? receiver.sourceStart() : (receiver
				.sourceEnd() + 1) : (node.sourceStart());

		for (int t = 0; t < 2; t++) { // correct not more 2 chars
			if (pos < position
					&& !OvertureSyntaxUtils.isStrictIdentifierCharacter(content
							.charAt(pos))) // for (...).name and Foo::name
				// calls
				pos++;
		}

		String starting = content.substring(pos, position).trim();

		if ((receiver == null) || (receiver.sourceEnd() == node.sourceStart())) {
			completeSimpleRef(moduleDeclaration, starting, position);
			completeConstant(moduleDeclaration, starting, position, false);
		}

		this.setSourceRange(position - starting.length(), position);

		if (starting.startsWith("__")) { //$NON-NLS-1$
			String[] keywords = OvertureKeywords.findByPrefix("__"); //$NON-NLS-1$
			for (int j = 0; j < keywords.length; j++) {
				reportKeyword(keywords[j]);
			}
		}

		if ((receiver != null) && (receiver.sourceEnd() != node.sourceStart())) {
			completeClassMethods(moduleDeclaration, receiver, starting);
		} else {
			IEvaluatedType self = OvertureTypeInferencingUtils.determineSelfClass(
					mixinModel, currentModule, moduleDeclaration, position);
			if ((self != null) && "Object".equals(self.getTypeName())) { //$NON-NLS-1$
				ASTNode minNode = node;
				ASTNode[] wayToNode = ASTUtils.restoreWayToNode(
						moduleDeclaration, node);
				for (int cnt = (wayToNode.length - 2); cnt >= 0; cnt--) {
					if ((wayToNode[cnt] instanceof TypeDeclaration)
							|| (wayToNode[cnt] instanceof ModuleDeclaration)) {
						minNode = wayToNode[cnt];

						break;
					}
				}
				ExpressionTypeGoal goal = new ExpressionTypeGoal(
						new BasicContext(currentModule, moduleDeclaration),
						minNode);
				IEvaluatedType self2 = inferencer
						.evaluateType(goal, TI_TIMEOUT);
				if (self2 != null) {
					self = self2;
				}
			}
			completeClassMethods(moduleDeclaration, self, starting, true);
		}

	}

	protected String processFieldName(IField field, String token) {
		return field.getElementName();
	}

	private void reportMethod(IMethod method, int rel) {
		this.intresting.add(method);
		String elementName = method.getElementName();
		if (completedNames.contains(elementName)) {
			return;
		}
		completedNames.add(elementName);
		if (elementName.indexOf('.') != -1) {
			elementName = elementName.substring(elementName.indexOf('.') + 1);
		}
		char[] name = elementName.toCharArray();
		char[] compl = name;

		// accept result
		noProposal = false;
		if (!requestor.isIgnored(CompletionProposal.METHOD_DECLARATION)) {
			CompletionProposal proposal = createProposal(
					CompletionProposal.METHOD_DECLARATION,
					actualCompletionPosition);

			String[] params = null;
			try {
				params = method.getParameters();
			} catch (ModelException e) {
				// ssanders: Ignore
			}

			if (params != null && params.length > 0) {
				char[][] args = new char[params.length][];
				for (int i = 0; i < params.length; ++i) {
					args[i] = params[i].toCharArray();
				}
				proposal.setParameterNames(args);
			}

			proposal.setModelElement(method);
			proposal.setName(name);
			proposal.setCompletion(compl);
			try {
				proposal.setFlags(method.getFlags());
			} catch (ModelException e) {
				// ssanders: Ignore
			}
			proposal.setReplaceRange(this.startPosition - this.offset,
					this.endPosition - this.offset);
			proposal.setRelevance(rel);
			this.requestor.accept(proposal);
			if (DEBUG) {
				this.printDebug(proposal);
			}
		}

	}

	private void reportType(IType type, int rel) {
		this.intresting.add(type);
		String elementName = type.getElementName();
		if (completedNames.contains(elementName)) {
			return;
		}
		completedNames.add(elementName);
		char[] name = elementName.toCharArray();
		if (name.length == 0)
			return;

		// accept result
		noProposal = false;
		if (!requestor.isIgnored(CompletionProposal.TYPE_REF)) {
			CompletionProposal proposal = createProposal(
					CompletionProposal.TYPE_REF, actualCompletionPosition);

			proposal.setModelElement(type);
			proposal.setName(name);
			proposal.setCompletion(elementName.toCharArray());
			// proposal.setFlags(Flags.AccDefault);
			try {
				proposal.setFlags(type.getFlags());
			} catch (ModelException e) {
			}
			proposal.setReplaceRange(this.startPosition - this.offset,
					this.endPosition - this.offset);
			proposal.setRelevance(rel);
			this.requestor.accept(proposal);
			if (DEBUG) {
				this.printDebug(proposal);
			}
		}

	}

	private void reportField(IField field, int rel) {
		this.intresting.add(field);
		String elementName = field.getElementName();
		if (completedNames.contains(elementName)) {
			return;
		}
		completedNames.add(elementName);
		char[] name = elementName.toCharArray();
		if (name.length == 0)
			return;

		// accept result
		noProposal = false;
		if (!requestor.isIgnored(CompletionProposal.FIELD_REF)) {
			CompletionProposal proposal = createProposal(
					CompletionProposal.FIELD_REF, actualCompletionPosition);

			proposal.setModelElement(field);
			proposal.setName(name);
			proposal.setCompletion(elementName.toCharArray());
			// proposal.setFlags(Flags.AccDefault);
			proposal.setReplaceRange(this.startPosition - this.offset,
					this.endPosition - this.offset);
			proposal.setRelevance(rel);
			this.requestor.accept(proposal);
			if (DEBUG) {
				this.printDebug(proposal);
			}
		}

	}

	private void reportKeyword(String name) {
		// accept result
		noProposal = false;
		if (!requestor.isIgnored(CompletionProposal.KEYWORD)) {
			CompletionProposal proposal = createProposal(
					CompletionProposal.KEYWORD, actualCompletionPosition);

			proposal.setName(name.toCharArray());
			proposal.setCompletion(name.toCharArray());
			// proposal.setFlags(Flags.AccDefault);
			proposal.setReplaceRange(this.startPosition - this.offset,
					this.endPosition - this.offset);
			proposal.setRelevance(RELEVANCE_KEYWORD);
			this.requestor.accept(proposal);
			if (DEBUG) {
				this.printDebug(proposal);
			}
		}

	}

}
