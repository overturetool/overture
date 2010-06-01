/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import java.net.URI;

import org.overture.ide.debug.core.dbgp.IDbgpSessionInfo;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public class DbgpSessionInfo implements IDbgpSessionInfo {
	private final String appId;

	private final String ideKey;

	private final String session;

	private final String threadId;

	private final String parentId;

	private final String language;

	private final URI fileUri;

	private DbgpException error;

	public DbgpSessionInfo(String appId, String ideKey, String session,
			String threadId, String parentId, String language, URI fileUri, DbgpException error) {
		super();
		this.appId = appId;
		this.ideKey = ideKey;
		this.session = session;
		this.threadId = threadId;
		this.parentId = parentId;
		this.language = language;
		this.fileUri = fileUri;
		this.error = error;
	}

	public String getApplicationId() {
		return appId;
	}

	public URI getFileUri() {
		return fileUri;
	}

	public String getIdeKey() {
		return ideKey;
	}

	public String getLanguage() {
		return language;
	}

	public String getParentAppId() {
		return parentId;
	}

	public String getSession() {
		return session;
	}

	public String getThreadId() {
		return threadId;
	}

	public DbgpException getError() {
		return error;
	}

}
