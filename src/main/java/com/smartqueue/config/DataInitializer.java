package com.smartqueue.config;

import com.smartqueue.entity.Role;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.entity.User;
import com.smartqueue.repository.ServiceRepository;
import com.smartqueue.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ServiceRepository serviceRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        initAdminUser();
        initDefaultServices();
    }

    private void initAdminUser() {
        Optional<User> adminOpt = userRepository.findByUsername("admin");
        if (adminOpt.isEmpty()) {
            User admin = new User(
                    "System Administrator",
                    "admin",
                    "admin@smartqueue.com",
                    passwordEncoder.encode("admin123"),
                    "9876543210",
                    Role.ADMIN
            );
            userRepository.save(admin);
            logger.info("Default admin user created successfully (username: admin)");
        } else {
            User admin = adminOpt.get();
            boolean updated = false;
            if (admin.getRole() != Role.ADMIN) {
                admin.setRole(Role.ADMIN);
                updated = true;
            }
            if (!passwordEncoder.matches("admin123", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("admin123"));
                updated = true;
            }
            if (updated) {
                userRepository.save(admin);
                logger.info("Default admin user credentials/role synchronized successfully");
            }
        }
    }

    private void initDefaultServices() {
        if (serviceRepository.count() == 0) {
            List<ServiceEntity> defaultServices = List.of(
                    new ServiceEntity("Admission", "College admission process, document submission, and seat verification.", 10, true),
                    new ServiceEntity("Fees", "Tuition fee payment, receipts, and clearance certificates.", 5, true),
                    new ServiceEntity("Examination", "Hall ticket collection, exam registration, and grade cards.", 8, true),
                    new ServiceEntity("Scholarship", "Scholarship application, verification, and disbursement info.", 12, true),
                    new ServiceEntity("General Enquiry", "General office inquiries, certificates, and student ID cards.", 5, true)
            );
            serviceRepository.saveAll(defaultServices);
            logger.info("Default college queue services initialized successfully");
        }
    }
}
