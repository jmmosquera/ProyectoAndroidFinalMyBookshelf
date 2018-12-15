package iezv.jmm.mybookshelfproject.Book;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import iezv.jmm.mybookshelfproject.R;

public class Cover extends AppCompatActivity {

    private android.widget.ImageView fullCover;

    //Este método carga la imagen de la portada ocupando toda la actividad.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        this.fullCover = (ImageView) findViewById(R.id.fullCover);

        Intent i = getIntent();
        String cover = i.getStringExtra("cover");
        if (cover==null) {
            fullCover.setImageDrawable(this.getResources().getDrawable(R.drawable.defaultcover));
        } else{
            Picasso.with(this).load("file://"+cover).into(fullCover);
        }


        fullCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
