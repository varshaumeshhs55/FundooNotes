package com.bridgelabz.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.spring.model.User;
import com.bridgelabz.spring.service.UserService;

@RestController
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	@Qualifier("userValidator")
	private Validator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "/registeruser", method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(@Validated @RequestBody User user, BindingResult result,
			HttpServletRequest request) {
		if (result.hasErrors()) {
			return new ResponseEntity<String>("Invalid entry", HttpStatus.NOT_FOUND);
		} else {
			try {
				if (userService.register(user, request))
					return new ResponseEntity<Void>(HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<Void>(HttpStatus.CONFLICT);
			}
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
	}

	@RequestMapping(value = "/loginuser", method = RequestMethod.POST)
	public ResponseEntity<?> loginUser(@RequestBody User user, HttpServletRequest request,
			HttpServletResponse response) {

		User newUser = userService.loginUser(user, request, response);
		if (newUser != null) {
			return new ResponseEntity<User>(newUser, HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("Incorrect emailId or password", HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/updateuser", method = RequestMethod.PUT)
	public ResponseEntity<?> updateUser(@RequestHeader("token") String token, @RequestBody User user,
			HttpServletRequest request) {

		User newUser = userService.updateUser(token, user, request);
		if (newUser != null) {
			return new ResponseEntity<User>(newUser, HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("Email incorrect. Please enter valid email address present in database",
					HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/deleteuser", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@RequestHeader("token") String token, HttpServletRequest request) {

		User user = userService.deleteUser(token, request);
		if (user != null) {
			return new ResponseEntity<User>(user, HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("Email incorrect. Please enter valid email address present in database",
					HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/activationstatus/{token:.+}", method = RequestMethod.GET)
	public ResponseEntity<?> activateUser(@PathVariable("token") String token, HttpServletRequest request) {

		User user = userService.activateUser(token, request);
		if (user != null) {
			return new ResponseEntity<String>("Activated", HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("Email incorrect. Please enter valid email address present in database",
					HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/forgotpassword", method = RequestMethod.POST)
	public ResponseEntity<?> forgotpassword(@RequestParam("emailId") String emailId, HttpServletRequest request) {
		if (userService.forgotPassword(emailId, request)) {
			return new ResponseEntity<String>("Link sent to your emailId", HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("Email incorrect. Please enter valid email address present in database",
					HttpStatus.NOT_FOUND);
		}
	}

	@RequestMapping(value = "/resetpassword/{token:.+}", method = RequestMethod.PUT)
	public ResponseEntity<?> resetpassword(@RequestBody User user, @PathVariable("token") String token,
			HttpServletRequest request) {
		User userDetails = userService.resetPassword(user, token, request);
		if (userDetails != null) {
			return new ResponseEntity<String>("Password reset", HttpStatus.FOUND);
		} else {
			return new ResponseEntity<String>("couldnot reset the password", HttpStatus.NOT_FOUND);
		}
	}

}
