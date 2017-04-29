package com.veryworks.android.bbsbasic;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.veryworks.android.bbsbasic.domain.Memo;
import com.veryworks.android.bbsbasic.interfaces.ListInterface;

import java.util.ArrayList;
import java.util.List;


public class ListFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<Memo> datas = new ArrayList<>();
    private static ListFragment fragment;

    Context context = null;
    ListInterface listInterface;

    View view;
    RecyclerView recyclerView;
    ListAdapter listAdapter;

    public ListFragment() {}

    public static ListFragment newInstance() {
        if(fragment != null)
            return fragment;
        fragment = new ListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(view != null)
            return view;

        view = inflater.inflate(R.layout.layout_list, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);

        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        listAdapter = new ListAdapter(context, datas);
        recyclerView.setAdapter(listAdapter);

        FloatingActionButton floatTop = (FloatingActionButton) view.findViewById(R.id.floatTop);
        FloatingActionButton floatPlus = (FloatingActionButton) view.findViewById(R.id.floatPlus);
        floatPlus.setOnClickListener(this);
        floatTop.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.listInterface = (ListInterface) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setData(List<Memo> datas){
        this.datas = datas;
    }


    public void refreshAdapter() {
        listAdapter = new ListAdapter(context, datas);
        Log.d("리스트어댑터", "-------------------------" + listAdapter);
        Log.d("리사이클러뷰", "-------------------------" + recyclerView);
        recyclerView.setAdapter(listAdapter);
//        listAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floatPlus:
                listInterface.goDetail();
                break;

            case R.id.floatTop:
                Log.d("플로팅버튼 클릭트", "-------------");
                recyclerView.smoothScrollToPosition(0);
                Log.d("플로팅버튼 클릭트2", "-------------");
                break;
        }
    }
}


