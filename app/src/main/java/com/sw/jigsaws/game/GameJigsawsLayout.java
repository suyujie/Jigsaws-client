package com.sw.jigsaws.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sw.jigsaws.data.GameData;
import com.sw.jigsaws.ui.PlayGame;
import com.sw.jigsaws.utils.DisplayDensity;
import com.sw.jigsaws.utils.ImagePiece;
import com.sw.jigsaws.utils.ImageSize;
import com.sw.jigsaws.utils.ImageSplitterUtil;
import com.sw.jigsaws.utils.Utils;

import java.util.Collections;
import java.util.List;

public class GameJigsawsLayout extends RelativeLayout implements OnClickListener {

    //容器的内边距
    private int mPadding;
    //每张小图之间的距离（横、纵） dp
    private int mMargin = 1;
    private ImageView[] pieceItems;
    private int mItemWidth;
    private int mItemHeight;
    // 游戏的图片
    private List<ImagePiece> pieces;
    private boolean once;

    public GameListener mListener;

    private static final int GAME_SUCCESS = 0x111;

    /**
     * 设置接口回调
     */
    public void setOnGameListener(GameListener mListener) {
        this.mListener = mListener;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GAME_SUCCESS:

                    PlayGame playGame = (PlayGame) getContext();
                    playGame.showImage();

                    break;

            }
        }

    };

    public GameJigsawsLayout(Context context) {
        this(context, null);
    }

    public GameJigsawsLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameJigsawsLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        mPadding = Utils.min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!once) {

            //图片比例缩放
            GameData.game.bitmap = ImageSize.resizeImage(GameData.game.bitmap, getMeasuredWidth(), getMeasuredHeight() - DisplayDensity.dip2px(getContext(), 50), false);
            // 进行切图，以及排序
            initBitmap();
            // 设置ImageView(Item)的宽高等属性
            initItem();

            this.setLayoutParams(new LayoutParams(GameData.game.bitmap.getWidth(), GameData.game.bitmap.getHeight()));

            once = true;

        }

    }


    /**
     * 进行切图，以及排序
     */
    private void initBitmap() {

        pieces = ImageSplitterUtil.splitImage(GameData.game.bitmap, GameData.game.line, GameData.game.column, mMargin);

        for (ImagePiece ip : pieces) {
            mItemWidth = ip.getBitmap().getWidth();
            mItemHeight = ip.getBitmap().getHeight();
        }

        // 乱序
        Collections.shuffle(pieces);

    }

    /**
     * 设置ImageView(Item)的宽高等属性
     */
    private void initItem() {

        pieceItems = new ImageView[GameData.game.line * GameData.game.column];
        // 生成我们的Item，设置Rule
        for (int i = 0; i < pieceItems.length; i++) {

            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(pieces.get(i).getBitmap());
            item.setScaleType(ImageView.ScaleType.FIT_XY);

            pieceItems[i] = item;
            item.setId(i + 1);

            // 在Item的tag中存储了index
            item.setTag(i + "_" + pieces.get(i).getIndex());

            LayoutParams lp = new LayoutParams(mItemWidth, mItemHeight);

            lp.topMargin = mMargin;
            lp.bottomMargin = mMargin;
            lp.leftMargin = mMargin;
            lp.rightMargin = mMargin;

            // 不是第一列,放到pieceItems[i - 1].getId()的右边
            if (i % GameData.game.column != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, pieceItems[i - 1].getId());
            }
            // 如果不是第一行 , 设置topMargin和rule，放到pieceItems[i - line].getId()的下边
            if ((i + 1) > GameData.game.column) {
                lp.addRule(RelativeLayout.BELOW, pieceItems[i - GameData.game.column].getId());
            }

            addView(item, lp);
        }

    }

    private ImageView mFirst;
    private ImageView mSecond;

    @Override
    public void onClick(View v) {
        if (isAniming)
            return;

        // 两次点击同一个Item
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        } else {
            mSecond = (ImageView) v;
            // 交换我们的Item
            exchangeView();
        }

    }

    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;
    private boolean isAniming;

    /**
     * 交换我们的Item
     */
    private void exchangeView() {

        mFirst.setColorFilter(null);
        // 构造我们的动画层
        setUpAnimLayout();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = pieces.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = pieces.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        // 设置动画
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation animSecond = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop() + mFirst.getTop());
        animSecond.setDuration(300);
        animSecond.setFillAfter(true);
        second.startAnimation(animSecond);

        this.setBackgroundColor(Color.argb(Utils.randomInt(0, 255), Utils.randomInt(0, 255), Utils.randomInt(0, 255), Utils.randomInt(0, 255)));

        // 监听动画
        anim.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                // 判断用户游戏是否成功
                checkSuccess();
                isAniming = false;
            }
        });

    }

    /**
     * 判断用户游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;

        for (int i = 0; i < pieceItems.length; i++) {

            ImageView imageView = pieceItems[i];

            if (getImageIndexByTag((String) imageView.getTag()) != i) {
                isSuccess = false;
            }
        }

        if (isSuccess) {
            mHandler.sendEmptyMessage(GAME_SUCCESS);
        }

    }

    /**
     * 根据tag获取Id
     *
     * @param tag
     * @return
     */
    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    public int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构造我们的动画层
     */
    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }

}
