package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.user.Gender;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserType;

@Component
public class DataInitializer implements CommandLineRunner {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public void run(String... args) throws Exception {
		// Fix database constraint for user type
		try {
			jdbcTemplate.execute("ALTER TABLE _user DROP CONSTRAINT IF EXISTS _user_type_check");
			jdbcTemplate.execute("ALTER TABLE _user ADD CONSTRAINT _user_type_check CHECK (type IN ('ADMIN', 'USER'))");
			System.out.println("Database constraint _user_type_check updated successfully");
		} catch (Exception e) {
			System.out.println("Note: Could not update constraint (may already be correct): " + e.getMessage());
		}
		
		// Create admin user if it doesn't exist
		if (!userRepository.existsByEmail("admin@projectbid2buy.com")) {
			User admin = new User();
			admin.setFirstName("Admin");
			admin.setLastName("User");
			admin.setUsername("admin");
			admin.setEmail("admin@projectbid2buy.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setType(UserType.ADMIN);
			admin.setGender(Gender.MALE);
			admin.setAddress("System Address");
			admin.setAge(30);
			admin.setProfilePicture("default.jpg");
			userRepository.save(admin);
			System.out.println("Admin user created: username=admin, password=admin123");
		}
	}
}

