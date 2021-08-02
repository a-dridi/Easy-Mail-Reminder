/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.repository;

import at.adridi.easymailreminder.model.MailReminder;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 *
 * @author A.Dridi
 */
@Repository
public interface MailReminderRepository extends JpaRepository<MailReminder, Long> {

    Optional<MailReminder> findByMailreminderId(Long mailreminderId);

    Optional<MailReminder> findByMailreminderIdAndEmail(Long mailreminderId, String emailAddress);
    
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM Mail_Reminder WHERE mailreminder_id=?1", nativeQuery = true)
    void deleteByMailreminderId(Long decisionId);

}
