package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import pojosplaceid.Result;

public class FavouriteViewModel extends ViewModel {

  public LiveData<Result> mResult;

  public FavouriteViewModel(LiveData<Result> mResult) {
    this.mResult = mResult;
  }

  public LiveData<Result> getResult() {
    return mResult;
  }

  public void setResult(LiveData<Result> mResult) {
    this.mResult = mResult;
  }
}
