package com.veryworks.android.bbsbasic;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.veryworks.android.bbsbasic.data.DBHelper;
import com.veryworks.android.bbsbasic.domain.Memo;

import java.util.ArrayList;
import java.util.List;

public class EditFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match

    public static Memo memoEdit;
    Button btnEdit, btnCancel;
    EditText editMemo1;
    MainActivity activity;
    Context context = null;
    ListFragment listFragment = ListFragment.newInstance();
    Dao<Memo, Integer> memoSave;
    DBHelper dbHelper;
    ImageView imageViewEdit;

    int position = 0;
    View view;

    public EditFragment() {
        // Required empty public constructor
    }

    public void getPosition(int position){
        this.position = position;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("온크리에이트뷰", "-------"+memoEdit.getUri());

        if(view == null) {
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_edit, container, false);

            btnEdit = (Button) view.findViewById(R.id.btnSave);
            btnCancel = (Button) view.findViewById(R.id.btnCancel);
            editMemo1 = (EditText) view.findViewById(R.id.editMemo1);

            imageViewEdit = (ImageView)view.findViewById(R.id.imageViewEdit);

            btnEdit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF4444));

        }


        if(memoEdit.getUri() != null) {
            imageViewEdit.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(Uri.parse(memoEdit.getUri()))
                    .into(imageViewEdit);
            Log.d("에딧에 이미지 들어감ㅋ","---------------------");
        }else{
            Log.d("이미지 uri : ","-------------" + memoEdit.getUri());
            imageViewEdit.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        editMemo1.setText(memoEdit.getMemo());
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnSave:
                    dbHelper = OpenHelperManager.getHelper(context, DBHelper.class);
                    memoSave = dbHelper.getMemoDao();
                    String temp = editMemo1.getText().toString();
                    memoEdit.setMemo(temp);
                    Toast.makeText(context, "Edited!", Toast.LENGTH_SHORT).show();
                    memoSave.update(memoEdit);
                    listFragment.refreshAdapter();
                    break;
                case R.id.btnCancel:
                    activity.backToList();
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        this.activity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("detach", "-------");
    }



//      에딧에 줄을 쳐보려 노력... 했지만 별의미 없는것 같아서
//    public class LinedEditText extends EditText {
//        private Rect mRect;
//        private Paint mPaint;
//
//        // we need this constructor for LayoutInflater
//        public LinedEditText(Context context, AttributeSet attrs) {
//            super(context, attrs);
//
//            mRect = new Rect();
//            mPaint = new Paint();
//            mPaint.setStyle(Paint.Style.STROKE);
//            mPaint.setColor(0x800000FF);
//        }
//        @Override
//        protected void onDraw(Canvas canvas) {
//            int count = getLineCount();
//            Rect r = mRect;
//            Paint paint = mPaint;
//
//            for (int i = 0; i < count; i++) {
//                int baseline = getLineBounds(i, r);
//                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
//            }
//            super.onDraw(canvas);
//        }
//    }



}
