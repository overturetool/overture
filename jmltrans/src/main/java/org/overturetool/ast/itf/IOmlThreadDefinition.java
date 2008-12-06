package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlThreadDefinition extends IOmlDefinitionBlock
{
	abstract IOmlThreadSpecification getThreadSpecification() throws CGException;
	abstract Boolean hasThreadSpecification() throws CGException;
}

