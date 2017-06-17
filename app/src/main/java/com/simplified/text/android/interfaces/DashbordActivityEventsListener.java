package com.simplified.text.android.interfaces;

/**
 * Created by pbadmin on 13/6/17.
 */

public interface DashbordActivityEventsListener {

    public void isEditMode(boolean isEditable);

    public void pageChanged();

    public void performSearch(String searchKey);

}
