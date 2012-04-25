package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlTypelessExplicitFunction extends IOmlFunctionShape
{
	abstract String getIdentifier() throws CGException;
	abstract Vector getParameterList() throws CGException;
	abstract IOmlFunctionBody getBody() throws CGException;
	abstract IOmlFunctionTrailer getTrailer() throws CGException;
}

