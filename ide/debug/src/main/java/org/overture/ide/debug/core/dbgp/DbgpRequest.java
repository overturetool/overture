/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.overture.ide.debug.core.dbgp.internal.utils.Base64Helper;
import org.overture.ide.debug.utils.StrUtils;

public class DbgpRequest implements IDbgpRawPacket {
	private final Map options;

	private final String command;
	private final boolean async;

	private String data;

	public DbgpRequest(String command) {
		this(command, false);
	}

	public DbgpRequest(String command, boolean async) {
		this.command = command;
		this.async = async;
		this.options = new HashMap();
	}

	public String getCommand() {
		return command;
	}

	public void addOption(String optionNmae, int optionValue) {
		addOption(optionNmae, new Integer(optionValue));
	}

	public void addOption(String optionName, Object optionValue) {
		if (optionValue == null) {
			throw new IllegalArgumentException();
		}

		options.put(optionName, optionValue.toString());
	}

	public String getOption(String optionName) {
		return (String) options.get(optionName);
	}

	public boolean hasOption(String optionName) {
		return options.containsKey(optionName);
	}

	public int optionCount() {
		return options.size();
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData() {
		return this.data;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer(command);

		Iterator it = options.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();

			sb.append(' ');
			sb.append(entry.getKey());
			sb.append(' ');
			sb.append(entry.getValue());
		}

		if (data != null) {
			sb.append(" -- "); //$NON-NLS-1$
			sb.append(Base64Helper.encodeString(data));
		}

		return sb.toString();
	}

	public boolean equals(Object o) {
		if (o instanceof DbgpRequest) {
			DbgpRequest request = (DbgpRequest) o;

			return command.equals(request.command) && async == request.async
					&& options.equals(request.options)
					&& StrUtils.equals(data, request.data);
		}

		return false;
	}

	/**
	 * @return the async
	 */
	public boolean isAsync() {
		return async;
	}

	public void writeTo(OutputStream output) throws IOException {
		// TODO optimize - send directly to stream without string
		output.write(toString().getBytes("ASCII")); //$NON-NLS-1$
	}

	public String getPacketAsString() {
		return toString();
	}
}
