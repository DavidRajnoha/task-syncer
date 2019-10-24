package com.redhat.unit.accessorTests;

//public class JiraConnectionTest {
//
//    String jiraUri = "https://drajnoha.atlassian.net";
//    String jiraUserName = "drajnoha@seznam.cz";
//    String jiraPassword = "I0AECdr84uhABHboz94CA2F1";
//
//    @Test
//    public void GetIsssuesFromJira(){
//        JiraRestClient jiraRestClient = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI.create(jiraUri), jiraUserName, jiraPassword);
//
//
//        String newJQL = "";
//
//        Stream<Issue> issuesStream = StreamSupport.stream(jiraRestClient
//                .getSearchClient()
//                .searchJql(newJQL)
//                .claim()
//                .getIssues()
//                .spliterator(), false);
//        List<Issue> realList = issuesStream.collect(Collectors.toList());
//
//        System.out.println(realList);
//    }
//}
