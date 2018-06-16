package googleplacesapi;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.MarkerOptions;

public class MarkerOptionList implements Parcelable{

  public MarkerOptionList(){

  }

  private MarkerOptions markerOption;


  public MarkerOptionList(Parcel in) {
    this.markerOption = in.readParcelable(MarkerOptionList.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(markerOption, flags);
  }

  public static final Parcelable.Creator<MarkerOptionList> CREATOR = new Parcelable.Creator<MarkerOptionList>() {
    public MarkerOptionList createFromParcel(Parcel in) {
      return new MarkerOptionList(in);
    }

    public MarkerOptionList[] newArray(int size) {
      return new MarkerOptionList[size];
    }
  };
}
