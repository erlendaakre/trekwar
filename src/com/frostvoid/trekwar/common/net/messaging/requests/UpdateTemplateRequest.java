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
package com.frostvoid.trekwar.common.net.messaging.requests;

import java.util.HashMap;

import com.frostvoid.trekwar.common.net.messaging.Request;

/**
 * Tells server to update or create a new ship template
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class UpdateTemplateRequest extends Request {
    private String templateName;
    private String hullClass;
    
    private HashMap<Integer, String> components;
    
    public UpdateTemplateRequest(String templateName, String hullClass) {
        this.templateName = templateName;
        this.hullClass = hullClass;
        
        components = new HashMap<Integer, String>();
    }

    /**
     * @return the templateName
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * @return the hullClass
     */
    public String getHullClass() {
        return hullClass;
    }
    
    public void addComponent(int slot, String name) {
        components.put(slot, name);
    }
    
    public HashMap<Integer, String> getComponents() {
        return components;
    }
}