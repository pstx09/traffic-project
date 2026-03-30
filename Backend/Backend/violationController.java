package com.example.traffic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.traffic.model.violation;
import com.example.traffic.repository.violationRepository;

@Controller
public class violationController {

    @Autowired
    private violationRepository repository;

    // LOGIN PAGE
    @GetMapping("/")
    public String loginPage() {
        return "login";
    }

    // LOGIN CHECK
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {

        if(username.equals("admin") && password.equals("admin123")) {
            return "redirect:/admin";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
    }

    // ADMIN DASHBOARD
    @GetMapping("/admin")
    public String adminPage(@RequestParam(required = false) String keyword, Model model) {

        List<violation> list;

        if(keyword != null && !keyword.isEmpty()) {
            list = repository.findByVehicleNumberContaining(keyword);
        } else {
            list = repository.findByPaidFalse();
        }

        model.addAttribute("violations", list);
        model.addAttribute("keyword", keyword);

        return "admin";
    }

    // ADD FORM
    @GetMapping("/addViolation")
    public String showAddForm(Model model) {
        model.addAttribute("violation", new violation());
        return "addViolation";
    }

    // ✅ SAVE VIOLATION (WITH IMAGE URL)
    @PostMapping("/saveViolation")
    public String saveViolation(
            @RequestParam("vehicleNumber") String vehicleNumber,
            @RequestParam("violationType") String violationType,
            @RequestParam("fineAmount") double fineAmount,
            @RequestParam("imageUrl") String imageUrl) {

        violation v = new violation();

        v.setVehicleNumber(vehicleNumber);
        v.setViolationType(violationType);
        v.setFineAmount(fineAmount);

        // 🔥 store image URL directly
        v.setImageName(imageUrl);

        repository.save(v);

        return "redirect:/admin";
    }

    // MARK PAID
    @GetMapping("/deleteViolation/{id}")
    public String deleteViolation(@PathVariable Long id) {

        violation v = repository.findById(id).orElse(null);

        if(v != null) {
            v.setPaid(true);
            repository.save(v);
        }

        return "redirect:/admin";
    }

    // CHECK PAGE
    @GetMapping("/checkFine")
    public String openCheckFinePage() {
        return "checkFine";
    }

    // CHECK FINES
    @PostMapping("/checkFine")
    public String checkFine(@RequestParam String vehicleNumber, Model model) {

        List<violation> list = repository.findByVehicleNumber(vehicleNumber);

        if(list == null || list.isEmpty()) {
            model.addAttribute("error", "No violations found for this vehicle");
            return "checkFine";
        }

        double total = 0;
        boolean isPaid = true;

        for(violation v : list){
            total += v.getFineAmount();
            if(!v.isPaid()) {
                isPaid = false;
            }
        }

        model.addAttribute("violations", list);
        model.addAttribute("totalFine", total);
        model.addAttribute("paid", isPaid);

        return "fineResult";
    }

    // PAYMENT CHECK
    @GetMapping("/checkPayment/{vehicleNumber}")
    public String checkPayment(@PathVariable String vehicleNumber, Model model) {

        List<violation> list = repository.findByVehicleNumber(vehicleNumber);

        for(violation v : list) {
            v.setPaid(true);
            repository.save(v);
        }

        double total = 0;
        for(violation v : list){
            total += v.getFineAmount();
        }

        model.addAttribute("violations", list);
        model.addAttribute("totalFine", total);
        model.addAttribute("paid", true);

        return "fineResult";
    }

    // RECEIPT
    @GetMapping("/downloadReceiptByVehicle/{vehicleNumber}")
    @ResponseBody
    public String downloadReceiptByVehicle(@PathVariable String vehicleNumber) {

        List<violation> list = repository.findByVehicleNumber(vehicleNumber);

        if(list.isEmpty()) {
            return "No data found";
        }

        if(!list.get(0).isPaid()) {
            return "Please complete payment first!";
        }

        double total = 0;
        StringBuilder receipt = new StringBuilder();

        receipt.append("----- TRAFFIC FINE RECEIPT -----\n\n");
        receipt.append("Vehicle Number: ").append(vehicleNumber).append("\n\n");

        for(violation v : list) {
            receipt.append("Violation: ").append(v.getViolationType()).append("\n");
            receipt.append("Fine: ₹").append(v.getFineAmount()).append("\n\n");
            total += v.getFineAmount();
        }

        receipt.append("Total Paid: ₹").append(total).append("\n\n");
        receipt.append("Status: PAID\n");
        receipt.append("Thank you!");

        return receipt.toString();
    }
}