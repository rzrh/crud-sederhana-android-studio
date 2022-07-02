package com.example.tugasfragment.ui.news;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tugasfragment.R;

import java.util.ArrayList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

public class FragmentNews extends Fragment {

    View view;
    RecyclerView recyclerView;
    NewsAdapter newsAdapter;
    ArrayList<NewsItem> newsItems;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressBar progressBar;

//    static String deskripsi1 = "Keterangan mengenai Flash Drive";
//    static String deskripsi2 = "Keterangan mengenai Handphone";
//    static String deskripsi3 = "Keterangan mengenai Kamera";


    public static FragmentNews newInstance() {
        return new FragmentNews();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycleview);
        progressBar = (ProgressBar)view.findViewById(R.id.progressbarNews);
        newsItems = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsItems, getActivity());

//        newsItems.add(new NewsItem(R.drawable.drive_icon, "Flash Drive", deskripsi1));
//        newsItems.add(new NewsItem(R.drawable.phone_icon, "Handphone", deskripsi2));
//        newsItems.add(new NewsItem(R.drawable.camera_icon, "Camera", deskripsi3));


        recyclerView.setAdapter(newsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        Log.v("test","Load Firebase");
        getData();
    }
    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("news").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        newsItems.clear();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                NewsItem itemNewsData = new NewsItem(
                                        document.getString("img"),
                                        document.getString("title"),
                                        document.getString("desc")
                                );
                                itemNewsData.setId(document.getId());
                                newsItems.add(itemNewsData);
                            }
                            newsAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(getActivity().getApplicationContext(), "Data gagal diambil",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}