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

package org.opennms.alec.feature.graph.rest.model;

import java.util.LinkedHashMap;
import java.util.Map;

import org.opennms.alec.features.graph.graphml.GraphMLEdge;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdgeDTO {

    private final String id;
    private final String sourceId;
    private final String targetId;
    private final Map<String,String> properties = new LinkedHashMap<>();

    public EdgeDTO(GraphMLEdge edge) {
        this.id = edge.getId();
        if (edge.getSource() != null) {
            this.sourceId = edge.getSource().getId();
        } else {
            this.sourceId = null;
        }
        if (edge.getTarget() != null) {
            this.targetId = edge.getTarget().getId();
        } else {
            this.targetId = null;
        }

        edge.getProperties().forEach((k,v) -> {
            properties.put(k,v != null ? v.toString() : null);
        });
    }

    public String getId() {
        return id;
    }

    @JsonProperty("source-id")
    public String getSourceId() {
        return sourceId;
    }

    @JsonProperty("target-id")
    public String getTargetId() {
        return targetId;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
}
