/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.commands;

import java.io.File;

import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;

public interface IDbgpOvertureCommands {

	public void getCoverage(File file) throws DbgpException;

	public void writeCoverage(File file) throws DbgpException;

	public void writeLog(String file) throws DbgpException;

	public void init() throws DbgpException;

	public void getCurrentline() throws DbgpException;

	public void getSource() throws DbgpException;

	public void writeLatex(File dir, File file) throws DbgpException;

	public void writeLatexdoc(File dir, File file)
			throws DbgpException;

	public void getPog(String name) throws DbgpException;

	public void getStack() throws DbgpException;

	public void getList() throws DbgpException;

	public void getFiles() throws DbgpException;

	public void getClasses() throws DbgpException;

	public void getModules() throws DbgpException;

	public void getDefault(String name) throws DbgpException;

	public void createInstance(String var, String exp)
			throws DbgpException;

	public void writeTrace(File file, int lnum, String display)
			throws DbgpException;

	public void runTrace(String name, int testNo, boolean debug)
			throws DbgpException;

}
