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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * This dialog is used to enter a description and contents. The contents are put
 * on the clipboard, whereas the description is used to show a particular entry
 * in lists or dialogs.
 *
 * @author thomas
 */
public class AddEntryDialog extends AbstractDialog {

    private final JTextField textfieldDescription;
    private final JTextArea textareaContents;

    private String title;

    public AddEntryDialog() {
        // the description field
        JPanel panelDescription = new JPanel(new BorderLayout());
        textfieldDescription = new JTextField();
        panelDescription.add(new JLabel(Messages.getString("STR_DESCRIPTION")), BorderLayout.NORTH);
        panelDescription.add(textfieldDescription, BorderLayout.CENTER);
        // the contents field
        JPanel panelContents = new JPanel(new BorderLayout());
        panelContents.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panelContents.add(new JLabel(Messages.getString("STR_CONTENTS")), BorderLayout.NORTH);
        textareaContents = new JTextArea();
        textareaContents.setWrapStyleWord(true);
        textareaContents.setLineWrap(true);
        JScrollPane sp = new JScrollPane(textareaContents,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setPreferredSize(UIHelper.PREFERRED_SIZE);
        panelContents.add(sp, BorderLayout.CENTER);
        add(panelDescription, BorderLayout.NORTH);
        add(panelContents, BorderLayout.CENTER);
        // ActionListener
        final ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();
                String result = PluginManager.callPlugin(cmd, textareaContents.getText());
                textareaContents.setText(result);
            }
        };
        // populate plugin menu
        final JPopupMenu pm = new JPopupMenu();
        String[] piNames = PluginManager.getPluginNames();
        for (String piName : piNames) {
            UIHelper.createJMenuItem(piName, pm, al);
            if (pm.getComponentCount() == 1) {
                pm.addSeparator();
            }
        }
        // MouseListener
        final MouseListener ml = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                maybeShowPopup(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                maybeShowPopup(mouseEvent);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    pm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

        };
        textareaContents.addMouseListener(ml);
    }

    public int showDialog(String headline, String description, String contents) {
        textfieldDescription.setText(description);
        textareaContents.setText(contents);
        title = headline;
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

    public String getDescription() {
        return textfieldDescription.getText();
    }

    public String getContents() {
        return textareaContents.getText();
    }
}
