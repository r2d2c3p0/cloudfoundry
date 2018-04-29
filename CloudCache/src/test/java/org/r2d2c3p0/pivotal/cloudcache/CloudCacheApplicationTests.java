package org.r2d2c3p0.pivotal.cloudcache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CloudCacheApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    /*@Test
    public void homePage() throws Exception {
        mockMvc.perform(get("/index.html"))
                .andExpect(content().string(containsString("Home page")));
    }
    */

    @Test
    public void teamNoToken() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    public void teamWithUser() throws Exception {
        mockMvc.perform(post("/team").param("name", "Dallas Cowboys"))
                .andExpect(content().string(containsString("Team selected: Dallas Cowboys!")));
    }

    @Test
    public void teamPostLogin() throws Exception {
        mockMvc.perform(get("/").sessionAttr("name", "NE Patriots"))
                .andExpect(content().string(containsString("Team selected: NE Patriots")));
    }

}