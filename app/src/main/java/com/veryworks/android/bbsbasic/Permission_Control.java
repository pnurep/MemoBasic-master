package com.veryworks.android.bbsbasic;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

    //권한처리 담당하는 클래스
    //권한변경시 PERMISSION_ARRAY의 값만 변경해주면 된다.
public class Permission_Control {


    // 권한처리수정
    // 1. 요청할 권한 목록 작성
    public static final String PERMISSION_ARRAY[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};


    // 1. 권한체크
    @TargetApi(Build.VERSION_CODES.M) // 타겟 지정 애너테이션
    public static boolean checkPermission(Activity activity, int req_permission){
        // 1.1 런타임 권한 체크

        boolean permCheck = true;

        for(String perm : PERMISSION_ARRAY) {
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permCheck = false;
                break;
            }
        }
        //퍼미션이 모두 true이면 그냥 프로그램 실행
        if(!permCheck) {
            activity.requestPermissions(PERMISSION_ARRAY, req_permission);
            return false;
        }else {
           return true;
        }
    }


    // 2. 권한체크 후 콜백 < 사용자가 확인후 시스템이 호출하는 함수
    public static boolean onCheckResult(int[] grantResults) {

        boolean resultCheck = true;
        for(int result : grantResults) {
            if(result != PackageManager.PERMISSION_GRANTED) {
                resultCheck = false;
                break;
            }
        }
        return resultCheck;
    }
}
