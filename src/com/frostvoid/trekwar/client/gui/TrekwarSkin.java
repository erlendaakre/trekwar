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
package com.frostvoid.trekwar.client.gui;

import org.jvnet.substance.api.ColorSchemeAssociationKind;
import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.colorscheme.MetallicColorScheme;
import org.jvnet.substance.colorscheme.SunsetColorScheme;

/**
 * The Substance skin used by the game
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class TrekwarSkin extends org.jvnet.substance.skin.RavenSkin {

    public TrekwarSkin() {
        SubstanceColorScheme activeScheme = new MetallicColorScheme().tint(0.15).named("Custom Active");
        SubstanceColorScheme defaultScheme = new MetallicColorScheme().shade(0.1).named("Custom Default");
        SubstanceColorScheme disabledScheme = defaultScheme;

        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(activeScheme, defaultScheme, disabledScheme);
        defaultSchemeBundle.registerColorScheme(new SunsetColorScheme(), ColorSchemeAssociationKind.TAB, ComponentState.SELECTED);

        this.selectedTabFadeStart = 1.0f;
        this.selectedTabFadeEnd = 1.0f;

    }
}