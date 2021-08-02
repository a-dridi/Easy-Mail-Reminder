/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.util;

import at.adridi.easymailreminder.model.MailReminder;
import at.adridi.easymailreminder.service.MailReminderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * A runnable task to send a mail reminder in a schedule.
 *
 * @author A.Dridi
 */
public class MailReminderTask implements Runnable {

    @Autowired
    private MailReminderService mailReminderService;
    private MailReminder mailReminder;

   public MailReminderTask(MailReminder mailReminder) {
        this.mailReminder = mailReminder;
    }

    @Override
    public void run() {
        this.mailReminderService.sendEmailReminder(this.mailReminder);
    }

}
