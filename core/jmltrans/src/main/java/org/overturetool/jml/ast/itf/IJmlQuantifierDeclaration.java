package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlQuantifierDeclaration extends IJmlNode
{
	abstract IJmlBoundModifiers getBound() throws CGException;
	abstract Boolean hasBound() throws CGException;
	abstract IJmlType getType() throws CGException;
	abstract Vector getVars() throws CGException;
}

