package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlForAllExpression extends IJmlExpression
{
	abstract IJmlQuantifierDeclaration getBindList() throws CGException;
	abstract Vector getExpression() throws CGException;
}

