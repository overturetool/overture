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
package org.overture.ide.debug.ui.log;

import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;

public class VdmDebugLogItem {

	private final long timestamp;
	private final String type;
	private final int sessionId;
	private final String message;

	public VdmDebugLogItem(String type, String message) {
		this.timestamp = System.currentTimeMillis();
		this.type = type;
		this.sessionId = 0;
		this.message = message;
	}

	public VdmDebugLogItem(String type, int sessionId, IDbgpRawPacket message) {
		this(System.currentTimeMillis(), type, sessionId, message);
	}

	/**
	 * @param message
	 * @param timestamp
	 * @param type
	 */
	public VdmDebugLogItem(long timestamp, String type, int sessionId,
			IDbgpRawPacket message) {
		this.timestamp = timestamp;
		this.type = type;
		this.sessionId = sessionId;
		this.message = message.getPacketAsString();
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public int getSessionId() {
		return sessionId;
	}

	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return type + '\t' + message;
	}

}
