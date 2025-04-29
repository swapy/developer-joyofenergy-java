# Welcome to PowerDale

PowerDale is a small town with around 100 residents. Most houses have a smart meter installed that can save and send
information about how much power a house is using at a given point in time.

There are three major providers of energy in town that charge different amounts for the power they supply.

- _Dr Evil's Dark Energy_
- _The Green Eco_
- _Power for Everyone_

# Introducing JOI Energy

JOI Energy is a new start-up in the energy industry. Rather than selling energy they want to differentiate themselves
from the market by recording their customers' energy usage from their smart meters and recommending the best supplier to
meet their needs.

You have been placed into their development team, whose current goal it is to produce an API which their customers and
smart meters will interact with.

Unfortunately, two members of the team are on annual leave, and another one has called in sick! You are left with
another ThoughtWorker to progress with the current user stories on the story wall. This is your chance to make an impact
on the business, improve the code base and deliver value.

## Users

To trial the new JOI software 5 people from the JOI accounts team have agreed to test the service and share their energy
data.

| User    | Smart Meter ID  | Power Supplier        |
| ------- | --------------- | --------------------- |
| Sarah   | `smart-meter-0` | Dr Evil's Dark Energy |
| Peter   | `smart-meter-1` | The Green Eco         |
| Charlie | `smart-meter-2` | Dr Evil's Dark Energy |
| Andrea  | `smart-meter-3` | Power for Everyone    |
| Alex    | `smart-meter-4` | The Green Eco         |

These values are used in the code and in the following examples too.

## Requirements

The project requires [Java 21](https://adoptium.net/en-GB/). If you have multiple JVMs on your machine, you might want to consider
using a tool such as [sdkman](https://sdkman.io/) or [mise](https://mise.jdx.dev/) (amongst others) to handle switching between versions.

The project makes use of Gradle and uses the [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html), which means you don't need Gradle
installed.

## Useful Gradle commands

### List all Gradle tasks

List all the tasks that Gradle can run, such as `build` and `test`.

```console
$ ./gradlew tasks
```

### Build the project

Compiles the project, runs the test and then creates an executable JAR file

```console
$ ./gradlew build
```

Run the application using Java and the executable JAR file produced by the Gradle `build` task. The application will be
listening on port `8080`.

```console
$ java -jar build/libs/developer-joyofenergy-java.jar
```

### Run the tests

There are two types of tests, the unit tests and the functional tests. These can be executed as follows.

- Run unit tests only

  ```console
  $ ./gradlew test
  ```

- Run functional tests only

  ```console
  $ ./gradlew functionalTest
  ```

- Run both unit and functional tests

  ```console
  $ ./gradlew check
  ```

### Run the application

Run the application which will be listening on port `8080`.

```console
$ ./gradlew bootRun
```

## API

Below is a list of API endpoints with their respective input and output. Please note that the application needs to be
running for the following endpoints to work. For more information about how to run the application, please refer
to [run the application](#run-the-application) section above.

### Store Readings

Endpoint

```text
POST /readings/store
```

Example of body

```json
{
  "smartMeterId": <smartMeterId>,
  "electricityReadings": [
    {
      "time": <time>,
      "reading": <reading>
    }
  ]
}
```

Parameters

| Parameter      | Description                                                    |
|----------------|----------------------------------------------------------------|
| `smartMeterId` | One of the smart meters ids listed above.                      |
| `time`         | The date/time (as epoch seconds) when the _reading_ was taken. |
| `reading`      | The power consumption in `kW` at the _time_ of the reading.    |

Example readings

| Date & Time (`GMT/UTC`) | Epoch timestamp (seconds) | Power Reading (`kW`) |
|-------------------------|---------------------------|----------------------|
| `2020-11-29 8:00`       | 1606636800                | 0.0503               |
| `2020-11-29 8:01`       | 1606636860                | 0.0621               |
| `2020-11-29 8:02`       | 1606636920                | 0.0222               |
| `2020-11-29 8:03`       | 1606636980                | 0.0423               |
| `2020-11-29 8:04`       | 1606637040                | 0.0191               |

In the above example, the smart meter sampled power readings, in `kW`, every minute. Note that the reading is in `kW` and
not `kWH`, which means that each reading represents the _power_ consumption at the reading time. If no power is being consumed
at the time of reading, then the reading value will be `0`. Given that `0` may introduce new challenges, we can assume
that there is always some consumption, and we will never have a `0` reading value. These readings are then sent by the
smart meter to the application using the HTTP API. 

There is a service in the application that calculates the energy used in `kWH` from these power readings over time.

The following POST request, is an example request using `curl`, sends the readings shown in the table above.

```console
$ curl \
  -X POST \
  -H "Content-Type: application/json" \
  "http://localhost:8080/readings/store" \
  -d '{"smartMeterId":"smart-meter-0","electricityReadings":[{"time":1606636800,"reading":0.0503},{"time":1606636860,"reading":0.0621},{"time":1606636920,"reading":0.0222},{"time":1606636980,"reading":0.0423},{"time":1606637040,"reading":0.0191}]}'
```

The above command does not return anything beyond the HTTP 200 status.

### Get Stored Readings

Endpoint:

```text
GET /readings/read/<smartMeterId>
```

Parameters:

| Parameter      | Description                              |
| -------------- |------------------------------------------|
| `smartMeterId` | One of the smart meter ids listed above. |

Retrieving readings using `curl`:

```console
$ curl "http://localhost:8080/readings/read/smart-meter-0"
```

Example output:

```json
[
  {
    "time": "2020-11-29T08:00:00Z",
    "reading": 0.0503
  },
  {
    "time": "2020-11-29T08:01:00Z",
    "reading": 0.0621
  },
  {
    "time": "2020-11-29T08:02:00Z",
    "reading": 0.0222
  },
  {
    "time": "2020-11-29T08:03:00Z",
    "reading": 0.0423
  },
  {
    "time": "2020-11-29T08:04:00Z",
    "reading": 0.0191
  }
]
```

### View Current Price Plan and Compare Usage Cost Against all Price Plans

Endpoint

```text
GET /price-plans/compare-all/<smartMeterId>
```

Parameters

| Parameter      | Description                              |
|----------------|------------------------------------------|
| `smartMeterId` | One of the smart meter ids listed above. |

Retrieving readings using `curl`:

```console
$ curl "http://localhost:8080/price-plans/compare-all/smart-meter-0"
```

Example output:

```json
{
  "pricePlanComparisons": {
    "price-plan-2": 0.0002,
    "price-plan-1": 0.0004,
    "price-plan-0": 0.002
  },
  "pricePlanId": "price-plan-0"
}
```

### View Recommended Price Plans for Usage

Endpoint:

```text
GET /price-plans/recommend/<smartMeterId>[?limit=<limit>]
```

Parameters

| Parameter      | Description                                           |
| -------------- |-------------------------------------------------------|
| `smartMeterId` | One of the smart meters ids listed above.             |
| `limit`        | (Optional) limit the number of plans to be displayed. |

Retrieving readings using `curl`:

```console
$ curl "http://localhost:8080/price-plans/recommend/smart-meter-0?limit=2"
```

Example output:

```json
[
  {
    "price-plan-2": 0.0002
  },
  {
    "price-plan-1": 0.0004
  }
]
```
