package mo.ed.amit.dayeleven.mygoogleapis.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mo.ed.amit.dayeleven.mygoogleapis.R;

public class SavedPlacesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final Context mContext;
    private final ArrayList<String> feedItemList;
    private final boolean mTwoPane;
    private String savedPlace;

    public SavedPlacesRecyclerAdapter(Context mContext, ArrayList<String> feedItemList, boolean twoPane) {
        this.mContext = mContext;
        this.feedItemList = feedItemList;
        this.mTwoPane = twoPane;
    }
    
    @NonNull
    @Override
    public SavedPlacesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View articleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_custom_save_place_layout,
                parent, false);
        return new SavedPlacesRecyclerAdapter.ViewHolder(articleView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder placeViewHolder = (ViewHolder) holder;
        if (feedItemList!=null){
            if (feedItemList != null && feedItemList.size() > 0) {
                savedPlace = feedItemList.get(position);
                if (mContext != null) {
                    TextView tv=(TextView) placeViewHolder.itemView.findViewById(R.id.title);
                    tv.setText(savedPlace);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}
