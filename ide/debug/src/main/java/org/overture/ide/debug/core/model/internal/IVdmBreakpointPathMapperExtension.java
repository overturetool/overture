/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jae Gangemi - initial API and Implementation
 *******************************************************************************/
package org.overture.ide.debug.core.model.internal;

import org.overture.ide.debug.core.model.IVdmBreakpointPathMapper;


public interface IVdmBreakpointPathMapperExtension extends
		IVdmBreakpointPathMapper {

	void clearCache();

}
