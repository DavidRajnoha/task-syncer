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

    public AbstractIssue update(AbstractIssue newIssue) {
        AbstractIssue oldIssue = issueRepository.findByRemoteIssueIdAndRepository_repositoryName(newIssue.getRemoteIssueId(),
                newIssue.getRepository().getRepositoryName())
                .orElse(newIssue);

        if(oldIssue.getId() != null) {
            // there exists such issue (the old issue has an id, therefor was saved, therefor exists in repository)
            oldIssue.updateProperties(newIssue);
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
                oldIssue.removeChildIssue(childIssue);
                oldIssue.addChildIssue(update(childIssue));
            }
        });

        return issueRepository.save(oldIssue);
    }

    public void updateIssues(List<AbstractIssue> issues) {
        for(AbstractIssue i : issues) {
             this.update(i);
        }
    }


    public Set<AbstractIssue> getIssuesByType(IssueType type){
        return issueRepository.findByIssueType(type);
    }

    public List<AbstractIssue> getAll(){
        return issueRepository.findAll();
    }

    public List<AbstractIssue> getIssue(String projectName, String repoName, String remoteIssueId){
        return issueRepository.findByRepository_Project_nameAndRemoteIssueIdAndRepository_repositoryName(projectName,
                remoteIssueId, repoName);

    }


    public List<AbstractIssue> getProject(String projectName) {
        return issueRepository.findByRepository_Project_name(projectName);
    }
}
