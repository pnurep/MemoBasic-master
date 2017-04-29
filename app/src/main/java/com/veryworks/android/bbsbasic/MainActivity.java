package com.veryworks.android.bbsbasic;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.veryworks.android.bbsbasic.data.DBHelper;
import com.veryworks.android.bbsbasic.domain.Memo;
import com.veryworks.android.bbsbasic.interfaces.DetailInterface;
import com.veryworks.android.bbsbasic.interfaces.ListInterface;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.veryworks.android.bbsbasic.R.id.editMemo;
import static com.veryworks.android.bbsbasic.R.id.editMemo1;

public class MainActivity extends AppCompatActivity implements ListInterface, DetailInterface{
    private static final String TAG="MemoMain";
    public static final int REQ_CODE = 100;


    ListFragment list;
    DetailFragment detail = DetailFragment.newInstance();
    EditFragment editFragment;

    FrameLayout main;
    FragmentManager manager;

    DBHelper dbHelper;

    List<Memo> datas = new ArrayList<>();
    Dao<Memo, Integer> memoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF4444));
//        0xFFF3691C 주황색
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            init();
        }
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.d("포트레이트","----------------------");
        }else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.d("랜드스케이프","-------------------------");
        }
    }



    public void init() {
        list = ListFragment.newInstance();
        detail = new DetailFragment();
        editFragment = new EditFragment();
        main = (FrameLayout) findViewById(R.id.activity_main);
        manager = getSupportFragmentManager();
        try {
            loadData();
        }catch (SQLException e){
            Log.e(TAG, e+"============================"); }
        list.setData(datas);
        setList();
    }

    private void checkPermission() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(Permission_Control.checkPermission(this, REQ_CODE)) {
                init(); // 프로그램실행
            }
        } else {
            init(); // 사실상 여기에서 main 시작
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_CODE) {
            if(Permission_Control.onCheckResult(grantResults)) {
                init();
            } else {
                Toast.makeText(this, "권한을 허용하지 않으시면 프로그램을 실행할수 없습니다.", Toast.LENGTH_LONG).show();
                // 선택1 종료, 2 권한체크 다시 물어보기
                finish();
            }
        }
    }



    public void loadData() throws SQLException {
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        datas = memoDao.queryForAll();
        Log.e(TAG, "data size============================"+datas.size());
    }



    // 목록 프래그먼트 FrameLayout 에 add
    private void setList(){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activity_main, list);
        transaction.commit();
    }

    @Override
    public void goDetail(){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activity_main, detail);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void goDetail(int position) {}


    public void goEdit() throws SQLException {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.activity_main, editFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        if(detail.isAdded()){
            detail.editMemo.setText("");
            detail.imageViewDetail.setImageResource(R.mipmap.ic_launcher);

        }
        super.onBackPressed();
    }

    @Override
    public void backToList() {
        super.onBackPressed();
    }

    @Override
    public void saveToList(Memo memo) throws SQLException {

        Log.i(TAG,"memo============================================"+memo.getMemo());

        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        memoDao = dbHelper.getMemoDao();
        memoDao.create(memo);
        loadData();
        list.setData(datas);
        super.onBackPressed();
        list.refreshAdapter();
    }


}
