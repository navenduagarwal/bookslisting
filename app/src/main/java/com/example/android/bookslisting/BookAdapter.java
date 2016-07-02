package com.example.android.bookslisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Navendu Agarwal on 01-Jul-16.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, ArrayList<Book> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Book book = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        TextView titleText = (TextView) convertView.findViewById(R.id.list_title_textview);
        titleText.setText(book.getTitle());

        if (book.getAuthor() != null) {
            TextView authorText = (TextView) convertView.findViewById(R.id.list_author_textview);
            authorText.setText(book.getAuthor());
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_image);
        Picasso.with(getContext())
                .load(book.getBookImageURL())
                .placeholder(R.drawable.placeholder_book)
                .fit()
                .into(imageView);
        return convertView;
    }
}
