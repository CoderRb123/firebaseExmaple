package com.rehan.firebasedemo.adapter;

/**
* Interface to Control Action Like on Accept and Reject
* from Adapter to Parent Activity. Will act like callback
*/
public interface AdminAction {
    /**
    * OnAccept Method will take Assignment id and will
    * perform update query with status updated to ACCEPTED
    * */
    void onAccept(String id);
    /**
     * OnReject Method will take Assignment id and will
     * perform update query with status updated to REJECT
     * */
    void  onReject(String id);
}
