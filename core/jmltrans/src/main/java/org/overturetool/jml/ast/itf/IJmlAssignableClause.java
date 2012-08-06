package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlAssignableClause extends IJmlTrailers
{
	abstract Vector getAssignableList() throws CGException;
}

