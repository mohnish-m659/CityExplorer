package com.citiesExplorer.main;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityTableModel {
	
	private static final int PAGE_SIZE = 100;
	
	private static CityTableModel instance = new CityTableModel();
	
	private SoftReference<Map<Integer, SoftReference<List<City>>>> cache;
	
	private CityTableModel() {
		super();
	}

	public static City getDataAt(int index, Map<String, String> criteria, Map<String, String> sortOrder) throws IndexOutOfBoundsException{
		instance.initCacheIfNull();
		int pageIndx = index / PAGE_SIZE;
		List<City> cities = instance.loadPage(pageIndx,criteria,sortOrder);
		City city = cities.get(index % PAGE_SIZE);
		System.out.println("Cache Size" + instance.cache.get().size());
		if(instance.cache.get().size() > 100) {
			instance.cache.clear();
		}
		return city;
	}

	private List<City> loadPage(int pageIndx, Map<String, String> criteria, Map<String, String> sortOrder) {
		if(criteria.isEmpty() && sortOrder.isEmpty()) {
			return loadPage(pageIndx);
		}
		cache.get().compute(pageIndx, (k, v)->{
			SoftReference<List<City>> ref = new SoftReference<List<City>>(CityDataSource.getInstance()
					.getCities(pageIndx * PAGE_SIZE, PAGE_SIZE, criteria, sortOrder));
			return ref;
		});
		return null;
	}

	private void initCacheIfNull() {
		if(cache == null || cache.refersTo(null)) {
			cache = new SoftReference<Map<Integer,SoftReference<List<City>>>>(new HashMap<Integer,SoftReference<List<City>>>());
		}
	}

	private List<City> loadPage(int pageIndx) {
		return cache.get().computeIfAbsent(pageIndx, (k)->{
			SoftReference<List<City>> ref = new SoftReference<List<City>>(CityDataSource.getInstance().getCities(pageIndx * PAGE_SIZE, PAGE_SIZE));
			return ref;
		}).get();
	}
	
	public static int getRowCount() {
		return CityDataSource.getInstance().getCount();
	}
	
	private static CityTableModel getInstance() {
		return instance;
	}
	
}
