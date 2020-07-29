package org.springframework.samples.petclinic.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.samples.petclinic.service.ApplicationTestConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
public class RootRestControllerTest
{
    @Autowired
    private RootRestController rootRestController;

    private MockMvc mockMvc;

    @Before
    public void init()
    {
        this.mockMvc = MockMvcBuilders.standaloneSetup(rootRestController)
                .setControllerAdvice(new ExceptionControllerAdvice()).build();
    }

    @Test
    public void testRedirectToSwagger() throws Exception
    {
        this.mockMvc.perform(get("/").accept(MediaType.ALL_VALUE)).andExpect(status().isFound())
                .andExpect(redirectedUrl("/petclinic/swagger-ui.html"));
    }
}
