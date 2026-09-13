package com.smartqueue.controller;

import com.smartqueue.service.QueueService;
import com.smartqueue.service.ServiceManagementService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HomeController {

    private final QueueService queueService;
    private final ServiceManagementService serviceManagementService;

    public HomeController(QueueService queueService, ServiceManagementService serviceManagementService) {
        this.queueService = queueService;
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        return "index";
    }

    @GetMapping("/queue-display")
    public String queueDisplay(Model model) {
        model.addAttribute("displayData", queueService.getPublicQueueDisplayData());
        return "queue-display";
    }

    @GetMapping("/api/public/queue-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getQueueStatusJson() {
        return ResponseEntity.ok(queueService.getPublicQueueDisplayData());
    }
}
