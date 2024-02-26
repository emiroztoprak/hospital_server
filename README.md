# Spring Boot project using gRPC framework


Your task is to build a simple hospital system defined in grpc using Spring Boot with Hibernate and JPA that makes it possible to enter and store information about patient treatments.

------------------

Setup:

- Define the interface using grpc; use this library: https://github.com/yidongnan/grpc-spring-boot-starter. The documentation should provide all the information you need.
- Implement the project using Spring Boot with Hibernate and JPA. You can use the spring initializr (https://start.spring.io/) to generate your project. Use Gradle as build tool.
- We recommend using an H2 in-memory database for this task.

------------------
Model:

Add reasonable fields to the entities. No need to go overboard, but think of what information may be useful.

- Hospital
- Patient: A patient can be registered in multiple hospitals.

------------------

Service:

Define and implement the following operations.

- Create, modify, and delete hospitals. Patients should not be deleted if a hospital is deleted.
- Create, modify, and delete patients
- Register a patient in a hospital
- List all patients of a hospital
- List all hospitals in which a patient has been registered

------------------

Testing:

Write a few simple tests that demonstrate that operations work correctly. No need to be exhaustive.

------------------

Special request:

The director of the largest hospital in the country (around 500.000 patients per year) wants to have an overview of the average age of the patients who visited the hospital, divided by sex and per month, for the past 10 years. He wants to see this information immediately (<200ms). Describe in detail how you would fulfill this request.


