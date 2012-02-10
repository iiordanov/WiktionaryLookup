// WikiDici.java
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

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.iiordanov.wiktionarylookup.util.ResourceUtil;
import com.iiordanov.wiktionarylookup.util.ThreadUtil;

/**
 * The main Activity of the Wiktionary application.
 */
public class WikiDici extends Activity {

	/**
	 * OnClickListener for Lookup button.
	 */
	private class LookupButtonClickListener implements OnClickListener {
		@Override
		public void onClick (View v) {
			lookupWord ();
		}
	}
	/**
	 * OnKeyListener for Lookup EditText. Used to handle special case when user
	 * presses enter key.
	 */
	private class LookupTextKeyListener implements OnKeyListener {
		@Override
		public boolean onKey (View view, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				wv.setSelected (true);
				lookupWord ();
				return true;
			}
			return false;
		}
	}
	/**
	 * TextWatcher for Lookup EditText. Eventually used for offering
	 * word suggestions.
	 */
	private class LookupTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged (Editable s) {
		}

		@Override
		public void beforeTextChanged (CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged (CharSequence s, int start, int before, int count) {
			// Log.i ("Wiktionary", s.toString ());
		}
	}
	/**
	 * WebViewClient for main WebView. Used to override url load events. Currently,
	 * if a user presses an intra-wiki link, we intercept it, display the reformatted
	 * word page, and update other interface elements.
	 */
	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading (WebView view, String url) {
			if (Preferences.LINKS_ACTIVE) {
				String word = Wiktionary.wikiUrlToWord (url);
				if (word != null) {
					lookup_text.setText (word);
					lookupWord (word);
				}
			}
			return true;
		}
	}
	private static final String	HTML_KEY	= "html";
	
	static final String LOOKUP_TEXT_IS_FOCUSED_KEY = "lookup_textisFocused";

	private static final String	LOOKUP_TEXT_KEY	= "lookup_text";

	private static final int	MENU_WOTD	= 0;

	private static final String	WORD_KEY	= "word";

	private String html, word;
	private Button lookup_button;
	private AutoCompleteTextView lookup_text;
	
	private SharedPreferences prefs;
	private WebView wv;
	
	private void displayLoadingPage () {
		displayWebViewHtml (ResourceUtil.loadResToString (R.raw.searching, this));
		setTitleWithMessage (null);
	}
	
	private void displayParserResult (WikiSimpleParser parser) {
		word = parser.getWord ();
		
		displayWebViewHtml (parser.getHtml ());
		lookup_text.setText (word);
		setTitleWithMessage (word);
		wv.requestFocus ();
	}
	
	private void displayWebViewHtml (String html) {
		this.html = html;
		WikiSimpleParser.loadHtmlIntoWebView (wv, html);
	}
	
	/**
	 * Loads the word not found page (/res/raw/word_not_found.xml) into the WebView.
	 */
	private void displayWordNotFound (WikiSimpleParser parser) {
		displayWebViewHtml (ResourceUtil.loadResToString (R.raw.word_not_found, this));
	}
	
	private void displayWordOfTheDay () {
		findWordOfTheDay ();
	}
	
	/**
	 * Loads the wiki content for the current word (contents of Lookup EditText)
	 * into the WebView.
	 */
	private void lookupWord () {
		lookupWord (lookup_text.getText ().toString ());
	}

	/**
	 * Loads the wiki content for the given word into the WebView.
	 * 
	 * Initiates an asynchronous call to parser.parse. If parse succeeds,
	 * displayParserResult is called on the main thread. If parse fails,
	 * displayWordNotFound is called on the main thread.
	 * 
	 * @param word The word to lookup and whose wiki data should be displayed.
	 */
	private void lookupWord (String word) {
		ThreadUtil.Task<WikiSimpleParser, WikiSimpleParser> task;
		ThreadUtil.SuccessHandler<WikiSimpleParser>         success;
		ThreadUtil.FailHandler<WikiSimpleParser>            fail;
		
		if (this.word != null && this.word.equals (word)) {
			return;
		}
		
		displayLoadingPage ();
		
		task = new ThreadUtil.Task<WikiSimpleParser, WikiSimpleParser> () {
			public WikiSimpleParser perform (WikiSimpleParser parser) throws Exception {
				parser.parse ();
				return parser;
			}
		};
			
		success = new ThreadUtil.SuccessHandler<WikiSimpleParser> () {
			public void handle (WikiSimpleParser parser) {
				displayParserResult (parser);
			}
		};
		
		fail = new ThreadUtil.FailHandler<WikiSimpleParser> () {
			public void handle (WikiSimpleParser parser, Exception e) {
				displayWordNotFound (parser);
			}
		};
		
		ThreadUtil.performTaskAsync (new WikiSimpleParser (word, this), task, success, fail);
	}
	
	/**
	 * Finds the word of the day and looks it up.
	 */
	private void findWordOfTheDay () {
		ThreadUtil.Task<Object, String>   task;
		ThreadUtil.SuccessHandler<String> success;
		ThreadUtil.FailHandler<Object>    fail;
		
		task = new ThreadUtil.Task<Object, String> () {
			public String perform (Object o) throws Exception {
				return Wiktionary.getWordOfTheDay ();
			}
		};
			
		success = new ThreadUtil.SuccessHandler<String> () {
			public void handle (String word) {
				lookup_text.setText (word);
				lookupWord (word);
			}
		};
		
		fail = new ThreadUtil.FailHandler<Object> () {
			public void handle (Object o, Exception e) {
				Log.e ("WikiDici", e.getMessage (), e);
				displayWordNotFound (null);
			}
		};
		
		ThreadUtil.performTaskAsync (null, task, success, fail);
	}
	
	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate (savedInstanceState);
		setContentView (R.layout.main);

		prefs = getPreferences (MODE_PRIVATE);
		
		wv            = (WebView) findViewById (R.id.webview);
		lookup_text   = (AutoCompleteTextView) findViewById (R.id.lookup_text);
		lookup_button = (Button) findViewById (R.id.lookup_button);
		
		//lookup_text.setThreshold (4);
		//lookup_text.setAdapter (new ArrayAdapter (this, R.layout.word_suggest_row, Words.WORDS));

		word = prefs.getString (WORD_KEY, "");
		setTitleWithMessage (word);
		
		String text = prefs.getString (LOOKUP_TEXT_KEY, "");
		lookup_text.setText (text);
		lookup_text.setSelection (text.length ());
		lookup_text.addTextChangedListener (new LookupTextWatcher ());
		lookup_text.setOnKeyListener (new LookupTextKeyListener ());
		if (prefs.getBoolean (LOOKUP_TEXT_IS_FOCUSED_KEY, true)) {
			lookup_text.requestFocus ();
		} else {
			wv.requestFocus ();
		}
		
		lookup_button.setOnClickListener (new LookupButtonClickListener ());
		
		wv.setWebViewClient (new MyWebViewClient ());
		displayWebViewHtml (
			prefs.getString (HTML_KEY, ResourceUtil.loadResToString (R.raw.index, this))
		);
	}
	
	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		menu.add (0, MENU_WOTD, Menu.NONE, R.string.wotd)
			.setIcon (R.drawable.wotd);
		
		return super.onCreateOptionsMenu (menu);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		switch (item.getItemId ()) {
	    case MENU_WOTD:
	        displayWordOfTheDay ();
	        return true;
	    }
	    return false;
	}

	@Override
	protected void onPause () {
		super.onPause ();
		
		SharedPreferences.Editor ed = prefs.edit ();
		ed.putString (WORD_KEY, word);
		ed.putString (HTML_KEY, html);
		ed.putString (LOOKUP_TEXT_KEY, lookup_text.getText ().toString ());
		ed.putBoolean (LOOKUP_TEXT_IS_FOCUSED_KEY, lookup_button.isFocused ());
		ed.commit ();
	}

	@Override
	protected void onResume () {
		super.onResume ();
	}

	private void setTitleWithMessage (String m) {
		if (m == null || m.equals (""))
			setTitle (getResources ().getText (R.string.app_name));
		else
			setTitle (getResources ().getText (R.string.app_name) + " - " + m);
	}
	
	
	
	
}
