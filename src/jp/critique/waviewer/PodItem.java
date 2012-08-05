/**
 * 
 */
package jp.critique.waviewer;

/**
 * @author tomokane
 *
 */
public class PodItem {
    public String title;
    public String plainText;
    public String imgUrl;
    
    @Override
    public String toString() {
        return plainText;
    }
}
