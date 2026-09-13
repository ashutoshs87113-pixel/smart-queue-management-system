package com.smartqueue.controller;

import com.smartqueue.dto.QueueResponse;
import com.smartqueue.dto.ServiceRequest;
import com.smartqueue.entity.ServiceEntity;
import com.smartqueue.service.QueueService;
import com.smartqueue.service.ServiceManagementService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QueueService queueService;
    private final ServiceManagementService serviceManagementService;

    public AdminController(QueueService queueService, ServiceManagementService serviceManagementService) {
        this.queueService = queueService;
        this.serviceManagementService = serviceManagementService;
    }

    @GetMapping({"", "/"})
    public String adminDashboard(Model model) {
        model.addAttribute("stats", queueService.getAdminDashboardStats());
        model.addAttribute("services", serviceManagementService.getAllServices());
        model.addAttribute("counters", queueService.getServiceCountersData());
        return "admin/dashboard";
    }

    @GetMapping("/services")
    public String manageServices(Model model) {
        if (!model.containsAttribute("serviceRequest")) {
            model.addAttribute("serviceRequest", new ServiceRequest());
        }
        model.addAttribute("services", serviceManagementService.getAllServices());
        return "admin/services";
    }

    @PostMapping("/services/add")
    public String addService(@Valid @ModelAttribute("serviceRequest") ServiceRequest serviceRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("services", serviceManagementService.getAllServices());
            return "admin/services";
        }
        try {
            serviceManagementService.createService(serviceRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Service '" + serviceRequest.getServiceName() + "' added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/edit")
    public String editService(@RequestParam("id") Long id,
                              @Valid @ModelAttribute("serviceRequest") ServiceRequest serviceRequest,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("services", serviceManagementService.getAllServices());
            return "admin/services";
        }
        try {
            serviceManagementService.updateService(id, serviceRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Service updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @PostMapping("/services/toggle")
    public String toggleService(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            ServiceEntity service = serviceManagementService.toggleServiceActive(id);
            String state = service.isActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", "Service '" + service.getServiceName() + "' has been " + state + ".");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/services";
    }

    @GetMapping("/queue")
    public String manageQueue(Model model) {
        model.addAttribute("services", serviceManagementService.getAllActiveServices());
        model.addAttribute("todayEntries", queueService.getTodayQueueEntries());
        model.addAttribute("stats", queueService.getAdminDashboardStats());
        model.addAttribute("counters", queueService.getServiceCountersData());
        return "admin/queue";
    }


    @PostMapping("/call-next")
    public String callNext(@RequestParam("serviceId") Long serviceId, RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.callNextToken(serviceId);
            redirectAttributes.addFlashAttribute("successMessage", "Now Serving Token: " + response.getTokenNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/queue";
    }

    @PostMapping("/complete")
    public String completeToken(@RequestParam("serviceId") Long serviceId, RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.completeCurrentToken(serviceId);
            redirectAttributes.addFlashAttribute("successMessage", "Completed service for Token: " + response.getTokenNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/queue";
    }

    @PostMapping("/skip")
    public String skipToken(@RequestParam("serviceId") Long serviceId, RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.skipCurrentToken(serviceId);
            redirectAttributes.addFlashAttribute("successMessage", "Skipped Token: " + response.getTokenNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/queue";
    }

    @PostMapping("/cancel")
    public String cancelTokenByAdmin(@RequestParam("queueId") Long queueId, RedirectAttributes redirectAttributes) {
        try {
            QueueResponse response = queueService.cancelTokenByAdmin(queueId);
            redirectAttributes.addFlashAttribute("successMessage", "Cancelled Token: " + response.getTokenNumber());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/queue";
    }
}
