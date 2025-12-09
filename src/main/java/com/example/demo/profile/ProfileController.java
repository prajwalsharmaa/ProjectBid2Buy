package com.example.demo.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.auth.Authentication;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

	@Autowired
	private Authentication authentication;
	
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/profile")
	public String getProfilePage(HttpServletRequest request, Model model) {
		// DONE: get user from cookie
		// user verification
		// user identification
		User user = authentication.authenticate(request);

		if (user == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", user);
		return "profile.html";
	}

	@GetMapping("/profile/edit")
	public String getProfileEditPage(HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		if (user == null) {
			return "redirect:/login";
		}

		ProfileEditForm form = new ProfileEditForm();
		form.setFirstName(user.getFirstName());
		form.setLastName(user.getLastName());
		form.setAddress(user.getAddress());
		form.setAge(user.getAge());
		
		model.addAttribute("user", user);
		model.addAttribute("form", form);
		return "profile-edit.html";
	}

	@PostMapping("/profile/edit")
	public String updateProfile(ProfileEditForm form, HttpServletRequest request) throws IOException {
		User user = authentication.authenticate(request);
		if (user == null) {
			return "redirect:/login";
		}
		
		// Update user information
		if (form.getFirstName() != null && !form.getFirstName().isBlank()) {
			user.setFirstName(form.getFirstName());
		}
		if (form.getLastName() != null && !form.getLastName().isBlank()) {
			user.setLastName(form.getLastName());
		}
		if (form.getAddress() != null && !form.getAddress().isBlank()) {
			user.setAddress(form.getAddress());
		}
		if (form.getAge() != null && form.getAge() > 0) {
			user.setAge(form.getAge());
		}
		
		//DONE: save user's profile picture
		MultipartFile profile = form.getProfile();
		if(profile != null && !profile.isEmpty()) {
			
			String originalFileName = profile.getOriginalFilename();
			String fileExtension = originalFileName != null && originalFileName.contains(".") 
				? originalFileName.substring(originalFileName.lastIndexOf(".") + 1) 
				: "jpg";
			String fileName = Instant.now().toEpochMilli() + "." + fileExtension;
					
			// Ensure uploads directory exists
			Path uploadsDir = Paths.get("uploads");
			if (!Files.exists(uploadsDir)) {
				Files.createDirectories(uploadsDir);
			}
			
			Path path = Paths.get("uploads", fileName);
			
			Files.copy(profile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			// Delete old profile picture if it exists
			if (user.getProfilePicture() != null && !user.getProfilePicture().isBlank()) {
				Path oldPicturePath = Paths.get("uploads", user.getProfilePicture());
				if (Files.exists(oldPicturePath)) {
					Files.delete(oldPicturePath);
				}
			}
			
			user.setProfilePicture(fileName);
		}
		
		
		userRepository.save(user);
		
		return "redirect:/profile";
	}

	@GetMapping("/profile/setup")
	public String getProfileSetupPage(HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		if (user == null) {
			return "redirect:/login";
		}
		
		// If profile is already complete, redirect to dashboard
		if (user.isProfileComplete()) {
			if (user.getType() == com.example.demo.user.UserType.ADMIN) {
				return "redirect:/admin/dashboard";
			} else {
				return "redirect:/user/dashboard";
			}
		}
		
		model.addAttribute("user", user);
		model.addAttribute("form", new ProfileEditForm());
		return "profile-setup.html";
	}

	@PostMapping("/profile/setup")
	public String setupProfile(ProfileEditForm form, HttpServletRequest request) throws IOException {
		User user = authentication.authenticate(request);
		if (user == null) {
			return "redirect:/login";
		}
		
		// Update user profile information
		if (form.getFirstName() != null && !form.getFirstName().isBlank()) {
			user.setFirstName(form.getFirstName());
		}
		if (form.getLastName() != null && !form.getLastName().isBlank()) {
			user.setLastName(form.getLastName());
		}
		if (form.getAddress() != null && !form.getAddress().isBlank()) {
			user.setAddress(form.getAddress());
		}
		if (form.getAge() != null && form.getAge() > 0) {
			user.setAge(form.getAge());
		}
		
		// Save profile picture
		MultipartFile profile = form.getProfile();
		if (profile != null && !profile.isEmpty()) {
			// Ensure uploads directory exists
			Path uploadsDir = Paths.get("uploads");
			if (!Files.exists(uploadsDir)) {
				Files.createDirectories(uploadsDir);
			}
			
			String originalFileName = profile.getOriginalFilename();
			String fileExtension = originalFileName != null && originalFileName.contains(".") 
				? originalFileName.substring(originalFileName.lastIndexOf(".") + 1) 
				: "jpg";
			String fileName = Instant.now().toEpochMilli() + "." + fileExtension;
			
			Path path = Paths.get("uploads", fileName);
			Files.copy(profile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			user.setProfilePicture(fileName);
		}
		
		userRepository.save(user);
		
		// Redirect to appropriate dashboard
		if (user.getType() == com.example.demo.user.UserType.ADMIN) {
			return "redirect:/admin/dashboard";
		} else {
			return "redirect:/user/dashboard";
		}
	}

}
