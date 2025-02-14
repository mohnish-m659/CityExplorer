package com.citiesExplorer.main;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.citiesExplorer.utils.HibernateUtil;

public class CityDataSource {

	private static CityDataSource instance = new CityDataSource();
			
	private CityDataSource() {}
	
	@SuppressWarnings("unchecked")
	public List<City> getCities(int start, int length){
		Transaction transaction = null;
		List<City> cities = null;
		try(Session session = HibernateUtil.getSessionFactory().openSession()){
			transaction = session.beginTransaction();
			cities = session.getNamedQuery(City.QUERY_CITIES).setFirstResult(start).setMaxResults(length).getResultList();
			transaction.commit();
		}catch (Exception e) {
			if(transaction!=null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		return cities;
	}
	
	
	public static CityDataSource getInstance() {
		return instance;
	}
	
}
