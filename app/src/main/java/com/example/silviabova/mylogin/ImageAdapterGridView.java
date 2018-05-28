package com.example.silviabova.mylogin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by cupia on 18/04/2018.
 */

public class ImageAdapterGridView extends RecyclerView.Adapter<ImageAdapterGridView.MyGridView> {

    private Context mContext;
    //private List<Bitmap> imagesIDs;
    private List<String> imageIDs, sisbn, stitle;

    public ImageAdapterGridView(Context c, List<String> image, List<String> isbn, List<String> title) {
        mContext=c;
        imageIDs = image;
        sisbn = isbn;
        stitle = title;
    }

    @Override
    public MyGridView onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.grid_view_home,parent,false);
        return new ImageAdapterGridView.MyGridView(view);
    }

    @Override
    public void onBindViewHolder(final MyGridView holder, final int position) {
        holder.title.setText(stitle.get(position).toString().trim());
        Log.d("title",stitle.get(position).toString().trim());
        FirebaseStorage.getInstance().getReference("image/"+imageIDs.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(mContext).load(uri.toString()).into(holder.book);
            }
        });
       // holder.book.setImageBitmap(imagesIDs.get(position));
       //Picasso.with(mContext).load(R.drawable.book).into(holder.book);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BookDetails.class);
                intent.putExtra("ISBN", sisbn.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sisbn.size();
    }


    public class MyGridView extends RecyclerView.ViewHolder {
        ImageView book;
        TextView title;
        CardView cardview;

        public MyGridView(View itemView){
            super(itemView);

            book = (ImageView) itemView.findViewById(R.id.bookimage);
            title = (TextView) itemView.findViewById(R.id.book_title);
            cardview = (CardView) itemView.findViewById(R.id.gridview);

        }
    }
}
