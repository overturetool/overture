/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.internal;

import java.io.File;

import org.overture.ide.core.resources.IVdmProject;
import org.overture.interpreter.traces.TraceReductionType;

public class TraceExecutionSetup
{
	final IVdmProject project;
	final String container;
	final String traceName;
	final File coverageFolder;
	final boolean customReduction;

	final float subset;
	final TraceReductionType reductionType;
	final long seed;

	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName, final File coverage)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = false;

		this.subset = 1.0F;
		this.reductionType = TraceReductionType.NONE;
		this.seed = 999;
	}

	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName,
			final File coverage, float subset,
			TraceReductionType reductionType, long seed)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = true;

		this.subset = subset;
		this.reductionType = reductionType;
		this.seed = seed;
	}

	public TraceExecutionSetup(final IVdmProject project,
			final String container, final String traceName,
			final File coverage, float subset,
			TraceReductionType reductionType, long seed, boolean customReduction)
	{
		this.project = project;
		this.container = container;
		this.traceName = traceName;
		this.coverageFolder = coverage;
		this.customReduction = customReduction;

		this.subset = subset;
		this.reductionType = reductionType;
		this.seed = seed;
	}
}
