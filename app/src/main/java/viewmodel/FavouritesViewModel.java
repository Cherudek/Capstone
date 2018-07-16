package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import java.util.List;
import pojosplaceid.Result;

public class FavouritesViewModel extends ViewModel {

  public LiveData<List<Result>> mResult;

  public FavouritesViewModel(LiveData<List<Result>> mResult) {
    this.mResult = mResult;
  }

  public LiveData<List<Result>> getResult() {
    return mResult;
  }

  public void setResult(LiveData<List<Result>> mResult) {
    this.mResult = mResult;
  }
}
