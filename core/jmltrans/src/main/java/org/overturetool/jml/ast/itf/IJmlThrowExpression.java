package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlThrowExpression extends IJmlNode
{
	abstract String getException() throws CGException;
	abstract Vector getParams() throws CGException;
}

