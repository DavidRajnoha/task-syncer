package com.redhat.tasksyncer.dao.accessors.issue;

import com.redhat.tasksyncer.dao.entities.issues.AbstractIssue;
import com.redhat.tasksyncer.dao.enumerations.IssueType;
import com.redhat.tasksyncer.dao.repositories.AbstractIssueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AbstractIssueAccessor {

    AbstractIssueRepository issueRepository;

    @Autowired
    public AbstractIssueAccessor(AbstractIssueRepository issueRepository){
        this.issueRepository = issueRepository;
    }

    /**
     * Saves the issue, if already exists issue with same remoteId and repository, then updates the old one
     *
     * @param newIssue issue to be saved in the local database
     * @return saved issue with updated parameter (if new also with assigned id)
     */
    public AbstractIssue update(AbstractIssue newIssue) {
        AbstractIssue issue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(),
                newIssue.getRepository().getRepositoryName())
                .orElse(newIssue);

        // there exists such issue (the old issue has an id, therefor was saved, therefor exists in repository)
        if(issue.getId() != null) {
            issue.updateProperties(newIssue);
        }

        // Gets the child issues, if there are child issues present, the child issue is updated
        // recursive calling of the update function is used, it stops when we reach issue with no childIssues
        // the update of the innermost issues is therefor finished first
        // It has to be prohibited to save circular referencing issues - TODO: assert this will not happen

        // If the issues has subIssue(s) then updates the subIssue(s)
        Optional.ofNullable(newIssue.getChildIssues()).ifPresent(childIssueSet -> {
            // New copy of set so the concurrentModification exception wouldn't be thrown
            Set<AbstractIssue> copyChildIssuesSet = new HashSet<>(childIssueSet.values());

            for (AbstractIssue childIssue : copyChildIssuesSet) {
                issue.removeChildIssue(childIssue);
                issue.addChildIssue(update(childIssue));
            }
        });

        return issueRepository.save(issue);
    }

    /**
     * Saves a list of issues to a local database, if issue with the same remoteId and from same repository exists,
     * then updates the saved one
     *
     * @param issues to save
     */
    public void updateIssues(List<AbstractIssue> issues) {
        for(AbstractIssue i : issues) {
             this.update(i);
        }
    }


    /**
     * Query based on repository type
     * @param type of the repository
     * @return issues of that repository type
     */
    public Set<AbstractIssue> getIssuesByType(IssueType type){
        return issueRepository.findByIssueType(type);
    }

    /**
     *
     * @return All saved issues
     */
    public List<AbstractIssue> getAll(){
        return issueRepository.findAll();
    }

    /**
     * Get unique issue
     *
     * @param projectName name of the project the issue is part of
     * @param repoName name of the repository the issue is in
     * @param remoteIssueId id of the issue
     * @return issue based on the params above
     */
    public List<AbstractIssue> getIssue(String projectName, String repoName, String remoteIssueId){
        return issueRepository.findByRepository_Project_nameAndRemoteIssueIdAndRepository_repositoryName(projectName,
                remoteIssueId, repoName);
    }

    /**
     * Returns all issues associated with the project of projectName
     * @param projectName name of the project the returned issues are associated with
     * @return issues from a project
     */
    public List<AbstractIssue> getProject(String projectName) {
        return issueRepository.findByRepository_Project_name(projectName);
    }
}
