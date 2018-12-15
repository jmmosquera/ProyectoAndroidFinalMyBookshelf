package iezv.jmm.mybookshelfproject;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iezv.jmm.mybookshelfproject.Book.AddBook;
import iezv.jmm.mybookshelfproject.Book.BookAdapter;
import iezv.jmm.mybookshelfproject.Book.CheckBook;
import iezv.jmm.mybookshelfproject.Firebase.firebase;
import iezv.jmm.mybookshelfproject.SQLite.BookViewModel;
import iezv.jmm.mybookshelfproject.SQLite.DBLibro;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private TextView emptyText;
    private RecyclerView rvBooks;
    private RecyclerView.LayoutManager rvBooksLm;
    private RecyclerView.Adapter rvBooksA;
    private List<DBLibro> myBooks = new ArrayList<DBLibro>();
    private List<DBLibro> filteredBooks = new ArrayList<DBLibro>();
    private BookViewModel BVM;
    private SearchView searchView;
    private ImageButton filterButton;
    private Dialog filterBookDialog;
    private boolean filtering = false;
    private int status = 0;
    private firebase FB = new firebase();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        rvBooks = findViewById(R.id.rvBooks);
        searchView = findViewById(R.id.searchView);
        filterButton = findViewById(R.id.filterButton);
        emptyText = findViewById(R.id.empty_view);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddBook.class);
                startActivityForResult(i, 0);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        BVM = ViewModelProviders.of(this).get(BookViewModel.class);

        try {
            init();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void init() throws InterruptedException {

        touchHandler();
        //Carga el RecyclerView principal. Cargará la vista modificada en caso de que el teléfono esté volteado.
        final BookAdapter adapter = new BookAdapter(this);
        rvBooks.setAdapter(adapter);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        }else{
            rvBooks.setLayoutManager(new LinearLayoutManager(this));
        }

        //Inicializa la base de datos y pone en marcha el Observer.

        FB.download(BVM);

        Thread.sleep(2000);

        BVM.getAllBooks().observe(this, new Observer<List<DBLibro>>() {
            @Override
            public void onChanged(@Nullable final List<DBLibro> books) {
                status = 0;
                filtering = false;
                MainActivity.this.myBooks = books;
                int size = books.size();

                DBLibro[] myArray = new DBLibro[size];
                int counter = 0;
                for (DBLibro libros : books) {
                    myArray[counter] = libros;
                    counter++;
                }

                FB.upload(myArray);
                adapter.setBooks(books);
                rvBooks.setAdapter(adapter);

                //Añade un mensaje en caso de que no haya libros en la base de datos.
                if((myBooks.isEmpty()||myBooks==null)&&(filteredBooks.isEmpty()||filteredBooks==null)){
                    rvBooks.setVisibility(View.INVISIBLE);
                    emptyText.setVisibility(View.VISIBLE);
                }else{
                    rvBooks.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                }
            }
        });
    }

    //Establece los métodos de la búsqueda.
    private void setupSearch(){

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                processQuery(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                processQuery(s);
                return false;
            }
        });

    }


    //Procesa la búsqueda en función de los caracteres introducidos por el usuario y pone en funcionamiento la Lista filtrada.
    private void processQuery(String query){

        List<DBLibro> searchingBooks = new ArrayList<DBLibro>();

        if(status!=0){
            for (DBLibro currentBook : myBooks) {
                if ((currentBook.getTitle().toLowerCase().contains(query.toLowerCase())&&currentBook.getReadingStatus()==status-1) || (currentBook.getAuthor().toLowerCase().contains(query.toLowerCase())&&currentBook.getReadingStatus()==status-1)) {
                    searchingBooks.add(currentBook);
                }
            }
        }else {
            for (DBLibro currentBook : myBooks) {
                if (currentBook.getTitle().toLowerCase().contains(query.toLowerCase()) || currentBook.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                    searchingBooks.add(currentBook);
                }
            }
        }

        final BookAdapter adapter = new BookAdapter(this);
        filteredBooks = searchingBooks;
        adapter.setBooks(filteredBooks);
        rvBooks.setAdapter(adapter);
        filtering = true;
    }

    //Acciones que podrá realizar el usuario.
    public void touchHandler(){

        setupSearch();


        //Recoge cualquier acción realizada sobre el RecyclerView y envía a la pantalla de consulta del libro seleccionado. Tomará la posición en función de si se está filtrando la lista o no.
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        rvBooks.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);
                        DBLibro item;
                        if(filtering){
                            item = filteredBooks.get(position);
                        }else {
                            item = myBooks.get(position);
                        }
                        Intent intent = new Intent(MainActivity.this, CheckBook.class);
                        intent.putExtra("book", item);
                        startActivity(intent);

                        return true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterBook();
            }
        });
    }

    //Inicia y un diálogo y filtra los libros según su estado actual. Inicializará la Lista filtrada en casio de que sea necesario.
    public void filterBook(){
        filterBookDialog = new Dialog(MainActivity.this, R.style.FullHeightDialog);
        filterBookDialog.setContentView(R.layout.filter_book);
        filterBookDialog.setCancelable(true);
        filterBookDialog.show();
        Button filterBookPopup = filterBookDialog.findViewById(R.id.filterBookPopup);

        filterBookPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup radioGroupStatus = filterBookDialog.findViewById(R.id.radioGroupStatus);
                int id = radioGroupStatus.getCheckedRadioButtonId();
                RadioButton selected = filterBookDialog.findViewById(id);
                String option = getResources().getResourceEntryName(id);//selected.getText().toString();

                switch (option){
                    case "status0":
                        status = 0;
                        filtering = false;
                        break;
                    case "status1":
                        status = 1;
                        filtering = true;
                        break;
                    case "status2":
                        status = 2;
                        filtering = true;
                        break;
                    case "status3":
                        status = 3;
                        filtering = true;
                        break;
                    case "status4":
                        status = 4;
                        filtering = true;
                        break;
                }
                if(status!=0){
                    filteredBooks = new ArrayList<>();

                    for(DBLibro currentBook : myBooks ){
                        if (currentBook.getReadingStatus()==status-1) {
                            filteredBooks.add(currentBook);
                        }
                    }

                    final BookAdapter adapter = new BookAdapter(MainActivity.this);
                    adapter.setBooks(filteredBooks);
                    rvBooks.setAdapter(adapter);
                }else{
                    final BookAdapter adapter = new BookAdapter(MainActivity.this);
                    adapter.setBooks(myBooks);
                    rvBooks.setAdapter(adapter);
                }
                filterBookDialog.dismiss();
            }
        });
    }

    //Métodos propios del Navigation Drawer.
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast toast1 =
                    Toast.makeText(getApplicationContext(),
                            "Toast por defecto", Toast.LENGTH_SHORT);

            toast1.show();
            return true;
        }
        if(id == R.id.action_login){
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivityForResult(i, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_help) {
            Intent i = new Intent(this, Help.class);
            startActivity(i);
        }else if (id == R.id.sortbytitleAZ){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 1);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 1);
            }
        }else if (id == R.id.sortbytitleZA){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 2);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 2);
            }
        }else if (id == R.id.sortbyauthorAZ){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 3);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 3);
            }
        }else if (id == R.id.sortbyauthorZA){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 4);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 4);
            }
        }else if (id == R.id.oldest){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 5);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 5);
            }
        }else if (id == R.id.newest){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 6);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 6);
            }
        }else if (id == R.id.lastfinished){
            if(filtering){
                sorter((ArrayList<DBLibro>) filteredBooks, 7);
            }else{
                sorter((ArrayList<DBLibro>) myBooks, 7);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Función a la que se accede a partir del Navigation Drawer. Ordena la Lista (ya sea la filtrada o la original, en función de cuál esté en uso) en base a ciertos criterios.
    public void sorter(List<DBLibro> books, int order){

        switch (order){
            case 1:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        @Override
                        public int compare(final DBLibro object1, final DBLibro object2) {
                            return object1.getTitle().compareTo(object2.getTitle());
                        }
                    });
                }
                break;
            case 2:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        @Override
                        public int compare(final DBLibro object1, final DBLibro object2) {
                            return object2.getTitle().compareTo(object1.getTitle());
                        }
                    });
                }
                break;
            case 3:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        @Override
                        public int compare(final DBLibro object1, final DBLibro object2) {
                            return object1.getAuthor().compareTo(object2.getAuthor());
                        }
                    });
                }
                break;
            case 4:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        @Override
                        public int compare(final DBLibro object1, final DBLibro object2) {
                            return object2.getAuthor().compareTo(object1.getAuthor());
                        }
                    });
                }
                break;
            case 5:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
                        @Override
                        public int compare(DBLibro o1, DBLibro o2) {
                            try {
                                return f.parse(o1.getStartDate()).compareTo(f.parse(o2.getStartDate()));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                }
                break;
            case 6:
                if (books.size() > 0) {
                    Collections.sort(books, new Comparator<DBLibro>() {
                        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

                        @Override
                        public int compare(DBLibro o1, DBLibro o2) {
                            try {
                                return f.parse(o2.getStartDate()).compareTo(f.parse(o1.getStartDate()));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                }
                break;
            case 7:
                List<DBLibro> finishedBooks = new ArrayList<>();
                for(DBLibro currentBook : myBooks ){
                    if (currentBook.getReadingStatus()==2) {
                        finishedBooks.add(currentBook);
                    }
                }
                if (finishedBooks.size() > 0) {
                    Collections.sort(finishedBooks, new Comparator<DBLibro>() {
                        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

                        @Override
                        public int compare(DBLibro o1, DBLibro o2) {
                            try {
                                return f.parse(o2.getEndDate()).compareTo(f.parse(o1.getEndDate()));
                            } catch (ParseException e) {
                                throw new IllegalArgumentException(e);
                            }
                        }
                    });
                }
                books = finishedBooks;
                filtering = true;
                break;
        }

        if(filtering){
            filteredBooks = books;
            final BookAdapter adapter = new BookAdapter(this);
            adapter.setBooks(filteredBooks);
            rvBooks.setAdapter(adapter);
        }else{
            myBooks = books;
            final BookAdapter adapter = new BookAdapter(this);
            adapter.setBooks(myBooks);
            rvBooks.setAdapter(adapter);
        }
    }
}
