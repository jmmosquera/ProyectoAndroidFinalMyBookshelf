package iezv.jmm.mybookshelfproject.Book;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import iezv.jmm.mybookshelfproject.R;
import iezv.jmm.mybookshelfproject.SQLite.DBLibro;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    public Context context;

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleView;
        private final TextView authorView;
        private final ImageView coverView;
        private final TextView titleShadowView;

        private BookViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.nombreMail);
            authorView = itemView.findViewById(R.id.email);
            coverView = itemView.findViewById(R.id.coverMini);
            titleShadowView = itemView.findViewById(R.id.tituloSombra);
        }
    }

    private final LayoutInflater mInflater;

    private List<DBLibro> mBooks = Collections.emptyList(); // copia caché de la lista

    public BookAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.book_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {

        if (mBooks != null) {
            DBLibro current = mBooks.get(position);
            holder.titleView.setText(current.getTitle());
            holder.titleShadowView.setText(current.getTitle());
            holder.authorView.setText(current.getAuthor());
                String coverLink = current.getCover();
            if (coverLink==null) {
                holder.coverView.setImageDrawable(context.getResources().getDrawable(R.drawable.defaultcover));
            } else{
                Picasso.with(context).load(Uri.parse("file://" + coverLink)).into(holder.coverView);
            }
        } else {
            // Para cuando no hay libros
            holder.titleView.setText(context.getResources().getText(R.string.emptyText));
        }
    }

    public void setBooks(List<DBLibro> books) {
        mBooks = books;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mBooks != null)
            return mBooks.size();
        else return 0;
    }
}
