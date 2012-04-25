package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlInterfaceInheritanceClause extends IJmlNode
{
	abstract Vector getIdentifierList() throws CGException;
}

