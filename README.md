# Camunda 7 FileService-connector
FileStorage Connector for Camunda 7

## Deploying the FileService Connector

```bash
git clone https://github.com/codot-pro/fs-connector-c7
cd fs-connector-c7
mvn clean install
```

## Run

You can create a **Maven** project and add a **dependency** to run the connector

    <dependency>
        <groupId>com.codot.camundaconnectors.filestorage</groupId>
        <artifactId>fs-connector</artifactId>
        <version>0.1.0-SNAPSHOT</version>
    </dependency>

After that you can run the project using the IDE or the command:
```bash
mvn exec:java -Dexec.mainClass="com.codot.camundaconnectors.filestorage.Main"
```

## Connector template

To add a template to Camunda Modeler, you need to open the application 
**modeler_root_folder/resources/element-templates** and put inside the template from 
**fs-connector-c7/element-templates** with the name **fileserviceconnector-c7.json**. 

Reload the application, and you will be able to assign a template.

To assign a template to a task, you need to press the SELECT button in the template section and select fileserviceconnector.

## Using

### Input data
First of all, you need to choose an operation. **The input data will depend on the selected operation.**

If you want to
- **upload** a file, you need to pass:
  - rest endpoint
  - file name
  - path relative to the connector's root folder
- **get** a file, you need to pass:
  - rest endpoint
  - file id
  - path relative to the connector's root folder
- **delete** a file, you need to pass:
  - rest endpoint
  - file id

Depending on the selected operation, only variables defined by the documentation will be used. You can leave the rest of the variables empty.

### Output data

Specify the NAME of the variable "Result Variable". The result of the operation of the connector will be written to it.
If the operation
- upload - uploaded file ID
- get - relative path where the file is saved
- delete - result of deletion ("Delete success")