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
package org.overturetool.eclipse.plugins.editor.core.internal.parser.mixin;

public class ExactMixinSearchPattern implements IMixinSearchPattern {

	private final String key;

	/**
	 * @param key
	 */
	public ExactMixinSearchPattern(String key) {
		this.key = key;
	}

	public boolean evaluate(String lastSegment) {
		return key.equals(lastSegment);
	}

	public String getKey() {
		return key;
	}

}
