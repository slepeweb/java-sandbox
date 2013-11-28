package com.slepeweb.sandbox.orm;

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
			target.assimilate(u);
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)
	public List<User> getAllUsers() {
		return this.sessionFactory.getCurrentSession().createQuery("from User order by alias").list();
	}

	@Transactional
	public void deleteUser(Integer id) {
        User user = (User) this.sessionFactory.getCurrentSession().load(User.class, id);
        if (null != user) {
            this.sessionFactory.getCurrentSession().delete(user);
        }
	}

	public void deleteUser(User u) {
		deleteUser(u.getId());
	}

	@Transactional(readOnly=true)
	public String[] getAvailableRoles() {
		@SuppressWarnings("unchecked")
		List<Object> rolesObj = this.sessionFactory.getCurrentSession().createQuery("from Role order by name").list();
		String [] roles = new String[rolesObj.size()];
		Role r;
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
