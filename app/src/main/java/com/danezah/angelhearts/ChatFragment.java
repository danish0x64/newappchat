package com.danezah.angelhearts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.danezah.angelhearts.adapter.RecentChatRecyclerAdapter;
import com.danezah.angelhearts.adapter.SearchUserRecyclerAdapter;
import com.danezah.angelhearts.adapter.UserRecyclerAdapter;
import com.danezah.angelhearts.model.ChatroomModel;
import com.danezah.angelhearts.model.UserModel;
import com.danezah.angelhearts.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.List;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    UserRecyclerAdapter adapter;


    public ChatFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);
        setupRecyclerView();

        return view;
    }

    private void setupRecyclerView() {
        // Assuming you have a "users" collection and UserModel class
        List<String> specificUserIds = Arrays.asList("+918169821604", "user2_id", "user3_id");

        Query query = FirebaseUtil.allUserCollectionReference().whereIn("phone", specificUserIds);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();
        adapter = new UserRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter!=null)
            adapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)
            adapter.notifyDataSetChanged();
    }
}