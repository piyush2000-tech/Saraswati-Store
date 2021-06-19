package com.appdevelopers.saraswatistore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    private TextView devMail,devWebSite,ownerEmail;

    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        devMail = view.findViewById(R.id.devemailid);
        devWebSite = view.findViewById(R.id.devemailid2);
        ownerEmail = view.findViewById(R.id.emailid);

        devMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailOpen("appdevelopers2060@gmail.com");
            }
        });


        ownerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailOpen("saraswatistore07@gmail.com");
            }
        });


        return view;
    }

    private void emailOpen(String email){
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {email};
        intent.putExtra(Intent.EXTRA_EMAIL,recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Write your subject....");
        intent.putExtra(Intent.EXTRA_TEXT,"Body of the content here....");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent,"Send E-Mail"));

    }
}