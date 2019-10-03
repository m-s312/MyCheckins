package android.bignerdranch.mycheckins;



import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

public class RecordListFragment extends Fragment {
    private RecyclerView mRecordRecyclerView;
    private RecordAdapter mAdapter;

    private class RecordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Record mRecord;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private TextView mPlaceTextView;
        public void bind(Record record) {
            mRecord = record;
            mTitleTextView.setText(mRecord.getTitle());
            mPlaceTextView.setText(mRecord.getPlace());
            mDateTextView.setText(mRecord.getDate().toString());

        }
        public RecordHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list_record, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.record_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.record_date);
            mPlaceTextView = (TextView) itemView.findViewById(R.id.record_place);
        }


        @Override
        public void onClick(View view) {
            Intent intent = RecordActivity.newIntent(getActivity(), mRecord.getId());
            startActivity(intent);

        }
    }
    private class RecordAdapter extends RecyclerView.Adapter<RecordHolder> {
        private List<Record> mRecords;
        public RecordAdapter(List<Record> records) {
            mRecords = records;
        }

        @Override
        public void onBindViewHolder(RecordHolder holder, int position) {
            Record record  = mRecords.get(position);
            holder.bind(record);
        }

        @Override
        public RecordHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater =
                    LayoutInflater.from(getActivity());
            return new RecordHolder(layoutInflater,
                    parent);
        }

        @Override
        public int getItemCount() {
            return mRecords.size();
        }
        public void setRecords(List<Record> records) {
            mRecords = records;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list, container, false);
        mRecordRecyclerView = (RecyclerView) view.findViewById(R.id.record_recycler_view);
        mRecordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_record_list, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_record:
                Record record = new Record();
                RecordLab.get(getActivity()).addRecord(record);
                Intent intent = RecordActivity.newIntent(getActivity(),
                        record.getId());
                startActivity(intent);
                return true;
            case R.id.show_help:

                startActivity(HelpWebView.newIntent(getActivity()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateUI() {
        RecordLab recordlab = RecordLab.get(getActivity());
        List<Record> records = recordlab.getRecords();
        if (mAdapter == null) {
            mAdapter = new RecordAdapter(records);
            mRecordRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecords(records);
            mAdapter.notifyDataSetChanged();
        }
    }
}
