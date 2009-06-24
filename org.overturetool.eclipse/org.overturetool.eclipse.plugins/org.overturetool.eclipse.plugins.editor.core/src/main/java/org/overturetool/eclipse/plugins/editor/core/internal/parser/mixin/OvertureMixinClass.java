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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;

public class OvertureMixinClass implements IOvertureMixinElement {

	private final String key;
	protected final OvertureMixinModel model;
	private final boolean module;

	public OvertureMixinClass(OvertureMixinModel model, String key, boolean module) {
		super();
		this.model = model;
		this.key = key;
		this.module = module;
	}

	public String getKey() {
		return key;
	}

	public boolean isModule() {
		return module;
	}

	public OvertureMixinClass getInstanceClass() {
		if (!isMeta())
			return this;
		String newkey = key + OvertureMixin.INSTANCE_SUFFIX;
		IOvertureMixinElement r = model.createOvertureElement(newkey);
		if (r instanceof OvertureMixinClass)
			return (OvertureMixinClass) r;
		return null;
	}

	public OvertureMixinClass getMetaclass() {
		if (isMeta())
			return this;
		String metakey = key.substring(0, key
				.indexOf(OvertureMixin.INSTANCE_SUFFIX));
		IOvertureMixinElement r = model.createOvertureElement(metakey);
		if (r instanceof OvertureMixinClass)
			return (OvertureMixinClass) r;
		return null;
	}

	public boolean isMeta() {
		return (!key.endsWith(OvertureMixin.INSTANCE_SUFFIX))
				&& (!key.endsWith(OvertureMixin.VIRTUAL_SUFFIX));
	}

	public String getName() {
		String name;
		if (key.indexOf(MixinModel.SEPARATOR) != -1)
			name = key.substring(key.lastIndexOf(MixinModel.SEPARATOR));
		else
			name = key;
		int pos;
		if ((pos = name.indexOf(OvertureMixin.INSTANCE_SUFFIX)) != -1)
			name = name.substring(0, pos);
		return name;
	}

	public IType[] getSourceTypes() {
		List result = new ArrayList();
		IMixinElement mixinElement = model.getRawModel().get(key);
		Object[] allObjects = mixinElement.getAllObjects();
		for (int i = 0; i < allObjects.length; i++) {
			OvertureMixinElementInfo info = (OvertureMixinElementInfo) allObjects[i];
			if (info == null)
				continue;
			if (info.getKind() == OvertureMixinElementInfo.K_CLASS
					|| info.getKind() == OvertureMixinElementInfo.K_MODULE) {
				if (info.getObject() != null)
					result.add(info.getObject());
			}
		}
		return (IType[]) result.toArray(new IType[result.size()]);
	}

	public OvertureMixinClass getSuperclass() {
		IMixinElement mixinElement = model.getRawModel().get(key);
		if (mixinElement == null)
			return null;
		Object[] allObjects = mixinElement.getAllObjects();
		// IType type = null;
		for (int i = 0; i < allObjects.length; i++) {
			OvertureMixinElementInfo info = (OvertureMixinElementInfo) allObjects[i];
			if (info == null) {
				continue;
			}
			// if (info.getKind() == RubyMixinElementInfo.K_CLASS) {
			// type = (IType) info.getObject();
			// if (type == null)
			// continue;
			// String key = RubyModelUtils.evaluateSuperClass(type);
			// if (key == null)
			// continue;
			// if (!this.isMeta())
			// key = key + RubyMixin.INSTANCE_SUFFIX;
			// RubyMixinClass s = (RubyMixinClass) model.createRubyElement(key);
			// return s;
			// }
//			if (info.getKind() == OvertureMixinElementInfo.K_SUPER) {
//				SuperclassReferenceInfo sinfo = (SuperclassReferenceInfo) info
//						.getObject();
//				BasicContext c = new BasicContext(sinfo.getModule(), sinfo
//						.getDecl());
//				ExpressionTypeGoal g = new ExpressionTypeGoal(c, sinfo
//						.getNode());
//				DLTKTypeInferenceEngine engine = new DLTKTypeInferenceEngine();
//				IEvaluatedType type2 = engine.evaluateType(g, 500);
//				if (type2 instanceof RubyClassType) {
//					RubyClassType rubyClassType = (RubyClassType) type2;
//					String key = rubyClassType.getModelKey();
//					if (!this.isMeta())
//						key = key + OvertureMixin.INSTANCE_SUFFIX;
//					return (OvertureMixinClass) model.createRubyElement(key);
//				}
//			}
		}
		String key;
		Set includeSet = new HashSet();
//		OvertureMixinClass[] includedClasses = model.createRubyClass(new RubyClassType("Object%")).getIncluded(); //$NON-NLS-1$
//		for (int cnt = 0, max = includedClasses.length; cnt < max; cnt++) {
//			includeSet.add(includedClasses[cnt].getKey());
//		}
		if (this.isMeta())
			if (this.isModule())
				key = "Module%"; //$NON-NLS-1$
			else
				key = "Class%"; //$NON-NLS-1$
		else if (isModule()
				&& ("Kernel%".equals(this.key) || includeSet.contains(this.getKey()))) //$NON-NLS-1$
			return null;
		else
			key = "Object"; //$NON-NLS-1$
		if (!this.isMeta())
			key = key + OvertureMixin.INSTANCE_SUFFIX;
		OvertureMixinClass s = (OvertureMixinClass) model.createOvertureElement(key);
		return s;
	}

	/**
	 * Returns modules included into this class.
	 * 
	 * @return
	 */
	public OvertureMixinClass[] getIncluded() {
		IMixinElement mixinElement = model.getRawModel().get(key);
		if (mixinElement == null)
			return new OvertureMixinClass[0];
		List result = new ArrayList();
		HashSet names = new HashSet();
		Object[] allObjects = mixinElement.getAllObjects();
		for (int i = 0; i < allObjects.length; i++) {
			OvertureMixinElementInfo info = (OvertureMixinElementInfo) allObjects[i];
			if (info == null) {
				continue;
			}
			if (info.getKind() == OvertureMixinElementInfo.K_INCLUDE) {
				String inclKey = (String) info.getObject();
				if (names.add(inclKey)) {
					if (/* !this.isMeta() && */!inclKey
							.endsWith(OvertureMixin.INSTANCE_SUFFIX))
						inclKey += OvertureMixin.INSTANCE_SUFFIX;
					IOvertureMixinElement element = model
							.createOvertureElement(inclKey);
					// TODO if element is not found - try to use different path
					// combinations
					if (element instanceof OvertureMixinClass)
						result.add(element);
				}
			}
		}
		return (OvertureMixinClass[]) result.toArray(new OvertureMixinClass[result
				.size()]);
	}

	/**
	 * Returns modules this class is extended from.
	 * 
	 * @return
	 */
	public OvertureMixinClass[] getExtended() {
		IMixinElement mixinElement = model.getRawModel().get(key);
		if (mixinElement == null)
			return new OvertureMixinClass[0];
		List result = new ArrayList();
		HashSet names = new HashSet();
		Object[] allObjects = mixinElement.getAllObjects();
		for (int i = 0; i < allObjects.length; i++) {
			OvertureMixinElementInfo info = (OvertureMixinElementInfo) allObjects[i];
			if (info == null) {
				continue;
			}
			if (info.getKind() == OvertureMixinElementInfo.K_EXTEND) {
				String extKey = (String) info.getObject();
				if (names.add(extKey)) {
					if (/* !this.isMeta() && */!extKey
							.endsWith(OvertureMixin.INSTANCE_SUFFIX))
						extKey += OvertureMixin.INSTANCE_SUFFIX;
					IOvertureMixinElement element = model.createOvertureElement(extKey);
					if (element instanceof OvertureMixinClass)
						result.add(element);
				}
			}
		}
		return (OvertureMixinClass[]) result.toArray(new OvertureMixinClass[result
				.size()]);
	}

	public void findMethods(IMixinSearchPattern pattern, IMixinSearchRequestor requestor) {
		findMethods(pattern, requestor, new HashSet());
	}

	protected void findMethods(IMixinSearchPattern pattern, IMixinSearchRequestor requestor, Set processedKeys) {
		if (!processedKeys.add(key)) {
			return;
		}
		IMixinElement mixinElement = model.getRawModel().get(key);
		if (mixinElement == null)
			return;
		final IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			final IMixinElement child = children[i];
			if (pattern.evaluate(child.getLastKeySegment())) {
				IOvertureMixinElement element = model.createOvertureElement(child);
				if (element instanceof OvertureMixinMethod) {
					requestor.acceptResult(element);
				} else if (element instanceof OvertureMixinAlias) {
					OvertureMixinAlias alias = (OvertureMixinAlias) element;
					IOvertureMixinElement oldElement = alias.getOldElement();
					if (oldElement instanceof OvertureMixinMethod) {
						AliasedOvertureMixinMethod a = new AliasedOvertureMixinMethod(
								model, alias);
						requestor.acceptResult(a);
					}
				}
			}
		}

		OvertureMixinClass[] included = this.getIncluded();
		for (int i = 0; i < included.length; i++) {
			included[i].findMethods(pattern, requestor, processedKeys);
		}

		OvertureMixinClass[] extended = this.getExtended();
		for (int i = 0; i < extended.length; i++) {
			extended[i].findMethods(pattern, requestor, processedKeys);
		}

		if (!this.key.endsWith(OvertureMixin.VIRTUAL_SUFFIX)) {
			OvertureMixinClass superclass = getSuperclass();
			if (superclass != null) {

				if (!superclass.getKey().equals(key)) {
					superclass.findMethods(pattern, requestor, processedKeys);
				}
			}
		} else {
			String stdKey = this.key.substring(0, key.length()
					- OvertureMixin.VIRTUAL_SUFFIX.length());
			IOvertureMixinElement realElement = model.createOvertureElement(stdKey);
			if (realElement instanceof OvertureMixinClass) {
				OvertureMixinClass overtureMixinClass = (OvertureMixinClass) realElement;
				overtureMixinClass.findMethods(pattern, requestor, processedKeys);
			}
		}

	}

	public OvertureMixinMethod[] findMethods(IMixinSearchPattern pattern) {
		final List result = new ArrayList();
		this.findMethods(pattern, new IMixinSearchRequestor() {
			final Set names = new HashSet();

			public void acceptResult(IOvertureMixinElement element) {
				if (element instanceof OvertureMixinMethod) {
					OvertureMixinMethod method = (OvertureMixinMethod) element;
					if (names.add(method.getName())) {
						result.add(method);
					}
				}
			}

		}, new HashSet());
		return (OvertureMixinMethod[]) result.toArray(new OvertureMixinMethod[result.size()]);
	}

	public OvertureMixinMethod getMethod(String name) {
		return getMethod(name, new HashSet());
	}

	protected OvertureMixinMethod getMethod(String name, Set processedKeys) {
		if (!processedKeys.add(key)) {
			return null;
		}
		String possibleKey = key + MixinModel.SEPARATOR + name;
		IMixinElement mixinElement = model.getRawModel().get(possibleKey);
		if (mixinElement != null) {
			IOvertureMixinElement element = model.createOvertureElement(mixinElement);
			if (element instanceof OvertureMixinMethod) {
				return (OvertureMixinMethod) element;
			}
			if (element instanceof OvertureMixinAlias) {
				OvertureMixinAlias alias = (OvertureMixinAlias) element;
				IOvertureMixinElement oldElement = alias.getOldElement();
				if (oldElement instanceof OvertureMixinMethod) {
					return new AliasedOvertureMixinMethod(model, alias);
				}
			}
		}

		OvertureMixinClass[] included = this.getIncluded();
		for (int i = 0; i < included.length; i++) {
			if (!this.key.equals(included[i].key)) {
				OvertureMixinMethod method = included[i].getMethod(name,
						processedKeys);
				if (method != null)
					return method;
			}
		}

		OvertureMixinClass[] extended = this.getExtended();
		for (int i = 0; i < extended.length; i++) {
			OvertureMixinMethod method = extended[i].getMethod(name, processedKeys);
			if (method != null)
				return method;
		}

		// search superclass
		// if (!this.key.equals("Object") && !this.key.equals("Object%")) {
		OvertureMixinClass superclass = getSuperclass();
		if (superclass != null) {
			if (superclass.getKey().equals(key))
				return null;
			return superclass.getMethod(name, processedKeys);
		}
		// }
		return null;
	}

	public OvertureMixinClass[] getClasses() {
		List result = new ArrayList();
		IMixinElement mixinElement = model.getRawModel().get(key);
		IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			IOvertureMixinElement element = model.createOvertureElement(children[i]);
			if (element instanceof OvertureMixinClass)
				result.add(element);
		}
		return (OvertureMixinClass[]) result.toArray(new OvertureMixinClass[result
				.size()]);
	}

	public OvertureMixinVariable[] getFields() {
		List result = new ArrayList();
		IMixinElement mixinElement = model.getRawModel().get(key);
		IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			IOvertureMixinElement element = model.createOvertureElement(children[i]);
			if (element instanceof OvertureMixinVariable)
				result.add(element);
		}
		OvertureMixinClass superclass = getSuperclass();
		if (superclass != null && superclass.key != "Object" && superclass.key != "Object%") {
			if (superclass.getKey().equals(key))
				return null;
			OvertureMixinVariable[] superFields = superclass.getFields();
			result.addAll(Arrays.asList(superFields));
		}
		return (OvertureMixinVariable[]) result.toArray(new OvertureMixinVariable[result.size()]);
	}

}
