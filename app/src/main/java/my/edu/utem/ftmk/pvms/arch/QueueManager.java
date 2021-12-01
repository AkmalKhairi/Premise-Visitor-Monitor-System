package my.edu.utem.ftmk.pvms.arch;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import my.edu.utem.ftmk.pvms.model.Queue;

@Dao
interface QueueManager
{
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	public void addQueue(Queue queue);

	@Delete
	public void deleteQueue(Queue queue);

	@Query("SELECT * FROM Queue WHERE premiseID = :premiseID")
	public Queue getQueue(String premiseID);

	@Query("SELECT * FROM Queue")
	public LiveData<List<Queue>> getQueues();
}