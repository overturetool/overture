package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlWrappedJmlClass extends IJmlNode
{
	abstract String getPackage() throws CGException;
	abstract String getRefine() throws CGException;
	abstract Vector getImportsJava() throws CGException;
	abstract Vector getImportsJml() throws CGException;
	abstract IJmlClass getClassVal() throws CGException;
}

