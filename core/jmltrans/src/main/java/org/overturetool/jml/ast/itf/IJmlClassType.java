package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlClassType extends IJmlType
{
	abstract String getId() throws CGException;
	abstract Vector getFieldList() throws CGException;
}

