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
	private ExpAssistantCG expAssistant;
	private DeclAssistantCG declAssistant;
	private StmAssistantCG stmAssistant;
	private TypeAssistantCG typeAssistant;
	private LocationAssistantCG locationAssistant;
	private BindAssistantCG bindAssistant;

	public AssistantManager()
	{
		this.expAssistant = new ExpAssistantCG(this);
		this.declAssistant = new DeclAssistantCG(this);
		this.stmAssistant = new StmAssistantCG(this);
		this.typeAssistant = new TypeAssistantCG(this);
		this.locationAssistant = new LocationAssistantCG(this);
		this.bindAssistant = new BindAssistantCG(this);
	}

	public ExpAssistantCG getExpAssistant()
	{
		return expAssistant;
	}

	public DeclAssistantCG getDeclAssistant()
	{
		return declAssistant;
	}

	public StmAssistantCG getStmAssistant()
	{
		return stmAssistant;
	}

	public TypeAssistantCG getTypeAssistant()
	{
		return typeAssistant;
	}

	public LocationAssistantCG getLocationAssistant()
	{
		return locationAssistant;
	}

	public BindAssistantCG getBindAssistant()
	{
		return bindAssistant;
	}
}
