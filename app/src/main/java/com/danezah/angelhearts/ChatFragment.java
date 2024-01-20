package com.danezah.angelhearts;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Retrieve the user's role from Firestore
            FirebaseUtil.currentUserDetails().get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserModel currentUserModel = task.getResult().toObject(UserModel.class);
                            if (currentUserModel != null) {
                                String currentRole = currentUserModel.getRole();

                                Query query;
                                // Check the role of the current user
                                if ("Angel".equals(currentRole)) {
                                    // If the current user is a "seeker," query all "angel" users
                                    // Assuming userIds is a list of user IDs
                                    query = FirebaseUtil.allUserCollectionReference().whereEqualTo("role", "Seeker");


                                } else {
                                    // If the current user is an "angel," modify the query based on your specific condition

                                    query = FirebaseUtil.allUserCollectionReference().whereEqualTo("role", "Angel");
                                }


                                FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                                        .setQuery(query, UserModel.class)
                                        .build();

                                adapter = new UserRecyclerAdapter(options, getContext());
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                recyclerView.setAdapter(adapter);
                                adapter.startListening();
                            }
                        } else {
                            // Handle the case where fetching user details fails
                            Log.e("ChatFragment", "Error fetching user details", task.getException());
                        }
                    });
        }
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