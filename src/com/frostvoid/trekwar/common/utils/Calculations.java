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
package com.frostvoid.trekwar.common.utils;

import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.User;

/**
 * Simple static calculations
 * 
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class Calculations {
    
    /**
     * Calculates how many turns are left until a research is completed
     * @param user the user doing the research
     * @param tech the research being done
     * 
     * @return the number of turns required until the research is done
     */
    public static int turnsLeftToResearch(User user, Technology tech) {
        double turnsLeft = Math.ceil((tech.getResearchCost() - user.getResearchPoints()) / (double)user.getResearchOutput());
        return (int)turnsLeft;
    }
    
    /**
     * Calculates how long is left on a general operation
     * 
     * @param target target value
     * @param current current value
     * @param perTurn increase per turn
     * 
     * @return turns left
     */
    public static int turnsLeft(int target, int current, int perTurn) {
        double res = Math.ceil( ((double)target - (double)current) / perTurn);
        return (int)res;
    }
}