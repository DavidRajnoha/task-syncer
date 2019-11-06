package com.redhat.unit.entitiesTests;

//public class RepositoryAccessorTests {
//
//    @Autowired
//    private AbstractRepositoryRepository repositoryRepository;
//    @Autowired
//    private AbstractIssueRepository issueRepository;
//
//    private AbstractRepository githubRepository;
//    private AbstractRepository gitlabRepository;
//    private AbstractRepository unsuportedRepository;
//
//    private RepositoryAccessor repositoryAccessor;
//
//    private String firstCredential = "login";
//    private String secondCredential = "password";
//    private String repoName = "repoName";
//    private String repoNamespace = "repoNamespace";
//
//
//
//    @Before
//    public void setUp() throws RepositoryTypeNotSupportedException, IOException {
//        githubRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("github",
//                firstCredential, secondCredential, repoName, repoNamespace);
//
//        gitlabRepository = AbstractRepository.newInstanceOfTypeWithCredentialsAndRepoNameAndNamespace("gitlab",
//                firstCredential, secondCredential, repoName, repoNamespace);
//
//        unsuportedRepository = new UnsuportedRepository();
//
//    }
//
//    @Test
//    public void whenPassingGitHubRepository_thenGitHubRepositoryAccessorShouldBeCreated() throws IOException, RepositoryTypeNotSupportedException {
//        repositoryAccessor = RepositoryAccessor.getInstance(githubRepository, repositoryRepository, issueRepository);
//        assertThat(repositoryAccessor).isInstanceOf(GithubRepositoryAccessor.class);
//    }
//
//    @Test
//    public void whenPassingGitlabRepository_thenGitlabRepositoryAccessorShouldBeCreated() throws IOException, RepositoryTypeNotSupportedException {
//        repositoryAccessor = RepositoryAccessor.getInstance(gitlabRepository, repositoryRepository, issueRepository);
//        assertThat(repositoryAccessor).isInstanceOf(GitlabRepositoryAccessor.class);
//    }
//
//    @Test
//    public void whenPassingUnsupportedRepository_thenExceptionIsThrown(){
//        assertThatThrownBy(() ->
//                RepositoryAccessor.getInstance(unsuportedRepository, repositoryRepository, issueRepository))
//                .isInstanceOf(RepositoryTypeNotSupportedException.class);
//    }
//}
//
//class UnsuportedRepository extends AbstractRepository{
//    UnsuportedRepository(){
//        super();
//    }
//}
