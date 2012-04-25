package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlMutexPredicate extends IOmlSyncPredicate
{
	abstract Vector getNameList() throws CGException;
}

