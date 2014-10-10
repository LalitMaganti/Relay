package com.fusionx.relay.core;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import co.fusionx.relay.provider.NickProvider;

public class ParcelableNickProvider implements NickProvider, Parcelable {

    public static final Parcelable.Creator<ParcelableNickProvider> CREATOR = new Parcelable
            .Creator<ParcelableNickProvider>() {
        public ParcelableNickProvider createFromParcel(Parcel in) {
            final List<String> nicks = new ArrayList<>();
            in.readStringList(nicks);
            return new ParcelableNickProvider(nicks);
        }

        public ParcelableNickProvider[] newArray(int size) {
            return new ParcelableNickProvider[size];
        }
    };

    private final List<String> mNicks = new ArrayList<>();

    public ParcelableNickProvider(final List<String> choices) {
        addAll(FluentIterable.from(choices));
    }

    public ParcelableNickProvider(final String... choices) {
        addAll(FluentIterable.of(choices));
    }

    @Override
    public String getFirst() {
        return mNicks.get(0);
    }

    @Override
    public String getNickAtPosition(final int position) {
        return position < getNickCount() ? mNicks.get(position) : "";
    }

    @Override
    public int getNickCount() {
        return mNicks.size();
    }

    private void addAll(final FluentIterable<String> fluentIterable) {
        fluentIterable.filter(new Predicate<String>() {
            @Override
            public boolean apply(final String input) {
                return StringUtils.isNotEmpty(input);
            }
        }).copyInto(mNicks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringList(mNicks);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ParcelableNickProvider) {
            final ParcelableNickProvider parcelableNickProvider = (ParcelableNickProvider) o;
            return mNicks.equals(parcelableNickProvider.mNicks);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mNicks.hashCode();
    }
}