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
package org.overturetool.eclipse.debug.internal.debug;

import org.eclipse.dltk.debug.core.AbstractDLTKDebugToolkit;

public class OvertureDebugToolkit extends AbstractDLTKDebugToolkit {

	public boolean isAccessWatchpointSupported() {
		return true;
	}

}
