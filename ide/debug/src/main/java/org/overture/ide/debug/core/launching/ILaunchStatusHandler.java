/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.overture.ide.debug.core.launching;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IDebugTarget;

public interface ILaunchStatusHandler {

	/**
	 * @param target
	 * @param monitor
	 */
	void initialize(IDebugTarget target, IProgressMonitor monitor);

	/**
	 * @param elapsedTime
	 */
	void updateElapsedTime(long elapsedTime);

	/**
	 * 
	 */
	void dispose();

}
