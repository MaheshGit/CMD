package mks.co.cloudmagicdemo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import mks.co.cloudmagicdemo.R;
import mks.co.cloudmagicdemo.network.CloudMagicManager;
import mks.co.cloudmagicdemo.network.model.FullMessage;
import mks.co.cloudmagicdemo.network.model.Participant;
import mks.co.cloudmagicdemo.utils.CommonUtilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageDetailActivity extends AppCompatActivity {
    private final String TAG = MessageDetailActivity.class.getSimpleName();
    private ImageView starIv;
    private TextView subjectTv, participantsTv, messageBody, timeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        starIv = (ImageView) findViewById(R.id.md_star);
        subjectTv = (TextView) findViewById(R.id.md_subject);
        participantsTv = (TextView) findViewById(R.id.md_participants);
        messageBody = (TextView) findViewById(R.id.md_body);
        timeTv = (TextView) findViewById(R.id.md_time);
        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt(Intent.EXTRA_TEXT);
        getMessageDetail(id);
    }

    private void getMessageDetail(int id) {
        Call<FullMessage> fullMessageCall = CloudMagicManager.getApiManager().getMessageDetail(id);
        fullMessageCall.enqueue(new Callback<FullMessage>() {
            @Override
            public void onResponse(Call<FullMessage> call, Response<FullMessage> response) {
                if (response.body() != null) {
                    if (response.body().getStarred()) {
                        starIv.setImageResource(R.drawable.ic_toggle_star_selected);
                    } else {
                        starIv.setImageResource(R.drawable.ic_toggle_star);
                    }
                    subjectTv.setText(response.body().getSubject());
                    timeTv.setText(CommonUtilities.getDateFromTimeStamp(response.body().getTs()));
                    String participants = getParticipants((ArrayList<Participant>) response.body().getParticipants());
                    Log.i(TAG, participants);
                    participantsTv.setText(participants);
                    messageBody.setText(response.body().getBody());
                }
            }

            @Override
            public void onFailure(Call<FullMessage> call, Throwable t) {
                Log.i(TAG, " " + "failure");
            }
        });
    }

    private String getParticipants(ArrayList<Participant> participants) {
        StringBuilder stringBuilder = new StringBuilder();
        String sep = ", ";
        for (int i = 0; i < participants.size(); i++) {
            stringBuilder.append(participants.get(i).getName());
            if (i != participants.size() - 1) {
                stringBuilder.append(sep);
            }
        }
        return stringBuilder.toString();
    }
}

