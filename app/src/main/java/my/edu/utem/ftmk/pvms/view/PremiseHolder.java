package my.edu.utem.ftmk.pvms.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import my.edu.utem.ftmk.pvms.PremiseActivity;
import my.edu.utem.ftmk.pvms.R;
import my.edu.utem.ftmk.pvms.model.Premise;

class PremiseHolder extends RecyclerView.ViewHolder
{
	public final View itemView;
	private final TextView txtName;
	private final TextView txtCounter;
	private final RatingBar ratingBar;
	private Premise premise;
	private Context context;

	public PremiseHolder(@NonNull View itemView,Context context)
	{
		super(itemView);
		this.context = context;

		this.itemView = itemView;
		this.txtName = itemView.findViewById(R.id.premiseName_lbl);
		this.txtCounter = itemView.findViewById(R.id.visiting_count);
		this.ratingBar = itemView.findViewById(R.id.ratingBar2);

		itemView.setOnClickListener(this::navigate);
	}

	public void setPremise(Premise premise, boolean queue)
	{
		Drawable isNotTracking  = ContextCompat.getDrawable(context, R.drawable.default_rectangle);
		Drawable isTracking = ContextCompat.getDrawable(context, R.drawable.selected_rectangle);
		this.premise = premise;

		itemView.setActivated(queue);
		if(queue){
			itemView.setBackground(isTracking);
		}
		else {
			itemView.setBackground(isNotTracking);
		}


		txtName.setText(premise.getName());
		txtCounter.setText(String.format(Locale.getDefault(), "%d / %d", premise.getVisitor(), premise.getCapacity()));
		ratingBar.setRating(Float.parseFloat(String.valueOf(premise.getTotalRate())));
	}

	private void navigate(View view)
	{
		Intent intent = new Intent(view.getContext(), PremiseActivity.class);

		intent.putExtra("premise", premise);
		view.getContext().startActivity(intent);
	}

}