package iezv.jmm.mybookshelfproject.SQLite;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "author_table")
public class DBAutor implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int aId;

    @ColumnInfo(name = "name")
    private String name;

    public DBAutor(int aId, String name) {
        this.aId = aId;
        this.name = name;
    }

    public int getAId() {
        return aId;
    }

    public void setAId(int aId) {
        this.aId = aId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected DBAutor(Parcel in) {
        aId = in.readInt();
        name = in.readString();
    }

    public static final Creator<DBAutor> CREATOR = new Creator<DBAutor>() {
        @Override
        public DBAutor createFromParcel(Parcel in) {
            return new DBAutor(in);
        }

        @Override
        public DBAutor[] newArray(int size) {
            return new DBAutor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(aId);
        dest.writeString(name);
    }
}
