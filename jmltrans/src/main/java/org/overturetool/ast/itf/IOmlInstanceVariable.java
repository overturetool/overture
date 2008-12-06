package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlInstanceVariable extends IOmlInstanceVariableShape
{
	abstract IOmlAccessDefinition getAccess() throws CGException;
	abstract IOmlAssignmentDefinition getAssignmentDefinition() throws CGException;
}

