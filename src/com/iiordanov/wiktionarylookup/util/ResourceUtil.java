// ResourceUtil.java
//
// Wiktionary Lookup is the legal property of its developers. Please refer to the
// COPYRIGHT file distributed with this source distribution.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.iiordanov.wiktionarylookup.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.util.Log;

public class ResourceUtil {
	
	public static String loadResToString (int resId, Context ctx) {

		try {
			int read;
			byte[] buffer = new byte[4096];
			ByteArrayOutputStream baos = new ByteArrayOutputStream ();
			InputStream is = ctx.getResources ().openRawResource (resId);

			while (0 < (read = is.read (buffer))) {
				baos.write (buffer, 0, read);
			}

			baos.close ();
			is.close ();

			String data = baos.toString ();

			//Log.i (Global.TAG, "ResourceUtils loaded resource to string: " + resId);

			return data;
		} catch (Exception e) {
			//Log.e (Global.TAG, "ResourceUtils failed to load resource to string", e);
			return null;
		}
	}
}
