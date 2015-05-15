/*
 * EditEntriesDialog.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2013 - 2015  Thomas Kuenneth
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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Thomas Kuenneth
 */
public class EditEntriesDialog extends AbstractDialog implements ListSelectionListener, ActionListener {

    private DefaultListModel snippets;
    private Clip4MoniApplication app;
    private JButton deleteButton, editButton, upButton, downButton;
    private JList list;
    private JPanel editEntriesPanel;

    public EditEntriesDialog(DefaultListModel snippets, Clip4MoniApplication app) {
        this.snippets = snippets;
        this.app = app;
        editEntriesPanel = new JPanel(new BorderLayout());
        JPanel buttonBox = new JPanel(new GridLayout(4, 1, 4, 4));
        list = new JList(snippets);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        list.addListSelectionListener(this);
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(UIHelper.PREFERRED_SIZE);
        upButton = UIHelper.createButton(buttonBox, Messages.BTTN_UP, this);
        downButton = UIHelper.createButton(buttonBox, Messages.BTTN_DOWN, this);
        editButton = UIHelper.createButton(buttonBox, Messages.BTTN_EDIT, this);
        deleteButton = UIHelper.createButton(buttonBox, Messages.BTTN_DELETE, this);
        editEntriesPanel.add(sp, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,
                0));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        buttonPanel.add(buttonBox);
        editEntriesPanel.add(buttonPanel, BorderLayout.EAST);
        add(editEntriesPanel, BorderLayout.CENTER);
    }

    @Override
    public int showDialog() {
        updateEditEntriesButtons();
        return super.showDialog();
    }

    @Override
    public String getTitle() {
        return Messages.TITLE_EDIT_ENTRIES;
    }

    @Override
    public int getOption() {
        return JOptionPane.OK_OPTION;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateEditEntriesButtons();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals(Messages.BTTN_DELETE)) {
            Entry entry = (Entry) list.getSelectedValue();
            if (entry != null) {
                app.deleteEntry(entry);
            }
            updateEditEntriesButtons();
        } else if (cmd.equals(Messages.BTTN_EDIT)) {
            Entry entry = (Entry) list.getSelectedValue();
            if (entry != null) {
                app.editContents(null, entry);
            }
        } else if (cmd.equals(Messages.BTTN_UP)) {
            moveEntry(true);
        } else if (cmd.equals(Messages.BTTN_DOWN)) {
            moveEntry(false);
        }
    }

    /*
     * move an entry up- or downwards
     */
    private void moveEntry(boolean moveUp) {
        int index = list.getSelectedIndex();
        int newIndex = index + (moveUp ? -1 : 1);
        if (index >= 0) {
            Entry entry = (Entry) snippets.getElementAt(index);
            snippets.remove(index);
            snippets.insertElementAt(entry, newIndex);
            updateEditEntriesButtons();
            list.setSelectedIndex(newIndex);
        }
    }

    /**
     * enables or disables buttons of the Edit entries dialog box
     */
    private void updateEditEntriesButtons() {
        int selectedIndex = list.getSelectedIndex();
        boolean isOneSelected = (selectedIndex != -1);
        deleteButton.setEnabled(isOneSelected);
        editButton.setEnabled(isOneSelected);
        upButton.setEnabled(selectedIndex > 0);
        downButton.setEnabled(isOneSelected
                && (selectedIndex < (snippets.size() - 1)));
    }
}
