package com.jamierf.dropwizard.logging.gelf;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.Layout;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.net.HostAndPort;
import gelf4j.logback.GelfAppender;
import io.dropwizard.logging.AbstractAppenderFactory;

import javax.validation.constraints.NotNull;

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

    @JsonProperty
    public HostAndPort getGraylog2Server() {
        return graylog2Server;
    }

    @JsonProperty
    public void setGraylog2Server(HostAndPort graylog2Server) {
        this.graylog2Server = graylog2Server;
    }

    @Override
    public Appender<ILoggingEvent> build(LoggerContext context, String applicationName, Layout<ILoggingEvent> layout) {
        final GelfAppender<ILoggingEvent> appender = new GelfAppender<>();

        appender.setName("gelf-appender");
        appender.setContext(context);
        appender.getConfig().setHost(graylog2Server.getHostText());

        if (graylog2Server.hasPort()) {
            appender.getConfig().setPort(graylog2Server.getPort());
        }

        addThresholdFilter(appender, threshold);
        appender.start();

        return wrapAsync(appender);
    }
}
