/**
 * 
 */
package jp.critique.waviewer;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


/**
 * @author tomokane
 *
 */
public class WAFeedHandler extends DefaultHandler {
    
    private final String TAG = "WAFeedHandler";
    
    private final String RESULT = "queryresult";
    private final String POD = "pod";
    private final String TEXT = "plaintext";
    private final String IMG = "img";
    
    public class PodItem {
        public String title;
        public String plainText;
        public String imgUrl;
        
        @Override
        public String toString() {
            return plainText;
        }
    }
    
    private StringBuffer buf;
    private ArrayList<PodItem> podItems;
    private PodItem item;
    
    private boolean inPod = false;
    
    public ArrayList<PodItem> getParsedItems() {
        return podItems;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startDocument()
     */
    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        podItems = new ArrayList<PodItem>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(localName.equalsIgnoreCase(POD)) {
            item = new PodItem();
            for (int i = 0; i < attributes.getLength(); i++) {// get title
                if(attributes.getLocalName(i).equals("title")) {
                    item.title = attributes.getValue(i);
                }
            }
            inPod = true;
        } else if (localName.equalsIgnoreCase(IMG) && inPod) {// get image url
            for (int i = 0; i < attributes.getLength(); i++) {
                if(attributes.getLocalName(i).equals("src")) {
                    item.imgUrl = attributes.getValue(i);
                }
            }
        } else if(localName.equalsIgnoreCase(TEXT) && inPod) {
            buf = new StringBuffer();
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
        if(localName.equalsIgnoreCase(POD)) {
            podItems.add(item);
            inPod = false;
        } else if (localName.equalsIgnoreCase(TEXT) && inPod) {
            item.plainText = buf.toString();
        }
        buf = null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        if(buf != null) {
            for (int i = 0; i < start + length; i++) {
                buf.append(ch[i]);
            }
        }
    }
    
    
    
}
