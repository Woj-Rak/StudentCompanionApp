package com.example.wojtek.studentcompanion.Activities;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.wojtek.studentcompanion.R;

import java.util.Random;

public class QuotesActivity extends AppCompatActivity {
    //Accesses the string array containing all the quotes and randomly selects one from the array
    public void displayQuote(){
        Resources res = getResources();
        String[] quotes = res.getStringArray(R.array.InspQuotes);
        int min = 0;
        int max = quotes.length;
        Random r = new Random();
        int ran = r.nextInt(max - min) + min;
        String chosenQuote = quotes[ran];
        TextView myTextView = (TextView) findViewById(R.id.textQuoteDisplay);
        myTextView.setText(chosenQuote);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button button = (Button) findViewById(R.id.btnNewQuote);
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                displayQuote();
            }
        });
        //On creation of this activity the quote display function is ran automatically
        displayQuote();
    }

}
