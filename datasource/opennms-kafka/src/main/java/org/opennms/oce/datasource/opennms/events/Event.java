/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2018 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2018 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.oce.datasource.opennms.events;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="event")
@XmlAccessorType(XmlAccessType.NONE)
public class Event {

    private static final ThreadLocal<DateFormat> FORMATTER_LONG = new ThreadLocal<DateFormat>() {
        @Override
        protected synchronized DateFormat initialValue() {
            final DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG);
            formatter.setLenient(true);
            return formatter;
        }
    };

    private final String createdAt = FORMATTER_LONG.get().format(new Date());

    @XmlElement(name="uei")
    private String uei;

    @XmlElement(name="source")
    private String source = "oce";

    @XmlElement(name="parms")
    private Parameters parameters = new Parameters();

    @XmlElement(name="severity")
    private String severity = "Critical";

    @XmlElement(name = "time")
    private String time = createdAt;

    public String getUei() {
        return uei;
    }

    public void setUei(String uei) {
        this.uei = uei;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Parameters parameters) {
        this.parameters = parameters;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void addParam(String key, String value) {
        getParameters().getParameters().add(new Parameter(key, value));
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(uei, event.uei) &&
                Objects.equals(source, event.source) &&
                Objects.equals(parameters, event.parameters) &&
                Objects.equals(severity, event.severity) &&
                Objects.equals(time, event.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uei, source, parameters, severity, time);
    }

    @Override
    public String toString() {
        return "Event{" +
                "uei='" + uei + '\'' +
                ", source='" + source + '\'' +
                ", parameters=" + parameters +
                ", severity='" + severity + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}