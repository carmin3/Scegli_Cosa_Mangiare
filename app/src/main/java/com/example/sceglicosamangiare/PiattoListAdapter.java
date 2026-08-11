package com.example.sceglicosamangiare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class PiattoListAdapter extends ArrayAdapter<Piatto>
{

    public PiattoListAdapter(Context context, int resource, List<Piatto> listaPiatti)
    {
        super(context,resource,listaPiatti);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Piatto piatto = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view_layout, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.nomePiattoTV);
        TextView tv2 = (TextView) convertView.findViewById(R.id.tipoPiattoTV);
        TextView tv3 = (TextView) convertView.findViewById(R.id.ingredientiPiattoTV);

        // null-safe setting
        if (piatto != null) {
            if (piatto.getNomePiatto() != null) tv.setText(piatto.getNomePiatto());
            if (piatto.getPortata() != null) tv2.setText(piatto.getPortata());
            if (piatto.getNutrienti() != null) tv3.setText(piatto.getNutrienti());
        }

        return convertView;
    }
}
