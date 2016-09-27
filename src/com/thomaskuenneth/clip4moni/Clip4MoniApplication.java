/*
 * Clip4MoniApplication.java
 * 
 * This file is part of Clip4Moni.
 * 
 * Copyright (C) 2008 - 2016  Thomas Kuenneth
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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Clip4MoniApplication implements ActionListener,
        ClipboardOwner {

    private static final Clip4MoniApplication INSTANCE = new Clip4MoniApplication();

    private static final String CLASSNAME = Clip4MoniApplication.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASSNAME);

    private static final String ICONFILENAME_16 = "com/thomaskuenneth/clip4moni/graphics/16x16.png";
    private static final String ICONFILENAME_22 = "com/thomaskuenneth/clip4moni/graphics/17x22.png";

    private static final String PROGRAMICON = "com/thomaskuenneth/clip4moni/graphics/logo.png";

    private Clipboard systemClipboard;
    private DataFlavor plainText;
    private DefaultListModel snippets;
    private PopupMenu menu;
    private Menu pluginMenu;

    private Clip4MoniApplication() {
    }

    public static Clip4MoniApplication getInstance() {
        return INSTANCE;
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            Helper.restoreLookAndFeel();
            INSTANCE.prepareClipboard();
            INSTANCE.createUI();
        });
    }

    private void prepareClipboard() {
        systemClipboard = Helper.getSystemClipboard();
        try {
            plainText = new DataFlavor("text/plain");
        } catch (ClassNotFoundException ex) {
            LOGGER.throwing(CLASSNAME, "prepareClipboard()", ex);
        }
    }

    private void createUI() {
        // is the system tray supported?
        if (!SystemTray.isSupported()) {
            LOGGER.log(Level.SEVERE, "system tray not supported");
            System.exit(1);
        }
        final SystemTray tray = SystemTray.getSystemTray();
        snippets = new DefaultListModel();
        loadList(Helper.getFileList());
        /*
         * build the menu
         */
        menu = new PopupMenu(Helper.PROGNAME);
        createPluginMenu();
        populatePopup();
        /*
         * load the icon to be shown in the system try and activate the
         * whole thing
         */
        Dimension preferredSize = tray.getTrayIconSize();
        LOGGER.log(Level.CONFIG, "tray icon size: {0}x{1} pixels",
                new Object[]{preferredSize.width, preferredSize.height});
        String name = (preferredSize.height >= 22) ? ICONFILENAME_22 : ICONFILENAME_16;
        ImageIcon icon = UIHelper.getImageIcon(getClass(), name);
        TrayIcon tray_icon = new TrayIcon(icon.getImage(), Helper.PROGNAME, menu);
        tray_icon.setActionCommand(Messages.MI_EDITLIST);
        tray_icon.addActionListener(this);
        try {
            tray.add(tray_icon);
        } catch (AWTException ex) {
            LOGGER.throwing(CLASSNAME, "createUI", ex);
        }
    }

    /**
     * load the contents of a file into the DefaultListModel snippets
     *
     * @param filelist file to load from
     */
    private void loadList(File filelist) {
        snippets.removeAllElements();
        String data = FileHelper.loadFile(filelist);
        String[] list = data.split("\n");
        for (String elem : list) {
            if (elem.length() > 0) {
                String line = elem.trim();
                snippets.addElement(Entry.createEntry(line));
            }
        }
    }

    /*
     * this method saves to contents of the snippets DefaultListModel into a
     * file
     */
    private void saveList(File filelist) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < snippets.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(((Entry) snippets.elementAt(i)).getAll());
        }
        FileHelper.saveFile(filelist, sb.toString());
    }

    private void invokePlugin(final String cmd) {
        new Thread(() -> {
            final String name = MacHelp.getFrontmostApp();
            MacHelp.activateApp(Helper.PROGNAME);
            try {
                Thread.sleep(250);
            } catch (InterruptedException ex) {
            }
            final String text = PluginManager.callPlugin(cmd, copyFromClipboard());
            SwingUtilities.invokeLater(() -> {
                setContents(text);
                MacHelp.activateApp(name);
            });
        }).start();
    }

    private void setContents(final String text) {
        Transferable t;
        if (text.startsWith("{\\rtf1")) {
            t = new Transferable() {

                @Override
                public DataFlavor[] getTransferDataFlavors() {
                    try {
                        return new DataFlavor[]{new DataFlavor("text/rtf")};
                    } catch (ClassNotFoundException ex) {
                        LOGGER.log(Level.SEVERE, "setContents()", ex);
                    }
                    return null;
                }

                @Override
                public boolean isDataFlavorSupported(DataFlavor flavor) {
                    return true;
                }

                @Override
                public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    return new ByteArrayInputStream(text.getBytes(Charset.forName("US-ASCII")));
                }

            };
        } else {
            t = new StringSelection(text);
        }
        systemClipboard.setContents(t, getInstance());
    }

    public void paste(String filename) {
        File f = FileHelper.createFilename(filename);
        String str = FileHelper.loadFile(f);
        setContents(str);
    }

    private void createPluginMenu() {
        final ActionListener al = (ActionEvent e) -> {
            String cmd = e.getActionCommand();
            invokePlugin(cmd);
        };
        pluginMenu = UIHelper.createMenu(Messages.MI_CLIPBOARD);
        PluginManager.populateMenu(pluginMenu, al);
    }

    /**
     * Populates the main popup menu.
     */
    private void populatePopup() {
        menu.removeAll();
        int num = snippets.size();
        for (int i = 0; i < num; i++) {
            Entry entry = (Entry) snippets.elementAt(i);
            UIHelper.createMenuItem(entry.getKey(), menu, this, entry.getValue());
        }
        if (num > 0) {
            menu.addSeparator();
        }
        menu.add(pluginMenu);
        menu.addSeparator();
        UIHelper.createMenuItem(Messages.MI_GETFROMCLIPBOARD, menu, this, null);
        UIHelper.createMenuItem(Messages.MI_EDITLIST, menu, this, null);
        menu.addSeparator();
        UIHelper.createMenuItem(Messages.MI_INFO, menu, this, null);
        UIHelper.createMenuItem(Messages.MI_SETTINGS, menu, this, null);
        menu.addSeparator();
        UIHelper.createMenuItem(Messages.MI_QUIT, menu, this, null);
    }

    /**
     * Shows the Edit entries dialog
     */
    private void editList() {
        EditEntriesDialog d = new EditEntriesDialog(snippets);
        d.showDialog();
        saveList(Helper.getFileList());
    }

    /**
     * Creates a new entry or edits an existing one; if e is null we create a
     * new one; but if e is not null we read the contents from disc so the
     * contents parameter is ignored
     *
     * @param contents the contents of the entry
     * @param e existing entry or null
     */
    public void editContents(String contents, Entry e) {
        String title = "";
        String name = Long.toString(System.currentTimeMillis());
        String headline = Messages.getString("TITLE_NEW_CONTENTS");
        if (e != null) {
            title = e.getKey();
            name = e.getValue();
            File f = FileHelper.createFilename(name);
            contents = FileHelper.loadFile(f);
            headline = Messages.getString("TITLE_EDIT_CONTENTS");
        }
        AddEntryDialog addEntryDialog = new AddEntryDialog();
        if (addEntryDialog.showDialog(headline, title, contents) == JOptionPane.OK_OPTION) {
            title = addEntryDialog.getDescription();
            File f = FileHelper.createFilename(name);
            if (FileHelper.saveFile(f, addEntryDialog.getContents())) {
                if (e == null) {
                    /*
                     * add entry to snippets
                     */
                    snippets.addElement(new Entry(title, name));
                } else {
                    e.setKey(title);
                }
                saveList(Helper.getFileList());
            }
        }
    }

    /**
     * Enables or disables the entries of the popup menu. Quit is always
     * enabled.
     *
     * @param state enabled or disable the entries
     */
    private void enableMenuEntries(boolean state) {
        for (int i = 0; i < menu.getItemCount(); i++) {
            final MenuItem item = menu.getItem(i);
            // that's the way AWT creates a separator
            if (menu.getLabel().startsWith("-")) {
                continue;
            }
            boolean _state = item.getActionCommand().equals(Messages.MI_QUIT) ? true : state;
            item.setEnabled(_state);
        }
    }

    private void readFromClipboard() {
        editContents(copyFromClipboard(), null);
    }

    /**
     * Reads from the clipboard.
     *
     * @return the contents of the clipboard as a {@code String}.
     */
    private String copyFromClipboard() {
        StringBuilder sb = new StringBuilder();
        try {
            Transferable contents = systemClipboard.getContents(null);
            if (contents != null) {
                DataFlavor[] flavors = contents.getTransferDataFlavors();
                for (DataFlavor flav : flavors) {
                    if (flav.isMimeTypeEqual(plainText)) {
                        Reader in = null;
                        try {
                            in = flav.getReaderForText(contents);
                            int ch;
                            while ((ch = in.read()) != -1) {
                                sb.append((char) ch);
                            }
                        } catch (UnsupportedFlavorException | IOException e) {
                            LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } catch (IllegalStateException e) {
            LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e);
        }
        return sb.toString();
    }

    /*
     * Show a copyright box
     */
    private void info() {
        ImageIcon icon = UIHelper.getImageIcon(getClass(), PROGRAMICON);
        JOptionPane.showMessageDialog(null, new AboutView(), Messages.getString("STR_ABOUT"),
                JOptionPane.INFORMATION_MESSAGE, icon);
    }

    /**
     * Shows the settings dialog box.
     */
    private void settings() {
        SettingsDialog d = new SettingsDialog();
        if (d.showDialog() == JOptionPane.OK_OPTION) {
            Helper.setSnippetsDir(d.getSnippetsDir());
            Helper.storeLookAndFeel(d.getLookAndFeel());
            Helper.setMacOSXWorkaroundActive(d.isMacOSXWorkaroundActive());
            loadList(Helper.getFileList());
            populatePopup();
        }
    }

    /*
     * Quits the application.
     */
    private void quit() {
        System.exit(0);
    }

    /**
     * Remove an Entry from our list and delete the file.
     *
     * @param e entry to be deleted
     */
    public void deleteEntry(Entry e) {
        File f = FileHelper.createFilename(e.getValue());
        if (f.delete()) {
            snippets.removeElement(e);
            saveList(Helper.getFileList());
        }
    }

    @Override // ActionListener
    public void actionPerformed(ActionEvent e) {
        String name = MacHelp.getFrontmostApp();
        MacHelp.activateApp(Helper.PROGNAME);
        enableMenuEntries(false);
        String cmd = e.getActionCommand();
        if (Messages.MI_GETFROMCLIPBOARD.equals(cmd)) {
            readFromClipboard();
        } else if (Messages.MI_INFO.equals(cmd)) {
            info();
        } else if (Messages.MI_SETTINGS.equals(cmd)) {
            settings();
        } else if (Messages.MI_QUIT.equals(cmd)) {
            quit();
        } else if (Messages.MI_EDITLIST.equals(cmd)) {
            editList();
        } else {
            paste(cmd);
        }
        enableMenuEntries(true);
        populatePopup();
        MacHelp.activateApp(name);
    }

    @Override // ClipboardOwner
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        LOGGER.info(contents.toString());
    }
}
