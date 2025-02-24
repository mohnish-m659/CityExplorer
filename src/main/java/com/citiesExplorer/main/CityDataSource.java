package com.citiesExplorer.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.citiesExplorer.main.CityGUI.SearchSortField;
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

	public List<City> getCities(int i, int pageSize, Map<SearchSortField, String> criteria) {
		Transaction transaction = null;
		List<City> cities = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			transaction = session.beginTransaction();
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
			CriteriaQuery<City> critQuery = criteriaBuilder.createQuery(City.class);
			Root<City> root = critQuery.from(City.class);
			
			List<Predicate> filters = new ArrayList<>();
			Predicate cityFilter = null;
			String cityValue = criteria.get(SearchSortField.CITY);
			if(StringUtils.isNotBlank(cityValue)) {
				cityFilter = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), getLike(cityValue));
				filters.add(cityFilter);
			}
			
			Predicate countryFilter = null;
			String countryValue = criteria.get(SearchSortField.COUNTRY);
			if(StringUtils.isNotBlank(countryValue)) {
				countryFilter = criteriaBuilder.like(criteriaBuilder.lower(root.get("country")), getLike(countryValue));
				filters.add(countryFilter);
			}
			
			critQuery.select(root).where(criteriaBuilder.and(filters.toArray(new Predicate[0])));
			
			String sortBy = criteria.get(SearchSortField.SORTBY);
			critQuery.orderBy(criteriaBuilder.asc(root.get(sortBy)));
			
			Query<City> query = session.createQuery(critQuery);
			cities = query.setFirstResult(i).setMaxResults(pageSize).getResultList();
			
		}catch (Exception e) {
			if(transaction!=null) {
				transaction.rollback();
			}
			e.printStackTrace();
		}
		return cities;
	}

	private String getLike(String string) {
		return "%"+StringUtils.lowerCase(string)+"%";
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
