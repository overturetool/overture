package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlCompositeType extends IOmlType
{
	abstract String getIdentifier() throws CGException;
	@SuppressWarnings("rawtypes")
	abstract Vector getFieldList() throws CGException;
}

