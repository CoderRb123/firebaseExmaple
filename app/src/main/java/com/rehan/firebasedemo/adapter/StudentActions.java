package com.rehan.firebasedemo.adapter;

/**
 * Interface to Control Action Like on Student Card
 * from Adapter to Parent Activity. Will act like callback
 */
public interface StudentActions {
    /**
     * Here we will take id as parameter
     * and perform action onDelete Code
     * From Parent Activity
     * */
    void onDelete(String id);
}
