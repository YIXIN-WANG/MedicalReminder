package com.example.medicalreminder;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private EditText careEmail;
    private TextView email;
    private ProgressBar progressBar;
    private Button saveButton;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        careEmail = view.findViewById(R.id.edit_text_care_email);
        email = view.findViewById(R.id.text_email);
        progressBar = view.findViewById(R.id.progressbar);
        saveButton = view.findViewById(R.id.button_save);

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
                                Toast.makeText(getContext(), "Update Successfully!", Toast.LENGTH_LONG).show();
                                Log.d("Update Profile", "Success");
                            }
                            else{
                                Toast.makeText(getContext(), "Update Failed!", Toast.LENGTH_LONG).show();
                                Log.e("Update Profile: ",task.getException().getMessage());
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getContext(), "Email already updated!", Toast.LENGTH_LONG).show();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }
}
