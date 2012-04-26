/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jackrabbit.oak.jcr.nodetype;

import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

import org.apache.jackrabbit.oak.namepath.NameMapper;

class PropertyDefinitionImpl
        extends ItemDefinitionImpl implements PropertyDefinition {

    private final int requiredType;

    private final boolean multiple;

    public PropertyDefinitionImpl(
            NodeType type, NameMapper mapper, String name, boolean autoCreated,
            boolean mandatory, int onParentRevision, boolean isProtected,
            int requiredType, boolean multiple) {
        super(type, mapper, name, autoCreated,
                mandatory, onParentRevision, isProtected);
        this.requiredType = requiredType;
        this.multiple = multiple;
    }

    @Override
    public int getRequiredType() {
        return requiredType;
    }

    @Override
    public String[] getValueConstraints() {
        return new String[0];
    }

    @Override
    public Value[] getDefaultValues() {
        return new Value[0];
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public String[] getAvailableQueryOperators() {
        return new String[0];
    }

    @Override
    public boolean isFullTextSearchable() {
        return false;
    }

    @Override
    public boolean isQueryOrderable() {
        return false;
    }

}
