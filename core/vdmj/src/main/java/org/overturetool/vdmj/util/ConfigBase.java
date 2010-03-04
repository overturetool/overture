/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Properties;

public class ConfigBase
{
	private static Properties props = new Properties();

	public static void init(String resource, Class<?> target) throws Exception
	{
		FileInputStream fis = null;

		try
		{
    		URL rurl = ConfigBase.class.getResource("/" + resource);

    		if (rurl == null)
    		{
    			throw new Exception(resource + " is not on the classpath");
    		}

    		String propertyFile = rurl.getPath();
			fis = new FileInputStream(propertyFile);
			props.load(fis);

			for (Field f : target.getFields())
			{
				String name = f.getName();
				Class<?> type = f.getType();
				String value = props.getProperty(name.replace('_', '.'));

				if (value != null)
				{
					if (type == Integer.TYPE)
					{
						f.setInt(target, Integer.parseInt(value));
					}
					else if (type == Boolean.TYPE)
					{
						f.setBoolean(target, Boolean.parseBoolean(value));
					}
					else if (type == String.class)
					{
						f.set(target, value);
					}
				}
			}
		}
		catch (Exception ex)
		{
			throw new Exception("Config exception : " + ex.getMessage());
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close();
				}
				catch (IOException e)
				{
					// so?
				}
			}
		}
	}

	public static String getProperty(String key, String def)
	{
		return props.getProperty(key, def);
	}
}
