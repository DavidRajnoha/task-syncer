package com.redhat.accessorTests;

import com.redhat.tasksyncer.Application;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ComponentScan("com.redhat.tasksyncer")
@SpringBootTest(classes = Application.class)
public class GitlabRepositoryAccessorTest {

    @Before
    public void setUp(){

    }
}
