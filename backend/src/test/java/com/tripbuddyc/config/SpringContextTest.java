package com.tripbuddyc.config;

import com.tripbuddyc.config.jwt.JwtUtils;
import com.tripbuddyc.tripbrowser.BrowsingResults;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpringContextTest {

    @Test
    void getBean_WhenExists() {
        JwtUtils jwtUtils = SpringContext.getBean(JwtUtils.class);

        assertNotNull(jwtUtils);
    }

    @Test
    void getBean_WhenDoesNotExist() {
        BrowsingResults browsingResults = SpringContext.getBean(BrowsingResults.class);

        assertNull(browsingResults);
    }
}