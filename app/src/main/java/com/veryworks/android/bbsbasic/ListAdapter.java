package com.veryworks.android.bbsbasic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.veryworks.android.bbsbasic.data.DBHelper;
import com.veryworks.android.bbsbasic.domain.Memo;

import java.sql.SQLException;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    Context context;
    List<Memo> datas;
    MainActivity activity;
    DBHelper dbHelper;
    Dao<Memo, Integer> memoDao;

    ListFragment listFragment = ListFragment.newInstance();
    FragmentManager manager;


    public ListAdapter() {}

    public ListAdapter(Context context, List<Memo> datas) {
        this.context = context;
        this.datas = datas;
        activity = (MainActivity) context; //메인액티비티가 컨텍스트로 넘어왔으니까 다시 메인액티비티로 다운캐스팅이 필요하다
    }


    public static ListAdapter getInstance() {
        ListAdapter listAdapter = new ListAdapter();

        return listAdapter;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Memo memo = datas.get(position);
        holder.textView.setText(memo.getMemo());
        holder.position = position;
        holder.memo = memo;

        if(memo.getUri() != null){
            Glide.with(context)
                    .load(Uri.parse(memo.getUri()))
                    .into(holder.imageViewThumb);

        }else {
            Glide.with(context)
                    .load(R.mipmap.ic_launcher )
                    .into(holder.imageViewThumb);
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textView;
        ImageView imageViewThumb;
        int position;
        Memo memo;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.cardView);
            imageViewThumb = (ImageView) view.findViewById(R.id.imageViewThumb);
            textView = (TextView) view.findViewById(R.id.textView);
            cardView.setMaxCardElevation(getPixelsFromDPs(10));
            cardView.setCardElevation(getPixelsFromDPs(6));
            cardView.setRadius(getPixelsFromDPs(5));
            cardView.setOnClickListener(this);
            cardView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    switch (v.getId()){
                        case R.id.cardView :
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                            alertDialog.setTitle("Delete Item");
                            alertDialog.setMessage("Do you want Delete item?");
                            alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(context,"삭제했습니다.", Toast.LENGTH_LONG).show();
                                    dbHelper = OpenHelperManager.getHelper(context, DBHelper.class);
                                    try {
                                        memoDao = dbHelper.getMemoDao();
                                        memoDao.delete(memo);
                                        try {
                                            datas = memoDao.queryForAll();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        listFragment.setData(datas);
                                        listFragment.refreshAdapter();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            alertDialog.setNeutralButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            //5. show함수로 팝업창에 화면에 띄운다.
                            alertDialog.show();
                            break;
                    }
                    return false;
                }
            });
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.cardView :
                    try {
                        EditFragment.memoEdit = memo;
                        activity.goEdit();
                        Log.i("메모","" + memo.getMemo());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    // Custom method for converting DP/DIP value to pixels
    protected int getPixelsFromDPs(int dps){
        Resources r = context.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

}
