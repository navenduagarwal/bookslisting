package com.example.android.bookslisting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    private String keywords;
    private BookAdapter mBookListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        //get score values from last activity
        Intent intent = this.getIntent();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                keywords = null;
            } else {
                keywords = extras.getString("keywords");
            }
        } else {
            keywords = (String) savedInstanceState.getSerializable("keywords");
        }
        Toast.makeText(ResultsActivity.this, keywords, Toast.LENGTH_SHORT).show();
        updateBooks(keywords);

        //The ArrayAdapter will take the data from a source and
        // use it to populate the ListView it's attached
        mBookListAdapter = new BookAdapter(this, new ArrayList<Book>());
        ListView listView = (ListView) this.findViewById(R.id.listview_book);
        listView.setAdapter(mBookListAdapter);
    }

    public void updateBooks(String keywords) {
        FetchBooksTask bookTasks = new FetchBooksTask();
        bookTasks.execute(keywords);
    }

    public class FetchBooksTask extends AsyncTask<String, Void, ArrayList<Book>> {

        private final String LOG_TAG = FetchBooksTask.class.getSimpleName();

        private ArrayList<Book> getBooksDataFromJson(String bookJsonStr)
                throws JSONException {
            final String OWM_ITEMS = "items";
            final String OWM_VOLUME = "volumeInfo";
            final String OWM_TITLE = "title";
            final String OWM_LINKS = "imageLinks";
            final String OWM_THUMBNAIL_URL = "thumbnail";
            final String OWM_AUTHORS = "authors";


            JSONObject booksResultJson = new JSONObject(bookJsonStr);
            JSONArray booksArray = booksResultJson.getJSONArray(OWM_ITEMS);
            ArrayList<Book> resultStrs = new ArrayList<>();

            for (int i = 0; i < booksArray.length(); i++) {
                String title;
                String thumbnailURl;
                String authors;

                //Get JSON Object representing the book
                JSONObject singleBook = booksArray.getJSONObject(i);

                //Get Volume Object as child array of book object called "volume info"
                JSONObject volumeObject = singleBook.getJSONObject(OWM_VOLUME);

                //  title is in the child array of Volume Objct
                title = volumeObject.getString(OWM_TITLE);

                //thumbnail is in the child array of volume object called "imagelinks"
                if (volumeObject.has(OWM_LINKS)) {
                    JSONObject linksObject = volumeObject.getJSONObject(OWM_LINKS);
                    thumbnailURl = linksObject.getString(OWM_THUMBNAIL_URL);
                } else {
                    thumbnailURl = null;
                }

                //authors is in the child array of volume object called "authors"
                if (volumeObject.has(OWM_AUTHORS)) {
                    JSONArray authorsArray = volumeObject.getJSONArray(OWM_AUTHORS);
                    authors = authorsArray.get(0).toString();
                    for (int j = 1; j < authorsArray.length(); j++) {
                        authors = authors + " , " + authorsArray.get(j).toString();
                    }
                } else {
                    authors = null;
                }
                Book newBook = new Book(title, authors, thumbnailURl);
                resultStrs.add(newBook);
            }
            return resultStrs;
        }

        @Override
        protected ArrayList<Book> doInBackground(String... params) {

            //if no query keyword
            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String bookJsonStr = null;

            try {

                final String QUERY_PARAM = "q";

                Uri.Builder builtUri = new Uri.Builder();
                builtUri.scheme("https")
                        .authority("www.googleapis.com")
                        .appendPath("books")
                        .appendPath("v1")
                        .appendPath("volumes")
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URL : " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                bookJsonStr = buffer.toString();
                Log.i(LOG_TAG, "Downloaded Data " + bookJsonStr);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the book data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }
            try {
                return getBooksDataFromJson(bookJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> result) {
            if (result != null) {
                mBookListAdapter.clear();
                for (Book bookJsonStr : result) {
                    mBookListAdapter.add(bookJsonStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
