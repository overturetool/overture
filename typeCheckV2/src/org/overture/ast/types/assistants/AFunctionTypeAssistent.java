package org.overture.ast.types.assistants;

import org.overture.ast.node.NodeList;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;



public class AFunctionTypeAssistent {

	public static AFunctionType typeResolve(AFunctionType ft, Environment env,
			Object object) {

		if (ft.getResolved())
			return ft;
		else {
			ft.setResolved(true);
		}

		try {
			NodeList<PType> fixed = new NodeList<PType>();

			for (Type type : parameters) {
				fixed.add(type.typeResolve(env, root));
			}

			parameters = fixed;
			result = result.typeResolve(env, root);
			return this;
		} catch (TypeCheckException e) {
			unResolve();
			throw e;
		}

	}

}
