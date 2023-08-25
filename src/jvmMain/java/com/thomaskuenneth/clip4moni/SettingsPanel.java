/*
 * SettingsPanel.java
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2013 - 2023 Thomas Kuenneth
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

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import java.io.File;
import java.util.Objects;

public class SettingsPanel extends javax.swing.JPanel {

    public SettingsPanel() {
        initComponents();
        updateSnippetsDir(Helper.getSnippetsDir());
        updateMacWorkaround();
    }

    public String getSnippetsDir() {
        return textfieldSnippetsDir.getText();
    }

    public String getLookAndFeel() {
        return ((UIManager.LookAndFeelInfo) Objects.requireNonNull(comboboxLAFChooser.getSelectedItem())).getClassName();
    }

    public boolean isMacOSXWorkaroundActive() {
        return checkboxMacWorkAround.isSelected() && checkboxMacWorkAround.isVisible();
    }

    private void initComponents() {

        javax.swing.JLabel labelSnippetsDir = new javax.swing.JLabel();
        textfieldSnippetsDir = new javax.swing.JTextField();
        javax.swing.JLabel labelLAFChooser = new javax.swing.JLabel();
        comboboxLAFChooser = new javax.swing.JComboBox<>();
        javax.swing.JButton buttonSnippetsDir = new javax.swing.JButton();
        checkboxMacWorkAround = new javax.swing.JCheckBox();
        javax.swing.JButton jButton1 = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("com/thomaskuenneth/clip4moni/Messages"); // NOI18N
        labelSnippetsDir.setText(bundle.getString("STR_SETTINGS_SNIPPETS_PATH")); // NOI18N

        textfieldSnippetsDir.setColumns(30);

        labelLAFChooser.setText(bundle.getString("STR_LAF")); // NOI18N

        comboboxLAFChooser.setModel(new LAFComboboxModel());

        buttonSnippetsDir.setText(bundle.getString("BTTN_SETTINGS_SNIPPETS_PATH"));
        buttonSnippetsDir.addActionListener(this::buttonSnippetsDirActionPerformed);

        checkboxMacWorkAround.setText(bundle.getString("STR_MAC_WORKAROUND"));

        jButton1.setText(bundle.getString("BTTN_REPAIR"));
        jButton1.addActionListener(this::jButton1ActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(labelSnippetsDir)
                                        .addComponent(labelLAFChooser))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(checkboxMacWorkAround)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(textfieldSnippetsDir, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                                        .addComponent(comboboxLAFChooser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(buttonSnippetsDir)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton1)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, buttonSnippetsDir, jButton1);

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(buttonSnippetsDir)
                                        .addComponent(textfieldSnippetsDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(labelSnippetsDir)
                                        .addComponent(jButton1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(labelLAFChooser)
                                        .addComponent(comboboxLAFChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(checkboxMacWorkAround)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }

    private void buttonSnippetsDirActionPerformed(java.awt.event.ActionEvent ignoredEvt) {
        chooseSnippetsDir();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent ignoredEvt) {
        repair();
    }

    private void chooseSnippetsDir() {
        JFileChooser jfc = new JFileChooser(textfieldSnippetsDir.getText());
        jfc.setDialogTitle(Messages.getString("STR_SETTINGS_SNIPPETS_PATH"));
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            updateSnippetsDir(jfc.getSelectedFile());
        }
    }

    private void repair() {
        StringBuilder sb = new StringBuilder();
        File snippetsDir = Helper.getSnippetsDir();
        File[] files = snippetsDir.listFiles();
        if (files != null) {
            int count = 1;
            for (File current : files) {
                if (current.isFile() && FileHelper.hasMagic(current)) {
                    String key = String.format("#%d", count++);
                    sb.append(String.format("%d|%s|%s\n",
                            key.length(),
                            key,
                            current.getName()));
                }
            }
        }
        FileHelper.saveFile(Helper.getFileList(), sb.toString());
    }

    private void updateSnippetsDir(File dir) {
        textfieldSnippetsDir.setText(dir.getAbsolutePath());
    }

    private void updateMacWorkaround() {
        checkboxMacWorkAround.setVisible(Helper.isMacOSX());
        checkboxMacWorkAround.setSelected(Helper.isMacOSXWorkaroundActive());
    }

    private javax.swing.JCheckBox checkboxMacWorkAround;
    private javax.swing.JComboBox<UIManager.LookAndFeelInfo> comboboxLAFChooser;
    private javax.swing.JTextField textfieldSnippetsDir;
}
