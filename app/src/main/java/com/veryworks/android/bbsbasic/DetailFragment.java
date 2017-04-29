package com.veryworks.android.bbsbasic;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.veryworks.android.bbsbasic.domain.Memo;
import com.veryworks.android.bbsbasic.interfaces.DetailInterface;

import java.sql.SQLException;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.veryworks.android.bbsbasic.R.attr.icon;


public class DetailFragment extends Fragment implements View.OnClickListener{
    private final int REQ_CAMERA = 101;
    private final int REQ_GALLARY = 102;
    Uri fileUri = null;
    String uri = null;
    String pic = null;

    Context context = null;
    DetailInterface detailInterface = null;

    ListAdapter listAdapter = ListAdapter.getInstance();

    MainActivity activity;

    View view = null;

    Button btnSave, btnCancel, btnCamera, btnGallery;
    EditText editMemo;
    ImageView imageViewDetail;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance() {
        DetailFragment fragment = new DetailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if(getArguments() != null) {
//            pic = getArguments().getString("uri");
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("온크리에이트뷰입장","------------------------");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_detail, container, false);
            btnSave = (Button) view.findViewById(R.id.btnSave);
            btnCancel = (Button) view.findViewById(R.id.btnCancel);
            btnCamera = (Button) view.findViewById(R.id.btnCamera);
            btnGallery = (Button) view.findViewById(R.id.btnGallery);

            imageViewDetail = (ImageView) view.findViewById(R.id.imageViewDetail);

            editMemo = (EditText) view.findViewById(R.id.editMemo);

            btnSave.setOnClickListener(this);
            btnCancel.setOnClickListener(this);
            btnCamera.setOnClickListener(this);
            btnGallery.setOnClickListener(this);

            imageViewDetail.setImageURI(null);

            ActionBar actionBar = activity.getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(0xFFFF4444));

//            if(pic != null) {
//                uri = pic;
//                imageViewDetail.setVisibility(View.VISIBLE);
//                Glide.with(context)
//                        .load(Uri.parse(pic))
//                        .into(imageViewDetail);
//                Log.d("디테일에 이미지 들어감ㅋ","---------------------");
//            }

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("온리줌 입장","---------------------");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        activity = (MainActivity) context;
        this.detailInterface = (DetailInterface) context;
    }

    //프래그먼트 전환시 키보드 안내려가던거 내려가게 해줌
    public static void downKeyboard(Context context, EditText editText) {
        InputMethodManager mInputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btnSave:
                try {
                    Memo memo = new Memo();
                    String string = editMemo.getText().toString();
                    editMemo.setText("");
                    memo.setMemo(string);
                    memo.setDate(new Date(System.currentTimeMillis()));
                    memo.setUri(uri);
                    fileUri = null;
                    uri = null;
                    imageViewDetail.setImageResource(R.mipmap.ic_launcher);
                    downKeyboard(context,editMemo);
                    detailInterface.saveToList(memo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btnCancel:
                editMemo.setText("");
                imageViewDetail.setImageResource(R.mipmap.ic_launcher);
                detailInterface.backToList();
                break;

            case R.id.btnCamera :
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // 롤리팝 이상 버전에서는 아래 코드를 반영해야 한다.
                // --- 카메라 촬영 후 미디어 컨텐트 uri 를 생성해서 외부저장소에 저장한다 ---
                if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    // 저장할 미디어 속성을 정의하는 클래스
                    ContentValues values = new ContentValues(1);
                    // 속성중에 파일의 종류를 정의
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                    // 전역변수로 정의한 fileUri에 외부저장소 컨텐츠가 있는 Uri 를 임시로 생성해서 넣어준다.
                    fileUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    // 위에서 생성한 fileUri를 사진저장공간으로 사용하겠다고 설정
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    // Uri에 읽기와 쓰기 권한을 시스템에 요청
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                // --- 여기 까지 컨텐트 uri 강제세팅 ---
                startActivityForResult(intent, REQ_CAMERA);

                break;

            case R.id.btnGallery :
                // 외부저장소 다 가지고 오는 것
                intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // 이미지 여러개 가져오는 플래그
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);

                intent.setType("image/*"); // 외부저장소에 있는 이미지만 가져오기 위한 필터링
                startActivityForResult( Intent.createChooser(intent,"Select Picture") , REQ_GALLARY);
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case REQ_GALLARY :
                if(resultCode == RESULT_OK) {
                    fileUri = data.getData();
                    uri = fileUri.toString();
                    addPhoto(uri);
                    Log.d("갤러리 이미지 들어감 ㅋ","------------------------");
                }
                break;

            case REQ_CAMERA :
                if (requestCode == REQ_CAMERA && resultCode == RESULT_OK) { // 사진 확인처리됨 RESULT_OK = -1
                    // 롤리팝 체크
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        Log.i("Camera", "data.getData()===============================" + data.getData());
                        fileUri = data.getData();
                        uri = fileUri.toString();
                    } Log.i("Camera", "fileUri===============================" + fileUri);

                    if (fileUri != null) {
                        uri = fileUri.toString();
                        addPhoto(uri);
                    } else {
                        Toast.makeText(getContext(), "사진파일이 없습니다", Toast.LENGTH_LONG).show();
                    }
                } else {
                }
                break;
        }

    }
    public void addPhoto(String uri){
        imageViewDetail.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(uri)
                .into(imageViewDetail);
    }



}
