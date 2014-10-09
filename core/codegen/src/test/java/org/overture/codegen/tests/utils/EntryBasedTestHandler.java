/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.tests.utils;

import java.io.File;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.values.Value;

public abstract class EntryBasedTestHandler extends ExecutableTestHandler
{
	protected static final String ENTRY_CLASS_NAME = "Entry";
	protected static final String ENTRY_METHOD_CALL = "Run()";
	protected static final String JAVA_ENTRY_CALL = ENTRY_CLASS_NAME + "."
			+ ENTRY_METHOD_CALL;
	protected static final String VDM_ENTRY_CALL = ENTRY_CLASS_NAME + "`"
			+ ENTRY_METHOD_CALL;

	public EntryBasedTestHandler(Release release)
	{
		super(release);
	}

	@Override
	public Value interpretVdm(File intputFile) throws Exception
	{
		return InterpreterUtil.interpret(Dialect.VDM_PP, VDM_ENTRY_CALL, intputFile);
	}
}
