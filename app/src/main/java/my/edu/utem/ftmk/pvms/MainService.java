package my.edu.utem.ftmk.pvms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

import my.edu.utem.ftmk.pvms.arch.MonitorViewModel;
import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.model.Queue;

public class MainService extends FirebaseMessagingService
{
	private static String ID;
	private MonitorViewModel monitorViewModel;
	private NotificationManager manager;

	@Override
	public void onCreate()
	{
		super.onCreate();

		ID = getString(R.string.app_name);
		monitorViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MonitorViewModel.class);
		manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationChannel channel = new NotificationChannel(ID, ID, NotificationManager.IMPORTANCE_DEFAULT);

		channel.enableLights(true);
		channel.enableVibration(true);
		channel.setLightColor(Color.GREEN);
		channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

		manager.createNotificationChannel(channel);
	}

	@Override
	public void onNewToken(@NonNull String s)
	{
		super.onNewToken(s);
	}

	@Override
	public void onMessageReceived(@NonNull RemoteMessage message)
	{
		Map<String, String> data = message.getData();

		if (data.containsKey("visitor") && data.containsKey("capacity"))
		{
			String premiseID = data.get("premiseID");
			Premise premise = monitorViewModel.getPremise(premiseID);
			Queue queue = monitorViewModel.getQueue(premiseID);
			int visitor = Integer.parseInt(Objects.requireNonNull(data.get("visitor"))), capacity = Integer.parseInt(Objects.requireNonNull(data.get("capacity")));

			System.out.printf("%s\t%d\t%d", premiseID, visitor, capacity);
			System.out.println(premise);

			premise.setVisitor(visitor);
			premise.setCapacity(capacity);
			monitorViewModel.updatePremises();

			if (queue != null)
			{
				String name = premise.getName(), text = name + (queue.getNumber() <= capacity - visitor ? " has free space now. You may visit the premise now." : " currently doesn't have enough available space for you. You'll be notified once there are available spaces for you.");
				manager.notify(premiseID.hashCode(), new Notification.Builder(this, ID).setContentTitle("Update from " + name).setContentText(text).setStyle(new Notification.BigTextStyle().bigText(text)).setSmallIcon(R.drawable.ic_launcher_foreground).setAutoCancel(true).build());
			}
		}
	}
}