package iezv.jmm.mybookshelfproject;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Help extends AppCompatActivity {

    private android.support.v7.widget.Toolbar helpToolbar;
    private Toolbar toolbarHelp;
    private android.widget.Button linkH1;
    private android.widget.Button linkH2;
    private android.widget.Button linkH3;
    private android.widget.Button linkH4;
    private android.widget.Button linkH5;
    private android.widget.TextView helpT1;
    private android.widget.TextView helpT3;
    private android.widget.TextView helpT5;
    private android.widget.TextView helpT7;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        this.helpT7 = (TextView) findViewById(R.id.helpT7);
        this.helpT5 = (TextView) findViewById(R.id.helpT5);
        this.helpT3 = (TextView) findViewById(R.id.helpT3);
        this.helpT1 = (TextView) findViewById(R.id.helpT1);
        this.linkH5 = (Button) findViewById(R.id.linkH5);
        this.linkH4 = (Button) findViewById(R.id.linkH4);
        this.linkH3 = (Button) findViewById(R.id.linkH3);
        this.linkH2 = (Button) findViewById(R.id.linkH2);
        this.linkH1 = (Button) findViewById(R.id.linkH1);
        this.toolbarHelp = (Toolbar) findViewById(R.id.toolbarHelp);
        this.helpToolbar = findViewById(R.id.toolbarHelp);
        this.scrollView = findViewById(R.id.scrollView);
        helpToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.allWhite));
        helpToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back));
        helpToolbar.setTitle(R.string.title_activity_help);

        helpToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        buttonHandler();

    }

    //Organiza los botones como un índice, de manera que cada uno lleva a una sección de la vista. El último botón retorna a la parte superior de la vista.
    public void buttonHandler(){

        linkH1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,helpT1.getTop());
            }
        });

        linkH2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,helpT3.getTop());
            }
        });

        linkH3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,helpT5.getTop());
            }
        });

        linkH4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,helpT7.getTop());
            }
        });

        linkH5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.smoothScrollTo(0,linkH1.getTop());
            }
        });
    }
}
