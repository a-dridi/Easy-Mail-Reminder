/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.service;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.time.Duration;

import at.adridi.easymailreminder.model.MailReminder;
import at.adridi.easymailreminder.repository.MailReminderRepository;
import at.adridi.easymailreminder.util.DataValueNotFoundException;
import at.adridi.easymailreminder.util.MailReminderTask;


/**
 *
 * @author A.Dridi
 */
@Service
@NoArgsConstructor
public class MailReminderService {

    @Autowired
    private MailReminderRepository mailReminderRepository;

    @Value("${mailreminder.host}")
    private String mailHost;
    public static String MailFrom;
    public static String MailPass;

    /**
     * Save new mail reminder.
     *
     * @param newMailReminder
     * @return saved mail reminder object. Null if not successful.
     */
    @Transactional
    public MailReminder save(MailReminder newMailReminder) {
        if (newMailReminder == null) {
            return null;
        }
        return this.mailReminderRepository.save(newMailReminder);
    }

    /**
     * Get certain Mail Reminder with the passed id and email address. Throws
     * DataValueNotFoundException if Mail Reminder is not available.
     *
     * @param id
     * @param emailAddress
     * @return
     */
    public MailReminder getMailReminderByIdAndEmail(Long id, String emailAddress) {
        return this.mailReminderRepository.findByMailreminderIdAndEmail(id, emailAddress)
                .orElseThrow(() -> new DataValueNotFoundException("Mail Reminder Does Not Exist"));
    }

    /**
     * Delete an existing Mail Reminder by id and email address.
     *
     * @param mailreminderId
     * @param emailAddress
     * @return true if successful
     */
    @Transactional
    public boolean deleteByIdAndEmail(Long mailreminderId, String emailAddress) {
        if (mailreminderId == null || mailreminderId == 0) {
            return false;
        }
        MailReminder mailReminder = null;
        try {
            mailReminder = this.getMailReminderByIdAndEmail(mailreminderId, emailAddress);
        } catch (DataValueNotFoundException e) {
        }

        if (mailReminder != null) {
            try {
                this.mailReminderRepository.delete(mailReminder);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Sent an email with the content in the passed object mailReminder.
     *
     * @param mailReminder
     * @return true when email was sent
     */
    public boolean sendEmailReminder(MailReminder mailReminder) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", this.mailHost);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MailReminderService.MailFrom, MailReminderService.MailPass);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MailFrom));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(mailReminder.getEmailAddress()));
            message.setSubject("[IMPORTANT] This is an email reminder!");
            String messageText = "This is an email to remind you about the following:\n\n<h2>"
                    + mailReminder.getTitle() + "</h2>\n\n" + "To cancel this reminder: 1. Go to the page where you created this reminder. 2. Click on Delete Reminder 3. Enter the reminder id and your email address"
                    + "\n\n - Reminder Id: " + mailReminder.getMailreminderId();
            message.setText(messageText);
            Transport.send(message);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * Create Mail Reminder that is run in the background according to its
     * defined values (frequency). When it's time for the reminder, then an
     * email is sent. UTC time is used.
     *
     * @param mailReminder The Mail reminder object that is used to
     * @return
     */
    public boolean createReminderProcess(MailReminder mailReminder) {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of("Europe/Reykjavík"));
        ScheduledExecutorService scheduledReminder = Executors.newScheduledThreadPool(1);

        Date reminderDate = mailReminder.getReminderDate();
        int hourAmount = Integer.parseInt((new SimpleDateFormat("hh")).format(reminderDate));
        int minutesAmount = Integer.parseInt((new SimpleDateFormat("MM")).format(reminderDate));
        ZonedDateTime mailReminderTime = currentTime.withHour(hourAmount).withMinute(minutesAmount).withMinute(minutesAmount);

        //0 - one time, 1 - daily, 2 - weekly, 3 - monthly
        switch (mailReminder.getFrequency()) {
            case 0:
                //Run schedule one time - after n milliseconds when the reminder date is reached. 
                long diffMilliseconds = mailReminder.getReminderDate().getTime() - ((currentTime.toEpochSecond() * 1000));
                scheduledReminder.schedule(new MailReminderTask(mailReminder), diffMilliseconds, TimeUnit.MILLISECONDS);
                break;
            case 1:
                //Run daily at reminder date time
                if (currentTime.compareTo(mailReminderTime) > 0) {
                    mailReminderTime = mailReminderTime.plusDays(1);
                }

                Duration duration = Duration.between(currentTime, mailReminderTime);
                long delayValue = duration.getSeconds();

                scheduledReminder.scheduleAtFixedRate(new MailReminderTask(mailReminder), delayValue, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
                break;
            case 2:
                //Run weekly at reminder date time
                if (currentTime.compareTo(mailReminderTime) > 0) {
                    mailReminderTime = mailReminderTime.plusDays(7);
                }

                duration = Duration.between(currentTime, mailReminderTime);
                delayValue = duration.getSeconds();

                scheduledReminder.scheduleAtFixedRate(new MailReminderTask(mailReminder), delayValue, (TimeUnit.DAYS.toSeconds(1) * 7), TimeUnit.SECONDS);
                break;
            case 3:
                //Run monthly at reminder date time
                if (currentTime.compareTo(mailReminderTime) > 0) {
                    mailReminderTime = mailReminderTime.plusDays(30);
                }

                duration = Duration.between(currentTime, mailReminderTime);
                delayValue = duration.getSeconds();

                scheduledReminder.scheduleAtFixedRate(new MailReminderTask(mailReminder), delayValue, (TimeUnit.DAYS.toSeconds(1) * 30), TimeUnit.SECONDS);
                break;
            default:
                diffMilliseconds = mailReminder.getReminderDate().getTime() - ((currentTime.toEpochSecond() * 1000));
                scheduledReminder.schedule(new MailReminderTask(mailReminder), diffMilliseconds, TimeUnit.MILLISECONDS);
        }
        return true;
    }

    @Value("${mailreminder.from}")
    public void setMailFrom(String mailFrom) {
        MailReminderService.MailFrom = mailFrom;
    }

    @Value("${mailreminder.pass}")
    public void setMailPass(String mailPass) {
        MailReminderService.MailPass = mailPass;
    }
}
