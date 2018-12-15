package iezv.jmm.mybookshelfproject.Book;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import iezv.jmm.mybookshelfproject.Firebase.firebase;
import iezv.jmm.mybookshelfproject.R;
import iezv.jmm.mybookshelfproject.SQLite.BookViewModel;
import iezv.jmm.mybookshelfproject.SQLite.DBAutor;
import iezv.jmm.mybookshelfproject.SQLite.DBLibro;


public class AddBook extends AppCompatActivity {

    private android.support.design.widget.TextInputEditText title;
    private android.support.design.widget.TextInputLayout titleLayout;
    private AutoCompleteTextView author;
    private android.support.design.widget.TextInputLayout authorLayout;
    private android.widget.Spinner readingStatus;
    private android.widget.ImageView uploadCover;
    private android.support.design.widget.TextInputEditText summary;
    private android.widget.Button saveBook;
    private android.widget.ImageButton imageButton;
    private android.support.design.widget.TextInputEditText startReading;
    private android.support.design.widget.TextInputLayout startReadingLayout;
    private android.support.design.widget.TextInputEditText endReading;
    private android.support.design.widget.TextInputLayout endReadingLayout;
    private int selected = 0;
    private Dialog rateBook;
    private RatingBar rateBookRatingbar;
    int rating;
    Uri coverUri;
    Bitmap cover = null;
    String coverPath;
    private android.support.v7.widget.Toolbar addBookToolbar;
    private ImageView deleteIcon;

    private DBLibro book;
    private DBLibro editableBook;
    private BookViewModel BVM;
    private static final int LOAD_IMAGE_CODE = 42;
    private firebase FB = new firebase();

    private List<String> autores= new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        this.endReadingLayout = findViewById(R.id.endReadingLayout);
        this.endReading = findViewById(R.id.endReading);
        this.startReadingLayout = findViewById(R.id.startReadingLayout);
        this.startReading = findViewById(R.id.startReading);
        this.saveBook = findViewById(R.id.saveBook);
        this.summary = findViewById(R.id.summary);
        this.uploadCover = findViewById(R.id.uploadCover);
        this.readingStatus = findViewById(R.id.readingStatus);
        this.authorLayout = findViewById(R.id.authorLayout);
        this.author = findViewById(R.id.author);
        this.titleLayout = findViewById(R.id.titleLayout);
        this.title = findViewById(R.id.title);
        this.imageButton = findViewById(R.id.imageButton);
        this.addBookToolbar = findViewById(R.id.addBookToolbar);
        this.deleteIcon = findViewById(R.id.deleteIcon);
        addBookToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.allWhite));

        //Inicializamos la base de datos y obtenemos la lista de los autores.
        BVM = ViewModelProviders.of(this).get(BookViewModel.class);
        getAuthors();

        //Si estamos accediendo a esta pantalla desde los datos de un libro existente, los cargamos.
        if(getIntent().getExtras() == null){
            newBook();
        }else {
            Bundle data = getIntent().getExtras();
            editableBook = data.getParcelable("book");
            chargeBook();
        }


    }

    //Este conjunto de acciones se cargará tanto si estamos añadiendo como editando un libro.
    public void buttonHandlerSetA(){
        //Cargamos la imagen.
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, LOAD_IMAGE_CODE);
            }
        });

        //Dependiendo de si hemos terminado el libro o no, mostraremos o dejaremos de mostrar la caja de texto con la fecha de finalización.
        readingStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = readingStatus.getSelectedItemPosition();
                if(selected>1){
                    endReadingLayout.setVisibility(View.VISIBLE);
                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    String date = day+"/"+(month+1)+"/"+year;
                    endReading.setText(date);
                }else{
                    endReadingLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Este botón permite guardar los datos del libro.
        saveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Controlamos si se ha introducido un título y un autor. Mostramos un mensaje de error si no es el caso.
                if(title.getText().toString().equals("")){
                    titleLayout.setError(getResources().getText(R.string.requiredField));
                }else if(author.getText().toString().equals("")){
                    authorLayout.setError(getResources().getText(R.string.requiredField));
                }else {
                    //En caso de que se haya terminado o abandonado el libro, mostramos un diálogo para valorarlo.
                    if (selected > 1) {

                        rateBook = new Dialog(AddBook.this, R.style.FullHeightDialog);
                        rateBook.setContentView(R.layout.rate_book);
                        rateBook.setCancelable(true);


                        Button rateBookButton = rateBook.findViewById(R.id.rateBookButton);

                        rateBookRatingbar = rateBook.findViewById(R.id.rateBookRatingbar);
                        rating = Math.round(rateBookRatingbar.getRating());

                        //Una vez valorado el libro, recogemos todos los datos y los guardamos en un nuevo objeto en la base de datos.
                        rateBookButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rating = Math.round(rateBookRatingbar.getRating());

                                DBAutor autor = new DBAutor(0, author.getText().toString());

                                BVM.insertAuthor(autor);

                                DBLibro book = new DBLibro(0, title.getText().toString(), autor.getName(), coverPath, startReading.getText().toString(), endReading.getText().toString(), summary.getText().toString(), selected, rating);
                                if (editableBook != null) {
                                    book.setBid(editableBook.getBid());
                                    if (book.getCover() == null) {
                                        book.setCover(editableBook.getCover());
                                    }
                                    storeBook(book);
                                    rateBook.dismiss();
                                    Intent returnIntent = new Intent();
                                    setResult(CheckBook.RESULT_OK, returnIntent);
                                    finish();
                                } else {
                                    storeBook(book);
                                    rateBook.dismiss();

                                    finish();
                                }

                            }
                        });
                        rateBook.show();
                        //En caso de no haber terminado el libro, guardamos los datos directamente, sin la valoración.
                    } else {
                        DBLibro book = new DBLibro(0, title.getText().toString(), author.getText().toString(), coverPath, startReading.getText().toString(), endReading.getText().toString(), summary.getText().toString(), selected, 0);
                        if (editableBook != null) {
                            if (book.getCover() == null) {
                                book.setCover(editableBook.getCover());
                            }
                            book.setBid(editableBook.getBid());
                        }
                        storeBook(book);

                        Intent returnIntent = new Intent();
                        setResult(CheckBook.RESULT_OK, returnIntent);
                        finish();
                    }

                }

            }
        });

        //Abre un diálogo que permite seleccionar la fecha de inicio de lectura.
        startReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePicker();
                datepicker.show(getSupportFragmentManager(), "datepicker1");

            }
        });

        //Abre un diálogo que permite seleccionar la fecha de finalización de lectura.
        endReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datepicker = new DatePicker();
                datepicker.show(getSupportFragmentManager(), "datepicker2");

            }
        });

        addBookToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //Este conjunto de acciones sólo se cargará en caso de que estemos editando un libro.
    public void buttonHandlerSetB(){
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AddBook.this);
                alert.setTitle(R.string.deletebook);
                alert.setMessage(R.string.confirm_deletebook);
                alert.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent returnIntent = new Intent();
                        deleteBook(editableBook);
                        setResult(CheckBook.RESULT_OK,returnIntent);
                        finish();
                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // close dialog
                        dialog.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    //Guarda o borra un libro en la base de datos.
    public void storeBook(DBLibro book){
        BVM.insert(book);
    }

    public void deleteBook(DBLibro book){
        BVM.deleteRead(book);
    }

    //Método que es llamado desde el diálogo para establecer la fecha.
    public void getDate(String date){
        startReading.setText(date);
        startReading.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    public void getEndDate(String date){
        endReading.setText(date);
        endReading.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    //Cambia el color brevemente tras editar la fecha.
    public void changeColorBriefly(){
        int editTextColour = 0;
        TypedArray themeArray = AddBook.this.getTheme().obtainStyledAttributes(new int[] {android.R.attr.editTextColor});
        try {
            int index = 0;
            int defaultColourValue = 0;
            editTextColour = themeArray.getColor(index, defaultColourValue);
        }
        finally
        {
            themeArray.recycle();
        }

        int counter = 200;

        try{
            do{

                counter--;
                SystemClock.sleep(1);
            }while(counter!=0);
        }catch(Exception e){
            e.toString();
        }finally {
            startReading.setTextColor(editTextColour);
            endReading.setTextColor(editTextColour);
        }
    }


    //Acciones que se llevarán a cabo en caso de que estemos añadiendo un libro.
    public void newBook(){
        buttonHandlerSetA();
        addBookToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        addBookToolbar.setTitle(R.string.addbook);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = day+"/"+(month+1)+"/"+year;
        startReading.setText(date);
    }

    //Acciones que se llevarán a cabo en caso de que estemos editando un libro. Se cargarán los datos del libro indicado como parcelable desde la pantalla anterior.
    public void chargeBook(){
        title.setText(editableBook.getTitle());
        author.setText(editableBook.getAuthor());
        readingStatus.setSelection(editableBook.getReadingStatus());
        Picasso.with(AddBook.this).load(Uri.parse("file://"+editableBook.getCover())).into(uploadCover);
        summary.setText(editableBook.getSummary());
        startReading.setText(editableBook.getStartDate());
        endReading.setText(editableBook.getEndDate());
        addBookToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        deleteIcon.setVisibility(View.VISIBLE);
        addBookToolbar.setTitle(R.string.editbook);
        buttonHandlerSetB();
        buttonHandlerSetA();

    }

    //Guarda una imagen en el dispositivo y añade su ruta a la base de datos.
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        String cPath = "/profile"+System.currentTimeMillis()/10000+".jpg";
        File mypath=new File(directory,cPath);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath()+cPath;
    }

    //Recibe los datos del método anterior. En caso de que la imagen que queremos guardar exceda cierto tamaño, la redimensiona hasta que esté por debajo de dichas dimensiones.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == LOAD_IMAGE_CODE) {
            if (data != null) {
                coverUri = data.getData();
                Picasso.with(AddBook.this).load(coverUri).into(uploadCover);
                try {
                    cover = MediaStore.Images.Media.getBitmap(this.getContentResolver(), coverUri);
                    int halfHeight;
                    int halfWidth;
                    if(cover.getWidth() > 500 || cover.getHeight() > 500){
                        halfHeight = cover.getWidth() / 2;
                        halfWidth = cover.getHeight() / 2;
                        while (halfWidth > 500 || halfHeight > 500) {
                            halfHeight = halfHeight / 2;
                            halfWidth = halfWidth / 2;
                        }
                        cover = Bitmap.createScaledBitmap(cover, halfHeight, halfWidth, true);

                    }
                    coverPath = saveToInternalStorage(cover);
                    Toast.makeText(AddBook.this, getResources().getText(R.string.imagesaved), Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(AddBook.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    //Carga la lista de autores desde la base de datos.
    public void getAuthors(){

        BVM.getAllAuthors().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                AddBook.this.autores = strings;

                int counter = 0;
                String[] authors = new String[autores.size()];

                for(String libro : autores){
                    authors[counter] = libro;
                    Log.v("ZZZ", libro);
                    counter++;
                }


                ArrayAdapter<String> autores = new ArrayAdapter<String>(AddBook.this, android.R.layout.simple_dropdown_item_1line, authors);
                author.setAdapter(autores);
            }
        });

    }



}
