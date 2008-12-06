package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlInvariantDefinitions extends IJmlDefinitionBlock
{
	abstract Vector getInvariantList() throws CGException;
}

