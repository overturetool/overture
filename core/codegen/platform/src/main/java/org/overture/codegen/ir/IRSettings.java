/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.ir;

import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;

public class IRSettings
{
	private boolean charSeqAsString;
	private boolean generateConc;

	private boolean generatePreConds;
	private boolean generatePreCondChecks;
	private boolean makePreCondsPublic;

	private boolean generatePostConds;
	private boolean generatePostCondChecks;
	private boolean makePostCondsPublic;

	private boolean generateTraces;

	private boolean generateInvariantChecks;
	private boolean addStateInvToModule = true;

	public IRSettings()
	{
	}

	public boolean generateConc()
	{
		return generateConc && (Settings.dialect == Dialect.VDM_PP
				|| Settings.dialect == Dialect.VDM_RT);
	}

	public void setGenerateConc(boolean generateConc)
	{
		this.generateConc = generateConc;
	}

	public boolean getCharSeqAsString()
	{
		return charSeqAsString;
	}

	public void setCharSeqAsString(boolean charSeqAsString)
	{
		this.charSeqAsString = charSeqAsString;
	}

	public boolean generatePreConds()
	{
		return generatePreConds;
	}

	public void setGeneratePreConds(boolean generatePreConds)
	{
		this.generatePreConds = generatePreConds;
	}

	public boolean generatePreCondChecks()
	{
		return generatePreCondChecks;
	}

	public void setGeneratePreCondChecks(boolean generatePreCondChecks)
	{
		this.generatePreCondChecks = generatePreCondChecks;
	}

	public boolean makePreCondsPublic()
	{
		return makePreCondsPublic;
	}

	public void setMakePreCondsPublic(boolean makePreCondsPublic)
	{
		this.makePreCondsPublic = makePreCondsPublic;
	}

	public boolean generatePostConds()
	{
		return generatePostConds;
	}

	public void setGeneratePostConds(boolean generatePostConds)
	{
		this.generatePostConds = generatePostConds;
	}

	public boolean generatePostCondChecks()
	{
		return generatePostCondChecks;
	}

	public void setGeneratePostCondChecks(boolean generatePostCondChecks)
	{
		this.generatePostCondChecks = generatePostCondChecks;
	}

	public boolean makePostCondsPublic()
	{
		return makePostCondsPublic;
	}

	public void setMakePostCondsPublic(boolean makePostCondsPublic)
	{
		this.makePostCondsPublic = makePostCondsPublic;
	}

	public boolean generateTraces()
	{
		return generateTraces;
	}

	public void setGenerateTraces(boolean generateTraces)
	{
		this.generateTraces = generateTraces;
	}

	public boolean generateInvariants()
	{
		return generateInvariantChecks;
	}

	public void setGenerateInvariants(boolean generateInvariantChecks)
	{
		this.generateInvariantChecks = generateInvariantChecks;
	}

	public boolean addStateInvToModule() {
		return addStateInvToModule;
    }

	public void setAddStateInvToModule(boolean addStateInvToModule) {
		this.addStateInvToModule = addStateInvToModule;
	}
}
