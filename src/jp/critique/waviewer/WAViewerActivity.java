package jp.critique.waviewer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class WAViewerActivity extends Activity implements OnClickListener{
	// -----------------------------------
	// constant variables
	// -----------------------------------
	private final String TAG = "WAViewerActivity";
	
	private final String SEARCH_ACTION = "jp.critique.waviewer.SEARCH";
	
	// -----------------------------------
	// instance variables
	// -----------------------------------
	private String appid = "HWET3H-LKJPV39GHV";
	private Uri.Builder builder;
	
	private WAPodAdapter adapter;
	
	// UI instance
	private Button submitQuery;
	private EditText inputText;
	private ListView listView;

    private ProgressDialog progress;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waviewer);
        
        
        
        submitQuery = (Button) findViewById(R.id.submitQuery);
        inputText = (EditText) findViewById(R.id.inputText);
        listView = (ListView) findViewById(R.id.listView1);
        
        adapter = new WAPodAdapter(this);
        
        listView.setAdapter(adapter);
        
        submitQuery.setOnClickListener(this);
        
        
    }

	/**
	 * 
	 */
	@SuppressLint("NewApi")
	private void checkHasAppid() {
		String id = SubmitAppidActivity.getID(getBaseContext());
        
        if(id.isEmpty()) {
        	Intent preferenceIntent = new Intent(this, SubmitAppidActivity.class);
        	startActivity(preferenceIntent);
        } else {
        	appid = id;
        }
	}

	/**
	 * @param search keyword
	 * @return uri query
	 */
	private String createQuery(String keyword) {
		builder = new Uri.Builder();
        builder.scheme(getString(R.string.schemeHttp));
        builder.encodedAuthority(getString(R.string.api_wolframalpha_com));
        builder.path(getString(R.string.pathForApi));
        
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
            hideKeyboard();
            
            String keyword = inputText.getText().toString();
            Log.d(TAG + "::onClick", createQuery(keyword));
            HttpGet request = new HttpGet(new URI(createQuery(keyword)));
            RestTask task = new RestTask(this, SEARCH_ACTION);
            task.execute(request);
            adapter.clearResults();
            progress = ProgressDialog.show(this, "Searching", "Waiting For Results...", true);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
		
	}
	
	public void hideKeyboard() {
	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
	}

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(SEARCH_ACTION));
        checkHasAppid();
        this.setTitle(appid);
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
            
            Log.d(TAG,response);
            
            try {
            	SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser p = factory.newSAXParser();
				
	            WAFeedHandler parser = new WAFeedHandler();
	            
	            p.parse(new InputSource(new StringReader(response)), parser);
	            
	            for(PodItem item : parser.getParsedItems()) {
	            	adapter.addResults(item);
	            }
	            adapter.notifyDataSetChanged();
	            
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
    };

    
}
