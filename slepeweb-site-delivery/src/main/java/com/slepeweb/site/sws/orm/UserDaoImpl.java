package com.slepeweb.site.sws.orm;

import java.util.Iterator;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoImpl implements UserDao {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public void addUser(User u) {
		this.sessionFactory.getCurrentSession().save(u);
	}

	@Transactional
	public void updateUser(User u) {
		User target = getUser(u.getId());
		
		if (target != null) {
			/*
			 *  The target object is now in a persistent state - any changes to it
			 *  will be automatically persisted to the db. Changes are made here by
			 *  assimilating the properties of a transient (form) object.
			 */
			target.assimilate(u);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<User> getAllUsers(boolean onlyDemoUsers) {
		String hql = onlyDemoUsers ? "from User where demo_user = 1 order by alias" : "from User order by alias";
		return this.sessionFactory.getCurrentSession().createQuery(hql).list();
	}

	@Transactional
	public void deleteUser(Integer id) {
        User user = (User) this.sessionFactory.getCurrentSession().load(User.class, id);
        if (null != user) {
            this.sessionFactory.getCurrentSession().delete(user);
        }
	}

	/*
	 * This is currently never used, and I suspect it may fail if called.
	 */
	public void deleteUser(User u) {
		deleteUser(u.getId());
	}

	@Transactional(readOnly=true)
	public String[] getAvailableRoles() {
		/*
		 * The <select> element on the Spring form wants a List or String[] containing the
		 * available options. We're using an array.
		 */
		@SuppressWarnings("unchecked")
		List<Object> rolesObj = this.sessionFactory.getCurrentSession().createQuery("from Role order by name").list();
		Role r;
		
		// HACK: remove CMS roles
		Iterator<Object> iter = rolesObj.iterator();
		while (iter.hasNext()) {
			r = (Role) iter.next();
			if (r.getName().toUpperCase().startsWith("CMS")) {
				iter.remove();
			}
		}
		
		String [] roles = new String[rolesObj.size()];
		int i = 0;
		
		for (Object o : rolesObj) {
			if (o instanceof Role) {
				r = (Role) o;
				roles[i++] = r.getName();
			}
		}
		
		return roles;
	}

	@Transactional(readOnly=true)
	public Role getRole(Integer id) {
		return (Role) this.sessionFactory.getCurrentSession().
				createQuery("from Role where role_id = ?").
				setInteger(0, id).
				uniqueResult();
	}

	@Transactional(readOnly=true)
	public Role getRole(String name) {
		return (Role) this.sessionFactory.getCurrentSession().
				createQuery("from Role where name = ?").
				setString(0, name).
				uniqueResult();
	}

	@Transactional(readOnly=true)
	public User getUser(String alias) {
		return (User) this.sessionFactory.getCurrentSession().
				createQuery("from User where alias = ?").
				setString(0, alias).
				uniqueResult();
	}

	@Transactional(readOnly=true)
	public User getUser(Integer id) {
		return (User) this.sessionFactory.getCurrentSession().
				createQuery("from User where user_id = ?").
				setInteger(0, id).
				uniqueResult();
	}

}
