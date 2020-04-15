package com.example.medicalreminder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.medicalreminder.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText careEmail;
    private TextView email;
    private ProgressBar progressBar;
    private Button saveButton;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        auth = FirebaseAuth.getInstance();
        careEmail = findViewById(R.id.edit_text_care_email);
        email = findViewById(R.id.text_email);
        progressBar = findViewById(R.id.progressbar);
        saveButton = findViewById(R.id.button_save);

        final Query query = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUser =  dataSnapshot.getValue(User.class);
                email.setText(currentUser.getEmail());
                careEmail.setText(currentUser.getCareGiverEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Retrieve Profile: ",databaseError.getMessage());
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String careMail = careEmail.getText().toString().trim();
                if(!currentUser.getCareGiverEmail().equals(careMail)){
                    progressBar.setVisibility(View.VISIBLE);
                    currentUser.setCareGiverEmail(careMail);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid())
                            .setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful()){
                                careEmail.setText(currentUser.getCareGiverEmail());
                                Toast.makeText(ProfileActivity.this, "Update Successfully!", Toast.LENGTH_LONG).show();
                                Log.d("Update Profile", "Success");
                            }
                            else{
                                Toast.makeText(ProfileActivity.this, "Update Failed!", Toast.LENGTH_LONG).show();
                                Log.e("Update Profile: ",task.getException().getMessage());
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(ProfileActivity.this, "Email already updated!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
