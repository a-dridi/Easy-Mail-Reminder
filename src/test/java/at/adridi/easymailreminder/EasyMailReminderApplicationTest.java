/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.adridi.easymailreminder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import at.adridi.easymailreminder.controller.MailReminderController;
import at.adridi.easymailreminder.service.MailReminderService;

/**
 * Test Cases for Mail Reminder Creation and Management
 *
 * @author A.Dridi
 */
@SpringBootTest
public class EasyMailReminderApplicationTest {

    private MockMvc mockMvc;

    @MockBean
    private MailReminderService mailReminderService;

    private MailReminderController mailReminderController;

    @BeforeEach
    public void setUpTests() {
        this.mailReminderController = new MailReminderController(mailReminderService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.mailReminderController).build();
    }

    @Test
    public void accessReminderForm() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect((redirectedUrl("index")));
    }
}
