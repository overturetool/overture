package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlRecordPattern extends IOmlPattern
{
	abstract IOmlName getName() throws CGException;
	abstract Vector getPatternList() throws CGException;
}

