package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlImplicitFunction extends IOmlFunctionShape
{
	abstract String getIdentifier() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getTypeVariableList() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getPatternTypePairList() throws CGException;
	@SuppressWarnings("unchecked")
	abstract Vector getIdentifierTypePairList() throws CGException;
	abstract IOmlFunctionTrailer getTrailer() throws CGException;
}

