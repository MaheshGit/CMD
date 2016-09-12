package mks.co.cloudmagicdemo.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import mks.co.cloudmagicdemo.CloudApplication;
import mks.co.cloudmagicdemo.R;
import mks.co.cloudmagicdemo.adapters.MessageAdapter;
import mks.co.cloudmagicdemo.network.CloudMagicManager;
import mks.co.cloudmagicdemo.network.model.Message;
import mks.co.cloudmagicdemo.utils.CommonUtilities;
import mks.co.cloudmagicdemo.utils.Constants;
import mks.co.cloudmagicdemo.utils.ListItemDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public MessageAdapter messageAdapter;
    private List<Message> messageArrayList;
    private List<Integer> deletedMessageIdList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.line_divider);
        recyclerView.addItemDecoration(new ListItemDecorator(dividerDrawable));
        registerForContextMenu(recyclerView);
        registerReceiver(broadcastReceiver, new IntentFilter(Constants.INTENT_NETWORK_AVAILABLE));
        messageArrayList = new ArrayList<Message>();
        deletedMessageIdList = new ArrayList<>();
        messageAdapter = new MessageAdapter(MainActivity.this, messageArrayList);
        recyclerView.setAdapter(messageAdapter);
        getMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getMessages();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                int postition = ((MessageAdapter) recyclerView.getAdapter()).getPosition();
                messageAdapter.removeItemAtPosition(postition);
                int deletedItemId = messageAdapter.getItem(postition).getId();
                if (CommonUtilities.isConnectedToInternet(this)) {
                    deleteMessage(deletedItemId);
                } else {
                    deletedMessageIdList.add(deletedItemId);
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void getMessages() {
        Call<ArrayList<Message>> messArrayListCall = CloudMagicManager.getApiManager().getCloudMessages();
        messArrayListCall.enqueue(new Callback<ArrayList<Message>>() {
            @Override
            public void onResponse(Call<ArrayList<Message>> call, Response<ArrayList<Message>> response) {
                messageAdapter.clearData();
                messageAdapter.addData(response.body());
                messageAdapter.notifyDataSetChanged();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ArrayList<Message>> call, Throwable t) {
                t.printStackTrace();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void deleteMessage(final int id) {
        Call<Void> responseCall = CloudMagicManager.getApiManager().deleteMessage(id);
        responseCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (deletedMessageIdList.size() > 0) {
                    deletedMessageIdList.remove(0);
                }
                if (deletedMessageIdList.size() > 0) {
                    deleteMessage(deletedMessageIdList.get(0));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                Log.i(TAG, "Message deletion failed");
            }
        });
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int size = deletedMessageIdList.size();
            if (CommonUtilities.isConnectedToInternet(CloudApplication.context) && size != 0) {
                int id = deletedMessageIdList.get(0);
                deleteMessage(id);
            }
        }
    };

}
