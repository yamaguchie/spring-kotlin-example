package de.meshcloud.example.springkotlin.services;

import de.meshcloud.example.springkotlin.config.DatabaseInitConfig;
import de.meshcloud.example.springkotlin.model.Project;
import de.meshcloud.example.springkotlin.model.Resource;
import de.meshcloud.example.springkotlin.model.ResourceType;
import de.meshcloud.example.springkotlin.repositories.ProjectRepository;
import de.meshcloud.example.springkotlin.repositories.ResourceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

@Component
public class DatabaseInitService implements ApplicationRunner {

    private DatabaseInitConfig config;

    private ProjectRepository projectRepository;

    private ResourceRepository resourceRepository;

    public DatabaseInitService(
            DatabaseInitConfig config,
            ProjectRepository projectRepository,
            ResourceRepository resourceRepository
    ) {
        this.config = config;
        this.projectRepository = projectRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDB();
    }

    private void initDB() {
        for (int i = 1; i <= 5; i++) {
            Project project = createProject(i);

            Resource resourceCF = createCFResource(project);
            Resource resourceOS = createOSResource(project);
            project.getResources().addAll(Arrays.asList(resourceCF, resourceOS));

            projectRepository.save(project);
        }
    }

    private Project createProject(int i) {
        Project project = new Project();
        project.setName(optimizedProjectName() + " " + i);
        project.setDescription("Just a test project");
        return projectRepository.save(project);
    }

    public String optimizedProjectName() {
        if (config.getProjectName() == null || config.getProjectName().equals("Really Bad Name")) {
            return "Slightly better name!";
        }
        return config.getProjectName();
    }

    private Resource createOSResource(Project project) {
        Resource resource = new Resource(
                "OpenStack",
                "All the IaaS magic",
                ResourceType.OPENSTACK,
                Instant.now().minusMillis(10000000),
                Instant.now().minusMillis(5000),
                project
        );
        return resourceRepository.save(resource);
    }

    private Resource createCFResource(Project project) {
        Resource resource = new Resource(
                "Cloud Foundry",
                "Simply runs my apps",
                ResourceType.CLOUDFOUNDRY,
                Instant.now().minusMillis(12000000),
                Instant.now().minusMillis(6000),
                project
        );
        return resourceRepository.save(resource);
    }
}
