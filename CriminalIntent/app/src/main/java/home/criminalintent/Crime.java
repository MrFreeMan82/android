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
    private Date mTime;
    private boolean mSolved;
    private String mSuspect;

    public UUID getID() {return mID;}

    public String getTitle() {return mTitle;}

    public void setTitle(String mTitle) {this.mTitle = mTitle;}

    public Date getDate() {return mDate;}

    public void setDate(Date mDate) {this.mDate = mDate;}

    public Date getTime(){return mTime;}

    public void setTime(Date time){this.mTime = time;}

    public boolean isSolved() {return mSolved;}

    public void setSolved(boolean mSolved) {this.mSolved = mSolved;}

    public String getSuspect(){return mSuspect;}

    public void setSuspect(String suspect){mSuspect = suspect;}

    public Crime() {this(UUID.randomUUID());}

    public Crime(UUID id){mID = id; mDate = new Date(); mTime = new Date();}

    public String getPhotoFileName()
    {
        return "IMG_" + getID().toString() + ".jpg";
    }
}
