package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlVarInformation extends IOmlNode
{
	abstract IOmlMode getMode() throws CGException;
	abstract Vector getNameList() throws CGException;
	abstract IOmlType getType() throws CGException;
	abstract Boolean hasType() throws CGException;
}

