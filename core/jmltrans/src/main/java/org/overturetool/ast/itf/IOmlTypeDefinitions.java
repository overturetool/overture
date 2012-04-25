package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypeDefinitions extends IOmlDefinitionBlock
{
	abstract Vector getTypeList() throws CGException;
}

