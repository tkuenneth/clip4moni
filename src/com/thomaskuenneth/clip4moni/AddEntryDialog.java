/*
 * AddEntryDialog.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2015  Thomas Kuenneth
 *
 * Clip4Moni is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.thomaskuenneth.clip4moni;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

public class AddEntryDialog extends AbstractDialog implements MouseListener,
        ActionListener {

    private String title;
    private JTextField tf;
    private JTextArea ta;
    private JPopupMenu pm;

    public AddEntryDialog() {
        JPanel panelNorth = new JPanel(new BorderLayout());
        panelNorth.setBorder(new TitledBorder(Messages.STR_DESCRIPTION));
        tf = new JTextField();
        panelNorth.add(tf, BorderLayout.CENTER);
        JPanel panelCenter = new JPanel(new BorderLayout());
        panelCenter.setBorder(new TitledBorder(Messages.STR_CONTENTS));
        ta = new JTextArea();
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        JScrollPane sp = new JScrollPane(ta,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(UIHelper.PREFERRED_SIZE);
        panelCenter.add(sp, BorderLayout.CENTER);
        add(panelNorth, BorderLayout.NORTH);
        add(panelCenter, BorderLayout.CENTER);
        pm = new JPopupMenu();
        String[] piNames = PluginManager.getPluginNames();
        for (int i = 0; i < piNames.length; i++) {
            UIHelper.createJMenuItem(piNames[i], pm, this);
        }
        ta.addMouseListener(this);
    }

    public int showDialog(String headline, String title, String contents) {
        tf.setText(title);
        ta.setText(contents);
        this.title = headline;
        return showDialog();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getOption() {
        return JOptionPane.OK_CANCEL_OPTION;
    }

    public String getText() {
        return tf.getText();
    }

    public String getContents() {
        return ta.getText();
    }

    private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            pm.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    // MouseListener interface
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {
    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        maybeShowPopup(mouseEvent);
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        maybeShowPopup(mouseEvent);
    }

    // ActionListener interface
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String cmd = actionEvent.getActionCommand();
        String result = PluginManager.callPlugin(cmd, ta.getText());
        ta.setText(result);
    }
}
