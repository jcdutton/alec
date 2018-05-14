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

package org.opennms.oce.engine.driver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.opennms.oce.model.alarm.api.Alarm;
import org.opennms.oce.model.alarm.api.Incident;

/**
 * @author smith
 *
 */
public class SetIntersectionStrategy implements ScoringStrategy {

    // Input Baseline Incident Set 
    private Set<Incident> baseline;

    // Incident Set to Score
    private Set<Incident> sut;

    private Set<String> baselineSignatures = new HashSet<>();

    private Set<String> intersection = new HashSet<>();

    private Set<String> missed = new HashSet<>();

    private Set<String> sutSignatures = new HashSet<>();

    private Set<String> baselineAlarms;

    private Set<String> sutAlarms;

    private Set<String> intersectionAlarms = new HashSet<>();

    private Set<String> unmatchedAlarms = new HashSet<>();

    @Override
    public ScoreReport score(Set<Incident> baseline, Set<Incident> sut) {
        this.baseline = baseline;
        this.sut = sut;
        createSets();

        ScoreReportBean report = new ScoreReportBean();
        report.setScore(Math.abs(100 - getAccuracy()));
        report.setMetrics(getMetrics());
        return report;
    }

    @Override
    public String getName() {
        return this.getName();
    }

    private List<ScoreMetricBean> getMetrics() {
        List<ScoreMetricBean> metrics = new ArrayList<>();
        metrics.add(getAlarmAccuracy());
        metrics.add(getFalsePositives());
        metrics.add(getFalseNegatives());
        return metrics;
    }

    
    // Percentage of the Base Tickets correctly found in the SUT
    public int getAccuracy() {
        int retained = intersection.size();
        return retained * 100 / baseline.size();
    }

    // Percentage of the Alarms correctly found in the SUT
    private ScoreMetricBean getAlarmAccuracy() {
        ScoreMetricBean metric = new ScoreMetricBean("AlarmAccuracy", "Percents of Alarms in the Baseline found in the Baseline.");
        int retained = intersectionAlarms.size();
        metric.setValue(retained * 100 / baselineAlarms.size());
        return metric;
    }

    private ScoreMetricBean getFalsePositives() {
        ScoreMetricBean metric = new ScoreMetricBean("FalsePositives", "Number of Incidents in the SUT not found in the Baseline.");
        metric.setValue(sut.size() - intersection.size());
        return metric;
    }

    private ScoreMetricBean getFalseNegatives() {
        ScoreMetricBean metric = new ScoreMetricBean("FalseNegatives", "Number of Incidents in the Baseline not included in the SUT.");
        metric.setValue(baseline.size() - intersection.size());
        return metric;
    }

    private void createSets() {
        createAlarmSignatures(baseline, sut);
        baselineAlarms = baseline.stream().map(i -> i.getAlarms()).flatMap(Collection::stream).map(a -> a.getId()).collect(Collectors.toSet());
        sutAlarms = sut.stream().map(i -> i.getAlarms()).flatMap(Collection::stream).map(a -> a.getId()).collect(Collectors.toSet());
        intersectionAlarms.addAll(baselineAlarms);
        intersectionAlarms.retainAll(sutAlarms);
        unmatchedAlarms.addAll(baselineAlarms);
        unmatchedAlarms.removeAll(sutAlarms);
    }


    private void createAlarmSignatures(Set<Incident> baseline, Set<Incident> sut) {
        baselineSignatures.addAll(baseline.stream().map(i -> getIncidentSignature(i)).collect(Collectors.toSet()));
        intersection.addAll(baselineSignatures);
        sutSignatures.addAll(sut.stream().map(i -> getIncidentSignature(i)).collect(Collectors.toSet()));
        intersection.retainAll(sutSignatures);
        missed.addAll(baselineSignatures);
        missed.removeAll(intersection);
    }

    private String getIncidentSignature(Incident i) {
        return getAlarmSignature(i.getAlarms());
    }

    // Create a standardized signature from a List of Alarms
    private String getAlarmSignature(Set<Alarm> alarms) {
        return alarms.stream().map(a -> a.getId()).sorted().collect(Collectors.joining("."));
    }

    private static Incident getIncident(Set<Incident> incidents, String id) {
        for (Incident i : incidents) {
            // TODO - what we really want to do here is search for a ticket that contains a given alarmId
            // Then we can make that the basis of our secondary comparison - i.e. how different are those two tickets?
            if (i.getId().equals(id)) {
                return i;
            }
        }
        return null;
    }

}