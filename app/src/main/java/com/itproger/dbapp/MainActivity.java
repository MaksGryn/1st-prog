package com.itproger.dbapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    Button sendMessage, showMessages;
    EditText nameField, commentField;
    TextView commentText;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendMessage = findViewById(R.id.sendMessage);
        showMessages = findViewById(R.id.showMessages);

        nameField = findViewById(R.id.nameField);
        commentField = findViewById(R.id.commentField);

        commentText = findViewById(R.id.commentText);

        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        comments = db.getReference("Comments");


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageToDB();
            }
        });
        showMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentMessages();
            }
        });
        
    }

    private void showCurrentMessages() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Comments");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getAllMessages((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
         });

    }

    private void getAllMessages(Map<String,Object> comments) {
        String mess = "";
        //BD
        for (Map.Entry<String, Object> entry : comments.entrySet()){

            //Commentarz
            Map singleComment = (Map) entry.getValue();
            //Imie i wiadomosc
            mess += "User: " +  singleComment.get("Name") + ". Mess: " + singleComment.get("Message") + "\n";
        }

        commentText.setText(mess);
    }

    private void sendMessageToDB() {
        Map<String, String> userData = new HashMap<String, String>();

        userData.put("Name", nameField.getText().toString());
        userData.put("Message", commentField.getText().toString());
        comments.push().setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Все добавлено!", Toast.LENGTH_SHORT).show();
                nameField.setText("");
                commentField.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Произошла ошибка! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
