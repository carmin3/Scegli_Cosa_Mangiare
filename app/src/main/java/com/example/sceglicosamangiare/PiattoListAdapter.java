package com.example.sceglicosamangiare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class PiattoListAdapter extends ArrayAdapter<Piatto> {

    private OnFavoriteSwipeListener swipeListener;

    public interface OnFavoriteSwipeListener {
        void onSwipeToFavorite(Piatto piatto);
    }

    public PiattoListAdapter(Context context, int resource, List<Piatto> listaPiatti) {
        super(context, resource, listaPiatti);
    }

    public PiattoListAdapter(Context context, List<Piatto> listaPiatti, OnFavoriteSwipeListener swipeListener) {
        super(context, 0, listaPiatti);
        this.swipeListener = swipeListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Piatto piatto = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_view_layout, parent, false);
        }

        View swipeBackground = convertView.findViewById(R.id.swipeBackground);
        View swipeForeground = convertView.findViewById(R.id.swipeForeground);
        ImageView star = convertView.findViewById(R.id.favoriteStar);
        TextView tv = convertView.findViewById(R.id.nomePiattoTV);
        TextView tv2 = convertView.findViewById(R.id.tipoPiattoTV);
        TextView tv3 = convertView.findViewById(R.id.ingredientiPiattoTV);

        // Reset translation and background state
        if (swipeForeground != null) {
            swipeForeground.setTranslationX(0);
        }
        if (swipeBackground != null) {
            swipeBackground.setVisibility(View.INVISIBLE);
        }

        // null-safe setting
        if (piatto != null) {
            if (piatto.getNomePiatto() != null) tv.setText(piatto.getNomePiatto());

            StringBuilder sb = new StringBuilder();
            if (piatto.getPortata() != null && !piatto.getPortata().isEmpty()) {
                sb.append(piatto.getPortata());
            }
            if (piatto.getNutrienti() != null && !piatto.getNutrienti().isEmpty() && !piatto.getNutrienti().equalsIgnoreCase("Base del Piatto")) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(piatto.getNutrienti());
            }
            if (piatto.getDominanzaNutrizionale() != null && !piatto.getDominanzaNutrizionale().isEmpty() && !piatto.getDominanzaNutrizionale().equalsIgnoreCase("Nutrienti")) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(piatto.getDominanzaNutrizionale());
            }
            if (piatto.getProfiloGustativo() != null && !piatto.getProfiloGustativo().isEmpty() && !piatto.getProfiloGustativo().equalsIgnoreCase("Gusto")) {
                if (sb.length() > 0) sb.append(" - ");
                sb.append(piatto.getProfiloGustativo());
            }
            if (tv3 != null) {
                tv3.setText(sb.toString());
            }
            if (tv2 != null) {
                tv2.setVisibility(View.GONE);
            }

            if (star != null) {
                if (Boolean.TRUE.equals(piatto.getFavorito())) {
                    star.setVisibility(View.VISIBLE);
                    star.setImageResource(R.drawable.ic_star_filled);
                } else {
                    star.setVisibility(View.GONE);
                }
            }

            if (swipeForeground != null && swipeBackground != null) {
                swipeForeground.setOnTouchListener(new View.OnTouchListener() {
                    private float downX;
                    private boolean swiping = false;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                downX = event.getRawX();
                                swiping = false;
                                swipeBackground.setVisibility(View.VISIBLE);
                                return false;

                            case MotionEvent.ACTION_MOVE:
                                float currentX = event.getRawX();
                                float deltaX = currentX - downX;
                                if (deltaX < 0) {
                                    swiping = true;
                                    float fgWidth = swipeForeground.getWidth();
                                    if (fgWidth == 0) fgWidth = 800;
                                    float translationX = Math.max(deltaX, -fgWidth);
                                    swipeForeground.setTranslationX(translationX);
                                    return true;
                                } else {
                                    swipeForeground.setTranslationX(0);
                                }
                                break;

                            case MotionEvent.ACTION_UP:
                            case MotionEvent.ACTION_CANCEL:
                                if (swiping) {
                                    float currentXUp = event.getRawX();
                                    float deltaXUp = currentXUp - downX;
                                    float fgWidth = swipeForeground.getWidth();
                                    if (fgWidth == 0) fgWidth = 500;

                                    if (deltaXUp < -fgWidth * 0.3f) {
                                        swipeForeground.animate()
                                                .translationX(-fgWidth)
                                                .setDuration(200)
                                                .withEndAction(() -> {
                                                    if (swipeListener != null) {
                                                        swipeListener.onSwipeToFavorite(piatto);
                                                    }
                                                    swipeForeground.setTranslationX(0);
                                                    swipeBackground.setVisibility(View.INVISIBLE);
                                                })
                                                .start();
                                    } else {
                                        swipeForeground.animate()
                                                .translationX(0)
                                                .setDuration(200)
                                                .withEndAction(() -> swipeBackground.setVisibility(View.INVISIBLE))
                                                .start();
                                    }
                                    swiping = false;
                                    return true;
                                }
                                swipeBackground.setVisibility(View.INVISIBLE);
                                break;
                        }
                        return false;
                    }
                });
            }
        }

        return convertView;
    }
}
