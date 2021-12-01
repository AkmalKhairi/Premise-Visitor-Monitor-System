package my.edu.utem.ftmk.pvms;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import my.edu.utem.ftmk.pvms.arch.MonitorViewModel;
import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.model.Queue;

public class PremiseActivity extends AppCompatActivity {
	private Premise premise;
	private MonitorViewModel monitorViewModel;
	private EditText txtNumber;
	private Button btnQueue;
	private TextView lblRating;
	private int premiseCapacity, rateGiven;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_premise);

		premise = (Premise) getIntent().getSerializableExtra("premise");
		monitorViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MonitorViewModel.class);
		txtNumber = findViewById(R.id.txtNumber);
		btnQueue = findViewById(R.id.btnQueue);
		lblRating = findViewById(R.id.lblRating);

		Queue queue = monitorViewModel.getQueue(premise.getPremiseID());
		TextView lblName = findViewById(R.id.lblName);
		TextView lblCategory = findViewById(R.id.lblCategory);
		TextView lblDescription = findViewById(R.id.lblDescription);
		TextView lblAddress = findViewById(R.id.lblAddress);
		TextView lblDistrict = findViewById(R.id.lblDistrict);
		TextView lblState = findViewById(R.id.lblState);

		lblName.setText(premise.getName());
		lblCategory.setText(premise.getCategory());
		lblDescription.setText(premise.getDescription());
		lblAddress.setText(premise.getAddress());
		lblDistrict.setText(premise.getDistrict());
		lblState.setText(premise.getState());
		lblRating.setText(String.valueOf(premise.getTotalRate() / premise.getTotalVote()));
		premiseCapacity = premise.getCapacity();

		if (queue == null){
			btnQueue.setText(R.string.btnSubmit_queue_label);
			btnQueue.setTextColor(Color.parseColor("#575DFC"));
		}
		else {
			txtNumber.setText(String.valueOf(queue.getNumber()));
			txtNumber.setEnabled(false);
			btnQueue.setText(R.string.btnSubmit_cancel_label);
			btnQueue.setTextColor(Color.parseColor("#A93226"));
		}
	}


	public void queue(View view) {
		String premiseID = premise.getPremiseID();
		Queue queue = monitorViewModel.getQueue(premiseID);

		if (queue == null) {
			queue = new Queue(premise.getPremiseID());
			String input_s = txtNumber.getText().toString();
			String message;

			if (input_s.equals("")) {
				message = "Input cannot be null";
				QueueWarning(message);
			} else {
				int input = Integer.parseInt(txtNumber.getText().toString());
				if (input < 1) {
					message = "Number of visitor(s) to queue is invalid.";
					QueueWarning(message);
				} else if (input > premiseCapacity) {
					message = "Sorry, visitor capacity for " + premise.getName() + " is " + premiseCapacity;
					QueueWarning(message);
				} else {
					queue.setNumber(Integer.parseInt(txtNumber.getText().toString()));
					monitorViewModel.addQueue(queue);
					txtNumber.setEnabled(false);
					btnQueue.setText(R.string.btnSubmit_cancel_label);
					finish();
				}
			}
		} else {
			StopQueuing(queue);
		}
	}


	public void navigate(View view) {
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + premise.getLatitude() + "," + premise.getLongitude()));

		mapIntent.setPackage("com.google.android.apps.maps");
		startActivity(mapIntent);
	}

	private void rates() {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL("https://premise-monitor.ml/api/v1/" + premise.getToken() + "/telemetry").openConnection();
			JSONObject request = new JSONObject();
			//int rating = Integer.parseInt(txtRating.getText().toString());

			request.put("rating", rateGiven);

			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.getOutputStream().write(request.toString().getBytes());

			if (connection.getResponseCode() / 100 == 2) {
				premise.setTotalRate(premise.getTotalRate() + rateGiven);
				premise.setTotalVote(premise.getTotalVote() + 1);
				lblRating.setText(String.valueOf(premise.getTotalRate() / premise.getTotalVote()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void QueueWarning(String message) {
		new AlertDialog.Builder(PremiseActivity.this)
				.setTitle(R.string.dlgQueueWarning_label)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, (dialog1, which1) -> {
				})
				.setCancelable(false).show();
	}

	public void StopQueuing(Queue queue) {
		AlertDialog.Builder builder = new AlertDialog.Builder(PremiseActivity.this);
		builder.setTitle(R.string.dlgStopQueue_label)
				.setMessage(R.string.dlgStopQueue_message)
				.setCancelable(false);

		DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					monitorViewModel.deleteQueue(queue);
					txtNumber.setEnabled(true);
					txtNumber.setText("");
					btnQueue.setText(R.string.btnSubmit_queue_label);
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					break;
				case DialogInterface.BUTTON_NEUTRAL:
					RatingDialog.showRatingDialog(this, rating -> {
						rateGiven = rating;
						String toast = " - Thank you for sharing your feedback";
						Toast.makeText(this, R.string.dlgRating_toast, Toast.LENGTH_SHORT).show();
						new Thread(this::rates).start();

						monitorViewModel.deleteQueue(queue);
						txtNumber.setEnabled(true);
						txtNumber.setText("");
						btnQueue.setText(R.string.btnSubmit_queue_label);
						finish();
					});
					break;
			}
		};

		builder.setPositiveButton("Confirm", dialogClickListener);
		builder.setNegativeButton("Cancel", dialogClickListener);
		builder.setNeutralButton("Rate", dialogClickListener);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

}