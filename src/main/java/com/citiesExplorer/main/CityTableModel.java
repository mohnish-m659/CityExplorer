package com.citiesExplorer.main;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityTableModel {
	
	private static final int PAGE_SIZE = 20;
	
	private static CityTableModel instance = new CityTableModel();
	
	private Map<Integer, SoftReference<List<City>>> cache = new HashMap<>();
	
	private CityTableModel() {
		super();
	}

	public static City getDataAt(int index) throws IndexOutOfBoundsException{
		int pageIndx = index / PAGE_SIZE;
		List<City> cities = getInstance().loadPage(pageIndx);
		City city = cities.get(index % PAGE_SIZE);
		return city;
	}

	private List<City> loadPage(int pageIndx) {
		return cache.computeIfAbsent(pageIndx, (k)->{
			SoftReference<List<City>> ref = new SoftReference<List<City>>(CityDataSource.getInstance().getCities(pageIndx * PAGE_SIZE, PAGE_SIZE));
			return ref;
		}).get();
	}
	
	private static CityTableModel getInstance() {
		return instance;
	}
	
}
