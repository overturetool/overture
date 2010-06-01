/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import java.util.Map;
import java.util.TreeMap;

import org.overture.ide.debug.core.dbgp.IDbgpStatus;

public class DbgpStatus implements IDbgpStatus {
	// Reasons
	public static final Integer REASON_OK = new Integer(0);

	public static final Integer REASON_ERROR = new Integer(1);

	public static final Integer REASON_ABORTED = new Integer(2);

	public static final Integer REASON_EXCEPTION = new Integer(3);

	// Status
	public static final Integer STATUS_STARTING = new Integer(0);

	public static final Integer STATUS_STOPPING = new Integer(1);

	public static final Integer STATUS_STOPPED = new Integer(2);

	public static final Integer STATUS_RUNNING = new Integer(3);

	public static final Integer STATUS_BREAK = new Integer(4);

	private static final Map statusParser = new TreeMap(
			String.CASE_INSENSITIVE_ORDER);

	private static final Map reasonParser = new TreeMap(
			String.CASE_INSENSITIVE_ORDER);

	static {
		statusParser.put("starting", STATUS_STARTING); //$NON-NLS-1$
		statusParser.put("stopping", STATUS_STOPPING); //$NON-NLS-1$
		statusParser.put("stopped", STATUS_STOPPED); //$NON-NLS-1$
		statusParser.put("running", STATUS_RUNNING); //$NON-NLS-1$
		statusParser.put("break", STATUS_BREAK); //$NON-NLS-1$

		reasonParser.put("ok", REASON_OK); //$NON-NLS-1$
		reasonParser.put("error", REASON_ERROR); //$NON-NLS-1$
		reasonParser.put("aborted", REASON_ABORTED); //$NON-NLS-1$
		reasonParser.put("exception", REASON_EXCEPTION); //$NON-NLS-1$
	}

	public static IDbgpStatus parse(String status, String reason) {
		return new DbgpStatus((Integer) statusParser.get(status),
				(Integer) reasonParser.get(reason));
	}

	private final Integer status;

	private final Integer reason;

	public DbgpStatus(Integer status, Integer reason) {
		if (status == null) {
			throw new IllegalArgumentException();
		}

		if (reason == null) {
			throw new IllegalArgumentException();
		}

		this.status = status;
		this.reason = reason;
	}

	public boolean reasonAborred() {
		return REASON_ABORTED == reason;
	}

	public boolean reasonError() {
		return REASON_ERROR == reason;
	}

	public boolean reasonException() {
		return REASON_EXCEPTION == reason;
	}

	public boolean reasonOk() {
		return REASON_OK == reason;
	}

	public boolean isRunning() {
		return STATUS_RUNNING == status;
	}

	public boolean isStarting() {
		return STATUS_STARTING == status;
	}

	public boolean isStopped() {
		return STATUS_STOPPED == status;
	}

	public boolean isStopping() {
		return STATUS_STOPPING == status;
	}

	public boolean isBreak() {
		return STATUS_BREAK == status;
	}

	public boolean equals(Object obj) {
		if (obj instanceof DbgpStatus) {
			DbgpStatus s = (DbgpStatus) obj;
			return this.status == s.status && this.reason == s.reason;
		}
		return false;
	}

	public int hashCode() {
		return (status.hashCode() << 8) | reason.hashCode();
	}

	public String toString() {
		return "Status: " + status.toString() + "; Reason: " //$NON-NLS-1$ //$NON-NLS-2$
				+ reason.toString();
	}
}
