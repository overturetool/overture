package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlSynchronizationDefinitions extends IOmlDefinitionBlock
{
	@SuppressWarnings("rawtypes")
	abstract Vector getSyncList() throws CGException;
}

