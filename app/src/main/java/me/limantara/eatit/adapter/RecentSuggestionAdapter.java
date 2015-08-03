package me.limantara.eatit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import me.limantara.eatit.R;
import me.limantara.eatit.app.AppController;
import me.limantara.eatit.model.Venue;

/**
 * Created by edwinlimantara on 8/2/15.
 */
public class RecentSuggestionAdapter
    extends RecyclerView.Adapter<RecentSuggestionAdapter.ViewHolder> {

    private List<Venue.Item> foodList;
    private static View rootView;

    public RecentSuggestionAdapter(List<Venue.Item> foodList) {
        this.foodList = foodList;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rootView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.food_list_row, parent, false);

        return new ViewHolder(rootView);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getPosition()} which will have the
     * updated position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Venue.Item food = foodList.get(position);
        holder.foodName.setText(food.name);
        holder.foodDescription.setText(food.description.trim());
        holder.fillImage(food.images.get(0));
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public TextView foodDescription;
        public NetworkImageView foodImage;
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        public ViewHolder(View itemView) {
            super(itemView);
            foodName = (TextView) itemView.findViewById(R.id.foodName);
            foodDescription = (TextView) itemView.findViewById(R.id.foodDescription);
            foodImage = (NetworkImageView) itemView.findViewById(R.id.foodImage);

            if(imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
        }

        public void fillImage(String url) {
            foodImage.setImageUrl(url, imageLoader);
        }
    }
}
