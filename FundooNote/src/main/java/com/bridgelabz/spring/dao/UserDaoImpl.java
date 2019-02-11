package com.bridgelabz.spring.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bridgelabz.spring.model.User;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private SessionFactory sessionFactory;

	public int register(User user) {
		int userId = 0;
		Session session = sessionFactory.getCurrentSession();
		userId = (Integer) session.save(user);
		return userId;
	}

	public User loginUser(String emailId) {

		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from User where emailId= :emailId");
		query.setString("emailId", emailId);
		User user = (User) query.uniqueResult();
		if (user != null) {
			System.out.println("User detail is=" + user.getId() + "," + user.getName() + "," + user.getEmailId() + ","
					+ user.getMobileNumber());
			session.close();
			return user;
		}
		return null;
	}

	public User getUserById(int id) {

		Session session = sessionFactory.openSession();
		Query query = session.createQuery("from User where id= :id");
		query.setInteger("id", id);
		User user = (User) query.uniqueResult();
		if (user != null) {
			session.close();
			return user;
		}
		return null;
	}

	public void updateUser(User user) {
		Session session = sessionFactory.openSession();
		Transaction transaction = session.beginTransaction();
		session.update(user);
		transaction.commit();
		session.close();
	}

	public void deleteUser(int id) {
		Session session = sessionFactory.openSession();
		Query query = session.createQuery("DELETE from User u where u.id= :id");
		query.setInteger("id", id);
		query.executeUpdate();
		session.close();
	}

}