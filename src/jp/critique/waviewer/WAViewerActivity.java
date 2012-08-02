package jp.critique.waviewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class WAViewerActivity extends Activity implements OnClickListener{
	// -----------------------------------
	// constant variables
	// -----------------------------------
	private final String TAG = "WAViewerActivity";
	
	private final String WA_URL = "http://api.wolframalpha.com/v2/query?input=pi&appid=XXXX";
	private final String APPID = "HWET3H-LKJPV39GHV";
	
	private final String SEARCH_ACTION = "jp.critique.waviewer.SEARCH";
	
	// -----------------------------------
	// instance variables
	// -----------------------------------
	private String appid = "HWET3H-LKJPV39GHV";
	private Uri.Builder builder;
	
	private DefaultHttpClient client;
	private HttpUriRequest method;
	private HttpResponse response = null;
	
	// UI instance
	private Button submitQuery;
	private EditText inputText;

    private ProgressDialog progress;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waviewer);
        
        checkHasAppid();
        
        client = new DefaultHttpClient();
        
        submitQuery = (Button) findViewById(R.id.submitQuery);
        inputText = (EditText) findViewById(R.id.inputText);
        
        submitQuery.setOnClickListener(this);
        
        
    }

	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void checkHasAppid() {
		String id = SubmitAppidActivity.getID(getBaseContext());
        Log.d(TAG,id);
        
        if(id.isEmpty()) {
        	Intent preferenceIntent = new Intent(this, SubmitAppidActivity.class);
        	startActivity(preferenceIntent);
        }
	}

	/**
	 * @param search keyword
	 * @return uri query
	 */
	private String createQuery(String keyword) {
		builder = new Uri.Builder();
        builder.scheme("http");
        builder.encodedAuthority("api.wolframalpha.com");
        builder.path("/v2/query");
        
        builder.appendQueryParameter("appid", appid);
        builder.appendQueryParameter("format", "image,plaintext");
        
        builder.appendQueryParameter("input", keyword);
        
        return builder.build().toString();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_waviewer, menu);
        return true;
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	Intent preferenceIntent = new Intent(this, SubmitAppidActivity.class);
    	startActivity(preferenceIntent);
		return super.onOptionsItemSelected(item);
	}

	public void onClick(View v) {
        try {
            String keyword = inputText.getText().toString();
            HttpGet request = new HttpGet(new URI(createQuery(keyword)));
            RestTask task = new RestTask(this, SEARCH_ACTION);
            task.execute(request);
            progress = ProgressDialog.show(this, "Searching", "Waiting For Results...", true);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
		
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(SEARCH_ACTION));
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if(progress != null) {
                progress.dismiss();
            }
            
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            
        }
    };

    
}
