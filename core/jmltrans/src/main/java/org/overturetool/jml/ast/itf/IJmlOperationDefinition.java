package org.overturetool.jml.ast.itf;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlOperationDefinition extends IJmlNode
{
	abstract IJmlMethodSpecifications getTrailer() throws CGException;
	abstract Boolean hasTrailer() throws CGException;
	abstract IJmlAccessDefinition getAccess() throws CGException;
	abstract Boolean getPure() throws CGException;
	abstract Boolean getStatickeyword() throws CGException;
	abstract Boolean getFinal() throws CGException;
	abstract IJmlType getReturningType() throws CGException;
	abstract String getIdentifier() throws CGException;
	abstract Vector getParameterList() throws CGException;
	abstract IJmlBody getBody() throws CGException;
	abstract Boolean hasBody() throws CGException;
}

