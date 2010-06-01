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
package org.overture.ide.debug.core.model;

import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.IDebugOptions.BooleanOption;
import org.overture.ide.debug.core.IDebugOptions.IntegerOption;
import org.overture.ide.debug.core.IDebugOptions.StringOption;

public class DefaultDebugOptions implements IDebugOptions {

	private static IDebugOptions defaultInstance = null;

	public static IDebugOptions getDefaultInstance() {
		if (defaultInstance == null) {
			defaultInstance = new DefaultDebugOptions();
		}
		return defaultInstance;
	}

	protected DefaultDebugOptions() {
		// empty
	}

	public boolean get(BooleanOption option) {
		return option.getDefaultValue();
	}

	public int get(IntegerOption option) {
		return option.getDefaultValue();
	}

	public String get(StringOption option) {
		return option.getDefaultValue();
	}

	public IVdmStackFrame[] filterStackLevels(IVdmStackFrame[] frames) {
		return (IVdmStackFrame[]) frames.clone();
	}

	public boolean isValidStack(IVdmStackFrame[] frames) {
		return true;
	}

}
