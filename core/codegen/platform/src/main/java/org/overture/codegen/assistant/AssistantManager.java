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
package org.overture.codegen.assistant;

public class AssistantManager
{
	private NodeAssistantIR nodeAssistant;
	private ExpAssistantIR expAssistant;
	private DeclAssistantIR declAssistant;
	private StmAssistantIR stmAssistant;
	private TypeAssistantIR typeAssistant;
	private LocationAssistantIR locationAssistant;
	private BindAssistantIR bindAssistant;
	private PatternAssistantIR patternAssistant;

	public AssistantManager()
	{
		this.nodeAssistant = new NodeAssistantIR(this);
		this.expAssistant = new ExpAssistantIR(this);
		this.declAssistant = new DeclAssistantIR(this);
		this.stmAssistant = new StmAssistantIR(this);
		this.typeAssistant = new TypeAssistantIR(this);
		this.locationAssistant = new LocationAssistantIR(this);
		this.bindAssistant = new BindAssistantIR(this);
		this.patternAssistant = new PatternAssistantIR(this);
	}

	public NodeAssistantIR getNodeAssistant()
	{
		return nodeAssistant;
	}
	
	public ExpAssistantIR getExpAssistant()
	{
		return expAssistant;
	}

	public DeclAssistantIR getDeclAssistant()
	{
		return declAssistant;
	}

	public StmAssistantIR getStmAssistant()
	{
		return stmAssistant;
	}

	public TypeAssistantIR getTypeAssistant()
	{
		return typeAssistant;
	}

	public LocationAssistantIR getLocationAssistant()
	{
		return locationAssistant;
	}

	public BindAssistantIR getBindAssistant()
	{
		return bindAssistant;
	}

	public PatternAssistantIR getPatternAssistant()
	{
		return patternAssistant;
	}
}
