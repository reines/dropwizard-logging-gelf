package com.jamierf.dropwizard.logging.gelf;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.net.HostAndPort;
import io.dropwizard.logging.AbstractAppenderFactory;
import me.moocar.logbackgelf.GelfAppender;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

/**
 * An {@link io.dropwizard.logging.AppenderFactory} implementation which provides an appender that writes events to a Graylog2 server.
 * <p/>
 * <b>Configuration Parameters:</b>
 * <table>
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
 *         <td>{@code graylog2Server}</td>
 *         <td><b>REQUIRED</b></td>
 *         <td>The server where current events are logged.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code additionalFields}</td>
 *         <td></td>
 *         <td>A map of additional fields to include in the gelf payload.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code userLoggerName}</td>
 *         <td>{@code true}</td>
 *         <td>Whether or not to include the field {@code _loggerName}, the fully qualified name of the logger, in the gelf payload.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code useThreadName}</td>
 *         <td>{@code true}</td>
 *         <td>Whether or not to include the field {@code _threadName} in the gelf payload.</td>
 *     </tr>
 *     <tr>
 *         <td>{@code includeFullMDC}</td>
 *         <td>{@code true}</td>
 *         <td>Whether or not all fields from the SLF4J MDC in the gelf payload.</td>
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
    private HostAndPort graylog2Server;

    @NotNull
    private Map<String, String> additionalFields = Collections.emptyMap();

    private boolean useLoggerName = true;

    private boolean useThreadName = true;

    private boolean includeFullMDC = true;

    @JsonProperty
    public HostAndPort getGraylog2Server() {
        return graylog2Server;
    }

    @JsonProperty
    public void setGraylog2Server(HostAndPort graylog2Server) {
        this.graylog2Server = graylog2Server;
    }

    @JsonProperty
    public Map<String, String> getAdditionalFields() {
        return additionalFields;
    }

    @JsonProperty
    public void setAdditionalFields(Map<String, String> additionalFields) {
        this.additionalFields = additionalFields;
    }

    public boolean isUseLoggerName() {
        return useLoggerName;
    }

    public void setUseLoggerName(boolean useLoggerName) {
        this.useLoggerName = useLoggerName;
    }

    public boolean isUseThreadName() {
        return useThreadName;
    }

    public void setUseThreadName(boolean useThreadName) {
        this.useThreadName = useThreadName;
    }

    public boolean isIncludeFullMDC() {
        return includeFullMDC;
    }

    public void setIncludeFullMDC(boolean includeFullMDC) {
        this.includeFullMDC = includeFullMDC;
    }

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
        final GelfAppender appender = new GelfAppender();

        appender.setName("gelf-appender");
        appender.setContext(context);
        appender.setFacility(applicationName);
        appender.setGraylog2ServerHost(graylog2Server.getHostText());

        if (graylog2Server.hasPort()) {
            appender.setGraylog2ServerPort(graylog2Server.getPort());
        }

        if (!additionalFields.isEmpty()) {
            appender.setAdditionalFields(additionalFields);
        }

        appender.setIncludeFullMDC(includeFullMDC);
        appender.setUseLoggerName(useLoggerName);
        appender.setUseThreadName(useThreadName);

        addThresholdFilter(appender, threshold);
        appender.start();

        return wrapAsync(appender);
    }
}
