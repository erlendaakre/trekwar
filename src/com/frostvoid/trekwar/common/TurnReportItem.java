/*
 * Copyright 2012 FrostVoid Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frostvoid.trekwar.common;

import java.io.Serializable;

/**
 * Represents an entry in the Turn Report list
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TurnReportItem implements Serializable {

    private long turn;
    private int x = -1;
    private int y = -1;
    private TurnReportSeverity severity;
    private String summary;
    private String detailed;

    public enum TurnReportSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Creates a new turn report item
     *
     * @param turn     the turn the event took place
     * @param x        the event location x
     * @param y        the event location y
     * @param severity the even importance
     */
    public TurnReportItem(long turn, int x, int y, TurnReportSeverity severity) {
        this.turn = turn;
        this.x = x;
        this.y = y;
        this.severity = severity;

        summary = "";
        detailed = "";
    }

    /**
     * @return the turn
     */
    public long getTurn() {
        return turn;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the severity
     */
    public TurnReportSeverity getSeverity() {
        return severity;
    }

    /**
     * @param severity the severity to set
     */
    public void setSeverity(TurnReportSeverity severity) {
        this.severity = severity;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the detailed
     */
    public String getDetailed() {
        return detailed;
    }

    /**
     * @param detailed the detailed to set
     */
    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }
}