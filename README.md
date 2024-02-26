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


To calculate the average with each request is not feasible. Loading up all the data and doing the calculations would definitely take more than 200 ms. To overcome this problem, we can create a new immutable (since entries need not to be changed) entity named AverageMonthlyAge, which has the following fields:

- int year
- int month
- String sex
- double averageAge

We can calculate the monthly average of different sexes using either "@Scheduled" (org.springframework.scheduling.annotation.Scheduled) of Spring or using scripts that run SQL on database to load the monthly data separated by sexes and save the results as a AverageMonthlyAge object after calculating the average age. Loading up 500000 patient objects could be tricky since it can cause memory overflow especially if the system is already busy with daily traffic. Here we need to make sure that the Patient objects have FetchType set as "Lazy". Because if not, with every Patient entry, we would also load into memory their linked hospital objects as well. This would greatly increase the memory usage by a large amount. 

If the memory bottleneck is still an issue, the operation can be done more frequently, e.g. biweekly or weekly. This would also provide a more up-to-date data compared to monthly calculations. In the long run, a system-level solution could also be implemented, such as a data warehouse. This would allow the data marts to load up the data for calculations while the system is still ongoing as if nothing is happening in the background. These systems create daily calculations similar to this special request so that the reports and the required analysis can go smoothly. This would not be needed for our case because the Patient object is very small in our system with only 6 simple fields.

So in summary, a monthly scheduled operation to calculate the average age divided by sex and month would be a very feasible solution for this problem. Reading the results from the AverageMonthlyAge table would be very simple and fast (<200ms). 

