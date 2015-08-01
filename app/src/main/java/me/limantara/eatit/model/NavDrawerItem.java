package me.limantara.eatit.model;

/**
 * Created by edwinlimantara on 7/31/15.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private int icon;

    /**
     * Create a new drawer item.
     */
    public NavDrawerItem() {

    }

    /**
     * Getter method for private showNotify instance member.
     *
     * @return
     */
    public boolean isShowNotify() {
        return showNotify;
    }

    /**
     * Setter method for private showNotify instance member.
     *
     * @param showNotify
     */
    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    /**
     * Getter method for private title instance member.
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Setter method for private title instance member.
     *
     * @param title
     * @return
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter method to get drawable id
     *
     * @return
     */
    public int getIcon() {
        return icon;
    }

    /**
     * Setter method to set icon with drawable id
     *
     * @param icon
     */
    public void setIcon(int icon) {
        this.icon = icon;
    }
}
