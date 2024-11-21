# 2024-fall-cs-122b-team-101

<!-- ABOUT THE PROJECT -->
## About the Project



### Demonstration

[Project 1 Video Link](https://www.youtube.com/watch?v=LL-pH1_uBWY&ab_channel=KristenBae)\
[Project 2 Video Link](https://youtu.be/31G4-Dydruw)\
[Project 3 Video Link](https://www.youtube.com/watch?v=E2viHgW8m_0&ab_channel=KristenBae)\
[Project 4 Video Link]()



### Member Contributions

#### Project 1:
● Jacky: repository setup, Movie List Page, Single Movie Page, Single Star Page\
● Kristen: AWS instance setup, web application deployment on AWS instance, Single Star Page, video demonstration

#### Project 2:
● Jacky: Login Page, Main Page, extend Movie List Page, extend Single Movie/Star Page, update jump functionality, Shopping Cart Page UI, Payment Page, Confirmation Page UI\
● Kristen: Shopping Cart Page functionality, Add-to-Cart feature on Movie List/Single Movie Page, Confirmation Page functionality, video demonstration

#### Project 3:
● Jacky: reCAPTCHA, PreparedStatement, add customers/employees encrypted password verification, Metadata Page, Add Star/Movie Page, add_movie stored procedure\
● Kristen: Add HTTPS, moviedb customers/employees password encryption, SAX XML parser, video demonstration

#### Project 4:
● Jacky: full-text search, autocomplete, JDBC connection pooling,\
● Kristen: full-text search,



### Substring Matching
Substring matching is used in conjunction with the LIKE SQL predicate in MovieListServlet.java to\
fetch records from moviedb with m.title, m.director, and s.name that have the retrieved parameters\
title, director, and name at any position, respectively. Next, these query excerpts are appended to\
the StringBuilder object, queryBuilder, which contains an incomplete SQL query.



### PreparedStatement Usage
The following .java files contain a PreparedStatement:

● ConfirmationServlet\
● DashboardLoginServlet\
● LoginServlet\
● MainServlet\
● MovieListServlet\
● PaymentServlet\
● ShoppingCartServlet\
● SingleMovieServlet\
● SingleStarServlet



### Performance Tuning
To maximize the efficiency of our SAX XML parser, we integrated the following:

1. Writing parsed movie attributes into .txt files during parsing, which contain stars, stars_in_movies, movies, genres, and genres_into_movies. The .txt files are efficiently loaded into temporary SQL tables.
2. Temporary tables to store parsed data from .txt files, which are then inserted into moviedb tables. Next, the temporary tables are dropped to eliminate residual data and possible duplicate insertions.

We found that these techniques vastly decreased the runtime of our SAX XML parser, as well as the time it took to load the .txt file data into our moviedb database.



### JDBC Connection Pooling
JDBC Connection Pooling and PreparedStatement is used in the following Java servlets:

● [src/AddMovieServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/AddMovieServlet.java)\
● [src/AddStarServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/AddStarServlet.java)\
● [src/AutocompleteServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/AutocompleteServlet.java)\
● [src/ConfirmationServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/ConfirmationServlet.java)\
● [src/DashboardLoginServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/DashboardLoginServlet.java)\
● [src/LoginServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/LoginServlet.java)\
● [src/MainServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/MainServlet.java)\
● [src/MetadataServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/MetadataServlet.java)\
● [src/MovieListServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/MovieListServlet.java)\
● [src/PaymentServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/PaymentServlet.java)\
● [src/ShoppingCartServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/ShoppingCartServlet.java)\
● [src/SingleMovieServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/SingleMovieServlet.java)\
● [src/SingleStarServlet.java](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/src/SingleStarServlet.java)\
● [WebContent/META-INF/context.xml](https://github.com/uci-jherold2-fall24-cs122b/2024-fall-cs-122b-team-101/blob/main/WebContent/META-INF/context.xml)

JDBC Connection Pooling is utilized in these servlets by initially defining a DataSource instance by allocating a set of connections from context.xml. 
When a connection is retrieved, an already-created connection is taken from the pool. The close() method is used after each servlet completes operations using the connection,
which returns it to the pool for future usage. As a result, establishing and closing connections has a reduced resource cost and an improved time-efficiency.



### Built With

* [![Java][java.com]][Java-url]
* [![JavaScript][javascript.com]][JavaScript-url]
* [![HTML][html.com]][HTML-url]
* [![CSS][css.com]][CSS-url]



<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[java.com]: https://img.shields.io/badge/logo-java-blue?logo=java
[Java-url]: https://www.java.com/en/
[javascript.com]: https://img.shields.io/badge/logo-javascript-blue?logo=javascript
[JavaScript-url]: https://www.javascript.com/
[html.com]: https://img.shields.io/badge/logo-html-blue?logo=html
[HTML-url]: https://www.w3schools.com/html/
[css.com]: https://img.shields.io/badge/logo-css-blue?logo=css
[CSS-url]: https://www.w3.org/Style/CSS/Overview.en.html
