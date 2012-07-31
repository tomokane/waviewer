package jp.critique.waviewer;

import java.io.IOException;

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
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class WAViewerActivity extends Activity implements OnClickListener{
	// -----------------------------------
	// constant variables
	// -----------------------------------
	private final String TAG = "WAViewerActivity";
	private final String WA_URL = "http://api.wolframalpha.com/v2/query?input=pi&appid=XXXX";
	
	// -----------------------------------
	// instance variables
	// -----------------------------------
	private String appid = "xxxxx";
	private Uri.Builder builder;
	
	private DefaultHttpClient client;
	private HttpUriRequest method;
	private HttpResponse response = null;
	
	// UI instance
	private Button submitQuery;
	private EditText inputText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waviewer);
        
        submitQuery = (Button) findViewById(R.id.submitQuery);
        inputText = (EditText) findViewById(R.id.inputText);
        
        submitQuery.setOnClickListener(this);
        
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
        
        builder.appendQueryParameter("in", keyword);
        
        return builder.build().toString();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_waviewer, menu);
        return true;
    }

	public void onClick(View v) {
		String keyword = inputText.getText().toString();
		
		HttpGet request = new HttpGet(createQuery(keyword));
		
		try {
			String result = client.execute(request, new ResponseHandler<String>() {

				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					switch (response.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						
						return EntityUtils.toString(response.getEntity(), "UTF-8");
					case HttpStatus.SC_NOT_FOUND:
						throw new RuntimeException("404 Not found.");

					default:
						throw new RuntimeException("Error");
					}
				}
			});
			Log.d(TAG,result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

    
}
