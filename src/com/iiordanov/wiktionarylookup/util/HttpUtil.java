// HttpUtil.java
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

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

	  public static String curl (String url) {
	    	String html = null;
	    	
	    	try {
	    		HttpUriRequest get = new HttpGet (url);
	    		HttpResponse response = new DefaultHttpClient ().execute (get);
	    		html = EntityUtils.toString (response.getEntity (), "utf-8");
			} catch (ClientProtocolException e) {
				//Log.e (Global.TAG, "Could not curl", e);
			} catch (IOException e) {
				//Log.e (Global.TAG, "Could not curl", e);
			}
			
			return html;
	    }
	
}
