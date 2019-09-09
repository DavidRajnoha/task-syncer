package com.redhat.tasksyncer;

import com.redhat.tasksyncer.dao.entities.*;
import com.redhat.tasksyncer.dao.repositories.AbstractBoardRepository;
import com.redhat.tasksyncer.dao.repositories.AbstractRepositoryRepository;
import com.redhat.tasksyncer.dao.repositories.ProjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Filip Cap
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(ProjectRepository projectRepository, AbstractBoardRepository abstractBoardRepository, AbstractRepositoryRepository abstractRepositoryRepository, AbstractRepositoryRepository a) {
        return (args) -> {
            Project project = new Project();
            AbstractBoard abstractBoard = new TrelloBoard();

            project.setName("Testing");
            projectRepository.save(project);


            AbstractRepository oldAbstractRepository = new GitlabRepository();
            oldAbstractRepository.setProject(project);
            oldAbstractRepository.setRepositoryName("Repo");
            abstractRepositoryRepository.save(oldAbstractRepository);

            AbstractRepository abstractRepository = abstractRepositoryRepository.findByRepositoryNameAndProject_Id(oldAbstractRepository.getRepositoryName(), oldAbstractRepository.getProject().getId());


            System.out.println("Bullsikh");

        };
    }
}


