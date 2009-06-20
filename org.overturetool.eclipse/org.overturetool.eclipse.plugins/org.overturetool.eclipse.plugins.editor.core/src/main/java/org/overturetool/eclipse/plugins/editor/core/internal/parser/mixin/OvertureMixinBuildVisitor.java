/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.expressions.CallExpression;
import org.eclipse.dltk.ast.expressions.StringLiteral;
import org.eclipse.dltk.ast.references.ConstantReference;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.VariableReference;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.mixin.IMixinRequestor;
import org.eclipse.dltk.core.mixin.MixinModel;
import org.eclipse.dltk.core.mixin.IMixinRequestor.ElementInfo;
import org.eclipse.dltk.internal.core.ModelElement;

public class OvertureMixinBuildVisitor extends ASTVisitor {

	private static final String INSTANCE_SUFFIX = OvertureMixin.INSTANCE_SUFFIX;
	private static final String VIRTUAL_SUFFIX = OvertureMixin.VIRTUAL_SUFFIX;
	private static final String SEPARATOR = MixinModel.SEPARATOR;

	private final ISourceModule sourceModule;
	private final boolean moduleAvailable;
	private final IMixinRequestor requestor;
	private final HashSet allReportedKeys = new HashSet();

	private abstract class Scope {
		private final ASTNode node;

		public Scope(ASTNode node) {
			super();
			this.node = node;
		}

		public ASTNode getNode() {
			return node;
		}

		public abstract String reportMethod(String name, IMethod object);

		public abstract String reportVariable(String name, IField object);

		public abstract String reportType(String name, IType object,
				boolean module);

		public abstract String reportInclude(String object);

		public abstract String reportExtend(String object);

		public abstract String getClassKey();

		public abstract String getKey();
	}

	private class SourceModuleScope extends Scope {

		public SourceModuleScope(ModuleDeclaration node) {
			super(node);
		}

		public String getClassKey() {
			return "Object"; //$NON-NLS-1$
		}
		
		private boolean isObjectReported = false;

		public String reportMethod(String name, IMethod object) {
			if (!isObjectReported) {
				report(getClassKey() + INSTANCE_SUFFIX, null);
				isObjectReported = true;
			}
			return report(getClassKey() + INSTANCE_SUFFIX + SEPARATOR + name,
					OvertureMixinElementInfo.createMethod(object));
		}

		public String reportType(String name, IType object, boolean module) {
			OvertureMixinElementInfo obj = null;
			if (module)
				obj = OvertureMixinElementInfo.createModule(object);
			else
				obj = OvertureMixinElementInfo.createClass(object);
			report(name + INSTANCE_SUFFIX, obj);
			return report(name, obj);
		}

		public String reportVariable(String name, IField object) {
			OvertureMixinElementInfo info = (name.endsWith(VIRTUAL_SUFFIX)) ? OvertureMixinElementInfo
					.createVirtualClass()
					: OvertureMixinElementInfo.createVariable(object);
			if (name.startsWith("$")) //$NON-NLS-1$
				return report(name, info);
			if (name.startsWith("@") || Character.isUpperCase(name.charAt(0))) //$NON-NLS-1$
				return report("Object" + SEPARATOR + name, info); //$NON-NLS-1$
			else {
				if (info.getKind() == OvertureMixinElementInfo.K_VIRTUAL)
					return report(name, info);
				return name; // no top-level vars
			}
		}

		public String getKey() {
			return "Object"; //$NON-NLS-1$
		}

		public String reportInclude(String object) {
			return report(getClassKey() + INSTANCE_SUFFIX,
					new OvertureMixinElementInfo(OvertureMixinElementInfo.K_INCLUDE,
							object));
		}

		public String reportExtend(String object) {
			return null;
		}

	}

	private class ClassScope extends Scope {

		private final String classKey;

		public ClassScope(ASTNode node, String classKey) {
			super(node);
			this.classKey = classKey;
		}

		public String reportMethod(String name, IMethod object) {
			String key = classKey + INSTANCE_SUFFIX + SEPARATOR + name;
			return report(key, OvertureMixinElementInfo.createMethod(object));
		}

		public String reportType(String name, IType obj, boolean module) {
			OvertureMixinElementInfo object = null;
			if (module)
				object = OvertureMixinElementInfo.createModule(obj);
			else
				object = OvertureMixinElementInfo.createClass(obj);
			String key = classKey + SEPARATOR + name;
			report(key + INSTANCE_SUFFIX, object);
			return report(key, object);
		}

		public String reportVariable(String name, IField object) {
			OvertureMixinElementInfo info = (name.endsWith(VIRTUAL_SUFFIX)) ? OvertureMixinElementInfo
					.createVirtualClass()
					: OvertureMixinElementInfo.createVariable(object);
			if (name.startsWith("$")) //$NON-NLS-1$
				return report(name, info);
			OvertureMixinElementInfo obj = info;
			String key = null;
			if (name.startsWith("@@")) { //$NON-NLS-1$
				key = classKey + SEPARATOR + name;
				report(classKey + INSTANCE_SUFFIX + SEPARATOR + name, obj);
				return report(key, obj);
			} else if (name.startsWith("@")) { //$NON-NLS-1$
				key = classKey + SEPARATOR + name;
				return report(key, obj);
			} else {
				key = classKey + SEPARATOR + name;
				return report(key, obj);
			}
		}

		public String getClassKey() {
			return classKey;
		}

		public String getKey() {
			return classKey;
		}

		public String reportInclude(String object) {
			return report(classKey + INSTANCE_SUFFIX, new OvertureMixinElementInfo(
					OvertureMixinElementInfo.K_INCLUDE, object));
		}

		public String reportExtend(String object) {
			return report(classKey + INSTANCE_SUFFIX, new OvertureMixinElementInfo(
					OvertureMixinElementInfo.K_EXTEND, object));
		}

	}

	private class MetaClassScope extends Scope {

		private final String classKey;

		public MetaClassScope(ASTNode node, String classKey) {
			super(node);
			this.classKey = classKey;
		}

		public String reportMethod(String name, IMethod object) {
			return report(classKey + SEPARATOR + name, OvertureMixinElementInfo
					.createMethod(object));
		}

		public String reportType(String name, IType object, boolean module) {
			OvertureMixinElementInfo obj = null;
			if (module)
				obj = OvertureMixinElementInfo.createModule(object);
			else
				obj = OvertureMixinElementInfo.createClass(object);
			report(classKey + SEPARATOR + name + INSTANCE_SUFFIX, obj);
			return report(classKey + SEPARATOR + name, obj);
		}

		public String reportVariable(String name, IField object) {
			OvertureMixinElementInfo info = (name.endsWith(VIRTUAL_SUFFIX)) ? OvertureMixinElementInfo
					.createVirtualClass()
					: OvertureMixinElementInfo.createVariable(object);
			if (name.startsWith("$")) //$NON-NLS-1$
				return report(name, info);
			OvertureMixinElementInfo obj = info;
			if (name.startsWith("@@")) { //$NON-NLS-1$
				report(classKey + INSTANCE_SUFFIX + SEPARATOR + name, obj);
				return report(classKey + SEPARATOR + name, obj);
			} else {
				return report(classKey + SEPARATOR + name, obj);
			}
		}

		public String getClassKey() {
			return classKey;
		}

		public String getKey() {
			return classKey;
		}

		public String reportInclude(String object) {
			return report(classKey, new OvertureMixinElementInfo(
					OvertureMixinElementInfo.K_INCLUDE, object));
		}

		public String reportExtend(String object) {
			return report(classKey, new OvertureMixinElementInfo(
					OvertureMixinElementInfo.K_EXTEND, object));
		}

	}

	private class MethodScope extends Scope {

		private final Scope classScope;
		private final String methodKey;

		public MethodScope(ASTNode node, Scope classScope, String methodKey) {
			super(node);
			this.classScope = classScope;
			this.methodKey = methodKey;
		}

		public String reportMethod(String name, IMethod object) {
			return classScope.reportMethod(name, object);
		}

		public String reportType(String name, IType obj, boolean module) {
			// throw new RuntimeException();
			return null;
		}

		public String reportVariable(String name, IField obj) {
			OvertureMixinElementInfo info = (name.endsWith(VIRTUAL_SUFFIX)) ? OvertureMixinElementInfo
					.createVirtualClass()
					: OvertureMixinElementInfo.createVariable(obj);
			if (name.startsWith("$")) //$NON-NLS-1$
				return report(name, info);
			OvertureMixinElementInfo object = info;
			if (name.startsWith("@@")) { //$NON-NLS-1$
				String key = classScope.getKey() + SEPARATOR + name;
				report(
						classScope.getKey() + INSTANCE_SUFFIX + SEPARATOR
								+ name, object);
				return report(key, object);
			}
			if (name.startsWith("@")) { //$NON-NLS-1$
				String key;
				if (classScope instanceof ClassScope) {
					key = classScope.getKey() + INSTANCE_SUFFIX + SEPARATOR
							+ name;
				} else {
					key = classScope.getKey() + SEPARATOR + name;
				}
				return report(key, object);
			} else {
				return report(methodKey + SEPARATOR + name, object);
			}
		}

		public String getClassKey() {
			return classScope.getClassKey();
		}

		public String getKey() {
			return methodKey;
		}

		public String reportInclude(String object) {
			return classScope.reportInclude(object);
		}

		public String reportExtend(String object) {
			return classScope.reportExtend(object);
		}

	}

	private Stack scopes = new Stack();
	private final ModuleDeclaration module;

	public OvertureMixinBuildVisitor(ModuleDeclaration module,
			ISourceModule sourceModule, boolean moduleAvailable,
			IMixinRequestor requestor) {
		super();
		this.module = module;
		this.sourceModule = sourceModule;
		this.moduleAvailable = moduleAvailable;
		this.requestor = requestor;
	}

	private Scope peekScope() {
		return (Scope) scopes.peek();
	}

	public boolean visit(ModuleDeclaration s) throws Exception {
		this.scopes.add(new SourceModuleScope(s));
		return true;
	}

	public boolean visit(MethodDeclaration decl) throws Exception {
		IMethod obj = null;
		String name = decl.getName();
		if (moduleAvailable) {
			IModelElement element = findModelElementFor(decl);
			if (element instanceof IMethod) {
				obj = (IMethod) element;
			} else {
				return false;
			}
		}
////		if (decl instanceof RubySingletonMethodDeclaration) {
////			RubySingletonMethodDeclaration singl = (RubySingletonMethodDeclaration) decl;
////			ASTNode receiver = singl.getReceiver();
////			if (receiver instanceof RubySelfReference) {
////				Scope scope = peekScope();
////				MetaClassScope metaScope = new MetaClassScope(scope.getNode(),
////						scope.getClassKey());
////				String method = metaScope.reportMethod(name, obj);
////				scopes.push(new MethodScope(decl, metaScope, method));
////			} else if (receiver instanceof ConstantReference) {
////				String evaluatedClassKey = evaluateClassKey(receiver);
////				if (evaluatedClassKey != null) {
////					MetaClassScope metaScope = new MetaClassScope(decl,
////							evaluatedClassKey);
////					String method = metaScope.reportMethod(name, obj);
////					scopes.push(new MethodScope(decl, metaScope, method));
////				}
////			} else {
////				// TODO
////			}
//		} else {
			Scope scope = peekScope();
			String method = scope.reportMethod(name, obj);
			scopes.push(new MethodScope(decl, scope, method));
//		}
		return true;
	}

	private IModelElement findModelElementFor(ASTNode decl)
			throws ModelException {
		// return null;
		return sourceModule.getElementAt(decl.sourceStart() + 1);
	}

	public boolean visitGeneral(ASTNode s) throws Exception {
//		if (s instanceof RubyMethodArgument) {
//			RubyMethodArgument argument = (RubyMethodArgument) s;
//			String name = argument.getName();
//			Scope scope = peekScope();
//			IField obj = null;
//			if (sourceModule != null) {
//				obj = new FakeField(sourceModule, name, s.sourceStart(), s
//						.sourceEnd()
//						- s.sourceStart());
//			}
//			scope.reportVariable(name, obj);
//		} else if (s instanceof RubyAssignment) {
//			RubyAssignment assignment = (RubyAssignment) s;
//			ASTNode left = assignment.getLeft();
//			if (left instanceof VariableReference) {
//				VariableReference ref = (VariableReference) left;
//				String name = ref.getName();
//				Scope scope = peekScope();
//				IField obj = null;
//				if (sourceModule != null)
//					obj = new FakeField(sourceModule, name, ref.sourceStart(),
//							ref.sourceEnd() - ref.sourceStart());
//				scope.reportVariable(name, obj);
//			}
//		} else if (s instanceof CallExpression) {
//			visit((CallExpression) s);
//		} else if (s instanceof RubyConstantDeclaration) {
//			RubyConstantDeclaration constantDeclaration = (RubyConstantDeclaration) s;
//			SimpleReference name2 = constantDeclaration.getName();
//			String name = name2.getName();
//			boolean closeScope = false;
//			if (constantDeclaration.getPath() instanceof RubyColonExpression) {
//				RubyColonExpression colon = (RubyColonExpression) constantDeclaration
//						.getPath();
//				String classKey = evaluateClassKey(colon.getLeft());
//				if (classKey != null) {
//					this.scopes.add(new ClassScope(colon, classKey));
//					closeScope = true;
//				}
//			}
//			Scope scope = peekScope();
//			IField obj = null;
//			if (sourceModule != null)
//				obj = new FakeField(sourceModule, name, name2.sourceStart(),
//						name2.sourceEnd() - name2.sourceStart(),
//						Modifiers.AccConstant);
//			scope.reportVariable(name, obj);
//			if (closeScope)
//				this.scopes.pop();
//		} else if (s instanceof RubyAliasExpression) {
//			RubyAliasExpression alias = (RubyAliasExpression) s;
//			String oldValue = alias.getOldValue();
//			if (!oldValue.startsWith("$")) { //$NON-NLS-1$
//				String newValue = alias.getNewValue();
//				String nkey = peekScope().reportMethod(newValue, null);
//				report(nkey, new OvertureMixinElementInfo(
//						OvertureMixinElementInfo.K_ALIAS, alias));
//			}
//		}
		return true;
	}

	public boolean visit(CallExpression call) throws Exception {
//		if (call.getReceiver() == null
//				&& call.getName().equals("include") && call.getArgs().getChilds().size() > 0) { //$NON-NLS-1$
//			ASTNode expr = (ASTNode) call.getArgs().getChilds().get(0);
//			if (expr instanceof RubyCallArgument)
//				expr = ((RubyCallArgument) expr).getValue();
//			Scope scope = peekScope();
//			String incl = evaluateClassKey(expr);
//			if (incl != null) {
//				// ssanders - Try to account for unqualified references,
//				// see 'include Assertions' in 'lib/ruby/1.8/test/unit/testcase.rb'
//				if (scope.getClassKey().indexOf(SEPARATOR) != -1) {
//					scope.reportInclude(scope.getClassKey().substring(0,
//							scope.getClassKey().lastIndexOf(SEPARATOR) + 1)
//							+ incl);
//				}
//				scope.reportInclude(incl);
//			}
//			return false;
//		} else if (call.getReceiver() == null
//				&& call.getName().equals("extend") //$NON-NLS-1$
//				&& call.getArgs().getChilds().size() > 0) {
//			ASTNode expr = (ASTNode) call.getArgs().getChilds().get(0);
//			if (expr instanceof RubyCallArgument)
//				expr = ((RubyCallArgument) expr).getValue();
//			scopes.push(new MetaClassScope(call, peekScope().getClassKey()));
//			Scope scope = peekScope();
//			String ext = evaluateClassKey(expr);
//			if (ext != null) {
//				// ssanders - Try to account for unqualified references
//				if (scope.getClassKey().indexOf(SEPARATOR) != -1) {
//					scope.reportExtend(scope.getClassKey().substring(0,
//							scope.getClassKey().lastIndexOf(SEPARATOR) + 1)
//							+ ext);
//				}
//				scope.reportExtend(ext);
//			}
//			scopes.pop();
//			return false;
//		} else if (RubyAttributeHandler.isAttributeCreationCall(call)
//				&& sourceModule != null) {
//			Scope scope = peekScope();
//			final Scope metaScope;
//			if (RubyAttributeHandler.isMetaAttributeCreationCall(call)) {
//				metaScope = new MetaClassScope(call, scope.getClassKey());
//			} else {
//				metaScope = null;
//			}
//			RubyAttributeHandler info = new RubyAttributeHandler(call);
//			List readers = info.getReaders();
//			for (Iterator iterator = readers.iterator(); iterator.hasNext();) {
//				ASTNode n = (ASTNode) iterator.next();
//				String attr = RubyAttributeHandler.getText(n);
//				if (attr == null)
//					continue;
//				FakeMethod fakeMethod = new FakeMethod(
//						(ModelElement) sourceModule, attr, n.sourceStart(),
//						attr.length(), n.sourceStart(), attr.length());
//				fakeMethod.setFlags(Modifiers.AccPublic);
//				scope.reportMethod(attr, fakeMethod);
//				if (metaScope != null) {
//					scopes.push(metaScope);
//					metaScope.reportMethod(attr, fakeMethod);
//					scopes.pop();
//				}
//			}
//			List writers = info.getWriters();
//			for (Iterator iterator = writers.iterator(); iterator.hasNext();) {
//				ASTNode n = (ASTNode) iterator.next();
//				String attr = RubyAttributeHandler.getText(n);
//				if (attr == null)
//					continue;
//				FakeMethod fakeMethod = new FakeMethod(
//						(ModelElement) sourceModule, attr + "=", n //$NON-NLS-1$
//								.sourceStart(), attr.length(), n.sourceStart(),
//						attr.length());
//				fakeMethod.setFlags(Modifiers.AccPublic);
//				fakeMethod.setParameters(new String[] { attr });
//				scope.reportMethod(attr + "=", fakeMethod); //$NON-NLS-1$
//				if (metaScope != null) {
//					scopes.push(metaScope);
//					metaScope.reportMethod(attr + "=", fakeMethod); //$NON-NLS-1$
//					scopes.pop();
//				}
//			}
//			return false;
//		} else if (call.getReceiver() == null
//				&& call.getName().equals("delegate") //$NON-NLS-1$
//				&& call.getArgs().getChilds().size() > 0) {
//			RubyCallArgument argNode;
//			String name;
//			for (Iterator iterator = call.getArgs().getChilds().iterator(); iterator.hasNext(); ) {
//				argNode = (RubyCallArgument)iterator.next();
//				name = null;
//				if (argNode.getValue() instanceof RubySymbolReference) {
//					name = ((RubySymbolReference)argNode.getValue()).getName();
//				}
//				else if (argNode.getValue() instanceof StringLiteral) {
//					name = ((StringLiteral)argNode.getValue()).getValue();
//				}
//				if (name != null) {
//					FakeMethod fakeMethod = new FakeMethod(
//							(ModelElement) sourceModule, name, argNode.sourceStart(),
//							name.length(), argNode.sourceStart(), name.length());
//					fakeMethod.setFlags(Modifiers.AccPublic);
//					peekScope().reportMethod(name, fakeMethod);
//				}
//			}
//			return false;
//		}
		return true;
	}

	public boolean visit(TypeDeclaration decl) throws Exception {
		IType obj = null;
		if (moduleAvailable) {
			IModelElement elementFor = findModelElementFor(decl);
			if (!(elementFor instanceof IType)) {
				elementFor = findModelElementFor(decl);
			}
			// mhowe - sometimes this the model element is a SourceMethod, so
			// get it's declaring type
			if (elementFor instanceof IMethod) {
				elementFor = ((IMethod) elementFor).getDeclaringType();
			}
			obj = (IType) elementFor;
		}
		boolean module = (decl.getModifiers() & Modifiers.AccModule) != 0;
//		if (decl instanceof RubySingletonClassDeclaration) {
//			RubySingletonClassDeclaration declaration = (RubySingletonClassDeclaration) decl;
//			ASTNode receiver = declaration.getReceiver();
//			if (receiver instanceof RubySelfReference) {
//				Scope scope = peekScope();
//				scopes.push(new MetaClassScope(decl, scope.getClassKey()));
//				return true;
//			} else if (receiver instanceof ConstantReference
//					|| receiver instanceof RubyColonExpression) {
//				String evaluatedClassKey = evaluateClassKey(receiver);
//				if (evaluatedClassKey != null) {
//					MetaClassScope metaScope = new MetaClassScope(decl,
//							evaluatedClassKey);
//					scopes.push(metaScope);
//					return true;
//				}
//			} else if (receiver instanceof VariableReference) {
//				VariableReference ref = (VariableReference) receiver;
//				Scope scope = peekScope();
//				String key = scope.reportVariable(ref.getName(), null);
//				if (key != null) {
//					key += VIRTUAL_SUFFIX;
//					report(key, new OvertureMixinElementInfo(
//							OvertureMixinElementInfo.K_VIRTUAL, obj));
//					scopes.push(new MetaClassScope(decl, key));
//					return true;
//				}
//			} else {
//				// TODO: add common method for singletons resolving
//			}
//		} else if (decl instanceof RubyClassDeclaration) {
//			RubyClassDeclaration declaration = (RubyClassDeclaration) decl;
//			ASTNode className = declaration.getClassName();
//			if (className instanceof ConstantReference) {
//				String name = ((ConstantReference) className).getName();
//				Scope scope = peekScope();
//				String newKey = scope.reportType(name, obj, module);
//				scopes.push(new ClassScope(decl, newKey));
//			} else {
//				String name = evaluateClassKey(className);
//				if (name != null) {
//					report(name, OvertureMixinElementInfo.createClass(obj));
//					scopes.push(new ClassScope(decl, name));
//				}
//			}
//			ASTListNode superClasses = declaration.getSuperClasses();
//			if (superClasses != null && superClasses.getChilds().size() == 1) {
//				ASTNode s = (ASTNode) superClasses.getChilds().get(0);
//				// if (this.sourceModule != null) {
//				SuperclassReferenceInfo ref = new SuperclassReferenceInfo(s,
//						this.module, sourceModule);
//				Scope scope = peekScope();
//				report(scope.getKey() + INSTANCE_SUFFIX,
//						new OvertureMixinElementInfo(OvertureMixinElementInfo.K_SUPER,
//								ref));
//				report(scope.getKey(), new OvertureMixinElementInfo(
//						OvertureMixinElementInfo.K_SUPER, ref));
//				// }
//			}
//			return true;
//		} else {
//			String name = decl.getName();
//			Scope scope = peekScope();
//			String newKey = scope.reportType(name, obj, module);
//			scopes.push(new ClassScope(decl, newKey));
//			return true;
//		}
		return false;
	}

	private String report(String key, OvertureMixinElementInfo object) {
		OvertureMixinModel.clearKeysCache(key);
		if (requestor != null) {
			ElementInfo info = new IMixinRequestor.ElementInfo();
			info.key = key;
			info.object = object;
			requestor.reportElement(info);
			// System.out.println("Mixin reported: " + key);
		}
		allReportedKeys.add(key);
		return key;
	}

	private String evaluateClassKey(ASTNode expr) {
		if (expr instanceof ConstantReference) {
			ConstantReference constantReference = (ConstantReference) expr;
			// simple heuristic
			int size = this.scopes.size();
			for (int i = size - 1; i >= 0; i--) {
				String possibleKey = ""; //$NON-NLS-1$
				if (i > 0) {
					Scope s = (Scope) this.scopes.get(i);
					possibleKey = s.getKey() + SEPARATOR
							+ constantReference.getName();
				} else
					possibleKey = constantReference.getName();
				if (this.allReportedKeys.contains(possibleKey))
					return possibleKey;
			}

			return constantReference.getName();
		}
		return null;
	}

	public void endvisitGeneral(ASTNode node) throws Exception {
		Scope scope = (Scope) scopes.peek();
		if (scope.getNode() == node) {
			scopes.pop();
		}
		super.endvisitGeneral(node);
	}

	public static String[] restoreScopesByNodes(ASTNode[] nodes) {
		Assert.isNotNull(nodes);
		Assert.isLegal(nodes.length > 0);
		String[] keys = new String[nodes.length];
		OvertureMixinBuildVisitor visitor = new OvertureMixinBuildVisitor(
				(ModuleDeclaration) nodes[0], null, false, null);
		for (int i = 0; i < nodes.length; i++) {
			try {
				if (nodes[i] instanceof ModuleDeclaration) {
					visitor.visit((ModuleDeclaration) nodes[i]);
				} else if (nodes[i] instanceof TypeDeclaration) {
					visitor.visit((TypeDeclaration) nodes[i]);
				} else if (nodes[i] instanceof MethodDeclaration) {
					visitor.visit((MethodDeclaration) nodes[i]);
				} else {
					visitor.visit(nodes[i]);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			Scope scope = visitor.peekScope();
			keys[i] = scope.getKey();
		}
		return keys;
	}

}
