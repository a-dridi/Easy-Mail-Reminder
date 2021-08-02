/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.model;

import lombok.Data;

/**
 *
 * @author A.Dridi
 */
@Data
public class DeleteReminder {

    private Long reminderId;
    private String email;
}
