package com.example.medicalreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalreminder.model.User;
import com.example.medicalreminder.utils.DataInitialization;
import com.example.medicalreminder.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private TextView textViewRegister;
    private EditText editTextEmail, editTextPassword;
    private Button loginButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        textViewRegister = findViewById(R.id.text_view_register);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_sign_in);
        progressBar = findViewById(R.id.progressbar);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    editTextEmail.setError("Email Required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Valid Email Required");
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty() || password.length() < 6) {
                    editTextPassword.setError("At least 6 chars password required");
                    editTextPassword.requestFocus();
                    return;
                }
                loginUser(email, password);
            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
    }

    private void loginUser(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                                    .child("loginTime").setValue(Helper.getCurrentTimeStamp());
                            Helper.goToHomeView(LoginActivity.this);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Query query = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user =  dataSnapshot.getValue(User.class);
                    if(Helper.checkValidSession(LoginActivity.this, user.getLoginTime())){
                        Helper.goToHomeView(LoginActivity.this);
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Your session is expired, Please login again!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Retrieve user: ",databaseError.getMessage());
                }
            });
        }
    }
}
