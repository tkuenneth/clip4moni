/*
 * EditEntriesDialog.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2013 - 2022  Thomas Kuenneth
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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;

import static com.thomaskuenneth.clip4moni.MainKt.paste;

/**
 * This dialog box shows a list of entries and allows to edit them.
 *
 * @author Thomas Kuenneth
 */
public class EditEntriesDialog extends AbstractDialog {

    private DefaultListModel<Entry> snippets;
    private JButton deleteButton, editButton, upButton, downButton,
            buttonCopy;
    private JList<Entry> list;

    private final ActionListener al = ((e) -> {
        String cmd = e.getActionCommand();
        if (cmd.equals(Messages.BTTN_DELETE)) {
            Entry entry = list.getSelectedValue();
            if (entry != null) {
                Clip4MoniApplication.Companion.getInstance().deleteEntry(entry);
            }
            updateEditEntriesButtons();
        } else if (cmd.equals(Messages.BTTN_EDIT)) {
            Entry entry = list.getSelectedValue();
            if (entry != null) {
                Clip4MoniApplication.Companion.getInstance().editContents(null, entry);
            }
        } else if (cmd.equals(Messages.BTTN_UP)) {
            moveEntry(true);
        } else if (cmd.equals(Messages.BTTN_DOWN)) {
            moveEntry(false);
        } else if (cmd.equals(Messages.BTTN_COPY)) {
            Entry entry = list.getSelectedValue();
            if (entry != null) {
                paste(entry.getValue());
            }
        }
    });

    private final ListSelectionListener lsl = (ListSelectionEvent e) -> updateEditEntriesButtons();

    public EditEntriesDialog(DefaultListModel<Entry> snippets) {
        createUI(snippets);
    }

    private void createUI(DefaultListModel<Entry> listModel) {
        // buttons with panels
        JPanel buttonBox = new JPanel(new GridLayout(5, 1, 4, 4));
        upButton = UIHelper.createButton(buttonBox, Messages.BTTN_UP, al);
        downButton = UIHelper.createButton(buttonBox, Messages.BTTN_DOWN, al);
        editButton = UIHelper.createButton(buttonBox, Messages.BTTN_EDIT, al);
        deleteButton = UIHelper.createButton(buttonBox, Messages.BTTN_DELETE, al);
        buttonCopy = UIHelper.createButton(buttonBox, Messages.BTTN_COPY, al);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0,
                0));
        buttonPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        buttonPanel.add(buttonBox);
        // list and scrollpane
        this.snippets = listModel;
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(lsl);
        list.setSelectedIndex(0);
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(UIHelper.PREFERRED_SIZE);
        // putting it all together
        add(sp, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
    }

    @Override
    public int showDialog() {
        updateEditEntriesButtons();
        return super.showDialog();
    }

    @Override
    public String getTitle() {
        return Messages.getString("TITLE_EDIT_ENTRIES");
    }

    @Override
    public int getOption() {
        return JOptionPane.OK_OPTION;
    }

    /*
     * move an entry up- or downwards
     */
    private void moveEntry(boolean moveUp) {
        int index = list.getSelectedIndex();
        int newIndex = index + (moveUp ? -1 : 1);
        if (index >= 0) {
            Entry entry = snippets.getElementAt(index);
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
        buttonCopy.setEnabled(isOneSelected);
        downButton.setEnabled(isOneSelected
                && (selectedIndex < (snippets.size() - 1)));
        upButton.setEnabled(selectedIndex > 0);
    }
}
