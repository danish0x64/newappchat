package com.danezah.angelhearts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.danezah.angelhearts.ChatActivity;
import com.danezah.angelhearts.R;
import com.danezah.angelhearts.model.UserModel;
import com.danezah.angelhearts.utils.AndroidUtil;
import com.danezah.angelhearts.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, UserRecyclerAdapter.UserViewHolder> {

    private Context context;
    private String currentUserId;

    public UserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context, String currentUserId) {
        super(options);
        this.context = context;
        this.currentUserId = currentUserId;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {
        FirebaseUtil.getOtherProfilePicStorageRef(model.getUserId()).getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(context, uri, holder.profilePic);
                    }
                });

        holder.usernameText.setText(model.getUsername());

        // Fetch and display the lastMessage from the chatrooms
        fetchLastMessage(currentUserId, model.getUserId(), holder);

        holder.itemView.setOnClickListener(v -> {
            // Navigate to user profile or chat activity
            Intent intent = new Intent(context, ChatActivity.class); // Adjust accordingly
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    private void fetchLastMessage(String currentUserId, String otherUserId, UserViewHolder holder) {
        // Fetch the lastMessage from the chatrooms collection in Firebase
        FirebaseUtil.chatroomsCollectionReference()
                .document(getChatroomId(currentUserId, otherUserId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            String lastMessage = documentSnapshot.getString("lastMessage");
                            // Display the lastMessage in your ViewHolder
                            holder.setLastMessage(lastMessage);
                        }
                    }
                });
    }

    private String getChatroomId(String userId1, String userId2) {
        // Create a unique chatroomId based on the two user IDs
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new UserViewHolder(view);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageView profilePic;
        TextView lastMessageText; // Add this line

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            lastMessageText = itemView.findViewById(R.id.last_message_text); // Initialize this with the appropriate ID
        }

        // Add this method to set the last message
        public void setLastMessage(String lastMessage) {
            lastMessageText.setText(lastMessage);
        }
    }
}