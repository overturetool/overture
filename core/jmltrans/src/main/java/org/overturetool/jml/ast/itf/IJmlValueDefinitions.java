package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlValueDefinitions extends IJmlDefinitionBlock
{
	abstract Vector getValueList() throws CGException;
}

