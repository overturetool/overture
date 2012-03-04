/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overturetool.test.examples;

import java.io.File;
import java.util.HashSet;

import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;

public class ProofObligationSlTestCase extends TypeCheckSlTestCase
{
	public ProofObligationSlTestCase()
	{
	}

	public ProofObligationSlTestCase(File file)
	{
		super(file);
	}

	@Override
	public void test() throws Exception
	{
		if (mode == ContentModed.None)
		{
			return;
		}

		Result<ModuleList> resTc = typeCheck();
		
		if(resTc.errors.size()>0)
		{
			return;
		}
		
		Result<ProofObligationList> res = new Result<ProofObligationList>( resTc.result.getProofObligations(),new HashSet<IMessage>(),new HashSet<IMessage>());

		
		compareResults(res,"po.result");
	}

}
