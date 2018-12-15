package iezv.jmm.mybookshelfproject.SQLite;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class BookViewModel extends AndroidViewModel {

    private BookRepository mRepository;

    private LiveData<List<DBLibro>> allBooks;
    private LiveData<List<DBLibro>> allBooksAuth;

    public BookViewModel (Application application) {
        super(application);
        mRepository = new BookRepository(application);
        allBooks = mRepository.getReadingsName();
        allBooksAuth = mRepository.getReadingsAuthor();
    }

    public LiveData<List<DBLibro>> getAllBooks() {
        return allBooks;
    }

    public LiveData<List<DBLibro>> getAllBooksAuth() {
        return allBooksAuth;
    }

    public LiveData<List<String>> getAllAuthors() {
        return mRepository.getAuth();
    }

    public void insert (DBLibro book) {
        mRepository.insert(book);
    }

    public void deleteRead(DBLibro book){
        mRepository.deleteReading(book);
    }

    public void insertAuthor(DBAutor author) {
        mRepository.insertAuth(author);
    }

    LiveData<List<DBLibro>> searchAuthor(String auth) {
        return mRepository.searchByAuthor(auth);
    }

    LiveData<List<DBLibro>> searchTitle(String titl) {
        return mRepository.searchByTitle(titl);
    }
}
