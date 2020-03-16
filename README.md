# task-syncer

Downloads issues from github, gitlab, trello and jira and stores them. It is possible to
push the stored issues to trello board, or access them vie GET endpoints. 

There is also implemented import to polarion - tested and used only from trello with specific
board structure.

Polarion Import:
----------------
**Step 1:**

Create project with correct column names.
The names must only contain valid polarion states.
(skipped, error, passed, failure)

Endpoint:

/v1/project/{projectName}/create\?columnNames=<COLUMN_NAMES>

Example curl:

curl -X POST localhost:9000/v1/project/2.8_ER2/create\?columnNames=failure,skipped,error,passed

**Step 2:**

Connect the trello board you wish to import to polarion.
For the column mapping is for now used
the id of the trello lists.

To find the id of a trello list, open a card in the list, add
.json to the url and find the first occurence
of an idList attribute.

Endpoint:

/v1/service/trello/project/{projectName}/connect/namespace/{trelloBoardId}

Requested parameters: 
* firstLoginCredential - trelloApplicationKey
* secondLoginCredential - trelloAccessToken 
* columnNames - same names as in step one in matching order with mapped columns,
 duplicates are allowed,
* columnMapping - the id of the columns to map to columnNames

Example curl:

curl -X POST localhost:9000/v1/service/trello/project/2.8_ER2/connect/namespace/
5e4fb758c02ec21958666bbd\?firstLoginCredential={trelloApplicationKey}
'&'secondLoginCredential={trelloAccessToken}
'&'columnNames=skipped,skipped,error,skipped,passed,failure
'&'columnMapping=5e4fb758c02ec21958666bbe,5e4fb758c02ec21958666bbf
,5e4fb758c02ec21958666bc0,5e4fbb552637932289421b53,5e4fb758c02ec21958666bc1
,5e4fb758c02ec21958666bc2


**Step Three**

Call the polarion import endpint to start the import to polarion.

Endpoint:

/v1/project/{projectName}/polarion/{polarionProjectId}

Requested params:
* userName - kerberos login
* password - kerberos password
* testCycle - the name of the testRun

Example curl:
curl -X POST localhost:9000/v1/project/2.8_ER2/polarion/3Scale
\?url=https://polarion-devel.engineering.redhat.com/polarion/import'&'
username={kerberosUserName}'&'password={kerberosPassword}'&'testcycle=2_8_ER_2

**Warning** - the import takes long time - it is necessary to let polarion
importer time to process the xml to ensure smooth linking of requirements, test cases
and results