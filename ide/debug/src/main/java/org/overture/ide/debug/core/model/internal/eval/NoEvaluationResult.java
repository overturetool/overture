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
package org.overture.ide.debug.core.model.internal.eval;

import org.eclipse.debug.core.DebugException;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.IVdmValue;
import org.overture.ide.debug.core.model.eval.IVdmEvaluationResult;
import org.overture.ide.debug.utils.CharOperation;

public class NoEvaluationResult implements IVdmEvaluationResult {

	private final String snippet;
	private final IVdmThread thread;

	/**
	 * @param snippet
	 * @param thread
	 */
	public NoEvaluationResult(String snippet, IVdmThread thread) {
		this.snippet = snippet;
		this.thread = thread;
	}

	public String[] getErrorMessages() {
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException() {
		return null;
	}

	public String getSnippet() {
		return snippet;
	}

	public IVdmThread getThread() {
		return thread;
	}

	public IVdmValue getValue() {
		return null;
	}

	public boolean hasErrors() {
		return false;
	}

}
