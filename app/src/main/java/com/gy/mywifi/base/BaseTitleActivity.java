package com.gy.mywifi.base;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gy.mywifi.R;
import com.gy.mywifi.untils.ObjectUtils;


/**
 * Created by algorithm on 2017/10/31.
 */

public abstract class BaseTitleActivity extends BaseActivity {

    protected TextView mTitle;
    protected ImageView mLeftImage, mRightImage;
    protected TextView mLeftText, mRightText;

    /**
     * @param idResLeft
     * @param title
     * @param idResRight
     */
    protected void setTitle(@DrawableRes int idResLeft, String title, @DrawableRes int idResRight) {

        mTitle = (TextView) findViewById(R.id.title);
        mLeftImage = (ImageView) findViewById(R.id.iv_left);
        mRightImage = (ImageView) findViewById(R.id.iv_right);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
        }

        if (idResLeft != 0) {
            mLeftImage.setImageResource(idResLeft);
            mLeftImage.setVisibility(View.VISIBLE);

            mLeftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }



        if (idResRight != 0) {
            mRightImage.setImageResource(idResRight);
            mLeftImage.setVisibility(View.VISIBLE);
            mRightImage.setVisibility(View.VISIBLE);
        }
    }

    /**
     *
     * @param idResLeft
     * @param title
     * @param rightText
     */
    protected void setTitle(@DrawableRes int idResLeft, String title, String rightText) {
        mTitle = (TextView) findViewById(R.id.title);
        mLeftImage = (ImageView) findViewById(R.id.iv_left);
        mRightText = (TextView) findViewById(R.id.tv_right);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
        }

        if (idResLeft != 0) {
            mLeftImage.setImageResource(idResLeft);
            mLeftImage.setVisibility(View.VISIBLE);

            mLeftImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (!TextUtils.isEmpty(rightText)) {
            mRightText.setText(rightText);
            mRightText.setVisibility(View.VISIBLE);
        }
    }

    protected void setTitle(String leftText, String title, String rightText) {
        mLeftText = (TextView) findViewById(R.id.tv_left);
        mTitle = (TextView) findViewById(R.id.title);
        mRightText = (TextView) findViewById(R.id.tv_right);

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(leftText)) {
            mLeftText.setText(leftText);
            mLeftText.setVisibility(View.VISIBLE);

            mLeftText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        if (!TextUtils.isEmpty(rightText)) {
            mRightText.setText(rightText);
            mRightText.setVisibility(View.VISIBLE);
        }

    }

    protected void setTitle(@DrawableRes int idResLeft, String title, String rightText, View.OnClickListener clickListener) {
        setTitle(idResLeft,title,rightText);
        if(!ObjectUtils.isNull(mRightText) && !ObjectUtils.isNull(clickListener)){
            mRightText.setText(rightText);
            mRightText.setVisibility(View.VISIBLE);
            mRightText.setOnClickListener(clickListener);
            mRightText.setTextColor(getResources().getColor(R.color.theme_color));
        }


    }
}
