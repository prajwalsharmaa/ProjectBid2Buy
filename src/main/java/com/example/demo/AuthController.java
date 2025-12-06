package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
	@GetMapping("/register")
	public String getRegisterPage(Model model) {
		model.addAttribute("registerForm", new RegistrationForm());
		return "register.html";
	}
	@PostMapping("/register")
	public String registerUser(RegistrationForm form) {
		System.out.println("Email:" + form.getEmail());
		System.out.println("Password:"+ form.getPassword());
		
		return "redirect:";
	}
}
