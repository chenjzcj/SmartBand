package com.mtk.map;
import java.util.ArrayList;


public class MessageList {
    private boolean mNewMessage ;
   // private String mCurrentTime;
    private int mSize;
    private ArrayList<MessageListItem> mMessageItems;
    //private boolean isOccupied;

    public MessageList(){
        reset();
    }
    synchronized void reset(){
        if (mMessageItems == null) {
            mMessageItems = new ArrayList<MessageListItem>();
        } else {
            mMessageItems.clear();
        }
        
       // mCurrentTime = UtcUtil.getCurrentTime();
        mSize = 0;
        //isOccupied = false;
        mNewMessage = false;
    }

    public synchronized boolean addSize(int size) {     
            mSize += size;
            return true;
    }   
    public synchronized boolean setNewMessage() {
    //  if (!isOccupied) {
            if (!mNewMessage) {
                mNewMessage = true;
            }
            //isOccupied = true;
        //  Time time = new Time();
    //      time.set(System.getCurrentTime());
    //      mCurrentTime = time.toString();
            return true;            
    //  } else {
//return false;
    //  }
    }
    public synchronized boolean addMessageItem(MessageListItem item) {
    //  if (isOccupied) {
            if (item != null) {
                mMessageItems.add(item);
            //  mSize += 1;
            }
            return true;
    //  } else {
    //      return false;
    //  }
    }

    
    public synchronized MessageListItem[] generateMessageItemArray(){
        return mMessageItems.toArray(new MessageListItem[mMessageItems.size()]);
    }

    public int getCurrentSize(){
        return mSize;
    }

    
}
