package android.bignerdranch.mycheckins;

import java.util.Date;
import java.util.UUID;

public class Record {
    private UUID mId;
    private String  mTitle;
    private Date mDate;
    private String mPlace;
    private String mDetails;
    private String mLatitude;
    private String mLongitude;
    public Record() {
// Generate unique identifier
       this( UUID.randomUUID());

    }
    public Record(UUID id) {
        mId = id;
        mDate = new Date();
    }
    public String toString() {
        return mTitle;
    }
    public UUID getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
    public Date getDate() {
        return mDate;
    }
    public void setDate(Date date) {
        mDate = date;
    }
    public String getPlace() {
        return mPlace;
    }
    public void setPlace(String place) {
        mPlace = place;
    }
    public String getDetails() {
        return mDetails;
    }
    public void setDetails(String details) {
        mDetails = details;
    }

    public String getLatitude() {
        return mLatitude;
    }
    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }
    public String getLongitude() {
        return mLongitude;
    }
    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }
    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
