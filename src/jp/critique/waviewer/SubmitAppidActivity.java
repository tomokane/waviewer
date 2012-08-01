/**
 * 
 */
package jp.critique.waviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author 0608AND
 *
 */
public class SubmitAppidActivity extends PreferenceActivity 
{
	private static final String APPID = "id";
	private static final Uri WA_SIGNUP_URI = Uri.parse("https://developer.wolframalpha.com/portal/apisignup.html");

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_submitappid);
	}
	
	public static String getID(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(APPID, "");
	}

}
