package my.edu.utem.ftmk.pvms.arch;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.model.Queue;

public class MonitorViewModel extends AndroidViewModel
{
	private final MonitorRepository monitorRepository;

	public MonitorViewModel(@NonNull Application application)
	{
		super(application);

		monitorRepository = MonitorRepository.getInstance(application);
	}

	public void updatePremises()
	{
		monitorRepository.updatePremises();
	}

	public Premise getPremise(String premiseID)
	{
		return monitorRepository.getPremise(premiseID);
	}

	public LiveData<List<Premise>> getPremises()
	{
		return monitorRepository.getPremises();
	}

	public void getPremises(int type, double latitude, double longitude)
	{
		monitorRepository.getPremises(type, latitude, longitude);
	}

	public void addQueue(Queue queue)
	{
		monitorRepository.addQueue(queue);
	}

	public void deleteQueue(Queue queue)
	{
		monitorRepository.deleteQueue(queue);
	}

	public Queue getQueue(String premiseID)
	{
		return monitorRepository.getQueue(premiseID);
	}

	public LiveData<List<Queue>> getQueues()
	{
		return monitorRepository.getQueues();
	}
}