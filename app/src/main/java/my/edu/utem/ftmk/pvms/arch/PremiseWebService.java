package my.edu.utem.ftmk.pvms.arch;

import androidx.lifecycle.MutableLiveData;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import my.edu.utem.ftmk.pvms.model.Premise;

class PremiseWebService
{
	private final MutableLiveData<List<Premise>> premises = new MutableLiveData<>();
	private boolean running;

	public void updatePremises()
	{
		List<Premise> list = premises.getValue();

		if (list != null)
			premises.postValue(new Vector<>(list));
	}

	public MutableLiveData<List<Premise>> getPremises()
	{
		return premises;
	}

	public void getPremises(int type, double latitude, double longitude)
	{
		if (!running)
		{
			synchronized (PremiseWebService.class)
			{
				try
				{
					running = true;
					HttpsURLConnection connection = (HttpsURLConnection) new URL("https://premise-monitor.ml/premises").openConnection();

					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.setRequestMethod("POST");

					ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());

					oos.writeInt(type);
					oos.writeDouble(latitude);
					oos.writeDouble(longitude);
					oos.flush();

					premises.postValue(Arrays.asList((Premise[]) new ObjectInputStream(connection.getInputStream()).readObject()));
					connection.disconnect();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					running = false;
				}
			}
		}
	}
}