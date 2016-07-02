package com.example.android.bookslisting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the View that shows the home button
        Button submitButton = (Button) findViewById(R.id.search_button);
        // Set a click listener on that View
        submitButton.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the home button is clicked on.
            @Override
            public void onClick(View view) {
                EditText keywords = (EditText) findViewById(R.id.search_text);
                String keywordsString = keywords.getText().toString().toLowerCase();
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                intent.putExtra("keywords", keywordsString);
                startActivity(intent);
            }
        });
    }


}
