package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import cn.ucai.fulicenter.R;

public class FuliCenterMain2Activity extends Activity {
    RadioButton mRadioNewGood;
    RadioButton mRadioBoutique;
    RadioButton mRadioCategory;
    RadioButton mRadioCart;
    RadioButton mRadioPersonalCenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main2);
        initView();
    }

    private void initView() {


    }

    private  void onCheckedChange(View view){

    }
}
