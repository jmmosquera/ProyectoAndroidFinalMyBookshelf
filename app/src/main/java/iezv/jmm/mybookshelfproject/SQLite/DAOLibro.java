package iezv.jmm.mybookshelfproject.SQLite;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface DAOLibro {

    @Insert
    void insert(DBLibro libro);

    @Insert
    void insertAuthor(DBAutor autor);

    @Query("SELECT name FROM author_table")
    LiveData<List<String>> getAuthors();

    @Query("SELECT COUNT(*) FROM author_table WHERE name = :name")
    int checkAuthor(String name);

    @Query("DELETE FROM reading_table")
    void deleteAll();

    @Query("DELETE FROM reading_table WHERE ID = :dId")
    void delete(int dId);

    @Query("SELECT * FROM reading_table ORDER BY title ASC")
    LiveData<List<DBLibro>> getReadingsByName();

    @Query("SELECT * FROM reading_table ORDER BY author ASC")
    LiveData<List<DBLibro>> getReadingsByAuthor();

    @Query("SELECT * FROM reading_table where author LIKE :sauthor")
    LiveData<List<DBLibro>> searchByAuthor(String sauthor);

    @Query("SELECT * FROM reading_table WHERE title LIKE :stitle")
    LiveData<List<DBLibro>> searchByTitle(String stitle);

    @Query("SELECT * FROM reading_table WHERE title LIKE :sId")
    List<DBLibro> searchById(int sId);

    @Query("UPDATE reading_table SET title = :uTitle, author = :uAuthor, cover = :uCover, summary = :uSummary, start_date = :uStartDate, end_date = :uEndDate, reading_status = :uStatus, rating = :uRating WHERE ID = :uId ")
    void updateDB(int uId, String uTitle, String uAuthor, String uCover, String uSummary, String uStartDate, String uEndDate, int uStatus, int uRating);


}
