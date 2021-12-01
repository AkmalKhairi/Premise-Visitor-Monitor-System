package my.edu.utem.ftmk.pvms.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Queue
{
	@NonNull
	@PrimaryKey
	private final String premiseID;
	private int number;

	public Queue(@NonNull String premiseID)
	{
		this.premiseID = premiseID;
	}

	@NonNull
	public String getPremiseID()
	{
		return premiseID;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}
}