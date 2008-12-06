package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlClass extends IJmlNode
{
	abstract IJmlAccessDefinition getAccess() throws CGException;
	abstract IJmlClassKind getKind() throws CGException;
	abstract String getIdentifier() throws CGException;
	abstract IJmlClassInheritanceClause getInheritanceClause() throws CGException;
	abstract Boolean hasInheritanceClause() throws CGException;
	abstract IJmlInterfaceInheritanceClause getInterfaceInheritance() throws CGException;
	abstract Boolean hasInterfaceInheritance() throws CGException;
	abstract Vector getClassBody() throws CGException;
}

