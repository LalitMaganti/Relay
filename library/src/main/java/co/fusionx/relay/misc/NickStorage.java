package co.fusionx.relay.misc;

import android.os.Parcel;
import android.os.Parcelable;

public class NickStorage implements Parcelable {

    public static final Parcelable.Creator<NickStorage> CREATOR = new Parcelable
            .Creator<NickStorage>() {
        public NickStorage createFromParcel(Parcel in) {
            return new NickStorage(in.readString(), in.readString(), in.readString());
        }

        public NickStorage[] newArray(int size) {
            return new NickStorage[size];
        }
    };

    private final String mFirstChoiceNick;

    private final String mSecondChoiceNick;

    private final String mThirdChoiceNick;

    public NickStorage(final String firstChoice, final String secondChoice,
            final String thirdChoice) {
        mFirstChoiceNick = firstChoice;
        mSecondChoiceNick = secondChoice;
        mThirdChoiceNick = thirdChoice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mFirstChoiceNick);
        parcel.writeString(mSecondChoiceNick);
        parcel.writeString(mThirdChoiceNick);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof NickStorage) {
            final NickStorage nickStorage = (NickStorage) o;
            return mFirstChoiceNick.equals(nickStorage.getFirstChoiceNick()) &&
                    mSecondChoiceNick.equals(nickStorage.getSecondChoiceNick()) &&
                    mThirdChoiceNick.equals(nickStorage.getThirdChoiceNick());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mFirstChoiceNick.hashCode() * 41 + mSecondChoiceNick.hashCode() * 43 +
                mThirdChoiceNick.hashCode() * 31;
    }

    // Getters and setters
    public String getFirstChoiceNick() {
        return mFirstChoiceNick;
    }

    public String getSecondChoiceNick() {
        return mSecondChoiceNick;
    }

    public String getThirdChoiceNick() {
        return mThirdChoiceNick;
    }
}