package iezv.jmm.mybookshelfproject.SQLite;

import android.graphics.Bitmap;
import android.provider.BaseColumns;

public class Contract {

    private Contract(){}

    public static abstract class BookTable implements BaseColumns{
        public static final String TABLE="books";
        public static final String IDCLOUD="idcloud";
        public static final String TITLE="title";
        public static final String AUTHOR="author";
        public static final String COVER="cover";
        public static final String READINGSTATUS="readingstatus";
        public static final String STARTDATE="startdate";
        public static final String ENDDATE="enddate";
        public static final String RATING="rating";
        public static final String SUMMARY="summary";
    }

    public static final String SQL_CREATE_BOOKS=
            "create table "+BookTable.TABLE+" ("+
                    BookTable._ID + " integer primary key autoincrement,"+
                    BookTable.IDCLOUD+" text,"+
                    BookTable.TITLE+" text,"+
                    BookTable.AUTHOR+" text,"+
                    BookTable.COVER+" blob,"+
                    BookTable.READINGSTATUS+" integer,"+
                    BookTable.STARTDATE+" text,"+
                    BookTable.ENDDATE+" text,"+
                    BookTable.RATING+" integer,"+
                    BookTable.SUMMARY+" text)";

    public static final String SQL_DROP_BOOKS = "drop table if exists "+BookTable.TABLE;



}
