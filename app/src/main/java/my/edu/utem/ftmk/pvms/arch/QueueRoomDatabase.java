package my.edu.utem.ftmk.pvms.arch;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import my.edu.utem.ftmk.pvms.model.Queue;

@Database(entities = {Queue.class}, version = 1, exportSchema = false)
abstract class QueueRoomDatabase extends RoomDatabase
{
	abstract QueueManager getQueueManager();
}