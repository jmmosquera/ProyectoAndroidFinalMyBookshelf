package iezv.jmm.mybookshelfproject.Book;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import iezv.jmm.mybookshelfproject.Book.Cover;
import iezv.jmm.mybookshelfproject.Mailing.Contact;
import iezv.jmm.mybookshelfproject.Mailing.ContactAdapter;
import iezv.jmm.mybookshelfproject.Mailing.Mailer;
import iezv.jmm.mybookshelfproject.MainActivity;
import iezv.jmm.mybookshelfproject.R;
import iezv.jmm.mybookshelfproject.SQLite.DBLibro;

import static android.view.View.INVISIBLE;


public class CheckBook extends AppCompatActivity {

    private DBLibro currentBook;
    private Toolbar toolbar;
    private android.support.design.widget.CollapsingToolbarLayout toolbarlayout;
    private android.support.design.widget.AppBarLayout appbar;
    private FloatingActionButton fab;
    private Button zoomIn;
    private TextView startDate;
    private TextView endDate;
    private TextView etReadingStatus;
    private RatingBar ratingBar;
    private TextView author;
    private TextView summary;
    private ImageView cover;
    private Dialog sendMailDialog;
    private ImageView mailButton;

    public final int EDITED = 23;
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_book);
        this.appbar = findViewById(R.id.app_bar);
        this.toolbarlayout = findViewById(R.id.toolbar_layout);
        this.toolbar = findViewById(R.id.toolbar);
        this.cover = findViewById(R.id.cover);
        this.mailButton = findViewById(R.id.mailButton);

        //Cargamos los datos del libro desde la pantalla principal.
        Bundle data = getIntent().getExtras();
        currentBook = data.getParcelable("book");

        //Establecemos el título de la actividad como el título del libro que etamos consultando. Cargamos en la actividad la portada.
        toolbar.setTitle(currentBook.getTitle());
        if (currentBook.getCover()==null) {
            cover.setImageDrawable(this.getResources().getDrawable(R.drawable.defaultcover));
        } else{
            Picasso.with(CheckBook.this).load(Uri.parse("file://"+currentBook.getCover())).into(cover);
        }
        setSupportActionBar(toolbar);
        this.zoomIn = findViewById(R.id.zoomIn);
        this.startDate = findViewById(R.id.startDate);
        this.endDate = findViewById(R.id.endDate);
        this.etReadingStatus = findViewById(R.id.etReadingStatus);
        this.ratingBar = findViewById(R.id.ratingBar);
        this.summary = findViewById(R.id.summary);
        this.author = findViewById(R.id.author);

        //Con este botón se podrá acceder a la pantalla de edición del libro.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckBook.this, AddBook.class);
                intent.putExtra("book", currentBook);
                startActivityForResult(intent, EDITED);
            }
        });

        loadData();
        buttonHandler();

    }

    //Conjunto de acciones a realizar con botones.
    public void buttonHandler(){
        //Abre la actividad Cover, que mostrará la portada completa.
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckBook.this, Cover.class);
                intent.putExtra("cover", currentBook.getCover());
                startActivity(intent);

            }
        });
        //Añade navegación hacia atrás.
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Añade la función de recomendar el libro por email.
        mailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

    }

    //Método que carga los datos recibidos del libro actual.
    public void loadData(){

        startDate.setText(this.getResources().getString(R.string.started)+" "+currentBook.getStartDate());
        author.setText(this.getResources().getString(R.string.by)+" "+currentBook.getAuthor());
        summary.setText(currentBook.getSummary());
        int status = currentBook.getReadingStatus()+1;
        String statusList[] = this.getResources().getStringArray(R.array.readingStatus);
        switch(status){
            case 1:
                etReadingStatus.setText(statusList[0]);
                break;
            case 2:
                etReadingStatus.setText(statusList[1]);
                break;
            case 3:
                etReadingStatus.setText(statusList[2]);
                break;
            case 4:
                etReadingStatus.setText(statusList[3]);
                break;
        }

        int statusR = currentBook.getReadingStatus();
        if(statusR<2){
            endDate.setVisibility(View.GONE);
            ratingBar.setVisibility(View.GONE);
        }
        String date = currentBook.getEndDate();
        endDate.setText(this.getResources().getString(R.string.ended)+" "+date);
        ratingBar.setRating(currentBook.getRating());
    }

    //Cerramos esta vista si hemos editado el libro para volver a la pantalla principal.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITED) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }


    //Permite recomendar el libro actual a uno de los contactos del teléfono.
    public void sendMail(){
        sendMailDialog = new Dialog(CheckBook.this, R.style.FullHeightDialog);
        sendMailDialog.setContentView(R.layout.mailer_book);
        sendMailDialog.setCancelable(true);

        RecyclerView rvMail = sendMailDialog.findViewById(R.id.rvMail);
        Button cancelMail = sendMailDialog.findViewById(R.id.cancelMail);
        ImageView ivSend = sendMailDialog.findViewById(R.id.ivSend);

        final GestureDetector mGestureDetector = new GestureDetector(CheckBook.this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        final List<Contact> contactos = loadContactList();
        LinearLayoutManager rvLm = new LinearLayoutManager(this);
        rvMail.setLayoutManager(rvLm);

        ContactAdapter cAdapter = new ContactAdapter(contactos, this);
        rvMail.setAdapter(cAdapter);

        rvMail.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                try {
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                    if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {

                        int position = recyclerView.getChildAdapterPosition(child);
                        Contact item = contactos.get(position);
                        String mail = item.getEmail();
                        //Sólo podremos escribir a aquellos contactos cuyo email tengamos guardado.
                        if(mail.equals(getResources().getText(R.string.noemail).toString())){
                            return false;
                        }else{
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { item.getEmail() });
                            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.irecomendyou)+currentBook.getTitle());
                            intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.irecomendyou)+currentBook.getTitle()+getResources().getString(R.string.abookof)+currentBook.getAuthor()+getResources().getString(R.string.myresume)+currentBook.getSummary());
                            startActivity(intent);
                            return true;
                        }


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


        cancelMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMailDialog.dismiss();
            }
        });
        sendMailDialog.show();
    }

    //Carga la lista de contactos. En caso de necesitar permisos los solicitará.
    private List<Contact> loadContactList(){

        Mailer mailer = new Mailer(this);
        List<Contact> contactos = new ArrayList<Contact>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)){

                Contact notResults = new Contact();
                notResults.setNombre(getResources().getString(R.string.permissionRequired));
                contactos.add(notResults);
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }else{
            contactos = mailer.getContacts();
            for (Contact c : contactos){
                List<String> mails = mailer.getMails(c.getId());
                if(mails.size()!=0){
                    c.setEmail(mails.get(0));
                }else{
                    c.setEmail(getResources().getText(R.string.noemail).toString());
                }
            }

            if(contactos.size()==0){
                Contact notResults = new Contact();
                notResults.setNombre(getResources().getString(R.string.not_results));
                contactos.add(notResults);
            }
        }

        return contactos;
    }
}
