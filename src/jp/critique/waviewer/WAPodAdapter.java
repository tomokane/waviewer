/**
 * 
 */
package jp.critique.waviewer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author tomokane
 *
 */
public class WAPodAdapter extends BaseAdapter {
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    private LayoutInflater inflater;
    
    private ArrayList<PodItem> poditems = new ArrayList<PodItem>();
    
    /**
     * 
     */
    public WAPodAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return poditems.size();
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return position;
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        
        ViewHolder holder;
        
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.pod_item, null);
            
            holder = new ViewHolder();
            holder.titleText = (TextView) convertView.findViewById(R.id.podTitle);
            holder.plainText = (TextView) convertView.findViewById(R.id.podPlainText);
            holder.imageView = (ImageView) convertView.findViewById(R.id.podImage);
            
            convertView.setTag(holder);
            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
        holder.titleText.setText(poditems.get(position).title);
        holder.plainText.setText(poditems.get(position).plainText);
        
        try {
//            ImageView i = (ImageView)findViewById(R.id.image);
            Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(poditems.get(position).imgUrl).getContent());
            holder.imageView.setImageBitmap(bitmap);
          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }

        return convertView;
    }
    
    public void clearResults() {
        poditems.clear();
        notifyDataSetChanged();
    }
    
    public void addResults(PodItem item) {
        poditems.add(item);
    }
    
    static class ViewHolder {
        TextView titleText;
        TextView plainText;
        ImageView imageView;
    }
    
    
}
