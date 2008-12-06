package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlInstanceVariableDefinitions extends IJmlDefinitionBlock
{
	abstract Vector getJmlVariables() throws CGException;
	abstract Vector getJavaVariables() throws CGException;
}

