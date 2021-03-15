package app.kevs.treeview;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

public class ViewHolder {
    public LinearLayout container;
    public TextView textView;

    public ViewHolder(View view) {
        this.container = view.findViewById(R.id.container);
        this.textView = view.findViewById(R.id.idTvnode);
    }
}
