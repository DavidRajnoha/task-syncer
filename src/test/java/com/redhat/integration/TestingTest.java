package com.redhat.integration;

import com.redhat.tasksyncer.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
public class TestingTest {

    @Test
    public void contextLoads(){

    }
}
