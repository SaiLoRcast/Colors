package com.polygalov.colors.data;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Константин on 13.02.2018.
 */

public class ColorItem implements Parcelable {

    //id цвета
    protected final long mId;

    //Значение int, представляющее значение цвета.
    protected int mColor;

    //Необязательное имя, которое пользователь может присвоить цвету.
    protected String mName;

    //представление времени создания цвета. (В миллисекундах).
    protected final long mCreationTime;

    //Человеко-читаемое строковое представление шестнадцатеричного значения цвета.
    protected transient String mHexString;

    //Человеко читаемое строковое представление значения RGB для цвета.
    protected transient String mRgbString;

    //Отображаемое пользователем строковое представление значения HSV для цвета.
    protected transient String mHsvString;

    //Создайте новый {@link com.polygalov.colors.data.ColorItem} с идентификатором и цветом.
    //
    public ColorItem(long id, int color) {
        mId = id;
        mColor = color;
        mCreationTime = System.currentTimeMillis();
    }

    private ColorItem(Parcel in) {
        this.mId = in.readLong();
        this.mColor = in.readInt();
        this.mCreationTime = in.readLong();
        this.mName = in.readString();
    }

    public ColorItem(int color) {
        mId = mCreationTime = System.currentTimeMillis();
        mColor = color;
    }

    public long getId() {
        return mId;
    }

    public int getColor() {
        return mColor;
    }

    public long getCreationTime() {
        return mCreationTime;
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            mHexString = makeHexString(mColor);
            mRgbString = makeRgbString(mColor);
            mHsvString = makeHsvString(mColor);
        }
    }

    public String getHexString() {
        if (mHexString == null) {
            mHexString = makeHexString(mColor);
        }
        return mHexString;
    }

    public String getRgbString() {
        if (mRgbString == null) {
            mRgbString = makeRgbString(mColor);
        }
        return mRgbString;
    }

    public String getHsvString() {
        if (mHsvString == null) {
            mHsvString = makeHsvString(mColor);
        }
        return mHsvString;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public static String makeHsvString(int value) {
        float[] hsv = new float[3];
        Color.colorToHSV(value, hsv);
        return "hsv(" + (int) hsv[0] + "°, " + (int) (hsv[1] * 100) + "%, " + (int) (hsv[2] * 100) + "%)";

    }

    public static String makeRgbString(int value) {
        return "rgb(" + Color.red(value) + ", " + Color.green(value) + ", " + Color.blue(value) + ")";
    }

    public static String makeHexString(int value) {
        return "#" + Integer.toHexString(value).substring(1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.mId);
        parcel.writeInt(this.mColor);
        parcel.writeLong(this.mCreationTime);
        parcel.writeString(this.mName);
    }

    public static final Creator<ColorItem> CREATOR = new Creator<ColorItem>() {

        @Override
        public ColorItem createFromParcel(Parcel parcel) {
            return new ColorItem(parcel);
        }

        @Override
        public ColorItem[] newArray(int i) {
            return new ColorItem[i];
        }
    };
}
