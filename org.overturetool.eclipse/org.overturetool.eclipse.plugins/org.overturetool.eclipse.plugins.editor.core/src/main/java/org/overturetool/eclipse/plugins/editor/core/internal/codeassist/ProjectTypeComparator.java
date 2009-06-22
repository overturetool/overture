/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.core.internal.codeassist;

import java.util.Comparator;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;

/**
 * This {@link Comparator} is used to sort type proposals in the following
 * order:
 * <ol>
 * <li>the specified module types should go first
 * <li>other project types should go next
 * <li>all other types should go last
 * </ol>
 */
public class ProjectTypeComparator implements Comparator {

	private final ISourceModule module;

	/**
	 * @param module
	 */
	public ProjectTypeComparator(ISourceModule module) {
		this.module = module;
	}

	public int compare(Object o1, Object o2) {
		final IType type1 = (IType) o1;
		final IType type2 = (IType) o2;
		if (type1.getParent() instanceof ISourceModule
				&& type2.getParent() instanceof ISourceModule) {
			final ISourceModule module1 = (ISourceModule) type1.getParent();
			final ISourceModule module2 = (ISourceModule) type2.getParent();
			if (module1.isReadOnly() != module2.isReadOnly()) {
				return module1.isReadOnly() ? +1 : -1;
			}
			final boolean same1 = module.equals(module1);
			if (same1 != module.equals(module2)) {
				return same1 ? -1 : +1;
			}
		}
		return type1.getElementName().compareTo(type2.getElementName());
	}
}
