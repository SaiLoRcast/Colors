package com.polygalov.colors.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Константин on 18.02.2018.
 */

public class Palette implements Parcelable {

    private final long mId;

    private String mName;

    private List<ColorItem> mColorItems;

    public Palette(String name, List<ColorItem> colorItems) {
        mId = System.currentTimeMillis();
        mName = name;
        mColorItems = new ArrayList<>(colorItems);
    }

    public Palette(Parcel parcel) {
        mColorItems = new ArrayList<>();

        mId = parcel.readLong();
        mName = parcel.readString();
        parcel.readTypedList(mColorItems, ColorItem.CREATOR);
    }

    public void addColor(ColorItem colorItem) {
        if (colorItem == null) {
            throw new IllegalArgumentException("Color item can't be null");
        }
        mColorItems.add(colorItem);
    }

    public long getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public List<ColorItem> getColors() {
        return new ArrayList<>(mColorItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeString(this.mName);
        dest.writeTypedList(this.mColorItems);
    }

    public static final Creator<Palette> CREATOR = new Creator<Palette>() {

        @Override
        public Palette createFromParcel(Parcel parcel) {
            return new Palette(parcel);
        }

        @Override
        public Palette[] newArray(int i) {
            return new Palette[i];
        }
    };
}
