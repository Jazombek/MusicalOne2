package pl.edu.pwr.musicalone;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Author extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        findViewById(R.id.authorback).setBackgroundColor(getIntent().getIntExtra("color2", Color.WHITE));
        ((TextView)findViewById(R.id.authorTextView)).setTextColor(getIntent().getIntExtra("color",Color.BLACK));
    }
}
