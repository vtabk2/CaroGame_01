package com.example.framgia.carobluetooth.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.framgia.carobluetooth.R;
import com.example.framgia.carobluetooth.data.Constants;
import com.example.framgia.carobluetooth.data.database.DBHelper;
import com.example.framgia.carobluetooth.data.model.History;
import com.example.framgia.carobluetooth.ui.adapter.HistoryRecyclerViewAdapter;
import com.example.framgia.carobluetooth.ui.listener.OnClickItemHistoryListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by framgia on 31/08/2016.
 */
public class HistoryActivity extends AppCompatActivity implements OnClickItemHistoryListener {
    private HistoryRecyclerViewAdapter mHistoryRecyclerViewAdapter;
    private List<History> mHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initViews();
        loadData();
    }

    private void initViews() {
        LinearLayout linearLayoutTitleHistoryItem =
            (LinearLayout) findViewById(R.id.linear_layout_title_history_item);
        ((TextView) linearLayoutTitleHistoryItem.findViewById(R.id.text_history_index))
            .setText(R.string.text_history_index);
        ((TextView) linearLayoutTitleHistoryItem.findViewById(R.id.text_history_human_status))
            .setText(R.string.text_history_human_status);
        ((TextView) linearLayoutTitleHistoryItem.findViewById(R.id.text_history_computer_status))
            .setText(R.string.text_history_computer_status);
        ((TextView) linearLayoutTitleHistoryItem.findViewById(R.id.text_history_level))
            .setText(R.string.text_history_level_game);
        ((TextView) linearLayoutTitleHistoryItem.findViewById(R.id.text_history_time_game))
            .setText(R.string.text_history_time_game);
        RecyclerView recyclerViewHistory = (RecyclerView) findViewById(R.id.recycler_view_history);
        mHistoryRecyclerViewAdapter = new HistoryRecyclerViewAdapter(mHistoryList, this);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewHistory.setAdapter(mHistoryRecyclerViewAdapter);
    }

    private void loadData() {
        DBHelper mDbHelper = new DBHelper(this);
        mHistoryList.addAll(mDbHelper.getHistoryAll());
        mHistoryRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickItem(HistoryRecyclerViewAdapter.HistoryViewHolder historyViewHolder,
                            int position) {
        Intent intent = new Intent(this, SinglePlayerActivity.class);
        intent.setAction(Constants.ACTION_HISTORY_GAME);
        intent.putExtra(Constants.ID_GAME_CARO, position);
        startActivity(intent);
    }
}
