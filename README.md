# MS Search project

[MS search](https://ms-search.us)
Provide rapid MS/MS search within a 2-million-entry MS spectra database.

1. Integration of over 2 million MS & MS/MS spectra, sourced from the [MoNA online public database](https://mona.fiehnlab.ucdavis.edu/) .
2. MS/MS search with sub-second search performance.
3. Batch search, task management, mail notification, queue function.
4. OAuth 2.0 & Spring security.

### **Technique Overview**

- RDBMS
    - MySQL
    - Index, Unique key, B-tree
- Java
    - Spring boot
    - Spring security
    - JDBC
    - Java mail sender
    - OAuth 2.0
- Redis
    - Queue, List
    - Task queue, Mail queue
- Docker
- Nginx
    - Load balancer
    - Proxy
- AWS
    - S3
    - CDN
- Frontend
    - JavaScript
    - RWD
    - AJAX
    - SCSS
- Network
    - PVE (Proxmox Virtual Environment)
    - Firewall policy
    - NAT, Virtual IP


## Highlights
- Integration of over 2 million MS & MS/MS spectra.
- MS/MS search with sub-second search performance.
    ![MS/MS search](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/msmsSearch.gif?raw=true)

    ![MS/MS search2](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/msmsSearch2.gif?raw=true)



- Then you access special url & API, backend service will require authorization.
- Authorization and authentication using spring security OAuth 2.0.
    ![SprintSecurityOAuth2](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/SpringSecurityOAuth2.gif?raw=true)



- Upload file & submitted task to multiple ms/ms search.
- Task view page for task status check.
    ![mutipleMSMSsearch](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/multipleMSMS.gif?raw=true)



- Task status mail notice.
    ![mailService](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/mailService.gif?raw=true)



## Project architecture
- Integrated AWS, SQL, Redis, CloudFlare with spring boot based web and background server.
    ![Project architecture](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/Architecture.png?raw=true)


### MS/MS search workflow
- Utilizing SQL with indexing and query data limitation.
- Employing a Hash Map to match two nested arrays.
- Assessing array similarity through cosine similarity.
    ![MS/MS search workflow](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/MSMS_search_wrokflow.png?raw=true)


### Multiple MS/MS search workflow
- Task/Mail management using Redis queue & MySQL
- File management via AWS S3, CDN & MySQL
- Background consumer with task processor & mail server
    ![Multiple MS/MS search workflow](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/Multiple_MSMS_search_wrokflow.png?raw=true)


### Background multiple MS/MS search task service data pipeline
- Retrieving task info from task queue service.
- Updating task status in MySQL.
- Programming spectrum factory package to MS raw data read, preparation, and array matching.
- Utilizing mail queue service.
    ![Background multiple MS/MS search task service data pipeline](https://github.com/wun-yu-lin/MS_search_server/blob/main/demo/BackgroundServer.png?raw=true)
