package iezv.jmm.mybookshelfproject.SQLite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;



@Entity(tableName = "reading_table")
public class DBLibro implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int bid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "author")
    private String author;

    @ColumnInfo(name = "cover")
    private String cover;

    @ColumnInfo(name = "start_date")
    private String startDate;

    @ColumnInfo(name = "end_date")
    private String endDate;

    @ColumnInfo(name = "summary")
    private String summary;

    @ColumnInfo(name = "reading_status")
    private int readingStatus;

    @ColumnInfo(name = "rating")
    private int rating;

    public DBLibro ( int bid, String title , String author , String cover , String startDate , String endDate , String summary , int readingStatus , int rating) {
        this.bid = bid;
        this.title = title;
        this.author = author;
        if (cover == null){this.cover="";}else{this.cover = cover;}
        this.startDate = startDate;
        this.endDate = endDate;
        this.summary = summary;
        this.readingStatus = readingStatus;
        this.rating = rating;
    }


    protected DBLibro(Parcel in) {
        bid = in.readInt();
        title = in.readString();
        author = in.readString();
        cover = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        summary = in.readString();
        readingStatus = in.readInt();
        rating = in.readInt();
    }

    public static final Creator<DBLibro> CREATOR = new Creator<DBLibro>() {
        @Override
        public DBLibro createFromParcel(Parcel in) {
            return new DBLibro(in);
        }

        @Override
        public DBLibro[] newArray(int size) {
            return new DBLibro[size];
        }
    };

    public int getBid () {
        return bid;
    }

    public String getTitle () {
        return title;
    }

    public String getAuthor () {
        return author;
    }

    public String getCover () {
        return cover;
    }

    public String getStartDate () {
        return startDate;
    }

    public String getEndDate () {
        return endDate;
    }

    public String getSummary () {
        return summary;
    }

    public int getReadingStatus () {
        return readingStatus;
    }

    public int getRating () {
        return rating;
    }

    public void setBid (int bid) {
        this.bid = bid;
    }

    public void setTitle (String name) {
        title = name;
    }

    public void setAuthor (String author) {
        this.author = author;
    }

    public void setCover (String cover) {
        this.cover = cover;
    }

    public void setStartDate (String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate (String endDate) {
        this.endDate = endDate;
    }

    public void setSummary (String summary) {
        this.summary = summary;
    }

    public void setReadingStatus (int readingStatus) {
        this.readingStatus = readingStatus;
    }

    public void setRating (int rating) {
        this.rating = rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(bid);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(cover);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(summary);
        dest.writeInt(readingStatus);
        dest.writeInt(rating);
    }


}

