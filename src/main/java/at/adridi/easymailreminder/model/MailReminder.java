/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.model;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

/**
 *
 * @author A.Dridi
 */
@Entity
@NoArgsConstructor
@Getter
@Setter
@Data
public class MailReminder {

    @Id
    @SequenceGenerator(name = "pk_mail_reminder", sequenceName = "mail_reminder_id_seq", allocationSize = 1)
    private Long mailreminderId;

    private String title;

    @DateTimeFormat(pattern="dd.MM.yyyy")
    private Date reminderDate = new Date();
    
    //0 - one time, 1 - daily, 2 - weekly, 3 - monthly
    private int frequency;
    
    private String emailAddress;
    
    
}
