package com.example.tugasfragment.ui.news;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasfragment.R;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.transition.Hold;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{
    private List<NewsItem> dataItem;
    Context context;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public NewsAdapter(List<NewsItem> dataItem, Context context) {
        this.dataItem = dataItem;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NewsViewHolder holder, int position) {
        NewsItem nItem = dataItem.get(position);
        holder.title.setText(nItem.getTitle());
        holder.desc.setText(nItem.getDesc());
//        holder.imageNews.setImageResource(nItem.getGambar());
//        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast t = Toast.makeText(context, "Item ke-" + holder.getAdapterPosition(), Toast.LENGTH_LONG);
//                t.setGravity(Gravity.CENTER, 0, 0);
//                t.show();
//            }
//        });
        Glide.with(context).load(nItem.getGambar()).into(holder.imageNews);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("test", "layout Clicked on pos" + holder.getAdapterPosition());
                final CharSequence[] dialogItem = {"Edit", "Hapus"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.v("test", "PREPARE DIALOG");
                        switch (i) {
                            case 0:
                                final Dialog dialog = new Dialog(context);
                                Log.v("test", "OPEN DIALOG");
                                dialog.setContentView(R.layout.editor);
                                EditText title, image, desc;
                                Button simpan;
                                title = dialog.findViewById(R.id.title);
                                image = dialog.findViewById(R.id.image);
                                desc = dialog.findViewById(R.id.desc);
                                simpan = dialog.findViewById(R.id.simpan);

                                title.setText(nItem.getTitle());
                                image.setText(nItem.getGambar());
                                desc.setText(nItem.getDesc());

                                dialog.show();
                                Window window = dialog.getWindow();
                                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                                        WindowManager.LayoutParams.MATCH_PARENT);

                                simpan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String judul = title.getText().toString();
                                        String urlGambar = image.getText().toString();
                                        String konten = desc.getText().toString();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("title", judul);
                                        data.put("img", urlGambar);
                                        data.put("desc", konten);
                                        dialog.setTitle("Title...");

                                        db.collection("news").document(nItem.getId()).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(context, "Berhasil!",Toast.LENGTH_SHORT).show();
                                                            Log.v("test", "Update Sukses");
                                                            holder.title.setText(judul);
                                                            holder.desc.setText(konten);
                                                            Glide.with(context).load(urlGambar).into(holder.imageNews);
                                                            dialog.dismiss();
                                                        } else {
                                                            Toast.makeText(context, "Gagal!",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                                break;
                            case 1:
                                deleteData(nItem.getId());
                                int removeIndex = holder.getAdapterPosition();
                                dataItem.remove(removeIndex);
                                notifyItemRemoved(removeIndex);
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataItem.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        TextView title, desc;
        ImageView imageNews;
        LinearLayout itemLayout;

        public NewsViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.newsTitle);
            desc = itemView.findViewById(R.id.newsDesc);
            imageNews = itemView.findViewById(R.id.imgNews);
            itemLayout = itemView.findViewById(R.id.LinearLayoutNewsItem);

        }
    }
    private void deleteData(String id) {

        db.collection("news").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Data gagal di hapus!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        notifyDataSetChanged();
                    }
                });
    }
}
