package com.bridgelabz.spring.Utility;

public interface TokenGenerator {
	String generateToken(String id);

	int authenticateToken(String token);
}
