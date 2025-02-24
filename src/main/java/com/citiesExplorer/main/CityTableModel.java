package com.citiesExplorer.main;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.citiesExplorer.main.CityGUI.SearchSortField;

public class CityTableModel {
	
	private static final int PAGE_SIZE = 20;
	
	private static CityTableModel instance = new CityTableModel();
	
	private static SoftReference<Map<Integer, SoftReference<List<City>>>> cache;
	
	private static int rowCount = 0;
	
	private CityTableModel() {
		super();
	}

	public static City getDataAt(int index, Map<SearchSortField, String> criteria) throws IndexOutOfBoundsException{
		synchronized (instance) {
			instance.initCacheIfNull();
			int pageIndx = index / PAGE_SIZE;
			List<City> cities = instance.loadPage(pageIndx,criteria);
			if(cities == null) {
				throw new NoSuchElementException("No Results found");
			}
			City city = cities.get(index % PAGE_SIZE);
			if(instance.cache.get().size() > 10) {
				instance.cache.clear();
			}
			return city;
		}
	}

	private List<City> loadPage(int pageIndx, Map<SearchSortField, String> criteria) {
		cache.get().computeIfAbsent(pageIndx, (k)->{
			SoftReference<List<City>> ref = new SoftReference<List<City>>(CityDataSource.getInstance()
					.getCities(pageIndx * PAGE_SIZE, PAGE_SIZE, criteria));
			addToRowCount(ref.get().size(), pageIndx);
			return ref;
		});
		return cache.get().get(pageIndx).get();
	}
	
	boolean indexPageAdded = false;

	private void addToRowCount(int size, int pageIndx) {
		int lastPageIndx = Math.floorDiv(rowCount-1, PAGE_SIZE);
		if((pageIndx == 0 && !indexPageAdded)) {
			System.out.println("Adding index row count " + size + " to current= " + rowCount);
			rowCount = rowCount + size;
			indexPageAdded = true;
			System.out.println("table Data row Count = " + rowCount);
			return;
		}
		if(pageIndx > lastPageIndx) {
			System.out.println("Adding to row count " + rowCount + " increase by= " + size + " pageIndx= " + pageIndx + " lastPageIndx=" + lastPageIndx);
			rowCount = rowCount + size;
			System.out.println("table data row count = " + rowCount);
		}
	}

	private void initCacheIfNull() {
		if(cache == null || cache.refersTo(null)) {
			cache = new SoftReference<Map<Integer,SoftReference<List<City>>>>(new HashMap<Integer,SoftReference<List<City>>>());
		}
	}
	
	private static CityTableModel getInstance() {
		return instance;
	}

	public static int getRowCount() {
		return rowCount;
	}

	public static void invalidateCache() {
		synchronized (instance) {
			cache.clear();
			rowCount = 0;
			instance.indexPageAdded = false;
		}
	}
	
}
