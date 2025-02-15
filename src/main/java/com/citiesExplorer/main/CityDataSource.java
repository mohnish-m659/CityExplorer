package com.citiesExplorer.main;

import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

	public List<City> getCities(int i, int pageSize, Map<String, String> criteria, Map<String, String> sortOrder) {
		Transaction transaction = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	public int getCount() {
		Transaction transaction = null;
		int count = 0;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			
			CriteriaQuery<Long> cq = cb.createQuery(Long.class);
			Root<City> root = cq.from(City.class);
			cq.select(cb.count(root));
			
			Query<Long> query = session.createQuery(cq);
			List<Long> result = query.getResultList();
			count = Integer.valueOf(result.get(0).toString());
			
		}catch (Exception e) {
			if(transaction!=null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		return count;
	}
	
}
