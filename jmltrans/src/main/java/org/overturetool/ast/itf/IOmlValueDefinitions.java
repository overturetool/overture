package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlValueDefinitions extends IOmlDefinitionBlock
{
	abstract Vector getValueList() throws CGException;
}

