package com.smartqueue.controller;

import com.smartqueue.dto.QueueResponse;
import com.smartqueue.entity.User;
import com.smartqueue.service.QueueService;
import com.smartqueue.service.ServiceManagementService;
import com.smartqueue.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final QueueService queueService;
    private final UserService userService;
    private final ServiceManagementService serviceManagementService;

    public UserController(QueueService queueService, UserService userService, ServiceManagementService serviceManagementService) {
        this.queueService = queueService;
        this.userService = userService;
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username).orElseThrow();

        List<QueueResponse> activeQueues = queueService.getUserActiveQueueStatus(username);
        model.addAttribute("user", user);
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        model.addAttribute("activeQueues", activeQueues);
        model.addAttribute("primaryQueue", activeQueues.isEmpty() ? null : activeQueues.get(0));

        return "dashboard";
    }

    @GetMapping("/my-queue")
    public String myQueue(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String username = userDetails.getUsername();
        User user = userService.findByUsername(username).orElseThrow();

        model.addAttribute("user", user);
        model.addAttribute("activeQueues", queueService.getUserActiveQueueStatus(username));
        model.addAttribute("queueHistory", queueService.getUserQueueHistory(username));

        return "my-queue";
    }

    @PostMapping("/join-queue")
    public String joinQueue(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("serviceId") Long serviceId,
                            RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.joinQueue(userDetails.getUsername(), serviceId);
            redirectAttributes.addFlashAttribute("successMessage", "Joined queue successfully! Your Token: " + response.getTokenNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/cancel-queue")
    public String cancelQueue(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam("queueId") Long queueId,
                             RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.cancelQueue(userDetails.getUsername(), queueId);
            redirectAttributes.addFlashAttribute("successMessage", "Queue token " + response.getTokenNumber() + " has been cancelled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/my-queue";
    }
}
