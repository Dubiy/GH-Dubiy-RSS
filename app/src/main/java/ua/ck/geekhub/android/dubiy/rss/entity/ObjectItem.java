package ua.ck.geekhub.android.dubiy.rss.entity;

/**
 * Created by Gary on 26.10.2014.
 */
public class ObjectItem {
    private int itemId;
    private String itemName;

    public ObjectItem(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return this.itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

}
