package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import pojos.Results;

public class FavouriteDetailSharedViewModel extends ViewModel {

    private final MutableLiveData<Results> selected = new MutableLiveData<Results>();

    public void select(Results item) {
      selected.setValue(item);
    }

    public LiveData<Results> getSelected() {
      return selected;
    }
  }


