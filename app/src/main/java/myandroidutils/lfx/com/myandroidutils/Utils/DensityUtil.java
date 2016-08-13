package myandroidutils.lfx.com.myandroidutils.Utils;

/**Create By Fangxing Liu
 */
import android.content.Context;

public class DensityUtil {
    /**
     * Transform dip(Coding) to dp(Xml)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Transform dp(Xml) to dip(Coding)
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
