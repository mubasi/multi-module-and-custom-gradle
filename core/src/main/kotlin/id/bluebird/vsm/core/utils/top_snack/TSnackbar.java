package id.bluebird.vsm.core.utils.top_snack;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.google.android.material.behavior.SwipeDismissBehavior;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import id.bluebird.vsm.core.R;


public final class TSnackbar {

    public abstract static class Callback {

        public static final int DISMISS_EVENT_SWIPE = 0;

        public static final int DISMISS_EVENT_ACTION = 1;

        public static final int DISMISS_EVENT_TIMEOUT = 2;

        public static final int DISMISS_EVENT_MANUAL = 3;

        public static final int DISMISS_EVENT_CONSECUTIVE = 4;

        public static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
        public static final Interpolator FAST_OUT_SLOW_IN_INTERPOLATOR = new FastOutSlowInInterpolator();
        public static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();


        @IntDef({
                DISMISS_EVENT_SWIPE, DISMISS_EVENT_ACTION, DISMISS_EVENT_TIMEOUT,
                DISMISS_EVENT_MANUAL, DISMISS_EVENT_CONSECUTIVE
        })

        @Retention(RetentionPolicy.SOURCE)
        public @interface DismissEvent {

        }

        public void onDismissed(TSnackbar snackbar, @DismissEvent int event) {

        }

        public void onShown(TSnackbar snackbar) {

        }
    }

    @IntDef({LENGTH_INDEFINITE, LENGTH_SHORT, LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {
    }

    public static final int LENGTH_INDEFINITE = -2;

    public static final int LENGTH_SHORT = -1;

    public static final int LENGTH_LONG = 0;

    private static final int ANIMATION_DURATION = 250;
    private static final int ANIMATION_FADE_DURATION = 180;

    private static final Handler sHandler;
    private static final int MSG_SHOW = 0;
    private static final int MSG_DISMISS = 1;

    static {
        sHandler = new Handler(Looper.getMainLooper(), message -> {
            if (message.what == MSG_SHOW) {
                ((TSnackbar) message.obj).showView();
                return true;
            } else if (message.what == MSG_DISMISS) {
                ((TSnackbar) message.obj).hideView(message.arg1);
                return true;
            } else {
                return false;
            }
        });
    }

    private final ViewGroup mParent;
    private final Context mContext;
    private final SnackbarLayout mView;
    private int mDuration;
    private Callback mCallback;

    private TSnackbar(ViewGroup parent) {
        mParent = parent;
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mView = (SnackbarLayout) inflater.inflate(R.layout.tsnackbar_layout, mParent, false);
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @NonNull CharSequence text, @Duration int duration) {
        TSnackbar snackbar = new TSnackbar(findSuitableParent(view));
        snackbar.setText(text);
        snackbar.setDuration(duration);
        return snackbar;
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @NonNull SpannableString text, @Duration int duration) {
        TSnackbar snackbar = new TSnackbar(findSuitableParent(view));
        snackbar.setText(text);
        snackbar.setDuration(duration);
        return snackbar;
    }

    @NonNull
    public static TSnackbar make(@NonNull View view, @StringRes int resId, @Duration int duration) {
        return make(view, view.getResources()
                .getText(resId), duration);
    }

    private static ViewGroup findSuitableParent(View view) {
        ViewGroup fallback = null;
        do {
            if (view instanceof CoordinatorLayout) {
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout) {
                if (view.getId() == android.R.id.content) {
                    return (ViewGroup) view;
                } else {
                    fallback = (ViewGroup) view;
                }
            } else if (view instanceof androidx.appcompat.widget.Toolbar || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && view instanceof Toolbar)
                    && view.getParent() instanceof ViewGroup) {
                return interfaceOfToolbar(view);
            }
            view = viewIsNoNull(view);
        } while (view != null);

        return fallback;
    }

    private static View viewIsNoNull(View view) {
        View result = null;
        if (view != null) {
            final ViewParent parent = view.getParent();
            result = parent instanceof View ? (View) parent : null;
        }
        return result;
    }


    private static ViewGroup interfaceOfToolbar(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        ViewGroup result = null;

        // check if there's something else beside toolbar
        if (parent.getChildCount() > 1) {
            result = getViewGroupChild(parent, view);
        }
        return result;
    }

    private static ViewGroup getViewGroupChild(ViewGroup parent, View view) {
        int childrenCnt = parent.getChildCount();
        int toolbarIdx;
        ViewGroup result = null;
        for (int i = 0; i < childrenCnt; i++) {
            // find the index of toolbar in the layout (most likely 0, but who knows)
            if (parent.getChildAt(i) == view) {
                toolbarIdx = i;
                // check if there's something else after the toolbar in the layout
                if (toolbarIdx < childrenCnt - 1) {
                    //try to find some ViewGroup where you can attach the toast
                    while (i < childrenCnt) {
                        i++;
                        View v = parent.getChildAt(i);
                        if (v instanceof ViewGroup) return (ViewGroup) v;
                    }
                }
                break;
            }
        }
        return result;
    }


    public void setText(@NonNull CharSequence message) {
        final TextView tv = mView.getMessageView();
        tv.setText(message);
    }

    public void setText(@StringRes int resId) {
        setText(mContext.getText(resId));
    }

    public void setDuration(@Duration int duration) {
        mDuration = duration;
    }

    @NonNull
    public View getView() {
        return mView;
    }

    public void show() {
        SnackbarManager.getInstance()
                .show(mDuration, mManagerCallback);
    }

    public void dismiss() {
        SnackbarManager.getInstance().dismiss(mManagerCallback, 1);
    }

    private void dispatchDismiss(@Callback.DismissEvent int event) {
        SnackbarManager.getInstance()
                .dismiss(mManagerCallback, event);
    }

    @NonNull
    public TSnackbar setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    public boolean isShownOrQueued() {
        return SnackbarManager.getInstance()
                .isCurrentOrNext(mManagerCallback);
    }

    private final SnackbarManager.Callback mManagerCallback = new SnackbarManager.Callback() {
        @Override
        public void show() {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_SHOW, TSnackbar.this));
        }

        @Override
        public void dismiss(int event) {
            sHandler.sendMessage(sHandler.obtainMessage(MSG_DISMISS, event, 0, TSnackbar.this));
        }
    };

    final void showView() {
        if (mView.getParent() == null) {
            final ViewGroup.LayoutParams lp = mView.getLayoutParams();

            if (lp instanceof CoordinatorLayout.LayoutParams) {
                final Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_START_TO_END);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() {
                    @Override
                    public void onDismiss(View view) {
                        dispatchDismiss(Callback.DISMISS_EVENT_SWIPE);
                    }

                    @Override
                    public void onDragStateChanged(int state) {
                        switch (state) {
                            case SwipeDismissBehavior.STATE_DRAGGING:
                            case SwipeDismissBehavior.STATE_SETTLING:

                                SnackbarManager.getInstance()
                                        .cancelTimeout(mManagerCallback);
                                break;
                            case SwipeDismissBehavior.STATE_IDLE:

                                SnackbarManager.getInstance()
                                        .restoreTimeout(mManagerCallback);
                                break;
                            default:
                                break;
                        }
                    }
                });
                ((CoordinatorLayout.LayoutParams) lp).setBehavior(behavior);
            }
            mParent.addView(mView);
        }

        mView.setOnAttachStateChangeListener(new SnackbarLayout.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                // nothing
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                if (isShownOrQueued()) {
                    sHandler.post(() -> onViewHidden(Callback.DISMISS_EVENT_MANUAL));
                }
            }
        });

        if (ViewCompat.isLaidOut(mView)) {
            animateViewIn();
        } else {
            mView.setOnLayoutChangeListener((view, left, top, right, bottom) -> {
                animateViewIn();
                mView.setOnLayoutChangeListener(null);
            });
        }
    }

    private void animateViewIn() {
        mView.setTranslationY(-mView.getHeight());
        ViewCompat.animate(mView)
                .translationY(0f)
                .setInterpolator(Callback.FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mView.animateChildrenIn();
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        if (mCallback != null) {
                            mCallback.onShown(TSnackbar.this);
                        }
                        SnackbarManager.getInstance()
                                .onShown(mManagerCallback);
                    }
                })
                .start();

    }

    private void animateViewOut(final int event) {
        ViewCompat.animate(mView)
                .translationY(-mView.getHeight())
                .setInterpolator(Callback.FAST_OUT_SLOW_IN_INTERPOLATOR)
                .setDuration(ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        mView.animateChildrenOut();
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        onViewHidden(event);
                    }
                })
                .start();

    }

    final void hideView(int event) {
        if (mView.getVisibility() != View.VISIBLE || isBeingDragged()) {
            onViewHidden(event);
        } else {
            animateViewOut(event);
        }
    }

    private void onViewHidden(int event) {

        SnackbarManager.getInstance()
                .onDismissed(mManagerCallback);

        if (mCallback != null) {
            mCallback.onDismissed(this, event);
        }

        final ViewParent parent = mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mView);
        }
    }

    private boolean isBeingDragged() {
        final ViewGroup.LayoutParams lp = mView.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            final CoordinatorLayout.LayoutParams cllp = (CoordinatorLayout.LayoutParams) lp;
            final CoordinatorLayout.Behavior behavior = cllp.getBehavior();

            if (behavior instanceof SwipeDismissBehavior) {
                return ((SwipeDismissBehavior) behavior).getDragState()
                        != SwipeDismissBehavior.STATE_IDLE;
            }
        }
        return false;
    }

    public static class SnackbarLayout extends LinearLayout {
        private TextView mMessageView;
        private ImageView mActionView;

        private final int mMaxWidth;
        private final int mMaxInlineActionWidth;

        interface OnLayoutChangeListener {
            void onLayoutChange(View view, int left, int top, int right, int bottom);
        }

        interface OnAttachStateChangeListener {
            void onViewAttachedToWindow(View v);

            void onViewDetachedFromWindow(View v);
        }

        private OnLayoutChangeListener mOnLayoutChangeListener;
        private OnAttachStateChangeListener mOnAttachStateChangeListener;

        public SnackbarLayout(Context context) {
            this(context, null);
        }

        public SnackbarLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
            mMaxWidth = a.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
            mMaxInlineActionWidth = a.getDimensionPixelSize(
                    R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
            if (a.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, a.getDimensionPixelSize(
                        R.styleable.SnackbarLayout_elevation, 0));
            }
            a.recycle();

            setClickable(true);


            LayoutInflater.from(context)
                    .inflate(R.layout.tsnackbar_layout_include, this);

            ViewCompat.setAccessibilityLiveRegion(this,
                    ViewCompat.ACCESSIBILITY_LIVE_REGION_POLITE);
        }

        @Override
        protected void onFinishInflate() {
            super.onFinishInflate();
            mMessageView = findViewById(R.id.snackbar_text);
            mActionView = findViewById(R.id.snackbar_action);
        }

        TextView getMessageView() {
            return mMessageView;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            if (mMaxWidth > 0 && getMeasuredWidth() > mMaxWidth) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxWidth, MeasureSpec.EXACTLY);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            final int multiLineVPadding = getResources().getDimensionPixelSize(
                    R.dimen.design_snackbar_padding_vertical_2lines);
            final int singleLineVPadding = getResources().getDimensionPixelSize(
                    R.dimen.design_snackbar_padding_vertical);
            final boolean isMultiLine = mMessageView.getLayout()
                    .getLineCount() > 1;

            boolean remeasure = false;
            if (isMultiLine && mMaxInlineActionWidth > 0
                    && mActionView.getMeasuredWidth() > mMaxInlineActionWidth) {
                if (updateViewsWithinLayout(VERTICAL, multiLineVPadding,
                        multiLineVPadding - singleLineVPadding)) {
                    remeasure = true;
                }
            } else {
                final int messagePadding = isMultiLine ? multiLineVPadding : singleLineVPadding;
                if (updateViewsWithinLayout(HORIZONTAL, messagePadding, messagePadding)) {
                    remeasure = true;
                }
            }

            if (remeasure) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }

        void animateChildrenIn() {
            int delay = ANIMATION_DURATION - ANIMATION_FADE_DURATION;
            int duration = ANIMATION_FADE_DURATION;
            mMessageView.setAlpha(0f);
            ViewCompat.animate(mMessageView)
                    .alpha(1f)
                    .setDuration(duration)
                    .setStartDelay(delay)
                    .start();

            if (mActionView.getVisibility() == VISIBLE) {
                mActionView.setAlpha(0f);
                ViewCompat.animate(mActionView)
                        .alpha(1f)
                        .setDuration(duration)
                        .setStartDelay(delay)
                        .start();
            }
        }

        void animateChildrenOut() {
            int delay = 0;
            int duration = ANIMATION_FADE_DURATION;
            mMessageView.setAlpha(1f);
            ViewCompat.animate(mMessageView)
                    .alpha(0f)
                    .setDuration(duration)
                    .setStartDelay(delay)
                    .start();

            if (mActionView.getVisibility() == VISIBLE) {
                mActionView.setAlpha(1f);
                ViewCompat.animate(mActionView)
                        .alpha(0f)
                        .setDuration(duration)
                        .setStartDelay(delay)
                        .start();
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (mOnLayoutChangeListener != null) {
                mOnLayoutChangeListener.onLayoutChange(this, l, t, r, b);
            }
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (mOnAttachStateChangeListener != null) {
                mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        void setOnLayoutChangeListener(OnLayoutChangeListener onLayoutChangeListener) {
            mOnLayoutChangeListener = onLayoutChangeListener;
        }

        void setOnAttachStateChangeListener(OnAttachStateChangeListener listener) {
            mOnAttachStateChangeListener = listener;
        }

        private boolean updateViewsWithinLayout(final int orientation,
                                                final int messagePadTop, final int messagePadBottom) {
            boolean changed = false;
            if (orientation != getOrientation()) {
                setOrientation(orientation);
                changed = true;
            }
            if (mMessageView.getPaddingTop() != messagePadTop
                    || mMessageView.getPaddingBottom() != messagePadBottom) {
                updateTopBottomPadding(mMessageView, messagePadTop, messagePadBottom);
                changed = true;
            }
            return changed;
        }

        private static void updateTopBottomPadding(View view, int topPadding, int bottomPadding) {
            if (ViewCompat.isPaddingRelative(view)) {
                ViewCompat.setPaddingRelative(view,
                        ViewCompat.getPaddingStart(view), topPadding,
                        ViewCompat.getPaddingEnd(view), bottomPadding);
            } else {
                view.setPadding(view.getPaddingLeft(), topPadding,
                        view.getPaddingRight(), bottomPadding);
            }
        }
    }

    final class Behavior extends SwipeDismissBehavior<SnackbarLayout> {
        @Override
        public boolean canSwipeDismissView(@NonNull View child) {
            return child instanceof SnackbarLayout;
        }

        @Override
        public boolean onInterceptTouchEvent(CoordinatorLayout parent, @NonNull SnackbarLayout child,
                                             MotionEvent event) {


            if (parent.isPointInChildBounds(child, (int) event.getX(), (int) event.getY())) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        SnackbarManager.getInstance()
                                .cancelTimeout(mManagerCallback);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        SnackbarManager.getInstance()
                                .restoreTimeout(mManagerCallback);
                        break;
                    default:
                        break;
                }
            }

            return super.onInterceptTouchEvent(parent, child, event);
        }
    }
}
