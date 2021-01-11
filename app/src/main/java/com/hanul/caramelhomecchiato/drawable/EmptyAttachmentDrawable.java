package com.hanul.caramelhomecchiato.drawable;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Objects;

public class EmptyAttachmentDrawable extends Drawable{
	private final Drawable icon;
	private final int width;
	private final int height;

	public EmptyAttachmentDrawable(Context context, @DrawableRes int drawable, int width, int height){
		this(ContextCompat.getDrawable(context, drawable), width, height);
	}

	public EmptyAttachmentDrawable(Drawable icon, int width, int height){
		this.icon = Objects.requireNonNull(icon);
		this.width = width;
		this.height = height;
	}

	@Override public void setBounds(int left, int top, int right, int bottom){
		int w = right-left;
		int h = bottom-top;
		if(w!=width){
			left = left+(w-width)/2;
			right = left+width;
		}
		if(h!=height){
			top = top+(h-height)/2;
			bottom = top+height;
		}
		icon.setBounds(left, top, right, bottom);
	}

	@Override public void draw(@NonNull Canvas canvas){icon.draw(canvas);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @NonNull @Override public Rect getDirtyBounds(){return icon.getDirtyBounds();}
	@Override public void setChangingConfigurations(int configs){icon.setChangingConfigurations(configs);}
	@Override public int getChangingConfigurations(){return icon.getChangingConfigurations();}
	@Override @Deprecated public void setDither(boolean dither){icon.setDither(dither);}
	@Override public void setFilterBitmap(boolean filter){icon.setFilterBitmap(filter);}
	@RequiresApi(api = Build.VERSION_CODES.M) @Override public boolean isFilterBitmap(){return icon.isFilterBitmap();}
	@Nullable @Override public Callback getCallback(){return icon.getCallback();}
	@Override public void invalidateSelf(){icon.invalidateSelf();}
	@Override public void scheduleSelf(@NonNull Runnable what, long when){icon.scheduleSelf(what, when);}
	@Override public void unscheduleSelf(@NonNull Runnable what){icon.unscheduleSelf(what);}
	@RequiresApi(api = Build.VERSION_CODES.M) @Override public int getLayoutDirection(){return icon.getLayoutDirection();}
	@RequiresApi(api = Build.VERSION_CODES.M) @Override public boolean onLayoutDirectionChanged(int layoutDirection){return icon.onLayoutDirectionChanged(layoutDirection);}
	@Override public void setAlpha(int alpha){icon.setAlpha(alpha);}
	@Override public int getAlpha(){return icon.getAlpha();}
	@Override public void setColorFilter(@Nullable ColorFilter colorFilter){icon.setColorFilter(colorFilter);}
	@SuppressWarnings("deprecation") @Override @Deprecated public void setColorFilter(int color, @NonNull PorterDuff.Mode mode){icon.setColorFilter(color, mode);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void setTint(int tintColor){icon.setTint(tintColor);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void setTintList(@Nullable ColorStateList tint){icon.setTintList(tint);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void setTintMode(@Nullable PorterDuff.Mode tintMode){icon.setTintMode(tintMode);}
	@Override public void setTintBlendMode(@Nullable BlendMode blendMode){icon.setTintBlendMode(blendMode);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Nullable @Override public ColorFilter getColorFilter(){return icon.getColorFilter();}
	@Override public void clearColorFilter(){icon.clearColorFilter();}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void setHotspot(float x, float y){icon.setHotspot(x, y);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void setHotspotBounds(int left, int top, int right, int bottom){icon.setHotspotBounds(left, top, right, bottom);}
	@RequiresApi(api = Build.VERSION_CODES.M) @Override public void getHotspotBounds(@NonNull Rect outRect){icon.getHotspotBounds(outRect);}
	@Override public boolean isProjected(){return icon.isProjected();}
	@Override public boolean isStateful(){return icon.isStateful();}
	@Override public boolean setState(@NonNull int[] stateSet){return icon.setState(stateSet);}
	@NonNull @Override public int[] getState(){return icon.getState();}
	@Override public void jumpToCurrentState(){icon.jumpToCurrentState();}
	@NonNull @Override public Drawable getCurrent(){return icon.getCurrent();}
	@Override public boolean setVisible(boolean visible, boolean restart){return icon.setVisible(visible, restart);}
	@Override public void setAutoMirrored(boolean mirrored){icon.setAutoMirrored(mirrored);}
	@Override public boolean isAutoMirrored(){return icon.isAutoMirrored();}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void applyTheme(@NonNull Resources.Theme t){icon.applyTheme(t);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public boolean canApplyTheme(){return icon.canApplyTheme();}
	@SuppressWarnings("deprecation") @Override @Deprecated public int getOpacity(){return icon.getOpacity();}
	@Nullable @Override public Region getTransparentRegion(){return icon.getTransparentRegion();}
	@Override public int getIntrinsicWidth(){return icon.getIntrinsicWidth();}
	@Override public int getIntrinsicHeight(){return icon.getIntrinsicHeight();}
	@Override public int getMinimumWidth(){return icon.getMinimumWidth();}
	@Override public int getMinimumHeight(){return icon.getMinimumHeight();}
	@Override public boolean getPadding(@NonNull Rect padding){return icon.getPadding(padding);}
	@NonNull @Override public Insets getOpticalInsets(){return icon.getOpticalInsets();}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void getOutline(@NonNull Outline outline){icon.getOutline(outline);}
	@NonNull @Override public Drawable mutate(){return icon.mutate();}
	@Override public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs) throws IOException, XmlPullParserException{icon.inflate(r, parser, attrs);}
	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs, @Nullable Resources.Theme theme) throws IOException, XmlPullParserException{icon.inflate(r, parser, attrs, theme);}
	@Nullable @Override public ConstantState getConstantState(){return icon.getConstantState();}
}
