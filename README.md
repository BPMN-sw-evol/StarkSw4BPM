# StarkSw4BPM
Business process-based software generator. This generator starts from a typical BPMN model and generates the software skeleton required for executing it on a Camunda engine embedded in Spring Boot.

## Technologies Used

- **Java 17**
- **Spring Boot**
- **Streamlit**
- **H2 Database**
- **Maven**

## Prerequisites

Make sure you have the following installed:

- [Git](https://git-scm.com/)
- [Java JDK 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [Python 3.9+](https://www.python.org/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended IDE for Java development)

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/BPMN-sw-evol/StarkSw4BPM.git
cd StarkSw4BPMN
```

### 2. Build the Spring Boot application

#### Enter the `bpmGenerator` directory

```bash
cd bpmGenerator
```

##### Run the Spring Boot application

```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at: [http://localhost:8080](http://localhost:8080)

### 3. Run the Streamlit UI

#### Open a new terminal at the project root and go to the `/UI` directory

```bash
cd UI
```

#### Install project dependencies (Only the first time)

```bash
pip install -r requirements.txt
```

#### Run the UI application

```bash
streamlit run app.py
```