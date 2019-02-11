package com.bridgelabz.spring.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.spring.Utility.Email;

import com.bridgelabz.spring.Utility.TokenGenerator;
import com.bridgelabz.spring.dao.UserDao;
import com.bridgelabz.spring.model.User;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private TokenGenerator tokenGenerator;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Autowired
	private Email email;

	@Transactional
	public boolean register(User user, HttpServletRequest request) {
		user.setPassword(bcryptEncoder.encode(user.getPassword()));
		int id = userDao.register(user);
		if (id > 0) {
			String token = tokenGenerator.generateToken(String.valueOf(id));
			StringBuffer Url = request.getRequestURL();
			String activeUrl = Url.substring(0, Url.lastIndexOf("/"));
			activeUrl = activeUrl + "/activationstatus/" + token;

			email.sendMail("varshaumesh555@gmail.com", "verification mail", activeUrl);
			return true;
		}
		return false;
	}
	@Transactional
	public User loginUser(User user, HttpServletRequest request, HttpServletResponse res) {
		User users = userDao.loginUser(user.getEmailId());
		if (users != null) {
		boolean matches = bcryptEncoder.matches(user.getPassword(), users.getPassword());
			if (matches && users.isActivationStatus()) {
				String token = tokenGenerator.generateToken(String.valueOf(users.getId()));
				res.setHeader("token", token);
				return users;
			}
		}
		return null;
	}


	@Transactional
	public User updateUser(String token, User user, HttpServletRequest request) {
		int userId = tokenGenerator.authenticateToken(token);
		User newUser = userDao.getUserById(userId);
		if (newUser != null) {
			newUser.setMobileNumber(user.getMobileNumber());
			newUser.setName(user.getName());
			newUser.setEmailId(user.getEmailId());
			newUser.setPassword(user.getPassword());
			newUser.setPassword(bcryptEncoder.encode(newUser.getPassword()));
			userDao.updateUser(newUser);
		}
		return newUser;
	}

	@Transactional
	public User deleteUser(String token, HttpServletRequest request) {
		int userId = tokenGenerator.authenticateToken(token);
		User newUser = userDao.getUserById(userId);
		if (newUser != null) {
			userDao.deleteUser(userId);
		}
		return newUser;
	}

	@Transactional
	public User activateUser(String token, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User user = userDao.getUserById(id);
		if (user != null) {
			user.setActivationStatus(true);
			userDao.updateUser(user);
		}
		return user;
	}


	@Transactional
	public boolean forgotPassword(String emailId, HttpServletRequest request) {
		User user = userDao.loginUser(emailId);
		if (user != null) {
			String token = tokenGenerator.generateToken(String.valueOf(user.getId()));
			StringBuffer Url = request.getRequestURL();
			String activeUrl = Url.substring(0, Url.lastIndexOf("/"));
			activeUrl = activeUrl + "/resetpassword/" + token;
			email.sendMail("varshaumesh555@gmail.com", "password verification", activeUrl);
			return true;
		}
		return false;
	}

	@Transactional
	public User resetPassword(User user, String token, HttpServletRequest request) {
		int id = tokenGenerator.authenticateToken(token);
		User userDetails = userDao.getUserById(id);
		if (userDetails != null) {
			userDetails.setPassword(bcryptEncoder.encode(user.getPassword()));
			userDao.updateUser(userDetails);
			return userDetails;
		}
		return null;
	}

}
