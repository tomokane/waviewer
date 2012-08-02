/**
 * 
 */
package jp.critique.waviewer;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * @author tomokane
 *
 */
public class RestTask extends AsyncTask<HttpUriRequest, Void, String> {
    
    public static final String HTTP_RESPONSE = "httpResponse";
    
    private Context context;
    private HttpClient client;
    private String action;
    
    /**
     * @param context
     * @param action
     */
    public RestTask(Context context, String action) {
        this.context = context;
        this.action = action;
        this.client = new DefaultHttpClient();
    }

    /**
     * @param context
     * @param client
     * @param action
     */
    public RestTask(Context context, String action, HttpClient client) {
        this.context = context;
        this.client = client;
        this.action = action;
    }

    @Override
    protected String doInBackground(HttpUriRequest... params) {
        try {
            HttpUriRequest request = params[0];
            HttpResponse serverResponse = client.execute(request);
            
            BasicResponseHandler handler = new BasicResponseHandler();
            String response = handler.handleResponse(serverResponse);
            return response;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent(action);
        intent.putExtra(HTTP_RESPONSE, result);
        
        context.sendBroadcast(intent);
    }
    
}
