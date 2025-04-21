Nexus Middleware Lite
=====================

Nexus Middleware Lite is an open source project developed by Muhammad Tellesy as part of the Nexus Middleware and Neptune Family of Banking solution. This is a lite and free version of Nexus Middleware designed to assist banks in complying with the Central Bank of Libya requirements.

Overview
--------

The primary goal of this project is to provide a robust endpoint for banks that allows them to:
- Enquire about the status of LYPay transactions.
- Support the clearing process.
- Minimize issues related to chargebacks.
- Enhance the quality and reliability of LYPay, the Instant Payment system.

Features
--------

- Open Source: Fully open for review, contribution, and community collaboration.
- Endpoint Integration: Provides a RESTful interface compliant with Central Bank of Libya standards.
- Transaction Enquiry: Enables efficient tracking and status enquiry for LYPay transactions.
- Clearing Process Support: Streamlines the clearing process for better operational efficiency.
- Chargeback Minimization: Designed to reduce discrepancies and issues leading to chargebacks.

Technology Stack
----------------

- Framework: Jakarta EE with Jakarta imports
- Persistence: Spring Data JPA
- Web Framework: Spring MVC
- Utility: Lombok
- Java Version: Java SDK 17

Getting Started
---------------

Prerequisites:
- Java SDK 17 installed on your development machine.
- Gradle installed (or use the Gradle wrapper provided).
- An IDE such as IntelliJ IDEA 2025.1 Ultimate Edition on macOS Sonoma (aarch64).

Installation:

1. Clone the repository:
   git clone https://github.com/your-repo/nexus-middleware-lite.git
   cd nexus-middleware-lite

2. Build the project using Gradle:

   You can build the project as either a JAR or a WAR.

    - To build as a JAR file:
      ./gradlew clean build -PpackagingType=jar

    - To build as a WAR file:
      ./gradlew clean build -PpackagingType=war

3. Running the application:

    - Running the JAR:
      java -jar build/libs/nexus-middleware-lite-0.1.0.jar

    - Running the WAR:
      The WAR can be deployed to your preferred servlet container, or if using Spring Boot’s embedded server, run:
      java -jar build/libs/nexus-middleware-lite-0.1.0.war

Configuration
-------------

Before running the application, update the application.yml file located in the src/main/resources directory with your database connection details and other configuration parameters.

Example application.yml:

spring:
application:
name: nexus-lite
datasource:
hikari:
jdbc-url:              <DATABASE-CONNECTION-STRING>
username:              <DB USER>
password:              <DB PASSWORD>
driver-class-name:     oracle.jdbc.OracleDriver
maximum-pool-size:     10
minimum-idle:          2
connection-timeout:    19000
idle-timeout:          450000
max-lifetime:          800000
data-source-properties:
defaultRowPrefetch:          100
implicitCachingEnabled:      true
fastConnectionFailoverEnabled: true
implicitStatementCacheSize:  50

server:
port: 7003

logging:
level:
root: info

Replace <DATABASE-CONNECTION-STRING>, <DB USER>, and <DB PASSWORD> with your actual database details.

API Example
-----------

The application exposes endpoints to check the status of LYPay transactions. For example, to look up a transaction, send a POST request to:

POST /api/v1/transactions/lookup

Sample Request:

{
"userId": "SWITCHUSER",
"rrn": "123456789012",
"stan": "123456",
"txnAmt": "1000.00",
"termId": "123456",
"setlDate": "31-12-2023"
}

Sample Response:

{
"code": "R1",
"message": "Transaction is Failed",
"transactionType": "CREDIT"
}

Note: The actual response depends on the transaction details and its status.

Contributing
------------

Contributions are welcome! If you'd like to contribute, please fork the repository and submit your pull requests. For larger changes, please open an issue to discuss what you would like to change.

License
-------

This project is licensed under the MIT License.

About the Author
----------------

Muhammad Tellesy developed this project as part of his work on the Nexus Middleware and Neptune Family of Banking solution. This lite version aims to provide banks with a cost-effective and efficient tool to meet the regulatory requirements of the Central Bank of Libya while ensuring high quality and reliability of LYPay services.

For any questions or further information, please open an issue on GitHub or contact the author directly via the repository.

Special Thanks
--------------

A special thank you to:
- **Naser Alagory** from Wahda Bank, for sharing the project specifications.
- **Esra Endisha**, Product Owner of LYPay, for her support and assistance with Flexcube CBS.
Happy coding!
