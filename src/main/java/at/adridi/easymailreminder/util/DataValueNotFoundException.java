/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * Exception for DAO implementation services
 *
 * @author A.Dridi
 */
@Data
@Getter
@Setter
public class DataValueNotFoundException extends RuntimeException {

    private String errorMessage;

    public DataValueNotFoundException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }
}
