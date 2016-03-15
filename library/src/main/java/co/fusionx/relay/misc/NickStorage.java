package co.fusionx.relay.misc;

import com.google.common.collect.FluentIterable;

import org.apache.commons.lang3.StringUtils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class NickStorage implements Parcelable {

    public static final Parcelable.Creator<NickStorage> CREATOR = new Parcelable
            .Creator<NickStorage>() {
        public NickStorage createFromParcel(Parcel in) {
            final List<String> nicks = new ArrayList<>();
            in.readStringList(nicks);
            return new NickStorage(nicks);
        }

        public NickStorage[] newArray(int size) {
            return new NickStorage[size];
        }
    };

    private final List<String> mNicks = new ArrayList<>();

    public NickStorage(final List<String> choices) {
        addAll(FluentIterable.from(choices));
    }

    public NickStorage(final String... choices) {
        addAll(FluentIterable.of(choices));
    }

    private void addAll(final FluentIterable<String> fluentIterable) {
        fluentIterable.filter(StringUtils::isNotEmpty).copyInto(mNicks);
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
        if (o instanceof NickStorage) {
            final NickStorage nickStorage = (NickStorage) o;
            return mNicks.equals(nickStorage.mNicks);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mNicks.hashCode();
    }

    public String getFirst() {
        return mNicks.get(0);
    }

    public String getNickAtPosition(final int position) {
        return position < getNickCount() ? mNicks.get(position) : "";
    }

    public int getNickCount() {
        return mNicks.size();
    }
}