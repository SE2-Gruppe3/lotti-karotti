package com.example.lottikarotti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.lottikarotti.Network.ServerConnection;

import java.net.URISyntaxException;

public class MutatorDialog  extends DialogFragment {
    public interface MutatorDialogListener {
        void onGameModeSelected(int which);
    }

    private MutatorDialogListener mutatorDialogListener;
    private int selectedMutator;
    private String lobbyId;

    public MutatorDialog(MutatorDialogListener listener, String lobbyId) {
        this.mutatorDialogListener = listener;
        this.lobbyId=lobbyId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] mutators = new String[]{"No Mutator", "Spicy Carrot", "Special Card"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(17F);
        textView.setText("\n Wait until everyone has joined Lobby: "+lobbyId+" \n\n Meanwhile you can choose a mutator:  ");
        builder.setCustomTitle(textView).setSingleChoiceItems(mutators, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int mutator) {
                selectedMutator = mutator;
            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                mutatorDialogListener.onGameModeSelected(selectedMutator);
                try {
                    ServerConnection.getInstance("x").emit("getMutator", 1);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                dialog.dismiss();
            }

        });


        return builder.create();
    }
}