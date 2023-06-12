package com.example.lottikarotti;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class MutatorDialog  extends DialogFragment {
    public interface MutatorDialogListener {
        void onGameModeSelected(int which);
    }

    private MutatorDialogListener mutatorDialogListener;
    private int selectedMutator;

    public MutatorDialog(MutatorDialogListener listener) {
        this.mutatorDialogListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] mutators = new String[]{"No Mutator", "Spicy Carrot", "Mutator 2"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pleas wait until everyone has joined. \nMeanwhile you can choose a Mutator").setSingleChoiceItems(mutators, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int mutator) {
                selectedMutator = mutator;
            }
        }).setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                mutatorDialogListener.onGameModeSelected(selectedMutator);
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}