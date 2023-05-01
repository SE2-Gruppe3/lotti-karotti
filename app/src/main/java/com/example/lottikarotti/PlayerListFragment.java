
package com.example.lottikarotti;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.lottikarotti.Listeners.IOnDataSentListener;
import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.Util.DisectJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayerListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_playerList.
     */
    public static PlayerListFragment newInstance(String param1, String param2) {
        PlayerListFragment fragment = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private  String[] names;
    Socket socket;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            socket = ServerConnection.getInstance("X");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on("getplayerslobby", jsonlist->{
            System.out.println(jsonlist[0].toString());
            names = DisectJSON.getNames(jsonlist[0].toString());
        });
        socket.emit("getplayerslobby", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_list,
                container, false);
        ImageButton btnclose = view.findViewById(R.id.button_close_playerList);
        btnclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    delegateClose();
            }
        });

        TableLayout tableLayout = view.findViewById(R.id.tblayout_players);
        if (names == null){
            names = new String[0];
        }
// Loop through the array
        for (String name : names) {
            // Create a new TableRow
            TableRow tableRow = new TableRow(getContext());

            // Create a new TextView for the name
            TextView textView = new TextView(getContext());
            textView.setText(name);

            // Set the text size to 18sp
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

            // Add 16dp of padding to the right
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
            textView.setPadding(30, 5, padding, 0);

            // Add the TextView to the TableRow
            tableRow.addView(textView);

            // Add the TableRow to the TableLayout
            tableLayout.addView(tableRow);
        }
        // Inflate the layout for this fragment
        return view;
    }

    public void delegateClose(){
        String data = "closeFragmentPlayerList";
        listener.onDataSent(data);

    }

    private IOnDataSentListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof IOnDataSentListener) {
            listener = (IOnDataSentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " not found");
        }
    }
}
