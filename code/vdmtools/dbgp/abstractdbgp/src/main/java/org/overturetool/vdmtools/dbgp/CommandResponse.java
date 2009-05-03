package org.overturetool.vdmtools.dbgp;

public abstract class CommandResponse {
	abstract protected String parseAndExecute(DBGPCommand command) throws DBGPException;
}
