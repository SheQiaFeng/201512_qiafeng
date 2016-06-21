package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.fragment.BoutiqueFragment;
import cn.ucai.fulicenter.fragment.CategoryFragment;
import cn.ucai.fulicenter.fragment.NewGoodFragment;
import cn.ucai.fulicenter.fragment.PersonalCenterFragment;

public class FuliCenterMain2Activity extends BaseActivity {

    TextView mTvCartHint;
    RadioButton mRadioNewGood;
    RadioButton mRadioBoutique;
    RadioButton mRadioCategory;
    RadioButton mRadioCart;
    RadioButton mRadioPersonalCenter;
    RadioButton[] mRadios = new RadioButton[5];
    NewGoodFragment mNewGoodFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    Fragment[] mFragments = new Fragment[5];
    private int index;
    private int currentTabIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main2);
        initView();
        initFragment();
        // 添加显示第一个fragment
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, mNewGoodFragment)
                .add(R.id.fragment_container, mBoutiqueFragment).hide(mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment).hide(mCategoryFragment)
                .add(R.id.fragment_container, mPersonalCenterFragment).hide(mPersonalCenterFragment)
                .show(mNewGoodFragment)
                .commit();
    }

    private void initFragment() {
        mNewGoodFragment = new NewGoodFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragments[0] = mNewGoodFragment;
        mFragments[1] = mBoutiqueFragment;
        mFragments[2] = mCategoryFragment;
        mFragments[4] = mPersonalCenterFragment;
    }

    private void initView() {
        mTvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mRadioNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        mRadioBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mRadioCategory = (RadioButton) findViewById(R.id.layout_category);
        mRadioCart = (RadioButton) findViewById(R.id.layout_cart);
        mRadioPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);

        mRadios[0] = mRadioNewGood;
        mRadios[1] = mRadioBoutique;
        mRadios[2] = mRadioCategory;
        mRadios[3] = mRadioCart;
        mRadios[4] = mRadioPersonalCenter;

    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
                index = 3;
                break;
            case R.id.layout_personal_center:
                if (FuLiCenterApplication.getInstance().getUser() != null) {
                    index = 4;
                } else {
                    gotoLogin();
                }

                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(mFragments[currentTabIndex]);
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragments[index]);
            }

            trx.show(mFragments[index]).commit();
            setRadioChecked(index);
            currentTabIndex = index;
        }
    }

    private void gotoLogin() {
        startActivity(new Intent(this, LoginActivity.class).putExtra("action", I.ACTION_TYPE_PERSONAL));
    }

    private void setRadioChecked(int index) {
        for (int i = 0; i < mRadios.length; i++) {
            if (i == index) {
                mRadios[i].setChecked(true);
            } else {
                mRadios[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String ation = getIntent().getStringExtra("action");
        Log.e("main", "action=" + ation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("main", "user=" + FuLiCenterApplication.getInstance().getUser());
        String action = getIntent().getStringExtra("action");
        if (FuLiCenterApplication.getInstance().getUser() != null && action != null) {
            if (action.equals(I.ACTION_TYPE_PERSONAL)) {
                index = 4;
            } else {
                setRadioChecked(index);
            }
            if (currentTabIndex != index) {
                FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                trx.hide(mFragments[currentTabIndex]);
                if (!mFragments[index].isAdded()) {
                    trx.add(R.id.fragment_container, mFragments[index]);
                }

                trx.show(mFragments[index]).commit();
                setRadioChecked(index);
                currentTabIndex = index;
            }
        } else {
            setRadioChecked(index);
        }

    }
}
