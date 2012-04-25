package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlSequenceEnumeration extends IJmlExpression
{
	abstract Vector getList() throws CGException;
}

