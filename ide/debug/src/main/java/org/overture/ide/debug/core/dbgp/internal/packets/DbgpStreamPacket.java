/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal.packets;

import org.w3c.dom.Element;

public class DbgpStreamPacket extends DbgpPacket {
	private static final String STDERR = "stderr"; //$NON-NLS-1$

	private static final String STDOUT = "stdout"; //$NON-NLS-1$

	private final String type;

	private final String textContent;

	public DbgpStreamPacket(String type, String textContent, Element content) {
		super(content);
		
		if (!STDERR.equalsIgnoreCase(type) && !STDOUT.equalsIgnoreCase(type)) {
			throw new IllegalArgumentException("invalidTypeValue");
		}

		if (textContent == null) {
			throw new IllegalArgumentException("contentCannotBeNull");
		}

		this.type = type;
		this.textContent = textContent;
	}

	public boolean isStdout() {
		return STDOUT.equalsIgnoreCase(type);
	}

	public boolean isStderr() {
		return STDERR.equalsIgnoreCase(type);
	}

	public String getTextContent() {
		return textContent;
	}

	public String toString() {
		return "DbgpStreamPacket (Type: " + type + "; Content: " + textContent //$NON-NLS-1$ //$NON-NLS-2$
				+ ";)"; //$NON-NLS-1$
	}
}
