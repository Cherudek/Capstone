package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import pojosplaceid.Result;

public class FavouriteDetailSharedViewModel extends ViewModel {

    private final MutableLiveData<Result> selected = new MutableLiveData<>();

    public void select(Result item) {
      selected.setValue(item);
    }

    public LiveData<Result> getSelected() {
      return selected;
    }
  }


