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
