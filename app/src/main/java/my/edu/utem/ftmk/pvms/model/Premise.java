package my.edu.utem.ftmk.pvms.model;

import java.io.Serializable;

public class Premise implements Serializable, Comparable<Premise>
{
	private static final long serialVersionUID = 1L;
	private String premiseID, name, category, description, address, district, state, token;
	private double latitude, longitude, totalRate;
	private int totalVote, openTime, closeTime, capacity, visitor, TrackVisitorCount;

	public String getPremiseID()
	{
		return premiseID;
	}

	public String getName()
	{
		return name;
	}

	public String getCategory()
	{
		return category;
	}

	public String getDescription()
	{
		return description;
	}

	public String getAddress()
	{
		return address;
	}

	public String getDistrict()
	{
		return district;
	}

	public String getState()
	{
		return state;
	}

	public String getToken()
	{
		return token;
	}

	public double getLatitude()
	{
		return latitude;
	}

	public double getLongitude()
	{
		return longitude;
	}

	public double getTotalRate()
	{
		return totalRate;
	}

	public void setTotalRate(double totalRate)
	{
		this.totalRate = totalRate;
	}

	public int getTotalVote()
	{
		return totalVote;
	}

	public void setTotalVote(int totalVote)
	{
		this.totalVote = totalVote;
	}

	public int getOpenTime()
	{
		return openTime;
	}

	public int getCloseTime()
	{
		return closeTime;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	public int getVisitor()
	{
		return visitor;
	}

	public void setVisitor(int visitor)
	{
		this.visitor = visitor;
	}

	@Override
	public int compareTo(Premise that)
	{
		return name.compareTo(that.name);
	}

}