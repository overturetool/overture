package org.overturetool.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlClass extends IOmlNode
{
	abstract String getIdentifier() throws CGException;
	abstract Vector getGenericTypes() throws CGException;
	abstract IOmlInheritanceClause getInheritanceClause() throws CGException;
	abstract Boolean hasInheritanceClause() throws CGException;
	abstract Vector getClassBody() throws CGException;
	abstract Boolean getSystemSpec() throws CGException;
}

