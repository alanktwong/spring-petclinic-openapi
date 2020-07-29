package org.springframework.samples.petclinic.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.samples.petclinic.model.User;
import org.springframework.samples.petclinic.service.ApplicationTestConfig;
import org.springframework.samples.petclinic.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class UserRestControllerTests
{
    @MockBean
    private UserService userService;

    @Autowired
    private UserRestController userRestController;

    private MockMvc mockMvc;

    @Before
    public void initVets()
    {
        this.mockMvc = MockMvcBuilders.standaloneSetup(userRestController)
                .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUserSuccess() throws Exception
    {
        final var user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        user.addRole("OWNER_ADMIN");
        final var mapper = new ObjectMapper();
        final var newVetAsJSON = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/users/").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateUserError() throws Exception
    {
        final var user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEnabled(true);
        final var mapper = new ObjectMapper();
        final var newVetAsJSON = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/api/users/").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateNullUserUnauthorized() throws Exception
    {
        final var mapper = new ObjectMapper();
        final var newVetAsJSON = mapper.writeValueAsString(null);
        this.mockMvc.perform(post("/api/users/").content(newVetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest());
    }
}
