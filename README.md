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
<table>
    <thead>
        <tr>
            <th>Telemetry</th>
            <th>Library</th>
            <th>Collector</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td rowspan=5>Metrics Monitoring</td>
            <td rowspan=1>SpringBoot-2 + Actuator + Micrometer Dependency</td>
            <td rowspan=1>Prometheus</td>
        </tr>
        <tr>
            <td rowspan=1>SpringBoot-3 + Actuator (Micrometer included)</td>
            <td rowspan=1>Prometheus</td>
        </tr>
        <tr>
            <td rowspan=1>SpringBoot + <b>Otel Java Agent</b></td>
            <td rowspan=3>Prometheus / Otel Collector</td>
        </tr>
        <tr>
            <td rowspan=1>SpringBoot + <b>Otel Starter Dependency</b></td>
        </tr>
        <tr>
            <td rowspan=1>SpringBoot + <b>Otel Starter Dependency + Micrometer </b> for custom Metrics</td>
        </tr>
        <tr>
            <td>Log Aggregation</td>
            <td> <b>SLF4J</b> : Logback / Log4J 2 </td>
            <td>Splunk / ELK / Loki</td>
        </tr>
        <tr>
            <td rowspan=3>Distributed Trace</td>
            <td>Spring Cloud Sleuth</td>
            <td rowspan=3>Zipkin / Jaeger / Tempo</td>
        </tr>
        <tr>
            <td>Spring Cloud Sleuth + Brave</td>
        </tr>
        <tr>
            <td>Otel Starter Dependency</td>
        </tr>
    </tbody>
</table>

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
