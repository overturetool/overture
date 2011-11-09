package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlOperationDefinitions extends IOmlDefinitionBlock
{
	@SuppressWarnings("rawtypes")
	abstract Vector getOperationList() throws CGException;
}

