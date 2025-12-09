package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.Authentication;
import com.example.demo.user.User;
import com.example.demo.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserDashboardController {
	
	@Autowired
	private Authentication authentication;
	
	@GetMapping("/user/dashboard")
	public String getUserDashboard(HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		// Only allow USER to access this dashboard (ADMIN goes to admin dashboard)
		if (user.getType() == UserType.ADMIN) {
			return "redirect:/admin/dashboard";
		}
		
		// If profile is not complete, redirect to profile setup
		if (!user.isProfileComplete()) {
			return "redirect:/profile/setup";
		}
		
		model.addAttribute("user", user);
		return "user-dashboard.html";
	}
	
}

