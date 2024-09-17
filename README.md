# OBSERVABILITY
<img src="observability.jpg" width="600" height="300">


## PILLARS OF OBSERVABILITY


<table style="border: none; width: 100%; ">
  <tr style="border: none">
    <td style="border: none"><img width="200" height="200" src="observability_pillars.jpg"></td>
    <td style="border: none; vertical-align: top">
        <ul>
            <li><b>Metrics: </b> numeric measurements at a moment in time</li>
            <li><b>Logs: </b></li>
            <li><b>Traces:  </b></li>
        </ul>
    </td>
  </tr>
 </table>


## OBSERVABILITY IN SPRING FRAMEWORK

| Telemetry          | Library             | Collector       |
|--------------------|---------------------|-----------------|
| Metrics Monitoring | Micrometer          | Prometheus      |
| Log Aggregation    | Logback             | Splunk / ELK    |
| Distributed Trace  | Spring Cloud Sleuth | Jaeger / Zipkin |


## log4j logging hierarchy order:

|       | Telemetry | Library | Collector | INFO | DEBUG | TRACE | ALL
|-------|-----------|---------|-----------|------|-------|-------|----
| OFF   |           |         |           |  |       |  | 
| FATAL | X         |         |           |  |       |  | 
| ERROR | X         | X       |           |  |       |  | 
| WARN  | X         | X       | X         |  |       |  | 
| INFO  | X         | X       | X         | X |       |  | 
| DEBUG | X         | X       | X         | X | X     |  | 
| TRACE | X         | X       | X         | X | X     | X | 
| ALL   | X         | X       | X         | X | X     | X | X

````java
 public interface TeaService {
    void makeTea(String name);

    void teaLeaves();

    void waters();
}

public class DefaultTeaService implements TeaService {
    @Override
    void makeTea(String name) {
        //TODO...
    }

    @Override
    void teaLeaves() {
        //TODO...
    }
}

public class ObservedTeaService implements TeaService {
    private final DefaultTeaService delegate;
    private final ObservationRegistry observationRegistry;

    @Override
    public void makeTea(String name) {
        Observation.createNotStarted("make.tea", observationRegistry)
                .lowCardinalityKeyValue("name", "name")
                .lowCardinalityKeyValue("size", size)
                .observe(() -> delegate.makeTea());
    }
}
````
