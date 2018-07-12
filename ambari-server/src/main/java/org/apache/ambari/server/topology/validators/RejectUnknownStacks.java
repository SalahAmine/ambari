/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology.validators;

import static java.util.stream.Collectors.joining;

import javax.inject.Provider;

import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.topology.ClusterTopology;
import org.apache.ambari.server.topology.InvalidTopologyException;

/**
 * Verifies that the topology only references known stacks.
 */
public class RejectUnknownStacks implements TopologyValidator {

  private final Provider<AmbariMetaInfo> metaInfo;

  RejectUnknownStacks(Provider<AmbariMetaInfo> metaInfo) {
    this.metaInfo = metaInfo;
  }

  @Override
  public ClusterTopology validate(ClusterTopology topology) throws InvalidTopologyException {
    String unknownStacks = topology.getStackIds().stream()
      .filter(stackId -> !metaInfo.get().isKnownStack(stackId))
      .sorted()
      .map(Object::toString)
      .collect(joining(", "));

    if (!unknownStacks.isEmpty()) {
      throw new InvalidTopologyException("Unknown stacks found in cluster creation request: " + unknownStacks);
    }
    return topology;
  }
}