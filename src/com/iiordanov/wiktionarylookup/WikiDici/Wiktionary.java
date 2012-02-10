// Wiktionary.java
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

package com.iiordanov.wiktionarylookup.WikiDici;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.iiordanov.wiktionarylookup.util.HttpUtil;

import android.util.Log;

public class Wiktionary {
	
	private static final String	START_LINK = "<link>";
	private static final String	END_LINK	= "</link>";
	
	private static final String	WORD_OF_THE_DAY_URL	= "http://toolserver.org/~cmackenzie/wotd-rss.php";
	private static final String ENCODING = "utf-8";
	public static final String WIKI_BASE = "http://en.wiktionary.org/";
	public static final String WIKI_PAGE = "http://en.wiktionary.org/wiki/";
	public static final String WIKI_PAGE_RELATIVE = "/wiki/";
	
	private static String wordOfTheDay = null;
	
	public static boolean isWikiUrl (String url) {
		return url.startsWith (WIKI_PAGE_RELATIVE) || url.startsWith (WIKI_PAGE);
		// return url.matches ("/wiki/[-\\w]+") || url.matches (WIKI_PAGE + "[-\\w]+");	
	}
	
	/**
	 * Given a potential Wiktionary word page URL, attempts to parse the word from it.
	 * @param url of a Wiktionary word page (e.g. "http://en.wiktionary.org/wiki/panacea")
	 * @return word for that page (e.g. "panacea") or null if things don't work out.
	 */
	public static String wikiUrlToWord (String url) {
		if (!isWikiUrl (url)) return null;
		
		String word = url.substring (url.lastIndexOf ("/") + 1, url.length ());
		try {
			word = URLDecoder.decode (word, ENCODING);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		word = word.replace ('_', ' ');
		return word;
	}
	
	/**
	 * Given a word, returns what is likely the url for the Wiktionary page for that word.
	 * @param word
	 * @return Wiktionary URl for word page
	 */
	public static String wordToWikiUrl (String word) {
    	String url = null;
    	try {
			url = WIKI_PAGE + URLEncoder.encode (word, ENCODING);
		} catch (UnsupportedEncodingException e) {
			Log.e ("WikiParser", null, e);
			return null;
		}
		// Wiktionary servers don't like '+' for space.
		url = url.replace ("+", "%20");
		return url;
    }

	public static String getWordOfTheDay () {
		if (null != wordOfTheDay) return wordOfTheDay;
		
		String word = null;
		String rss = HttpUtil.curl (WORD_OF_THE_DAY_URL);
		int start = 0, end = 0;
		while (word == null) {
			String link;
			
			start = rss.indexOf (START_LINK, start+1);
			end = rss.indexOf (END_LINK, start);
			if (start == -1 || end == -1) break;
			link = rss.substring (start + START_LINK.length (), end);
			word = wikiUrlToWord (link);
		}
		
		return wordOfTheDay = word;
	}
}
