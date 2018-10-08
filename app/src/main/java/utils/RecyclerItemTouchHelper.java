package utils;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.gregorio.capstone.adapters.FavouritesAdapter.FavouriteViewHolder;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

  private RecyclerItemTouchHelperListener listener;

  public RecyclerItemTouchHelper(int dragDirs, int swipeDirs,
      RecyclerItemTouchHelperListener listener) {
    super(dragDirs, swipeDirs);
    this.listener = listener;
  }

  @Override
  public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
    return true;
  }

  @Override
  public void onSelectedChanged(ViewHolder viewHolder, int actionState) {
    super.onSelectedChanged(viewHolder, actionState);
    if (viewHolder != null) {
      final View foregroundView = ((FavouriteViewHolder) viewHolder).viewForeground;
      getDefaultUIUtil().clearView(foregroundView);
    }
  }

  @Override
  public void onChildDrawOver(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
      float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    final View foregroundView = ((FavouriteViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil()
        .onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
  }

  @Override
  public void clearView(RecyclerView recyclerView, ViewHolder viewHolder) {
    super.clearView(recyclerView, viewHolder);
    final View foregroundView = ((FavouriteViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil().clearView(foregroundView);
  }

  @Override
  public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
      float dY, int actionState, boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    final View foregroundView = ((FavouriteViewHolder) viewHolder).viewForeground;
    getDefaultUIUtil()
        .onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
  }

  @Override
  public void onSwiped(ViewHolder viewHolder, int direction) {
    listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());

  }

  @Override
  public int convertToAbsoluteDirection(int flags, int layoutDirection) {
    return super.convertToAbsoluteDirection(flags, layoutDirection);
  }

  public interface RecyclerItemTouchHelperListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
  }
}
