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
package com.frostvoid.trekwar.client.gui.BottomMenu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import com.frostvoid.trekwar.client.Client;
import com.frostvoid.trekwar.client.gui.SimpleBar;
import com.frostvoid.trekwar.common.StarSystem;
import com.frostvoid.trekwar.common.Technology;
import com.frostvoid.trekwar.common.TurnReportItem;
import com.frostvoid.trekwar.common.orders.BuildStructureOrder;
import com.frostvoid.trekwar.common.Fleet;
import com.frostvoid.trekwar.common.orders.BuildShipOrder;

/**
 * Empire info for bottom menu
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class EmpirePanel extends JPanel {

    private JTabbedPane tabbedPane;
    private JPanel summaryPanel;
    private JLabel summaryLabel;
    private JPanel turnReportPanel, reportTablePanel;
    private JPanel systemsPanel, systemTablePanel;

    public EmpirePanel() {
        setBounds(0, 0, 780, 175);
        setLayout(new GridLayout(1, 1));

        tabbedPane = new JTabbedPane();

        summaryPanel = createSummaryPanel();
        turnReportPanel = createTurnReportPanel();
        systemsPanel = createSystemsPanel();

        tabbedPane.add(Client.getLanguage().getU("summary"), summaryPanel);
        tabbedPane.add(Client.getLanguage().getU("turn_report"), turnReportPanel);
        tabbedPane.add(Client.getLanguage().getU("starsystems"), systemsPanel);

        add(tabbedPane);
    }

    public void updateInfo() {
        updateSummaryLabel();
        updateTurnReportTable();
        updateSystemsReportTable();
    }
    
    void selectTurnReportTab() {
        tabbedPane.setSelectedComponent(turnReportPanel);
    }

    private JPanel createSystemsPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1, 1));

        systemTablePanel = new JPanel();
        systemTablePanel.setLayout(new BoxLayout(systemTablePanel, BoxLayout.Y_AXIS));

        updateSystemsReportTable();

        JScrollPane sp = new JScrollPane(systemTablePanel);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p.add(sp);
        return p;
    }

    private void updateSystemsReportTable() {
        int industryMax = 0;
        int researchMax = 0;
        int troopsMax = 0;
        int popMax = 0;

        for (StarSystem s : Client.getInstance().getLocalUser().getStarSystems()) {
            if (s.getSystemIndustryProduced() > industryMax) {
                industryMax = s.getSystemIndustryProduced();
            }
            if (s.getSystemResearchProduced() > researchMax) {
                researchMax = s.getSystemResearchProduced();
            }
            if (s.getTroopProduction() > troopsMax) {
                troopsMax = s.getTroopProduction();
            }
            if (s.getPopulation() > popMax) {
                popMax = s.getPopulation();
            }
        }

        systemTablePanel.removeAll();
        for (StarSystem s : Client.getInstance().getLocalUser().getStarSystems()) {
            addStarSystemItem(systemTablePanel, s, industryMax, researchMax, troopsMax, popMax);
        }
    }

    private void addStarSystemItem(JPanel p, final StarSystem s, int industryMax, int researchMax, int troopsMax, int popMax) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(null);
        Dimension size = new Dimension(340, 65);
        itemPanel.setSize(size);
        itemPanel.setMaximumSize(size);
        itemPanel.setMinimumSize(size);
        itemPanel.setPreferredSize(size);

        MouseListener selectTileListener = new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                Client.getInstance().getMapPanel().setSelection(s.getX(), s.getY());
                Client.getInstance().getBottomGuiPanel().displaySystem(s);
            }
        };
        
        itemPanel.addMouseListener(selectTileListener);

        JLabel titleLabel = new JLabel("<html><b>" + s.getName() + "</b> @ " + s.getX() + ":" + s.getY() + "</html>");
        titleLabel.setBounds(2, 2, 150, 18);
        itemPanel.add(titleLabel);

        JLabel buildingHeader = new JLabel(Client.getLanguage().getU("turn_report_building") + ":");
        buildingHeader.setBounds(2, 20, 150, 18);
        itemPanel.add(buildingHeader);

        JLabel buildingOrder = null;
        if (s.getBuildQueue().size() > 0) {
            String item = "";
            if (s.getBuildQueue().get(0) instanceof BuildStructureOrder) {
                item = ((BuildStructureOrder) (s.getBuildQueue().get(0))).getStructure().getName();
            }
            if (s.getBuildQueue().get(0) instanceof BuildShipOrder) {
                item = ((BuildShipOrder) (s.getBuildQueue().get(0))).getTemplate().getName();
            }
            buildingOrder = new JLabel(item);
        } else {
            buildingOrder = new JLabel("<html><i>" + Client.getLanguage().get("nothing") + "</i></html>");
        }
        buildingOrder.setBounds(2, 40, 150, 20);
        itemPanel.add(buildingOrder);

        SimpleBar industryBar = new SimpleBar(12, 200, (int) (s.getSystemIndustryProduced() / (industryMax / 100d)), Color.YELLOW, Color.GRAY, SimpleBar.Alignment.HORIZONTAL);
        industryBar.setToolTipText(Client.getLanguage().getU("turn_report_industry"));
        industryBar.setBounds(150, 3, 200, 12);
        itemPanel.add(industryBar);

        SimpleBar researchBar = new SimpleBar(12, 200, (int) (s.getSystemResearchProduced() / (researchMax / 100d)), Color.BLUE, Color.GRAY, SimpleBar.Alignment.HORIZONTAL);
        researchBar.setToolTipText(Client.getLanguage().getU("turn_report_research"));
        researchBar.setBounds(150, 18, 200, 12);
        itemPanel.add(researchBar);

        SimpleBar troopsBar = new SimpleBar(12, 200, (int) (s.getTroopCount() / (troopsMax / 100d)), Color.RED, Color.GRAY, SimpleBar.Alignment.HORIZONTAL);
        troopsBar.setToolTipText(Client.getLanguage().getU("turn_report_troops"));
        troopsBar.setBounds(150, 33, 200, 12);
        itemPanel.add(troopsBar);

        SimpleBar popBar = new SimpleBar(12, 200, (int) (s.getPopulation() / (popMax / 100d)), Color.GREEN, Color.GRAY, SimpleBar.Alignment.HORIZONTAL);
        popBar.setToolTipText(Client.getLanguage().getU("turn_report_population"));
        popBar.setBounds(150, 48, 200, 12);
        itemPanel.add(popBar);

        p.add(itemPanel);
    }

    private JPanel createSummaryPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1, 1));

        summaryLabel = new JLabel();
        summaryLabel.setHorizontalAlignment(SwingConstants.LEFT);
        summaryLabel.setVerticalAlignment(SwingConstants.TOP);
        updateSummaryLabel();

        JScrollPane sp = new JScrollPane(summaryLabel);
        p.add(sp);
        return p;
    }

    private JPanel createTurnReportPanel() {
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(1, 1));

        reportTablePanel = new JPanel();
        reportTablePanel.setLayout(new BoxLayout(reportTablePanel, BoxLayout.Y_AXIS));

        updateTurnReportTable();

        JScrollPane sp = new JScrollPane(reportTablePanel);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        p.add(sp);
        return p;
    }

    private void updateTurnReportTable() {
        reportTablePanel.removeAll();
        for (TurnReportItem i : Client.getInstance().getLocalUser().getTurnReports()) {
            addTurnReportItem(reportTablePanel, i);
        }
    }

    private void addTurnReportItem(JPanel p, TurnReportItem item) {

        String imgUrl = "";
        switch (item.getSeverity()) {
            case LOW:
                imgUrl = "report_low.png";
                break;
            case MEDIUM:
                imgUrl = "report_medium.png";
                break;
            case HIGH:
                imgUrl = "report_high.png";
                break;
            case CRITICAL:
                imgUrl = "report_critical.png";
                break;
            default:
                imgUrl = "report.png";
        }
        ImageIcon severityIcon = new ImageIcon("graphics/misc_icons/" + imgUrl);

        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(null);
        Dimension size = new Dimension(740, 45);
        itemPanel.setSize(size);
        itemPanel.setMaximumSize(size);
        itemPanel.setMinimumSize(size);
        itemPanel.setPreferredSize(size);
        itemPanel.setOpaque(true);

        JLabel turnNumber = new JLabel(Client.getLanguage().getU("turn") + " " + item.getTurn());
        turnNumber.setBounds(2, 2, 60, 20);
        JLabel severityLabel = new JLabel(severityIcon);
        severityLabel.setBounds(62, 2, 20, 20);

        JLabel locLabel = new JLabel("" + item.getX() + ":" + item.getY() + "");
        if (item.getX() == -1) {
            locLabel.setText("");
        }
        locLabel.setBounds(90, 2, 60, 20);
        JLabel reportSummaryLabel = new JLabel("<html><b>" + item.getSummary() + "</b></html>");
        reportSummaryLabel.setBounds(140, 2, 600, 20);

        JLabel detailsLabel = new JLabel(item.getDetailed());
        detailsLabel.setBounds(2, 22, 738, 20);

        SimpleBar seperator = new SimpleBar(1, 800, 100, Color.WHITE, Color.WHITE, SimpleBar.Alignment.HORIZONTAL);
        seperator.setBounds(0, 44, 800, 1);

        itemPanel.add(turnNumber);
        itemPanel.add(severityLabel);
        itemPanel.add(locLabel);
        itemPanel.add(reportSummaryLabel);
        itemPanel.add(detailsLabel);
        itemPanel.add(seperator);

        p.add(itemPanel);
    }

    private void updateSummaryLabel() {
        int shipNum = 0;
        for (Fleet f : Client.getInstance().getLocalUser().getFleets()) {
            shipNum += f.getShips().size();
        }
        int planetNum = 0;
        int populationTotal = 0;
        int deutTotal = 0;
        for (StarSystem s : Client.getInstance().getLocalUser().getStarSystems()) {
            planetNum += s.getPlanets().size();
            populationTotal += s.getPopulation();
            deutTotal += s.getDeuteriumPerTurn();

        }
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<b>").append(Client.getInstance().getLocalUser().getUsername()).append(Client.getLanguage().get("s_empire")).append("</b><br>");
        sb.append("<br>");
        sb.append(Client.getLanguage().getU("starsystems")).append(": ").append(Client.getInstance().getLocalUser().getStarSystems().size()).append("<br>");
        sb.append(Client.getLanguage().getU("planets")).append(": ").append(planetNum).append("<br>");
        sb.append(Client.getLanguage().getU("population")).append(": ").append(((double) populationTotal) / 1000).append(" ").append(Client.getLanguage().get("billion")).append("<br>");
        sb.append(Client.getLanguage().getU("deuterium")).append(Client.getLanguage().get("per_turn")).append(": ").append(deutTotal).append("<br>");
        sb.append("<br>");
        sb.append(Client.getLanguage().getU("fleets")).append(": ").append(Client.getInstance().getLocalUser().getFleets().size()).append("<br>");
        sb.append(Client.getLanguage().getU("ships")).append(": ").append(shipNum).append("<br>");
        sb.append(Client.getLanguage().getU("upkeep")).append(": ").append(Client.getInstance().getLocalUser().getShipUpkeepUsed()).append(" / ").append(Client.getInstance().getLocalUser().getShipUpkeepSupply()).append("<br>");
        sb.append(Client.getLanguage().getU("upkeep_surplus")).append(": ").append(Client.getInstance().getLocalUser().getShipUpkeepSurplus()).append("<br>");
        
        sb.append("<br>");
        Technology currentTech = Client.getInstance().getLocalUser().getCurrentResearch();
        String techStr = Client.getLanguage().get("nothing");
        if (currentTech != null) {
            techStr = currentTech.getName();
        }
        sb.append(Client.getLanguage().getU("researching")).append(": ").append(techStr).append("<br>");
        sb.append(Client.getLanguage().getU("structures_researched")).append(": ").append(Client.getInstance().getLocalUser().getAvailableStructures().size()).append("<br>");
        sb.append(Client.getLanguage().getU("hulls_researched")).append(": ").append(Client.getInstance().getLocalUser().getAvailableShipHulls().size()).append("<br>");
        sb.append(Client.getLanguage().getU("components_researched")).append(": ").append(Client.getInstance().getLocalUser().getAvailableShipComponents().size()).append("<br>");
        sb.append("</html></body>");
        summaryLabel.setText(sb.toString());
    }
}