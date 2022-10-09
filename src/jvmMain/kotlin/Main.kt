/*
 * Main.kt
 *
 * This file is part of Clip4Moni.
 *
 * Copyright (C) 2022  Thomas Kuenneth
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
package com.thomaskuenneth.clip4moni

import java.awt.*
import java.awt.datatransfer.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.Reader
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.util.ResourceBundle.getBundle
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.DefaultListModel
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

class Clip4Moni {
    companion object {
        @JvmField
        val VERSION: String = getBundle("version").getString("VERSION")
    }
}

private val CLASSNAME = Clip4MoniApplication::class.java.name
private val LOGGER = Logger.getLogger(CLASSNAME)

private lateinit var systemClipboard: Clipboard
private lateinit var plainText: DataFlavor

fun main() {
    SwingUtilities.invokeLater {
        Clip4MoniApplication.instance.setupTaskbar()
        Helper.restoreLookAndFeel()
        prepareClipboard()
        Clip4MoniApplication.instance.createUI()
        Clip4MoniApplication.instance.setupWatchService()
    }
//    application {
//        Tray(icon = painterResource(resourcePath = "com/thomaskuenneth/clip4moni/graphics/17x22.png")) {
//        }
//    }
}

fun paste(filename: String?) {
    val f = FileHelper.createFilename(filename)
    val str = FileHelper.loadFile(f)
    setContents(str)
}

private fun prepareClipboard() {
    systemClipboard = Helper.getSystemClipboard()
    try {
        plainText = DataFlavor("text/plain")
    } catch (ex: ClassNotFoundException) {
        LOGGER.throwing(CLASSNAME, "prepareClipboard()", ex)
    }
}

private fun setContents(text: String?) {
    if (text != null) {
        val t: Transferable = if (text.startsWith("{\\rtf1")) {
            object : Transferable {
                override fun getTransferDataFlavors(): Array<DataFlavor> {
                    try {
                        return arrayOf(DataFlavor("text/rtf"))
                    } catch (ex: ClassNotFoundException) {
                        LOGGER.log(Level.SEVERE, "setContents()", ex)
                    }
                    return arrayOf(DataFlavor.getTextPlainUnicodeFlavor())
                }

                override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
                    return true
                }

                override fun getTransferData(flavor: DataFlavor): Any {
                    return ByteArrayInputStream(text.toByteArray(StandardCharsets.US_ASCII))
                }
            }
        } else {
            StringSelection(text)
        }
        systemClipboard.setContents(t, Clip4MoniApplication.instance)
    }
}

private fun copyFromClipboard(): String {
    val sb = StringBuilder()
    try {
        systemClipboard.getContents(null)?.let { contents ->
            for (flavor in contents.transferDataFlavors) {
                if (flavor.isMimeTypeEqual(plainText)) {
                    var reader: Reader? = null
                    try {
                        reader = flavor.getReaderForText(contents)
                        var ch: Int
                        while (reader.read().also { ch = it } != -1) {
                            sb.append(ch.toChar())
                        }
                    } catch (e: UnsupportedFlavorException) {
                        LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e)
                    } catch (e: IOException) {
                        LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e)
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close()
                            } catch (e: IOException) {
                                LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e)
                            }
                        }
                    }
                    break
                }
            }
        }
    } catch (e: IllegalStateException) {
        LOGGER.throwing(CLASSNAME, "copyFromClipboard()", e)
    }
    return sb.toString()
}

private fun quit(result: Int = 0) {
    exitProcess(result)
}

class Clip4MoniApplication private constructor() : ActionListener, ClipboardOwner {

    private lateinit var menu: PopupMenu
    private lateinit var snippets: DefaultListModel<Entry>

    private var pluginMenu: Menu? = null
    private var launchMenu: Menu? = null

    private val execute = ActionListener { e: ActionEvent ->
        try {
            val args = e.actionCommand
            val l = ArrayList<String>()
            val sb = StringBuilder()
            var quoted = false
            for (element in args) {
                when (element) {
                    '\"' -> {
                        quoted = !quoted
                        if (!quoted) {
                            l.add(sb.toString())
                            sb.setLength(0)
                        }
                    }

                    ' ' -> if (!quoted) {
                        l.add(sb.toString())
                        sb.setLength(0)
                    } else {
                        sb.append(element)
                    }

                    else -> sb.append(element)
                }
            }
            Runtime.getRuntime().exec(l.toTypedArray())
        } catch (ex: IOException) {
            LOGGER.log(Level.SEVERE, "Runtime.getRuntime().exec()", ex)
        }
    }

    fun setupTaskbar() {
        if (Taskbar.isTaskbarSupported()) {
            val tb = Taskbar.getTaskbar()
            if (tb.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                val image = UIHelper.getImageIcon(javaClass, PROGRAMICON).image
                tb.iconImage = image
            }
        }
    }

    fun setupWatchService() {
        val t = Thread {
            try {
                val watchService = FileSystems.getDefault().newWatchService()
                val path =
                    Paths.get(Helper.getSnippetsDir().absolutePath)
                path.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY
                )
                var key: WatchKey
                while (watchService.take().also { key = it } != null) {
                    for (event in key.pollEvents()) {
                        val name = event.context().toString()
                        if (Helper.LISTNAME == name) {
                            loadList()
                            populatePopup()
                        }
                    }
                    key.reset()
                }
            } catch (e: IOException) {
                LOGGER.log(Level.SEVERE, "setupWatchService()", e)
            } catch (e: InterruptedException) {
                LOGGER.log(Level.SEVERE, "setupWatchService()", e)
            }
        }
        t.isDaemon = true
        t.start()
    }

    fun createUI() {
        if (!SystemTray.isSupported()) {
            LOGGER.log(Level.SEVERE, "system tray not supported")
            quit(1)
        }
        val tray = SystemTray.getSystemTray()
        snippets = DefaultListModel()
        loadList()
        // build the currentMenu
        menu = PopupMenu(Helper.PROGNAME)
        createPluginMenu()
        createLaunchMenu()
        populatePopup()
        // load and activate systemtray icon
        val preferredSize = tray.trayIconSize
        LOGGER.log(
            Level.CONFIG,
            "tray icon size: {0}x{1} pixels",
            arrayOf<Any>(preferredSize.width, preferredSize.height)
        )
        val name = if (preferredSize.height >= 22) ICONFILENAME_22 else ICONFILENAME_16
        val icon = UIHelper.getImageIcon(javaClass, name)
        val trayIcon = TrayIcon(icon.image, Helper.PROGNAME, menu)
        trayIcon.actionCommand = Messages.MI_EDITLIST
        trayIcon.addActionListener(this)
        try {
            tray.add(trayIcon)
        } catch (ex: AWTException) {
            LOGGER.throwing(CLASSNAME, "createUI", ex)
        }
    }

    @Synchronized
    private fun loadList() {
        snippets.removeAllElements()
        val data = FileHelper.loadFile(Helper.getFileList())
        val list = data.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (elem in list) {
            if (elem.isNotEmpty()) {
                val line = elem.trim { it <= ' ' }
                snippets.addElement(Entry.createEntry(line))
            }
        }
    }

    private fun saveList(fileList: File) {
        val sb = StringBuilder()
        for (i in 0 until snippets.size()) {
            if (i > 0) {
                sb.append('\n')
            }
            sb.append(snippets.elementAt(i).all)
        }
        FileHelper.saveFile(fileList, sb.toString())
    }

    private fun invokePlugin(cmd: String) {
        Thread {
            val name = MacHelp.getFrontmostApp()
            MacHelp.activateApp(Helper.PROGNAME)
            try {
                Thread.sleep(250)
            } catch (ignored: InterruptedException) {
            }
            val text = PluginManager.callPlugin(cmd, copyFromClipboard())
            SwingUtilities.invokeLater {
                setContents(text)
                MacHelp.activateApp(name)
            }
        }.start()
    }

    private fun createPluginMenu() {
        val al = ActionListener { e: ActionEvent ->
            val cmd = e.actionCommand
            invokePlugin(cmd)
        }
        pluginMenu = UIHelper.createMenu(Messages.MI_CLIPBOARD)
        PluginManager.populateMenu(pluginMenu, al)
    }

    private fun createLaunchMenu() {
        launchMenu = UIHelper.createMenu(Messages.MI_LAUNCH)
        val launchList = File(System.getProperty("user.home"), "LaunchList.txt")
        val data = FileHelper.loadFile(launchList)
        val menus: MutableList<Menu?> = ArrayList()
        menus.add(launchMenu)
        if (data != null) {
            val list = data.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (elem in list) {
                if (elem.isNotEmpty()) {
                    val last = menus.size - 1
                    val currentMenu = menus[last]
                    val line = elem.trim { it <= ' ' }
                    if (line.startsWith("---")) {
                        currentMenu!!.addSeparator()
                    } else if (line == "..") {
                        if (menus.size > 1) {
                            menus.removeAt(last)
                        }
                    } else if (line.startsWith(">> ")) {
                        val name = line.substring(3)
                        if (name.isNotEmpty()) {
                            val subMenu = Menu(name)
                            currentMenu!!.add(subMenu)
                            menus.add(subMenu)
                        }
                    } else {
                        val entry = line.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (entry.size == 2) {
                            UIHelper.createMenuItem(entry[0], currentMenu, execute, entry[1])
                        }
                    }
                }
            }
        }
    }

    @Synchronized
    private fun populatePopup() {
        menu.removeAll()
        val num = snippets.size()
        for (i in 0 until num) {
            val entry = snippets.elementAt(i)
            UIHelper.createMenuItem(entry.key, menu, this, entry.value)
        }
        if (num > 0) {
            menu.addSeparator()
        }
        menu.add(pluginMenu)
        menu.addSeparator()
        UIHelper.createMenuItem(Messages.MI_GETFROMCLIPBOARD, menu, this, null)
        UIHelper.createMenuItem(Messages.MI_EDITLIST, menu, this, null)
        menu.addSeparator()
        UIHelper.createMenuItem(Messages.MI_INFO, menu, this, null)
        UIHelper.createMenuItem(Messages.MI_SETTINGS, menu, this, null)
        if (launchMenu!!.itemCount > 0) {
            menu.add(launchMenu)
        }
        menu.addSeparator()
        UIHelper.createMenuItem(Messages.MI_QUIT, menu, this, null)
    }

    private fun editList() {
        val d = EditEntriesDialog(snippets)
        d.showDialog()
        saveList(Helper.getFileList())
    }

    fun editContents(contents: String?, e: Entry?) {
        var localContents = contents
        var title: String? = ""
        var name = System.currentTimeMillis().toString()
        var headline = Messages.getString("TITLE_NEW_CONTENTS")
        if (e != null) {
            title = e.key
            name = e.value
            val f = FileHelper.createFilename(name)
            localContents = FileHelper.loadFile(f)
            headline = Messages.getString("TITLE_EDIT_CONTENTS")
        }
        val addEntryDialog = AddEntryDialog()
        if (addEntryDialog.showDialog(headline, title, localContents) == JOptionPane.OK_OPTION) {
            title = addEntryDialog.description
            val f = FileHelper.createFilename(name)
            if (FileHelper.saveFile(f, addEntryDialog.contents)) {
                if (e == null) {
                    /*
                     * add entry to snippets
                     */
                    snippets.addElement(Entry(title, name))
                } else {
                    e.key = title
                }
                saveList(Helper.getFileList())
            }
        }
    }

    private fun enableMenuEntries(state: Boolean) {
        for (i in 0 until menu.itemCount) {
            val item = menu.getItem(i)
            // that's the way AWT creates a separator
            val label = menu.label
            if (label != null && menu.label.startsWith("-")) {
                continue
            }
            item.actionCommand?.run {
                item.isEnabled = this == Messages.MI_QUIT || state
            }
        }
    }

    private fun readFromClipboard() {
        editContents(copyFromClipboard(), null)
    }

    private fun info() {
        val icon = UIHelper.getImageIcon(javaClass, PROGRAMICON)
        JOptionPane.showMessageDialog(
            null, AboutView(), Messages.getString("STR_ABOUT"),
            JOptionPane.INFORMATION_MESSAGE, icon
        )
    }

    private fun settings() {
        val d = SettingsDialog()
        if (d.showDialog() == JOptionPane.OK_OPTION) {
            Helper.setSnippetsDir(d.snippetsDir)
            Helper.storeLookAndFeel(d.lookAndFeel)
            Helper.setMacOSXWorkaroundActive(d.isMacOSXWorkaroundActive)
            loadList()
            populatePopup()
        }
    }

    fun deleteEntry(e: Entry) {
        val f = FileHelper.createFilename(e.value)
        if (!f.delete()) {
            LOGGER.log(Level.SEVERE, String.format("%s not found", f.absolutePath))
        }
        snippets.removeElement(e)
        saveList(Helper.getFileList())
    }

    // ActionListener
    override fun actionPerformed(e: ActionEvent) {
        val name = MacHelp.getFrontmostApp()
        MacHelp.activateApp(Helper.PROGNAME)
        enableMenuEntries(false)
        val cmd = e.actionCommand
        if (Messages.MI_GETFROMCLIPBOARD == cmd) {
            readFromClipboard()
        } else if (Messages.MI_INFO == cmd) {
            info()
        } else if (Messages.MI_SETTINGS == cmd) {
            settings()
        } else if (Messages.MI_QUIT == cmd) {
            quit()
        } else if (Messages.MI_EDITLIST == cmd) {
            editList()
        } else {
            paste(cmd)
        }
        enableMenuEntries(true)
        populatePopup()
        MacHelp.activateApp(name)
    }

    // ClipboardOwner
    override fun lostOwnership(clipboard: Clipboard, contents: Transferable) {
        LOGGER.info(contents.toString())
    }

    companion object {
        val instance = Clip4MoniApplication()
        private const val ICONFILENAME_16 = "com/thomaskuenneth/clip4moni/graphics/16x16.png"
        private const val ICONFILENAME_22 = "com/thomaskuenneth/clip4moni/graphics/17x22.png"
        private const val PROGRAMICON = "com/thomaskuenneth/clip4moni/graphics/logo.png"
    }
}
