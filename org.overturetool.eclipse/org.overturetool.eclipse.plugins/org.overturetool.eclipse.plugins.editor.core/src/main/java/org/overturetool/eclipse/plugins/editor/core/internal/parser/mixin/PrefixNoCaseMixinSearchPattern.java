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

public class PrefixNoCaseMixinSearchPattern implements IMixinSearchPattern {

	private final String key;
	private final char[] chars;

	public PrefixNoCaseMixinSearchPattern(String key) {
		this.key = key;
		chars = key.toLowerCase().toCharArray();
	}

	public boolean evaluate(String lastSegment) {
		final int length = chars.length;
		if (lastSegment.length() < length) {
			return false;
		}
		for (int i = 0; i < length; ++i) {
			if (Character.toLowerCase(lastSegment.charAt(i)) != chars[i]) {
				return false;
			}
		}
		return true;
	}

	public String getKey() {
		return key;
	}

}
