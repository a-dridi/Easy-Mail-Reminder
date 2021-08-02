/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.adridi.easymailreminder.model.MailReminder;
import at.adridi.easymailreminder.service.MailReminderService;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author A.Dridi
 */
@Controller
public class MailReminderController {

    private MailReminderService mailReminderService;

    @Autowired
    public MailReminderController(MailReminderService mailReminderService) {
        this.mailReminderService = mailReminderService;
    }
    
    @GetMapping("/")
    public String reminderForm(Model model) {
        MailReminder mailReminder = new MailReminder();
        model.addAttribute("mailReminder", mailReminder);
        return "index";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String createReminder(@ModelAttribute("mailReminder") MailReminder mailReminder) {
        this.mailReminderService.save(mailReminder);
        this.mailReminderService.createReminderProcess(mailReminder);
        return "created_success";
    }

    @GetMapping("/delete")
    public String deleteForm() {
        return "delete";
    }

    @GetMapping("/deleteReminder/{reminderId}/{email}")
    public String deleteReminder(@PathVariable Long reminderId, @PathVariable String email) {
        if (this.mailReminderService.deleteByIdAndEmail(reminderId, email)) {
            return "deleted_success";
        } else {
            return "deleted_fail";
        }
    }

}
