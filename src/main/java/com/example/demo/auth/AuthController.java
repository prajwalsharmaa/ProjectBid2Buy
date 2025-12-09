package com.example.demo.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.Utilities;
import com.example.demo.ValidationError;
import com.example.demo.user.Gender;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserType;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("registerForm", new RegistrationForm());
		return "register.html";
	}

	@PostMapping("/register")
	public String registerUser(RegistrationForm form, Model model) {

		System.out.println("Email:" + form.getEmail());
		System.out.println("Password:" + form.getPassword());

		// DONE: Make sure first name is not empty
		if (form.getFirstName().isBlank()) {
			// send "First name cannot be empty"
			model.addAttribute("error", new ValidationError("First name cannot be empty"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}
		
		// TODO: Make sure email is valid

		// DONE: Make sure password contains special character, number
		if (!Utilities.isValidPassword(form.getPassword())) {
			model.addAttribute("error", new ValidationError(
					"Password should be at least 8 characters long and should contain at least one number and at least one uppercase letter"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Make sure password & confirm password are equal
		if (!form.getPassword().equals(form.getConfirmPassword())) {
			model.addAttribute("error", new ValidationError("Password and confirm password do not match"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: Make sure email is unique
		// cannot be done in client side
		if (userRepository.existsByEmail(form.getEmail())) {
			model.addAttribute("error", new ValidationError("Email already exists"));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// DONE: if any error exists; show it in the form

		// DONE: Create User entity object
		User user = new User();
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName());
		user.setEmail(form.getEmail());

		// DONE: store hash of the password
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		// Set user type to USER (all users can buy and sell)
		user.setType(UserType.USER);
		user.setUsername(form.getEmail());
		user.setGender(Gender.valueOf(form.getGender()));

		// DONE: Store the entity in db
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException e) {
			// Handle database constraint errors
			if (e.getMessage() != null && e.getMessage().contains("_user_type_check")) {
				model.addAttribute("error", new ValidationError(
					"Database configuration error. Please contact administrator. Error: Database constraint needs to be updated for new user types."));
			} else {
				model.addAttribute("error", new ValidationError("Registration failed. Please try again or contact support."));
			}
			model.addAttribute("registerForm", form);
			return "register.html";
		} catch (Exception e) {
			model.addAttribute("error", new ValidationError("An unexpected error occurred. Please try again."));
			model.addAttribute("registerForm", form);
			return "register.html";
		}

		// TODO: Send successful message and redirect to login page.

		return "redirect:/login";
	}

	@GetMapping("/login")
	public String getLoginPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("form", new LoginForm());
		return "login.html";
	}

	@PostMapping("/login")
	public String loginUser(LoginForm form, Model model, HttpServletResponse response) throws IOException {

		// Find user in db by email or username
		// Try email first, then username
		User user = userRepository.findByEmail(form.getEmail());
		if (user == null) {
			user = userRepository.findByUsername(form.getEmail());
		}

		// DONE: match the form's password with db password using encoder
		// DONE: if match not found, send "Invalid credentials" error message
		if (user == null || !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
			model.addAttribute("error", new ValidationError("Invalid credentials!"));
			model.addAttribute("form", form);
			return "login.html";
		}
		
		// DONE: if match found, set a new cookie (session) for the user
		String sessionID = Utilities.getRandomString(20);
		Cookie cookie = new Cookie("SESSIONID", sessionID);
		cookie.setPath("/");
		response.addCookie(cookie);
		
		// DONE: store created cookie in db
		user.setSession(sessionID);
		userRepository.save(user);
		
		// Redirect based on user type and profile completion
		if (user.getType() == UserType.ADMIN) {
			return "redirect:/admin/dashboard";
		} else {
			// Check if profile is complete
			if (!user.isProfileComplete()) {
				return "redirect:/profile/setup";
			} else {
				return "redirect:/user/dashboard";
			}
		}
	}

}
