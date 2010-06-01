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

public class DbgpResponsePacket extends DbgpPacket {
	private final int transactionId;

	public DbgpResponsePacket(Element element, int transactionId) {
		super(element);

		this.transactionId = transactionId;
	}

	public int getTransactionId() {
		return transactionId;
	}
}
