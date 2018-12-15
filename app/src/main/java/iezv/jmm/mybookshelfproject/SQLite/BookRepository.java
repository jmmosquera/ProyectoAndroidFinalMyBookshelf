package iezv.jmm.mybookshelfproject.SQLite;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class BookRepository {

    private static DAOLibro mDao;
    private LiveData<List<DBLibro>> mBooksByTitle;
    private LiveData<List<DBLibro>> mBooksByAuthor;
    private List<DBAutor> mAuthors;

    private LiveData<List<DBLibro>> searchResult;

    BookRepository (Application application) {
        BookRoomDataBase db = BookRoomDataBase.getDatabase(application);
        mDao = db.Dao();
        mBooksByTitle = mDao.getReadingsByName();
        mBooksByAuthor = mDao.getReadingsByAuthor();
    }

    LiveData<List<DBLibro>> getReadingsName(){
        return mBooksByTitle;
    }

    LiveData<List<DBLibro>> getReadingsAuthor(){
        return mBooksByAuthor;
    }

    LiveData<List<DBLibro>> searchByAuthor( String author) {
        return mDao.searchByAuthor(author);
    }

    LiveData<List<DBLibro>> searchByTitle( String title) {
        return mDao.searchByTitle(title);
    }

    LiveData<List<String>>getAuth(){
        return mDao.getAuthors();
    }

    static boolean exists (int checkId){
        return !mDao.searchById(checkId).isEmpty();
    }


    void deleteReading(DBLibro book){
        new deleteAsyncTask(mDao).execute(book);
    }

    public void insertAuth(DBAutor... author) {
        new insertAsyncAuthor(mDao).execute(author);
    }

    public void insert(DBLibro... book) {
        new insertAsyncTask(mDao).execute(book);
    }


    private static class insertAsyncAuthor extends AsyncTask<DBAutor, Void, Void> {

        private DAOLibro mAsyncTaskDao;

        insertAsyncAuthor(DAOLibro dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBAutor... params) {

            for (DBAutor author : params){
                if(mAsyncTaskDao.checkAuthor(author.getName())==0) {
                    mAsyncTaskDao.insertAuthor(author);
                }
            }
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<DBLibro, Void, Void> {

        private DAOLibro mAsyncTaskDao;

        deleteAsyncTask(DAOLibro dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBLibro... params) {

            for (DBLibro book : params){
                if(book.getBid() != 0){
                    mAsyncTaskDao.delete(book.getBid());
                }
            }
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<DBLibro, Void, Void> {

        private DAOLibro mAsyncTaskDao;

        insertAsyncTask(DAOLibro dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DBLibro... params) {

            for (DBLibro book : params){
                if(book.getBid() != 0){
                    mDao.updateDB(book.getBid(), book.getTitle(), book.getAuthor(), book.getCover(), book.getSummary(), book.getStartDate(), book.getEndDate(), book.getReadingStatus(), book.getRating());
                }else{
                    mAsyncTaskDao.insert(book);
                }
            }
            return null;
        }
    }
}
