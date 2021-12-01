package my.edu.utem.ftmk.pvms.arch;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.model.Queue;

class MonitorRepository
{
	private static MonitorRepository INSTANCE;
	private final PremiseWebService premiseWebService;
	private final QueueManager queueManager;
	private final LiveData<List<Premise>> premises;
	private final LiveData<List<Queue>> queues;

	private MonitorRepository(Application application)
	{
		premiseWebService = new PremiseWebService();
		QueueRoomDatabase quueuRoomDatabase = Room.databaseBuilder(application.getApplicationContext(), QueueRoomDatabase.class, "dbQueues").build();
		queueManager = quueuRoomDatabase.getQueueManager();
		premises = premiseWebService.getPremises();
		queues = queueManager.getQueues();
	}

	static MonitorRepository getInstance(Application application)
	{
		synchronized (MonitorRepository.class)
		{
			if (INSTANCE == null)
				INSTANCE = new MonitorRepository(application);
		}

		return INSTANCE;
	}

	public Premise getPremise(String premiseID)
	{
		Premise premise = null;
		List<Premise> list = premises.getValue();

		if (list != null)
		{
			for (Premise p : list)
			{
				if (p.getPremiseID().equals(premiseID))
				{
					premise = p;
					break;
				}
			}
		}

		return premise;
	}

	public void updatePremises()
	{
		premiseWebService.updatePremises();
	}

	public LiveData<List<Premise>> getPremises()
	{
		return premises;
	}

	public void getPremises(int type, double latitude, double longitude)
	{
		new Thread(() -> premiseWebService.getPremises(type, latitude, longitude)).start();
	}

	public void addQueue(Queue queue)
	{
		new Thread(() -> queueManager.addQueue(queue)).start();
	}

	public void deleteQueue(Queue queue)
	{
		new Thread(() -> queueManager.deleteQueue(queue)).start();
	}

	public Queue getQueue(String premiseID)
	{
		Queue queue = null;
		List<Queue> list = queues.getValue();

		if (list != null)
		{
			for (Queue q : list)
			{
				if (q.getPremiseID().equals(premiseID))
				{
					queue = q;
					break;
				}
			}
		}

		return queue;
	}

	public LiveData<List<Queue>> getQueues()
	{
		return queues;
	}
}