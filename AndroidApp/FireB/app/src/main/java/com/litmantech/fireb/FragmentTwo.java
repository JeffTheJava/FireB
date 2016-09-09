package com.litmantech.fireb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.litmantech.fireb.database.DatabaseHandler;
import com.litmantech.fireb.database.channels.Channel;
import com.litmantech.fireb.login.LoginHandler;

/**
 * Created by Jeff_Dev_PC on 9/8/2016.
 */
public class FragmentTwo extends Fragment implements View.OnClickListener {
    public static final String TAG = "FragmentTwo";
    private Channel mChannel;
    private Button backButton;
    private DatabaseHandler dbHolder;


    public void setSelectedChannel(Channel channel) {
        this.mChannel = channel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two, container, false);

        TextView text2 = (TextView) view.findViewById(R.id.textView2);
        backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(this);

        text2.setText("you are in the \n"+mChannel.getTitle()+"\n\nChannel\n\n\n Press back button to go back");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dbHolder = ((MainActivity)this.getActivity()).getDatabaseHandler();

        dbHolder.initMessages(mChannel);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_button:
                backPressed();
                break;
        }
    }

    private void backPressed() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
