package com.example.lottikarotti;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class GameEndDialog extends DialogFragment {

    public interface GameEndDialogListener {
        void onRestartGame();
        void onExitGame(boolean isHost);
    }

    private GameEndDialogListener gameEndDialogListener;
    private String dialogMessage;
    private boolean isHost;


    public GameEndDialog(GameEndDialogListener listener, String winningMessage, boolean isHost) {
        this.gameEndDialogListener = listener;
        this.dialogMessage = winningMessage;
        this.isHost = isHost;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Game has ended, " + dialogMessage + " has won the game!")
                .setNegativeButton("Exit", (dialog, id) -> gameEndDialogListener.onExitGame(isHost));
                if(isHost) {
                      builder.setPositiveButton("End Game", (dialog, id) -> gameEndDialogListener.onRestartGame());
                }

        return builder.create();
    }
}
