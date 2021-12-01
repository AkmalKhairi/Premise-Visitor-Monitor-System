package my.edu.utem.ftmk.pvms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;

import my.edu.utem.ftmk.pvms.arch.MonitorViewModel;
import my.edu.utem.ftmk.pvms.model.Premise;
import my.edu.utem.ftmk.pvms.view.PremiseAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher
{
	private MonitorViewModel monitorViewModel;
	private FusedLocationProviderClient fusedLocationClient;
	private PremiseAdapter premiseAdapter;
	private Spinner spnType;
	private Spinner spnCategory;
	private EditText txtName;
	private SwipeRefreshLayout srlPremises;
	private String allCategories;
	private int selectedType, selectedCategory;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FirebaseMessaging.getInstance().subscribeToTopic("premise_notifier");

		monitorViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MonitorViewModel.class);
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
		premiseAdapter = new PremiseAdapter(this, this);
		LiveData<List<Premise>> premises = monitorViewModel.getPremises();

		premises.observe(this, this::setPremises);
		monitorViewModel.getQueues().observe(this, premiseAdapter::setQueues);

		allCategories = getResources().getString(R.string.spnCategory_all);
		spnType = findViewById(R.id.spnType);
		spnCategory = findViewById(R.id.spnCategory);
		txtName = findViewById(R.id.txtName);
		srlPremises = findViewById(R.id.srlPremises);
		RecyclerView rcvPremises = findViewById(R.id.rcvPremises);
		ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
		ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		typeAdapter.add(getString(R.string.spnType_10km));
		typeAdapter.add(getString(R.string.spnType_district));
		typeAdapter.add(getString(R.string.spnType_state));
		typeAdapter.add(getString(R.string.spnType_all));
		categoryAdapter.add(allCategories);

		spnType.setAdapter(typeAdapter);
		spnType.setOnItemSelectedListener(this);
		spnCategory.setAdapter(categoryAdapter);
		spnCategory.setOnItemSelectedListener(this);
		txtName.addTextChangedListener(this);
		srlPremises.setOnRefreshListener(this::load);
		rcvPremises.setAdapter(premiseAdapter);
		rcvPremises.setLayoutManager(new LinearLayoutManager(this));

		if (savedInstanceState != null)
		{
			selectedType = savedInstanceState.getInt("selectedType");
			List<Premise> list = premises.getValue();

			if (list == null || list.isEmpty())
				load();
		}
		else
			load();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt("selectedType", selectedType);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		load();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		if (parent == spnType && position != selectedType)
			load();
		else if (parent == spnCategory && position != selectedCategory)
		{
			selectedCategory = position;
			filter();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		filter();
	}

	@Override
	public void afterTextChanged(Editable s)
	{
	}

	public void setPremises(List<Premise> premises)
	{
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add(allCategories);
		adapter.addAll(premises.stream().map(Premise::getCategory).collect(Collectors.toCollection(TreeSet::new)));

		spnCategory.setAdapter(adapter);
		spnType.setEnabled(true);
		spnCategory.setEnabled(true);
		txtName.setEnabled(true);
		srlPremises.setRefreshing(false);
		premiseAdapter.setPremises(premises);
	}

	private void load()
	{
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
		{
			spnType.setEnabled(false);
			spnCategory.setEnabled(false);
			txtName.setEnabled(false);
			srlPremises.setRefreshing(true);
			fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, null).addOnSuccessListener(this::load);
		}
		else
			new AlertDialog.Builder(this).setMessage(R.string.dlgPermission_label).setPositiveButton(android.R.string.ok, (dialog, which) -> ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1)).setCancelable(false).show();
	}

	private void load(Location location)
	{
		if (location == null)
			new AlertDialog.Builder(this).setMessage(R.string.dlgInactive_label).setPositiveButton(android.R.string.ok, (dialog, which) -> load()).setCancelable(false).show();
		else
			monitorViewModel.getPremises(selectedType = spnType.getSelectedItemPosition(), location.getLatitude(), location.getLongitude());
	}

	private void filter()
	{
		String category = spnCategory.getSelectedItem().toString(), name = txtName.getText().toString().trim().toLowerCase();
		List<Premise> list = monitorViewModel.getPremises().getValue();

		if (list != null)
			premiseAdapter.setPremises(list.stream().filter(premise -> category.equals(allCategories) || premise.getCategory().equals(category)).filter(premise -> name.isEmpty() || premise.getName().toLowerCase().contains(name)).sorted().collect(Collectors.toCollection(Vector::new)));
	}
}