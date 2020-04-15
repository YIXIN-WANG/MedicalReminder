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
import com.example.medicalreminder.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.medicalreminder.utils.Helper.getCurrentTimeStamp;

public class RegisterActivity extends AppCompatActivity {

    private TextView textViewLogin;
    private EditText editTextEmail, editTextPassword, editTextCareEmail, editOhipNumber;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        textViewLogin = findViewById(R.id.text_view_login);
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextCareEmail = findViewById(R.id.edit_text_care_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editOhipNumber = findViewById(R.id.edit_ohip_number);
        registerButton = findViewById(R.id.button_register);
        progressBar = findViewById(R.id.progressbar);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String careEmail = editTextCareEmail.getText().toString().trim();
                String ohipNumber = editOhipNumber.getText().toString().trim();

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

                if (careEmail.isEmpty()) {
                    editTextCareEmail.setError("Email Required");
                    editTextCareEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(careEmail).matches()) {
                    editTextCareEmail.setError("Valid Email Required");
                    editTextCareEmail.requestFocus();
                    return;
                }

                if(ohipNumber.length() != 12){
                    editOhipNumber.setError("Valid OHIP Number required");
                    editOhipNumber.requestFocus();
                    return;
                }

                registerUser(email, password, careEmail, ohipNumber);
            }
        });


        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(registerIntent);
            }
        });
    }


    private void registerUser(final String email, String password, final String careEmail, final String ohipNumber){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            User user = new User(email, careEmail, getCurrentTimeStamp(), ohipNumber);
                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Helper.goToHomeView(RegisterActivity.this);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("db", e.getMessage());
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
