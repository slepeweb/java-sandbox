package com.slepeweb.sandbox.orm;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ConfigDaoImpl implements ConfigDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Transactional(readOnly=true)
	public Config getConfig(String key) {
		return (Config) this.sessionFactory.getCurrentSession().
				createQuery("from Config where name = ?").
				setString(0, key).
				uniqueResult();
	}
}
