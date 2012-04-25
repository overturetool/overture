package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlOperationDefinitions extends IJmlDefinitionBlock
{
	abstract Vector getOperationList() throws CGException;
}

