package adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.gregorio.capstone.MuseumsFragment.OnListFragmentInteractionListener;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.dummy.DummyContent.DummyItem;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MuseumsAdapter extends
    RecyclerView.Adapter<MuseumsAdapter.ViewHolder> {

  private final List<DummyItem> mValues;
  private final OnListFragmentInteractionListener mListener;

  public MuseumsAdapter(List<DummyItem> items,
      OnListFragmentInteractionListener listener) {
    mValues = items;
    mListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.museums_item, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mIdView.setText(mValues.get(position).id);
    holder.mContentView.setText(mValues.get(position).content);

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          // Notify the active callbacks interface (the activity, if the
          // fragment is attached to one) that an item has been selected.
          mListener.onListFragmentInteraction(holder.mItem);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public final ImageView mView;
    public final TextView mIdView;
    public final TextView mContentView;
    public DummyItem mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view.findViewById(R.id.museums_photo_place_id);
      mIdView = view.findViewById(R.id.museums_place_name);
      mContentView = view.findViewById(R.id.museums_place_address);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mContentView.getText() + "'";
    }
  }
}
