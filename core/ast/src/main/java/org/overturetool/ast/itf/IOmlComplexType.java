package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlComplexType extends IOmlTypeShape
{
	abstract String getIdentifier() throws CGException;
	@SuppressWarnings("rawtypes")
	abstract Vector getFieldList() throws CGException;
	abstract IOmlInvariant getInvariant() throws CGException;
	abstract Boolean hasInvariant() throws CGException;
}

