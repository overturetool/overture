package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlTupleType extends IJmlType
{
	abstract Vector getVals() throws CGException;
}

