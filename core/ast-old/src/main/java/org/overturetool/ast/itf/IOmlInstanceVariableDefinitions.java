package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlInstanceVariableDefinitions extends IOmlDefinitionBlock
{
	@SuppressWarnings("rawtypes")
	abstract Vector getVariablesList() throws CGException;
}

