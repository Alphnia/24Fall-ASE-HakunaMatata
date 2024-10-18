# 24Fall-ASE-HakunaMatata

This is the GitHub repository for the team project of COMS 4156 Advanced Software Engineering.

## Building and Running a Local Instance
Please install the followings:
1. Maven 3.9.5: https://maven.apache.org/download.cgi Download and follow the installation instructions.
2. JDK 17: This project used JDK 17 for development: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
3. To build the project
```
$ pwd
COMS 4156 Advanced SE/Projects/4156-Miniproject-2024-Students-Java/IndividualProject
$ mvn -B package --file IndividualProject/pom.xml
```
4. Initialize the database: Do this before the first time you run the instance, or anytime you need to reset the database. Leave it running until you see “System Setup” in the terminal then terminate the program. This initializes the database and if you run into any trouble with the database this is the process you’ll need to perform.
```
// run this command for Java on MacOS
$ mvn spring-boot:run -Dspring-boot.run.arguments="setup"

// run this command for Java on Windows
$ mvn spring-boot:run -D"spring-boot.run.arguments=setup"
```
5. Run a local instance. Upon a successful render, you should be able to see "Welcome, in order to make an API call direct your browser or Postman to an endpoint This can be done using the following format: http:127.0.0.1:8080/endpoint?arg=value" on the screen.
```
mvn spring-boot:run
```

## Running a Cloud based Instance
You are able to play with our service deployed to Google Cloud Platform through this link: 
See the endpoint information in section **Endpoints**

## Endpoints
This section describes the endpoints that our service provides, as well as their inputs and outputs. 

#### GET /retrieveRoute
* Expected Input Parameters:
  * origin (String): The starting point.
  * destination (String): The endpoint.
* Expected Output: JSON containing route details or a message indicating the route's creation.
* Description: Retrieves an existing route or creates a new one if not found.
* Upon Success:
  * HTTP 200 Status Code is returned along with route details.
  * If a new route is created, "Successfully Created!" is returned.
* Upon Failure:
  * HTTP 404 Status Code if the route cannot be found or created.
  * HTTP 500 Status Code for unexpected errors.

#### GET /doesRouteExists
* Expected Input Parameters:
  * origin (String): The starting point.
  * destination (String): The endpoint.
* Expected Output: HTTP Status code indicating the presence of a route.
* Description: Checks if a route exists between the specified origin and destination.
* Upon Success:
  * HTTP 200 Status Code if the route exists.
* Upon Failure: HTTP 404 Status Code if the route does not exist, or HTTP 500 for unexpected errors.

#### GET /insertAnnoForTest
* Expected Input Parameters: None
* Expected Output: A message indicating the status of the insertion.
* Description: Inserts a test annotation into the Annotation collection in the MongoDB database.
* Upon Success: HTTP 200 Status Code is returned with "Insert complete" in the response body.
* Upon Failure:
  * HTTP 400 Status Code with "." if the insertion fails.
  * HTTP 500 Status Code for unexpected errors.

#### GET /checkAnno
* Expected Input Parameters:
  * routeID (String): The ID of the route.
  * userID (String): The ID of the user.
* Expected Output: HTTP Status code indicating the presence of the annotation.
* Description: Checks if a route and its associated annotation exist for a given user.
* Upon Success: HTTP 200 Status Code if the annotation exists.
* Upon Failure:
  * HTTP 404 Status Code if no route or annotation is found.
  * HTTP 500 Status Code for unexpected errors.

#### GET /users/name/{name}
* Expected Input Parameters:
  * name (String): The name of the user.
* Expected Output: A JSON object representing the user's details.
* Description: Retrieves a user's details by their name.
  * Upon Success: HTTP 200 Status Code is returned with the user's details.
  * Upon Failure: HTTP 404 Status Code if the user is not found.

#### GET /users/email/{email}
* Expected Input Parameters:
  * email (String): The email of the user.
* Expected Output: A JSON object representing the user's details.
* Description: Retrieves a user's details by their email.
  * Upon Success: HTTP 200 Status Code is returned with the user's details.
  * Upon Failure: HTTP 404 Status Code if the user is not found.

#### GET /users/{id}
* Expected Input Parameters:
  * id (ObjectId): The ID of the user.
* Expected Output: A JSON object representing the user's details.
* Description: Retrieves a user's details by their ID.
  * Upon Success: HTTP 200 Status Code is returned with the user's details.
  * Upon Failure: HTTP 404 Status Code if the user is not found.

#### POST /computeRoutes
* Expected Input Parameters:
  * A JSON object representing the route request details.
* Expected Output: A JSON response from the Google Directions API.
* Description: Sends a request to Google's Directions API to compute a route between specified points.
* Upon Success:
  * HTTP 200 Status Code is returned along with the response body containing route details.
* Upon Failure:
  * HTTP 500 Status Code is returned with "An unexpected error has occurred" in the response body.

#### POST /users
* Expected Input Parameters: A JSON object representing the user's details:
  * name (String): The user's name.
  * email (String): The user's email.
  * password (String): The user's password.
  * preferences (Object): The user's preferences.
  * geoLocation (Object): The user's geographic location data.
* Expected Output: A JSON object representing the created user.
* Description: Creates a new user and saves it to the database.
  * Upon Success: HTTP 200 Status Code is returned along with the created user's details.
  * Upon Failure: HTTP 500 Status Code with "An unexpected error has occurred" in the response body.

#### PATCH /createRoute
* Expected Input Parameters:
  * rawjson (String): JSON string of raw data from Google Maps.
  * origin (String): Starting point.
  * destination (String): Endpoint.
  * stoplist (String[]): List of route stops.
  * annotatedlist (String[]): List of annotations for the route.
* Expected Output: A message indicating the result of the route creation.
* Description: Creates a new route with the provided details.
* Upon Success: HTTP 200 Status Code with "New Route Created."
* Upon Failure:
  * HTTP 404 Status Code with "Insertion Failed" if the route creation fails.
  * HTTP 500 Status Code for unexpected errors.

#### PATCH /editRoute
* Expected Input Parameters:
  * routeID (String): The ID of the route to be edited.
  * userID (String): The ID of the user.
* Request Body: A list of maps representing the updated stop list for the route.
* Expected Output: A message indicating the result of the update or insertion.
* Description: Edits an existing route's stop list if the route exists, or inserts a new annotation if it does not.
* Upon Success: HTTP 200 Status Code is returned with either "Update success" or "Insert success" in the response body.
* Upon Failure:
  * HTTP 400 Status Code if the update or insertion fails.
  * HTTP 500 Status Code for unexpected errors.

#### PUT /users/{id}
* Expected Input Parameters:
  * id (ObjectId): The ID of the user.
* Request Body: A JSON object containing the user's updated details:
* name (String): The user's updated name.
* email (String): The user's updated email.
* password (String): The user's updated password.
* preferences (Object): The updated preferences.
* geoLocation (Object): The updated geographic location data.
* Expected Output: A JSON object representing the updated user.
* Description: Updates the user's details based on the provided ID.
  * Upon Success: HTTP 200 Status Code is returned along with the updated user's details.
  * Upon Failure: HTTP 404 Status Code if the user is not found.

#### DELETE /deleteRoute
* Expected Input Parameters:
  * origin (String): The starting point.
  * destination (String): The endpoint.
* Expected Output: A message indicating the result of the deletion.
* Description: Deletes the specified route from the database.
* Upon Success: HTTP 200 Status Code with "Successfully deleted."
* Upon Failure:
  * HTTP 404 Status Code with "Failed" if the route cannot be deleted.
  * HTTP 500 Status Code for unexpected errors.
 
#### DELETE /deleteAnno
* Expected Input Parameters:
  * routeID (String): The ID of the route.
  * userID (String): The ID of the user.
* Expected Output: A message indicating the status of the deletion.
* Description: Deletes an annotation for a specified route and user.
* Upon Success: HTTP 200 Status Code is returned with "Delete success" in the response body.
* Upon Failure:
  * HTTP 404 Status Code with "Annotation not found" if no annotation exists for the specified route and user.
  * HTTP 400 Status Code if the deletion fails.
  * HTTP 500 Status Code for unexpected errors.

#### DELETE /users/{id}
* Expected Input Parameters:
  * id (ObjectId): The ID of the user.
* Expected Output: None
* Description: Deletes the user specified by the ID.
  * Upon Success: HTTP 200 Status Code is returned if the deletion is successful.
  * Upon Failure: HTTP 404 Status Code if the user is not found.
 
## APIs Test using Postman
Make API calls through Postman: https:// </br>
For local machine: http:127.0.0.1:8080/endpoint?arg=value </br>
For GCP: https:///endpoint?arg=value

## Style Checking
Use the tool "checkstyle" to check the style of the code and generate style checking reports.</br>
```
mvn checkstyle:check
```
![WeChat7186f666dfcd0e8dd72e6ac1350cbf9b](https://github.com/user-attachments/assets/7abcaad5-96a2-422a-ab65-d6f67d2db30c)

## Branch Coverage Reporting
Use JaCoCo to run unit tests and analyze branch coverage.
```
mvn test jacoco:report
```
![WeChat4c418cd52c0e4d0fc25b5c9e1c71ed7d](https://github.com/user-attachments/assets/61226a5b-41f2-43a9-966d-26da09616752)

## Static Code Analysis Using PMD
I used the QuickStart guide on https://pmd.github.io/

```
// Where I downloaded the source file
$ pwd
  
$ wget https://github.com/pmd/pmd/releases/download/pmd_releases%2F7.5.0/pmd-dist-7.5.0-bin.zip
$ unzip pmd-dist-7.5.0-bin.zip
$ alias pmd="$HOME/pmd-bin-7.5.0/bin/pmd"
$ pmd check -d  -R rulesets/java/quickstart.xml -f text
```
This command also works fine with my project.

```
$ mvn pmd:check
```
![WeChatb6e2c5eba705d2ebdca245cb342de5bd](https://github.com/user-attachments/assets/f89a9bde-cecb-487d-9b4e-930d7aad6073)

## Tools used for development
The following are tools and technologies used in this project:
* Maven Package Manager
* GitHub Actions CI
  *  Please check the .github/workflows/maven.yml
  *  It runs Maven build and stylecheck, build and run unit tests
* Checkstyle
  *  This project used Checkstyle for code reporting.
* PMD
  *  Static Code Analysis
* JUnit
  *  Used for Unit tests, this is part of CI pipeline.
* JaCoCo
  *  Used for generating code coverage reports
* Postman
  * Used for testing API endpoints  
