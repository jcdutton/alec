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

package org.opennms.oce.datasource.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.opennms.oce.datasource.api.InventoryObject;
import org.opennms.oce.datasource.api.InventoryObjectPeerRef;
import org.opennms.oce.datasource.api.InventoryObjectRelativeRef;

public final class ImmutableInventoryObject implements InventoryObject {
    private final String type;
    private final String id;
    private final String parentType;
    private final String parentId;
    private final String subtype;
    private final String friendlyName;
    private final boolean isTopLevel;
    private final List<InventoryObjectPeerRef> peers;
    private final List<InventoryObjectRelativeRef> relatives;
    private final long weightToParent;

    private ImmutableInventoryObject(Builder builder) {
        this.type = builder.type;
        this.id = builder.id;
        this.parentType = builder.parentType;
        this.parentId = builder.parentId;
        this.subtype = builder.subtype;
        this.friendlyName = builder.friendlyName;
        this.isTopLevel = builder.isTopLevel;
        this.peers = builder.peers == null ? null : copyPeers(builder.peers);
        this.relatives = builder.relatives == null ? null : copyRelatives(builder.relatives);
        this.weightToParent = builder.weightToParent;
    }

    public static class Builder {
        private String type;
        private String id;
        private String parentType;
        private String parentId;
        private String subtype;
        private String friendlyName;
        private boolean isTopLevel;
        private List<InventoryObjectPeerRef> peers = new ArrayList<>();
        private List<InventoryObjectRelativeRef> relatives = new ArrayList<>();
        private long weightToParent;

        private Builder() {
            weightToParent = 1;
        }

        private Builder(InventoryObject inventoryObject) {
            this.type = inventoryObject.getType();
            this.id = inventoryObject.getId();
            this.parentType = inventoryObject.getParentType();
            this.parentId = inventoryObject.getParentId();
            this.subtype = inventoryObject.getSubtype();
            this.friendlyName = inventoryObject.getFriendlyName();
            this.isTopLevel = inventoryObject.isTopLevel();
            // Copy contents for the collections to avoid referencing a collection we don't control
            this.peers.addAll(inventoryObject.getPeers());
            this.relatives.addAll(inventoryObject.getRelatives());
            this.weightToParent = inventoryObject.getWeightToParent();
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setParentType(String parentType) {
            this.parentType = parentType;
            return this;
        }

        public Builder setParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setSubtype(String subtype) {
            this.subtype = subtype;
            return this;
        }

        public Builder setFriendlyName(String friendlyName) {
            this.friendlyName = friendlyName;
            return this;
        }

        public Builder setTopLevel(boolean topLevel) {
            isTopLevel = topLevel;
            return this;
        }

        public Builder setPeers(List<InventoryObjectPeerRef> peers) {
            this.peers = peers;
            return this;
        }

        public Builder addPeer(InventoryObjectPeerRef peer) {
            this.peers.add(peer);
            return this;
        }

        public Builder setRelatives(List<InventoryObjectRelativeRef> relatives) {
            this.relatives = relatives;
            return this;
        }

        public Builder addRelative(InventoryObjectRelativeRef relative) {
            this.relatives.add(relative);
            return this;
        }

        public Builder setWeightToParent(long weightToParent) {
            this.weightToParent = weightToParent;
            return this;
        }

        public ImmutableInventoryObject build() {
            Objects.requireNonNull(id, "Id cannot be null");
            Objects.requireNonNull(type, "Type cannot be null");

            return new ImmutableInventoryObject(this);
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilderFrom(InventoryObject inventoryObject) {
        return new Builder(inventoryObject);
    }

    /**
     * Copies a list of peers converting each peer to an immutable reference if needed.
     *
     * @param peers the list to copy
     * @return a new list containing only immutable objects
     */
    private List<InventoryObjectPeerRef> copyPeers(List<InventoryObjectPeerRef> peers) {
        return peers.stream()
                .map(peer -> {
                    if (!(peer instanceof ImmutableInventoryObjectPeerRef)) {
                        return ImmutableInventoryObjectPeerRef.newBuilderFrom(peer).build();
                    }

                    return peer;
                })
                .collect(Collectors.toList());
    }

    /**
     * Copies a list of relatives converting each relative to an immutable reference if needed.
     *
     * @param relatives the list to copy
     * @return a new list containing only immutable objects
     */
    private List<InventoryObjectRelativeRef> copyRelatives(List<InventoryObjectRelativeRef> relatives) {
        return relatives.stream()
                .map(relative -> {
                    if (!(relative instanceof ImmutableInventoryObjectRelativeRef)) {
                        return ImmutableInventoryObjectRelativeRef.newBuilderFrom(relative).build();
                    }

                    return relative;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSubtype() {
        return subtype;
    }

    @Override
    public String getParentType() {
        return parentType;
    }

    @Override
    public String getParentId() {
        return parentId;
    }

    @Override
    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public boolean isTopLevel() {
        return isTopLevel;
    }

    @Override
    public List<InventoryObjectPeerRef> getPeers() {
        return peers == null ? null : Collections.unmodifiableList(peers);
    }

    @Override
    public List<InventoryObjectRelativeRef> getRelatives() {
        return relatives == null ? null : Collections.unmodifiableList(relatives);
    }

    @Override
    public long getWeightToParent() {
        return weightToParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutableInventoryObject that = (ImmutableInventoryObject) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(id, that.id) &&
                Objects.equals(parentType, that.parentType) &&
                Objects.equals(parentId, that.parentId) &&
                Objects.equals(friendlyName, that.friendlyName) &&
                Objects.equals(peers, that.peers) &&
                Objects.equals(relatives, that.relatives);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id, parentType, parentId, friendlyName, peers, relatives);
    }

    @Override
    public String toString() {
        return "ImmutableInventoryObject{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", parentType='" + parentType + '\'' +
                ", parentId='" + parentId + '\'' +
                ", subtype='" + subtype + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                ", isTopLevel=" + isTopLevel +
                ", peers=" + peers +
                ", relatives=" + relatives +
                ", weightToParent=" + weightToParent +
                '}';
    }
}