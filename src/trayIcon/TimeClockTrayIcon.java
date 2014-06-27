package trayIcon;
/**
 * Created by Kyly on 6/25/2014.
 */

import idletime.State;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TimeClockTrayIcon {
    public static State status;

    public static void startTrayIcon() {

        /* Use an appropriate Look and Feel */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        /* Turn off metal's use of bold fonts */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        //Schedule a job for the event-dispatching thread:
        //adding TrayIcon.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI() {

        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        final PopupMenu popup = new PopupMenu();

        final Image image = createImage("C:\\Users\\Kyly\\Google Drive\\Mike Big Brother App\\src\\trayIcon\\images\\TrayIcon.png");
        int trayIconWidth = new TrayIcon(image).getSize().width;
        final TrayIcon trayIcon = new TrayIcon(image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));

        final SystemTray tray = SystemTray.getSystemTray();


        // Create a popup menu components
        MenuItem aboutItem = new MenuItem("About");

        MenuItem exitItem = new MenuItem("Exit");

        //Add components to popup menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon.setToolTip("TimeClock");
        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
        final String aboutMsg = "This application is a completely non-invasive and enjoyable tool,\n"
                + "which without a doubt will bring you a strong sense of inner\n"
                + "peace now that you, yes you, can take control of how your hours\n"
                + "at work are recorded. Enjoy :)";

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        aboutMsg);
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        aboutMsg);
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    protected static Image createImage(String path) {

        Image image = Toolkit.getDefaultToolkit().getImage(path);

        if (image == null)
            System.out.println("Image is null.");

        return image;
    }
}
