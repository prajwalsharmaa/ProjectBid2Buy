package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.Authentication;
import com.example.demo.user.User;
import com.example.demo.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {
	
	@Autowired
	private Authentication authentication;
	
	@GetMapping("/admin/dashboard")
	public String getAdminDashboard(HttpServletRequest request, Model model) {
		User user = authentication.authenticate(request);
		
		if (user == null) {
			return "redirect:/login";
		}
		
		// Only allow ADMIN to access this dashboard
		if (user.getType() != UserType.ADMIN) {
			return "redirect:/user/dashboard";
		}
		
		model.addAttribute("user", user);
		return "admin-dashboard.html";
	}
	
}
