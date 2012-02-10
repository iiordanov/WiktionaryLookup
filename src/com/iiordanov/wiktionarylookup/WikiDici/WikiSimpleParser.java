// WikiSimpleParser.java
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

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;

import com.iiordanov.wiktionarylookup.util.HttpUtil;
import com.iiordanov.wiktionarylookup.util.ResourceUtil;

/**
 * WikiParser is used to download and reformat wiki page data (html) for a given word.
 */
public class WikiSimpleParser {
	
	private static final String ENCODING = "utf-8";
	private static final String WIKI_BASE = "http://en.wiktionary.org/";
	private static final String WIKI_HOME = "http://en.wiktionary.org/images/wiktionary-en.png";
	
	private static void cleanLinks (StringBuilder html) {
		int nextA = -1;
		while ((nextA = html.indexOf ("<a ", nextA + 1)) != -1) {
			int endA = html.indexOf ("</a>", nextA) + 4;
			int startContent = html.indexOf (">", nextA) + 1;
			int endContent = html.indexOf ("</a>", nextA);
			if (startContent == 0 || endContent == -1) continue;
			int startHref = html.indexOf ("href=\"", nextA) + 6;
			String href = html.substring (startHref, html.indexOf ("\"", startHref));
			
			if (startHref == 5 || !Wiktionary.isWikiUrl (href))
				html.replace (nextA, endA, html.substring (startContent, endContent));
		}
	}
	
	/**
	 * Given a StringBuilder containing a Wiki page, injects style rules from resource.
	 * @param page Page being assembled.
	 * @param ctx Context to use when loading resources (must be main Context).
	 */
	private static void injectStyle (StringBuilder page, Context ctx) {
    	String css = ResourceUtil.loadResToString (R.raw.wiki_css, ctx);
    	page.insert (0, css);
    }
	
	/**
     * Given a web view, loads html returned by getHtml into it.
     * Only valid after successful parse.
     * @param wv
     */
    public static void loadHtmlIntoWebView (WebView wv, String html) {
    	wv.loadDataWithBaseURL (WIKI_BASE, html, null, ENCODING, null);
    }
    
    /**
	 * Removes contents before start and after end.
	 * @param sb
	 * @param start Remove contents up to (and possibly including) this.
	 * @param end Remove contents after (and possibly including) this.
	 * @param inclusive
	 */
	private static void selectSubstringFromTo (StringBuilder sb, String start, String end, boolean inclusive) {
    	int starti = sb.indexOf (start);
    	if (0 <= starti)
    		sb.delete (0, starti + (inclusive ? 0 : start.length ()));
    	
    	int endi = sb.indexOf (end);
    	if (0 <= endi)
    		sb.delete (endi + (inclusive ? end.length () : 0), sb.length ());
    }
    private String html;
    private Context mCtx;
    
    private String word;
    
    /**
     * @param word Word to parse Wiki page of.
     * @param context Main context so we can access resources.
     */
    public WikiSimpleParser (String word, Context context) {
    	this.word = word;
		mCtx = context;
	}
    
    /**
     * @return Html of Wiktionary word page. Only valid after successful parse.
     */
    public String getHtml () {
    	return html;
    }
    
    /**
     * @return Original word passed to constructor.
     */
    public String getWord () {
    	return word;
    }
    
    /**
     * Given a web view, loads html returned by getHtml into it.
     * Only valid after successful parse.
     * @param wv
     */
    public void loadHtmlIntoWebView (WebView wv) {
    	loadHtmlIntoWebView (wv, getHtml ());
    }

	/**
     * Downloads the wiki page and attempts to format it for the device's screen.
     * @return Html string of formatted wiki word page.
     * @throws WordNotFoundException
     */
    public String parse () throws WordNotFoundException {
    	html = HttpUtil.curl (Wiktionary.wordToWikiUrl (getWord ()));
    	if (html == null || html.contains ("<div class=\"noarticletext\">")) {
    		WordNotFoundException e = new WordNotFoundException (getWord ());
    		Log.e ("WikiParser", e.getMessage ());
    		throw e;
    	}
    	
    	{
	    	StringBuilder page = new StringBuilder (html);
	    	selectSubstringFromTo (
	    		page,
	    		"<!-- start content -->",
	    		"<p><a name=\"Translations\" id=\"Translations\"></a></p>",
	    		false
	    	);
	    	selectSubstringFromTo (
	    		page,
	    		"",
	    		"<div class=\"printfooter\">",
	    		false
	    	);
	    	injectStyle (page, mCtx);
	    	if (Preferences.LINKS_ACTIVE) {
	    		cleanLinks (page);
	    	} else {
	    		page.insert (0, "<style type=\"text/css\"> a { text-decoration: none; color: inherit; } </style>");
	    	}

	    	html = page.toString ();
	    	//String template = ResourceUtil.loadResToString (R.raw.wiki_page_template, mCtx);
	    	//html = template.replace ("CONTENT", html);   	
    	}  	
		return html;
    }
    
}
