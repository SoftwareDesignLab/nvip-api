/**
 * Copyright 2023 Rochester Institute of Technology (RIT). Developed with
 * government support under contract 70RSAT19CB0000020 awarded by the United
 * States Department of Homeland Security.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.nvip.data.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.apache.commons.codec.binary.Hex;
import org.nvip.api.serializers.CredentialsDTO;
import org.nvip.entities.*;
import org.springframework.stereotype.Repository;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.time.LocalDateTime;

@Repository
public class UserRepository {

	@PersistenceContext
	EntityManager entityManager;

	/**
	 * Function that Hash Encrypts passwords that are inputed into
	 * the system
	 * @param password - password to be hashed
	 * @param salt - salt to be used for hashing
	 * @param iterations - number of iterations to be used for hashing
	 * @param keyLength - key length to be used for hashing
	 * @return
	 */
	public byte[] hashPassword(final char[] password, final byte[] salt, final int iterations, final int keyLength) {

		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
			PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
			SecretKey key = skf.generateSecret(spec);
            return key.getEncoded();
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException(e);
		}
	}

	//TODO: remove all, set as interface, reference using JPA derived queries instead of manually

	/**
	 * save a new user entry to the database
	 */
	public User save(User user) {
		entityManager.persist(user);
		return user;
	}

	/**
	 * Function that generates Hex String for password hashing
	 * @param s - string to be converted to Hex String
	 * @return - Hex String
	 */
	/* s must be an even-length string. */
	public byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	/**
	 * Checks if a user with the given name exists in the NVIP Database
	 * @param userName - username to be checked
	 * @return - true if user exists, false otherwise
	 */
	private boolean checkUserExistence(String userName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> cq = criteriaBuilder.createQuery(Long.class);
		Root<User> root = cq.from(User.class);
		cq.select(criteriaBuilder.count(root))
				.where(criteriaBuilder.equal(root.get("userName"), userName));

		return entityManager.createQuery(cq).getSingleResult() > 0;
	}

	/**
	 * Login function called by LoginServlet (POST Request),
	 * Checks if the created user already exists,
	 * If not, create user and add to database
	 * @param user - user to be created
	 * @param password - password to be hashed and stored
	 * @return - 1 if user is created, -1 if something went wrong, -2 if user already exists
	 */
	@Transactional
	public int createUser(User user, String password) {
		boolean userExist = checkUserExistence(user.getUserName());
		if (userExist) {
			return -2;
		}

		SecureRandom random = new SecureRandom();
		byte[] saltBytes = new byte[64];
		random.nextBytes(saltBytes);

		String salt = Hex.encodeHexString(saltBytes);
		int iterations = 10000;
		int keyLength = 512;
		char[] passwordChars = password.toCharArray();

		byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
		String hashedPassword = Hex.encodeHexString(hashedBytes);
		hashedPassword = hashedPassword + salt;

		user.setPasswordHash(hashedPassword);
		entityManager.persist(user);

		return 1;
	}

	/**
	 * Function that collects User info for Servlets that require to
	 * verify user and role_id info
	 * @param userName
	 * @param token
	 * @return
	 */
	public User getRoleIDandExpirationDate(String userName, String token) {
//		return login(userName);
		return null;
	}

	public User findByUserName(String userName) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = criteriaBuilder.createQuery(User.class);
		Root<User> root = cq.from(User.class);
		CriteriaQuery<User> query = cq.where(criteriaBuilder.equal(root.get("userName"), userName));
		try {
			return entityManager.createQuery(query).getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Login function called by LoginServlet (GET Request),
	 * Verifies passwords match and creates token for user
	 * @param userName - username to be checked
	 * @param password - password to be checked
	 * @return - User object if login is successful, null otherwise
	 */
//	@Transactional
//	public User login(String userName, String password) {
//		// Password Hashing Logic
////		User user = login(userName.toLowerCase());
//		User user = null;
//		if (user == null) {
//			return null;
//		}
//
//		int iterations = 10000;
//		int keyLength = 512;
//		char[] passwordChars = password.toCharArray();
//
//		String dbHash = user.getPasswordHash().substring(0, keyLength / 4);
//		String salt = user.getPasswordHash().substring(keyLength / 4);
//		byte[] saltBytes = hexStringToByteArray(salt);
//
//		byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
//		String hashedString = Hex.encodeHexString(hashedBytes);
//
//		if (!hashedString.equalsIgnoreCase(dbHash)) {
//			return null;
//		}
//
//		SecureRandom random = new SecureRandom();
//		byte tokenBytes[] = new byte[64];
//		random.nextBytes(tokenBytes);
//		String tokenString = Hex.encodeHexString(tokenBytes);
//
//		LocalDateTime loginDate = LocalDateTime.now();
//		LocalDateTime expirationDate = null;
//		if (user.getRoleId() == 1) {
//			expirationDate = LocalDateTime.now().plusHours(3);
//		} else if (user.getRoleId() == 2) {
//			expirationDate = LocalDateTime.now().plusDays(5);
//		}
//
//		int rs = updateToken(userName, tokenString, loginDate, expirationDate);
//		if (rs == -1) {
//			// Something went wrong in updating user token
//			return null;
//		}
//
//		user.setToken(tokenString);
//		user.setExpirationDate(expirationDate);
//
//		return user;
//	}



	/**
	 * TODO: Could we possibly see if there's a way to merge this function with getRoleIDandExpirationDate
	 * (i.e. a function that just grabs user data with provided parameters)
	 *
	 * Helper function for Login that queries for User in NVIP Database
	 * @param userName
	 * @return
	 */
	private User login(CredentialsDTO credentials) {
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<User> cq = criteriaBuilder.createQuery(User.class);
		Root<User> root = cq.from(User.class);
//		CriteriaQuery<User> query = cq.where(criteriaBuilder.equal(root.get("userName"), userName));
return null;
//		return entityManager.createQuery(query).getSingleResult();

	}

	/**
	 * Function that updates a user's token once they log in
	 * When the token expires, the user will be forced to log out
	 * @param userName - username of user to be updated
	 * @param token - token to be updated
	 * @param loginDate - login date of user
	 * @param expirationDate - expiration date of user's token
	 * @return - 1 if token is updated, -1 if something went wrong
	 */
	private int updateToken(String userName, String token, LocalDateTime loginDate, LocalDateTime expirationDate) {

		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaUpdate<User> cq = criteriaBuilder.createCriteriaUpdate(User.class);
		Root<User> root = cq.from(User.class);
		cq.set("token", token);
		cq.set("expirationDate", Timestamp.valueOf(loginDate));
		cq.set("lastLoginDate", Timestamp.valueOf(expirationDate));
		cq.where(criteriaBuilder.equal(root.get("userName"), userName));

		return entityManager.createQuery(cq).executeUpdate();
	}
}