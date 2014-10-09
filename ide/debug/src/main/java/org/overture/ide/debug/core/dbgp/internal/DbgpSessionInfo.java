/*
 * #%~
 * org.overture.ide.debug
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.debug.core.dbgp.internal;

import java.net.URI;

import org.overture.ide.debug.core.dbgp.IDbgpSessionInfo;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public class DbgpSessionInfo implements IDbgpSessionInfo
{
	private final String appId;

	private final String ideKey;

	private final String session;

	private final String threadId;

	private final String parentId;

	private final String language;

	private final URI fileUri;

	private DbgpException error;

	public DbgpSessionInfo(String appId, String ideKey, String session,
			String threadId, String parentId, String language, URI fileUri,
			DbgpException error)
	{
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

	public String getApplicationId()
	{
		return appId;
	}

	public URI getFileUri()
	{
		return fileUri;
	}

	public String getIdeKey()
	{
		return ideKey;
	}

	public String getLanguage()
	{
		return language;
	}

	public String getParentAppId()
	{
		return parentId;
	}

	public String getSession()
	{
		return session;
	}

	public String getThreadId()
	{
		return threadId;
	}

	public DbgpException getError()
	{
		return error;
	}

}
