package model;

/**
 * Created by me866chuan on 8/24/14.
 */
public class ObjectItem {
    public int itemId;
    public String stringId;
    public String itemName;

    // constructor
    public ObjectItem(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    // constructor
    public ObjectItem(String stringId, String itemName) {
        this.stringId = stringId;
        this.itemName = itemName;
    }
}
