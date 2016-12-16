package home.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Дима on 15.12.2016.
 */

public class Crime {

    private UUID mID;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    public UUID getID() {return mID;}

    public String getTitle() {return mTitle;}

    public void setTitle(String mTitle) {this.mTitle = mTitle;}

    public Date getDate() {return mDate;}

    public void setDate(Date mDate) {this.mDate = mDate;}

    public boolean isSolved() {return mSolved;}

    public void setSolved(boolean mSolved) {this.mSolved = mSolved;}



    public Crime(){mID = UUID.randomUUID(); mDate = new Date();}
}
