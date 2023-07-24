package com.example.testingad;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.testingad.admanagers.RewardedAdManager;

public class MyDialogueFragment extends DialogFragment {

    private final Activity activity;
    String AD_UNIT_ID;
    RewardedAdManager rewardedAdManager;

    public MyDialogueFragment(Activity activity, String AD_UNIT_ID) {
        this.activity=activity;
        this.AD_UNIT_ID=AD_UNIT_ID;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the custom layout for the dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialogue_activity, null);

        TextView modalTitle = rootView.findViewById(R.id.modal_title);
        TextView modalMessage = rootView.findViewById(R.id.modal_message);
        Button cancelButton = rootView.findViewById(R.id.cancel_btn);

        Button watchButton = rootView.findViewById(R.id.watch_btn);

        rewardedAdManager=new RewardedAdManager(activity,AD_UNIT_ID);

        modalTitle.setText("My Custom Modal");
        modalMessage.setText("This is my custom modal!");

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the OK button click if needed
                dismiss(); // Close the dialog when the button is clicked
            }
        });

        watchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        // Set the custom view to the dialog builder
        builder.setView(rootView);
        return builder.create();
    }
}
