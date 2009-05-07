package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlExtendedExplicitOperation extends IOmlOperationShape
{
	abstract String getIdentifier() throws CGException;
	abstract Vector getPatternTypePairList() throws CGException;
	abstract Vector getIdentifierTypePairList() throws CGException;
	abstract IOmlOperationBody getBody() throws CGException;
	abstract IOmlOperationTrailer getTrailer() throws CGException;
}

