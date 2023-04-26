package com.example.lottikarotti;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.lottikarotti.Listeners.IOnDataSentListener;

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
    // TODO: Rename and change types and number of parameters
    public static PlayerListFragment newInstance(String param1, String param2) {
        PlayerListFragment fragment = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
