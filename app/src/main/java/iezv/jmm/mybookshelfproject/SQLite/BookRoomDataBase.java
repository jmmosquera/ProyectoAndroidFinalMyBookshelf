package iezv.jmm.mybookshelfproject.SQLite;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DBLibro.class, DBAutor.class}, version = 1)
public abstract class BookRoomDataBase extends RoomDatabase {

    public abstract DAOLibro Dao();

    private static volatile BookRoomDataBase INSTANCE;

    static BookRoomDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BookRoomDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BookRoomDataBase.class, "reading_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
