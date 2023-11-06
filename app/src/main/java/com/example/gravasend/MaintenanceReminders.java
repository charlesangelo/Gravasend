package com.example.gravasend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MaintenanceReminders extends AppCompatActivity {
    private ImageButton backButton;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    private TextView service;
    private TextView nextduemileage;
    private LinearLayout maintenanceBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maintenancereminders);

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("maintenanceReminders");

        // Get the current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        // Back Button
        backButton = findViewById(R.id.backButton);
        maintenanceBox = findViewById(R.id.maintenanceBox);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MaintenanceReminders.this, TruckInformation.class);
                startActivity(intent);
            }
        });

        // Load data from Firebase and set it to TextViews
        loadMaintenanceReminders();
    }

    private void loadMaintenanceReminders() {
        ArrayList<String> keys = new ArrayList<>();
        DatabaseReference userReference = databaseReference.child(currentUser.getUid());


        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot keySnapshot : dataSnapshot.getChildren()) {
                    // Retrieve each trip key and add it to the list
                    String maintenanceKey = keySnapshot.getKey();
                    keys.add(maintenanceKey);
                }

                // Iterate through the trip keys and read data for each key
                for (String key : keys) {
                    DatabaseReference maintenanceRef = databaseReference.child(key);

                    maintenanceRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(Task<DataSnapshot> task) {
                            if (task.isSuccessful()) {
                                DataSnapshot dataSnapshot = task.getResult();


                                if (dataSnapshot.exists()) {

                                    View maintenanceView = getLayoutInflater().inflate(R.layout.maintenance_item, null);
                                    TextView serviceTextView = maintenanceView.findViewById(R.id.service);
                                    TextView mileageTextView = maintenanceView.findViewById(R.id.nextduemileage);


                                    String serviceText = dataSnapshot.child("service").getValue(String.class);
                                    String nextDueMileage = dataSnapshot.child("nextduemileage").getValue(String.class);

                                    serviceTextView.setText(serviceText);
                                    mileageTextView.setText(nextDueMileage);

                                    maintenanceBox.addView(maintenanceView);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });
    }
}
