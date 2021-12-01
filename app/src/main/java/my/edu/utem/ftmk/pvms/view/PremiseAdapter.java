package my.edu.utem.ftmk.pvms.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import my.edu.utem.ftmk.pvms.MainActivity;
import my.edu.utem.ftmk.pvms.R;
import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.model.Queue;

public class PremiseAdapter extends RecyclerView.Adapter<PremiseHolder>
{
	//private final LayoutInflater layoutInflater;
	private List<Premise> premises;
	private TreeSet<String> queues;
	private final Context context;


	public PremiseAdapter(Context context,MainActivity activity)
	{
		//layoutInflater = LayoutInflater.from(activity);
		this.context = context;
	}

	@NonNull
	@Override
	public PremiseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		//return new PremiseHolder(layoutInflater.inflate(R.layout.activity_premise_cardview, parent, false),context,premises);
		return new PremiseHolder(LayoutInflater.from(context).inflate(R.layout.activity_pemise_cardview, parent, false), context);


	}

	@Override
	public void onBindViewHolder(@NonNull PremiseHolder holder, int position)
	{
		if (premises != null)
		{
			Premise premise = premises.get(position);
			holder.setPremise(premise, queues != null && queues.contains(premise.getPremiseID()));
		}
	}

	@Override
	public int getItemCount()
	{

		return premises == null ? 0 : premises.size();
	}

	public void setPremises(List<Premise> premises)
	{
		this.premises = premises;
		notifyDataSetChanged();
	}

	public void setQueues(List<Queue> queues)
	{
		this.queues = queues.stream().map(Queue::getPremiseID).collect(Collectors.toCollection(TreeSet::new));
		notifyDataSetChanged();
	}
}