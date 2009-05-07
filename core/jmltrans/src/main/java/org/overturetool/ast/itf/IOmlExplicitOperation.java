package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExplicitOperation extends IOmlOperationShape
{
	abstract String getIdentifier() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract Vector getParameterList() throws CGException;
	abstract IOmlOperationBody getBody() throws CGException;
	abstract IOmlOperationTrailer getTrailer() throws CGException;
}

