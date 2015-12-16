package com.jamierf.dropwizard.logging.gelf;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.net.HostAndPort;
import gelf4j.GelfTargetConfig;
import gelf4j.logback.GelfAppender;
import io.dropwizard.logging.AbstractAppenderFactory;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * <p>An {@link io.dropwizard.logging.AppenderFactory} implementation which provides an appender that writes events to a Graylog2 server.</p>
 * <b>Configuration Parameters:</b>
 * <table summary="Configuration">
 *     <tr>
 *         <td>Name</td>
 *         <td>Default</td>
 *         <td>Description</td>
 *     </tr>
 *     <tr>
 *         <td>{@code type}</td>
 *         <td><b>REQUIRED</b></td>
 *         <td>The appender type. Must be {@code gelf}.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code threshold}</td>
 *         <td>{@code ALL}</td>
 *         <td>The lowest level of events to write to the server.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code server}</td>
 *         <td>{@code localhost}</td>
 *         <td>The server where current events are logged.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code defaultFields}</td>
 *         <td>{@code {}}</td>
 *         <td> A JSON format object for constant values merged ito the message. Default: {} (optional)</td>
 *     </tr>
 *      <tr>
 *         <td>{@code additionalFields}</td>
 *         <td>{@code {"threadName": "threadName", "exception": "exception", "loggerName": "loggerName", "timestampMs": "timestampMs"}}</td>
 *         <td> A JSON object that describes dynamic fields that should be merged into the message.
 *         The key indicates the name of the field in message while the value is a symbolic key that indicates
 *         the source or type information that should be merged into the message.
 *         The supported symbolic keys vary between the different supported logging frameworks.
 *         Default: {"threadName": "threadName", "exception": "exception", "loggerName": "loggerName", "timestampMs": "timestampMs"} (optional)</td>
 *     </tr>
 *     <tr>
 *         <td>{@code logFormat}</td>
 *         <td>the default format</td>
 *         <td>
 *             The Logback pattern with which events will be formatted. See
 *             <a href="http://logback.qos.ch/manual/layouts.html#conversionWord">the Logback documentation</a>
 *             for details.
 *         </td>
 *     </tr>
 * </table>
 *
 * @see io.dropwizard.logging.AbstractAppenderFactory
 */
@JsonTypeName("gelf")
public class GelfAppenderFactory extends AbstractAppenderFactory {

    @NotNull
    private HostAndPort server = HostAndPort.fromString("localhost");
    private Map<String, String> defaultFields = Collections.emptyMap();
    private Map<String, String> additionalFields = Collections.emptyMap();

    @JsonProperty
    public HostAndPort getServer() {
        return server;
    }

    @JsonProperty
    public void setServer(HostAndPort server) {
        this.server = server;
    }

    @JsonProperty
    public void setDefaultFields(Map<String, String> defaultFields) {
        this.defaultFields = defaultFields;
    }

    @JsonProperty
    public Map<String, String> getDefaultFields() {
        return defaultFields;
    }

    @JsonProperty
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    @JsonProperty
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
        final GelfAppender<ILoggingEvent> appender = new GelfAppender<>();

        appender.setName("gelf-appender");
        appender.setContext(context);
        appender.getConfig().setHost(server.getHostText());

        if (server.hasPort()) {
            appender.getConfig().setPort(server.getPort());
        }

        appender.getConfig().getDefaultFields().put(GelfTargetConfig.FIELD_FACILITY, applicationName);
        appender.getConfig().getDefaultFields().putAll(defaultFields);
        if(!additionalFields.isEmpty())
        {
            appender.getConfig().getAdditionalFields().clear();
            appender.getConfig().getAdditionalFields().putAll(additionalFields);
        }

        addThresholdFilter(appender, threshold);
        appender.start();

        return wrapAsync(appender);
    }
}
