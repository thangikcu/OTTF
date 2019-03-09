package vn.poly.hailt.ottf.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class KeyboardVisibilityEvent {

    private final static int KEYBOARD_VISIBLE_THRESHOLD_DP = 100;
    private final static double KEYBOARD_MIN_HEIGHT_RATIO = 0.15;

    public static void setEventListener(final Activity activity,
                                        final KeyboardVisibilityEventListener listener) {

        final Unregistrar unregistrar = registerEventListener(activity, listener);
        activity.getApplication()
                .registerActivityLifecycleCallbacks(new AutoActivityLifecycleCallback(activity) {
                    @Override
                    protected void onTargetActivityDestroyed() {
                        unregistrar.unregister();
                    }
                });
    }

    private static Unregistrar registerEventListener(final Activity activity,
                                                     final KeyboardVisibilityEventListener listener) {

        final View activityRoot = getActivityRoot(activity);

        final Rect r = new Rect();

        final FrameLayout.LayoutParams frameLayoutParams = (FrameLayout.LayoutParams) activityRoot.getLayoutParams();

        final ViewTreeObserver.OnGlobalLayoutListener layoutListener =
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    private boolean wasOpened = false;
                    private int usableHeightPrevious;

                    @Override
                    public void onGlobalLayout() {
                        activityRoot.getWindowVisibleDisplayFrame(r);

                        int usableHeightNow = r.bottom - r.top;

                        if (usableHeightNow != usableHeightPrevious) {
                            int screenHeight = activityRoot.getRootView().getHeight();
                            int heightDiff = screenHeight - usableHeightNow;

                            boolean isOpen = heightDiff > screenHeight * KEYBOARD_MIN_HEIGHT_RATIO;

                            if (isOpen == wasOpened) {
                                // keyboard state has not changed
                                return;
                            }

                            wasOpened = isOpen;
                            usableHeightPrevious = usableHeightNow;
                            listener.onVisibilityChanged(isOpen);

                            if (isOpen) {
                                frameLayoutParams.height = screenHeight - heightDiff;
                            } else {
                                frameLayoutParams.height = usableHeightNow;
                            }
                            activityRoot.requestLayout();
                        }
                    }
                };
        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);

        return new SimpleUnregistrar(activity, layoutListener);
    }

    static View getActivityRoot(Activity activity) {
        return ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
    }

    public interface KeyboardVisibilityEventListener {

        void onVisibilityChanged(boolean isOpen);
    }

    public interface Unregistrar {
        void unregister();
    }
}
