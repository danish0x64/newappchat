package com.danezah.angelhearts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.danezah.angelhearts.model.UserModel;
import com.danezah.angelhearts.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class LoginUsernameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button letMeInBtn;
    ProgressBar progressBar;
    String googleAuth;
    UserModel userModel;
    RadioGroup userTypeRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);

        usernameInput = findViewById(R.id.login_username);
        letMeInBtn = findViewById(R.id.login_let_me_in_btn);
        progressBar = findViewById(R.id.login_progress_bar);
        userTypeRadioGroup = findViewById(R.id.user_type_radio_group);
        // Check if the Google authenticator value is present in the intent extras
        if (getIntent().hasExtra("googleAuthenticator")) {
            String googleAuth = getIntent().getStringExtra("email");
            // Use the Google authenticator value as needed
        } else {
            // Handle the case where the Google authenticator value is not present
            // You can show an error message or handle it as appropriate for your app
            // For now, I'm just printing a log statement
            Log.e("LoginUsernameActivity", "Google authenticator value not found in intent extras");
        }

        googleAuth = getIntent().getStringExtra("email");
        getUsername();

        letMeInBtn.setOnClickListener((v -> {
            setUsername();
        }));
    }

    void setUsername() {
        String username = usernameInput.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Timestamp createdTimestamp = Timestamp.now();

            // Call getUserType() to get the selected radio button value
            String userType = getUserType();

            if (userModel != null) {
                userModel.setUsername(username);
                userModel.setUserId(userId);
                userModel.setCreatedTimestamp(createdTimestamp);
                // Update user role
                userModel.setRole(userType);
            } else {
                userModel = new UserModel(currentUser.getEmail(), username, createdTimestamp, userId, userType);
            }

            FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    setInProgress(false);
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
        } else {
            // Handle the case where the current user is null
            Log.e("LoginUsernameActivity", "Current user is null");
            setInProgress(false);
        }
    }

    // Method to get the selected radio button value
    private String getUserType() {
        int selectedRadioButtonId = userTypeRadioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == R.id.radio_angel) {
            return "Angel";
        } else if (selectedRadioButtonId == R.id.radio_seeker) {
            return "Seeker";
        }
        // Default to a value or handle the case where no radio button is selected
        return "Seeker";
    }



    void getUsername() {
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()) {
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null) {
                        usernameInput.setText(userModel.getUsername());
                    }
                }
            }
        });


    }


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            letMeInBtn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            letMeInBtn.setVisibility(View.VISIBLE);
        }
    }

}


