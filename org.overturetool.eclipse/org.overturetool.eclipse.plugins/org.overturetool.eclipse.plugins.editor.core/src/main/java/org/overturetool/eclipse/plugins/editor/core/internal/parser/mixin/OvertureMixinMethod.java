/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *     xored software, Inc. - OvertureDocumentation display improvements (Alex Panchenko <alex@xored.com>)
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.mixin.IMixinElement;
import org.eclipse.dltk.core.mixin.MixinModel;

public class OvertureMixinMethod implements IOvertureMixinElement {

	private final String key;
	protected final OvertureMixinModel model;
	private IMethod[] sourceMethods;

	public OvertureMixinMethod(OvertureMixinModel model, String key) {
		super();
		this.model = model;
		this.key = key;
	}

	public String getName() {
		return key.substring(key.lastIndexOf(MixinModel.SEPARATOR) + 1);
	}

	public OvertureMixinMethod(OvertureMixinModel model, String key,
			IMethod[] sourceMethods) {
		super();
		this.model = model;
		this.key = key;
		this.sourceMethods = sourceMethods;
	}

	public String getKey() {
		return key;
	}

	public OvertureMixinClass getSelfType() {
		IMixinElement mixinElement = model.getRawModel().get(key);
		IMixinElement parent = null;
		if (mixinElement != null)
			parent = mixinElement.getParent();
		if (parent == null)
			return new OvertureObjectMixinClass(model, true);
		IOvertureMixinElement overtureParent = model.createOvertureElement(parent);
		if (overtureParent instanceof OvertureMixinClass) {
			return (OvertureMixinClass) overtureParent;
		}
		return null;
	}

	/**
	 * Allows to set precalculated source methods and not use mixin model to
	 * find them.
	 */
	public void setSourceMethods(IMethod[] sourceMethods) {
		this.sourceMethods = sourceMethods;
	}

	/**
	 * Returns model elements for this method. If they were previously saved
	 * using setSourceMethods() or constructor, then exactly they are returned.
	 * Else mixin model are used.
	 */
	public IMethod[] getSourceMethods() {
		if (this.sourceMethods != null)
			return sourceMethods;
		return OvertureMixinMethod.getSourceMethods(model, key);
	}

	protected static IMethod[] getSourceMethods(OvertureMixinModel model, String key) {
		final IMixinElement mixinElement = model.getRawModel().get(key);
		if (mixinElement != null) {
			final Object[] allObjects = mixinElement.getAllObjects();
			final List result = new ArrayList();
			for (int i = 0; i < allObjects.length; i++) {
				OvertureMixinElementInfo info = (OvertureMixinElementInfo) allObjects[i];
				if (info.getKind() == OvertureMixinElementInfo.K_METHOD) {
					result.add(info.getObject());
				}
			}
			return (IMethod[]) result.toArray(new IMethod[result.size()]);
		}
		return new IMethod[0];
	}

	public OvertureMixinVariable[] getVariables() {
		List result = new ArrayList();
		IMixinElement mixinElement = model.getRawModel().get(key);
		IMixinElement[] children = mixinElement.getChildren();
		for (int i = 0; i < children.length; i++) {
			IOvertureMixinElement element = model.createOvertureElement(children[i]);
			if (element instanceof OvertureMixinVariable)
				result.add(element);
		}
		return (OvertureMixinVariable[]) result
				.toArray(new OvertureMixinVariable[result.size()]);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "OvertureMixinMethod[" + key + "]"; //$NON-NLS-1$ //$NON-NLS-2$
	}

}
