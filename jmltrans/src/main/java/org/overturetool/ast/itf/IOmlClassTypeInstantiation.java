package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlClassTypeInstantiation extends IOmlType
{
	abstract IOmlName getName() throws CGException;
	abstract Vector getGenericTypes() throws CGException;
}

