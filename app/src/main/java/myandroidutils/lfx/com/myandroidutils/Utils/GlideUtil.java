package myandroidutils.lfx.com.myandroidutils.Utils;

/**Create By Fangxing Liu
 *
 */
import java.util.concurrent.ExecutionException;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import myandroidutils.lfx.com.myandroidutils.R;

public class GlideUtil {
    public final static int SCALETYPE_CENTER_CROP = 0;
    public final static int SCALETYPE_FIT_CENTER = 1;
    private static int loading_icon;
    private static int loading_fail_icon;
    private static int unlogin_avatar;

    /**
     * Input Resource id In Root File
     * @param loading_icon
     * @param loading_fail_icon
     * @param unlogin_avatar
     */
    public void setDefaultImages(int loading_icon, int loading_fail_icon, int unlogin_avatar){
        GlideUtil.loading_icon = loading_icon;
        GlideUtil.loading_fail_icon = loading_fail_icon;
        GlideUtil.unlogin_avatar = unlogin_avatar;
    }

    private GlideUtil(){

    }

    private static class GlideUtilInstanceHolder{
        private static GlideUtil glideUtil = new GlideUtil();
    }

    public GlideUtil getInstance(int loading_icon, int loading_fail_icon, int unlogin_avatar){
        GlideUtilInstanceHolder.glideUtil.setDefaultImages(loading_icon,loading_fail_icon, unlogin_avatar);
        return GlideUtilInstanceHolder.glideUtil;
    }

    /**
     * 把图片显示到一个ImageView中
     * ScaleTyle请使用常量设置
     */
    public void showImage(Context context, String url, ImageView iv, int ScaleType){
        switch (ScaleType) {
            case GlideUtil.SCALETYPE_CENTER_CROP:
                Glide.with(context).load(url).placeholder(loading_icon).error(loading_fail_icon).centerCrop().into(iv);
                break;
            case GlideUtil.SCALETYPE_FIT_CENTER:
                Glide.with(context).load(url).placeholder(loading_icon).error(loading_fail_icon).fitCenter().into(iv);
                break;
        }
    }
    /**
     * 把图片显示到一个ImageView中
     * ScaleTyle为默认CENTER_CROP
     */
    public void showImage(Context context, String url, ImageView iv){
        showImage(context,url,iv,0);
    }

    /**
     * 获取圆形图片
     */
    public void showCircleImage(Context context, String url, final ImageView iv){
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                Bitmap roundBitmap = RounderCornerBitmap.toRoundBitmap(arg0);
                iv.setImageBitmap(roundBitmap);
            }
        };
        Glide.with(context).load(url).asBitmap().placeholder(unlogin_avatar).error(unlogin_avatar).into(target);
    }
    /**
     * 获取圆角图片
     */
    public void showRoundCornerImage(Context context, String url, final ImageView iv){
        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap arg0, GlideAnimation<? super Bitmap> arg1) {
                Bitmap roundBitmap = RounderCornerBitmap.getRoundedCornerBitmap(arg0);
                iv.setImageBitmap(roundBitmap);
            }
        };
        Glide.with(context).load(url).asBitmap().placeholder(loading_fail_icon).error(loading_fail_icon).into(target);
    }
    /**
     * 获取bitmap
     * Bitmap myBitmap = Glide.with(applicationContext)
     .load(yourUrl)
     .asBitmap() //必须
     .centerCrop()
     .into(500, 500)
     .get()
     */
    public Bitmap getBimap(Context context, String url, int width, int height){
        try {
            return Glide.with(context).load(url).asBitmap().centerCrop().into(width, height).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class RounderCornerBitmap {
        public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            final float roundPx =10;

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
        public static Bitmap toRoundBitmap(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float roundPx;
            float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
            if (width <= height) {
                roundPx = width / 2;
                top = 0;
                bottom = width;
                left = 0;
                right = width;
                height = width;
                dst_left = 0;
                dst_top = 0;
                dst_right = width;
                dst_bottom = width;
            } else {
                roundPx = height / 2;
                float clip = (width - height) / 2;
                left = clip;
                right = width - clip;
                top = 0;
                bottom = height;
                width = height;
                dst_left = 0;
                dst_top = 0;
                dst_right = height;
                dst_bottom = height;
            }

            Bitmap output = Bitmap.createBitmap(width,
                    height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
            final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);

            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, src, dst, paint);
            return output;
        }
    }
}

