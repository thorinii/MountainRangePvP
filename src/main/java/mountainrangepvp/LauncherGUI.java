package mountainrangepvp;

import mountainrangepvp.game.settings.GameSettings;
import mountainrangepvp.game.mp.lanping.PingClient;
import mountainrangepvp.game.mp.lanping.PingClient.ServerData;
import mountainrangepvp.game.world.Player.Team;
import mountainrangepvp.util.Log;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * @author lachlan
 */
public class LauncherGUI extends javax.swing.JFrame {

    private final Preferences prefs = Preferences.userNodeForPackage(
            LauncherGUI.class);
    private DefaultListModel<String> serversListModel;
    private PingClient pingClient;
    private Timer pingReadTimer;

    /**
     * Creates new form LauncherGUI
     */
    public LauncherGUI() {
        initComponents();

        try {
            pingClient = new PingClient();
            pingClient.start();
        } catch (IOException ioe) {
            Log.warn("Could not start PingClient", ioe);
        }

        serversListModel = new DefaultListModel<>();
        serversList.setModel(serversListModel);
        serverIPTxt.setText("");

        pingReadTimer = new Timer(1000, new PingReader());
        pingReadTimer.setRepeats(true);
        pingReadTimer.start();


        teamBox.setRenderer(new TeamListCellRenderer());


        DefaultComboBoxModel<String> screenResModel = new DefaultComboBoxModel<>();
        screenResBox.setModel(screenResModel);

        setupResolutions(screenResModel);

        loadPrefs();

        // Center the window
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        setLocation(screen.width / 2 - getWidth() / 2,
                    screen.height / 2 - getHeight() / 2);
    }

    private void setupResolutions(DefaultComboBoxModel<String> displayModesModel) {
        DisplayMode[] modes = getModes();
        Set<DisplayMode> resolutions = new TreeSet<>(
                new Comparator<DisplayMode>() {
                    @Override
                    public int compare(DisplayMode o1, DisplayMode o2) {
                        if (o1.getWidth() == o2.getWidth())
                            return o1.getHeight() - o2.getHeight();
                        else
                            return o1.getWidth() - o2.getWidth();
                    }
                });

        resolutions.addAll(Arrays.asList(modes));

        for (DisplayMode mode : resolutions) {
            displayModesModel.addElement(mode.getWidth() + "x" + mode.
                    getHeight());
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mpTypeBtnGrp = new javax.swing.ButtonGroup();
        gameTypeBtnGrp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        mpTypeServerBtn = new javax.swing.JRadioButton();
        gameTypeFFABtn = new javax.swing.JRadioButton();
        gameTypeTeamBtn = new javax.swing.JRadioButton();
        jPanel5 = new javax.swing.JPanel();
        mpTypeClientBtn = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serversList = new javax.swing.JList();
        serverIPTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        playerNameTxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        teamBox = new javax.swing.JComboBox();
        startBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        fullscreenBtn = new javax.swing.JCheckBox();
        screenResBox = new javax.swing.JComboBox();
        refreshRateBox = new javax.swing.JComboBox();
        bitDepthBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mountain Range PvP Launcher");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(1, 1, 1), 1, true), "Multiplayer"));

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        mpTypeBtnGrp.add(mpTypeServerBtn);
        mpTypeServerBtn.setSelected(true);
        mpTypeServerBtn.setText("Start a Server");

        gameTypeBtnGrp.add(gameTypeFFABtn);
        gameTypeFFABtn.setSelected(true);
        gameTypeFFABtn.setText("Free For All");

        gameTypeBtnGrp.add(gameTypeTeamBtn);
        gameTypeTeamBtn.setText("Teams");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(mpTypeServerBtn)
                                                            .addGroup(jPanel4Layout.createSequentialGroup()
                                                                              .addGap(12, 12, 12)
                                                                              .addComponent(gameTypeFFABtn)
                                                                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                              .addComponent(gameTypeTeamBtn)))
                                          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(mpTypeServerBtn)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(gameTypeFFABtn)
                                                            .addComponent(gameTypeTeamBtn))
                                          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        mpTypeBtnGrp.add(mpTypeClientBtn);
        mpTypeClientBtn.setText("Join a Server");
        mpTypeClientBtn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                mpTypeClientBtnItemStateChanged(evt);
            }
        });

        jLabel3.setText("LAN Servers:");

        serversList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"IP Address"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        serversList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        serversList.setEnabled(false);
        serversList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                serversListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(serversList);

        serverIPTxt.setText("IP Address");
        serverIPTxt.setEnabled(false);

        jLabel2.setText("Server IP Address:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jScrollPane1)
                                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                                              .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(mpTypeClientBtn)
                                                                                                .addComponent(jLabel3))
                                                                              .addGap(0, 0, Short.MAX_VALUE))
                                                            .addGroup(jPanel5Layout.createSequentialGroup()
                                                                              .addComponent(jLabel2)
                                                                              .addGap(18, 18, 18)
                                                                              .addComponent(serverIPTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)))
                                          .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(mpTypeClientBtn)
                                          .addGap(18, 18, 18)
                                          .addComponent(jLabel3)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(serverIPTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel2))
                                          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                          .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                          .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(1, 1, 1), 1, true), "Player"));

        jLabel4.setText("Player Name:");

        jLabel6.setText("Team:");

        teamBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Blue", "Green", "Orange", "Red"}));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                            .addComponent(jLabel6)
                                                            .addComponent(jLabel4))
                                          .addGap(18, 18, 18)
                                          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(playerNameTxt)
                                                            .addComponent(teamBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                          .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(playerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel4))
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(teamBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(jLabel6))
                                          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        startBtn.setFont(startBtn.getFont().deriveFont(startBtn.getFont().getSize() + 5f));
        startBtn.setText("Start");
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "Settings"));

        jLabel5.setText("Screen Resolution:");

        fullscreenBtn.setText("Fullscreen");
        fullscreenBtn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                fullscreenBtnItemStateChanged(evt);
            }
        });

        screenResBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1000x800"}));
        screenResBox.setEnabled(false);
        screenResBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                screenResBoxItemStateChanged(evt);
            }
        });

        refreshRateBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        refreshRateBox.setEnabled(false);

        bitDepthBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        bitDepthBox.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                                              .addComponent(fullscreenBtn)
                                                                              .addGap(0, 0, Short.MAX_VALUE))
                                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                                              .addComponent(jLabel5)
                                                                              .addGap(18, 18, 18)
                                                                              .addComponent(screenResBox, 0, 120, Short.MAX_VALUE)
                                                                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                              .addComponent(refreshRateBox, 0, 94, Short.MAX_VALUE)
                                                                              .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                              .addComponent(bitDepthBox, 0, 94, Short.MAX_VALUE)))
                                          .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(fullscreenBtn)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                            .addComponent(jLabel5)
                                                            .addComponent(screenResBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(refreshRateBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                            .addComponent(bitDepthBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                          .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(startBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                          .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                          .addContainerGap()
                                          .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                          .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                          .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                          .addComponent(startBtn)
                                          .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void mpTypeClientBtnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_mpTypeClientBtnItemStateChanged
        serverIPTxt.setEnabled(mpTypeClientBtn.isSelected());
        serversList.setEnabled(mpTypeClientBtn.isSelected());

        gameTypeFFABtn.setEnabled(!mpTypeClientBtn.isSelected());
        gameTypeTeamBtn.setEnabled(!mpTypeClientBtn.isSelected());
    }//GEN-LAST:event_mpTypeClientBtnItemStateChanged

    private void serversListValueChanged(//GEN-FIRST:event_serversListValueChanged
                                         javax.swing.event.ListSelectionEvent evt) {//GEN-HEADEREND:event_serversListValueChanged
        if (serversList.getSelectedIndex() != -1) {
            serverIPTxt.setText(serversListModel.elementAt(serversList.
                    getSelectedIndex()));
        }
    }//GEN-LAST:event_serversListValueChanged

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        if (playerNameTxt.getText().isEmpty()) {
            return;
        }
        if (mpTypeClientBtn.isSelected() && serverIPTxt.getText().isEmpty()) {
            return;
        }

        dispose();
        pingReadTimer.stop();
        pingClient.stop();

        savePrefs();
        makeGame();
    }//GEN-LAST:event_startBtnActionPerformed

    private void savePrefs() {
        prefs.putBoolean("game-type-client", mpTypeClientBtn.isSelected());
        prefs.put("player-name", playerNameTxt.getText());
        prefs.putBoolean("fullscreen", fullscreenBtn.isSelected());
        prefs.putInt("screen-resolution", screenResBox.getSelectedIndex());
        prefs.putInt("team-colour", teamBox.getSelectedIndex());
        prefs.putBoolean("team-mode-on", gameTypeTeamBtn.isSelected());
    }

    private void loadPrefs() {
        try {
            mpTypeClientBtn.setSelected(
                    prefs.getBoolean("game-type-client", false));
            playerNameTxt.setText(prefs.get("player-name", ""));
            fullscreenBtn.setSelected(prefs.getBoolean("fullscreen", false));
            screenResBox.setSelectedIndex(prefs.getInt("screen-resolution", 0));
            teamBox.setSelectedIndex(prefs.getInt("team-colour", 0));
            gameTypeTeamBtn.setSelected(prefs.getBoolean("team-mode-on", false));
        } catch (Exception e) {
            Log.warn("Could not load prefs", e);
        }
    }

    private void makeGame() {
        GameSettings config = new GameSettings();
        config.fullscreen = fullscreenBtn.isSelected();

        if (fullscreenBtn.isSelected()) {
            String resolution = (String) screenResBox.getSelectedItem();
            String[] resSplit = resolution.split("x");

            config.resolutionWidth = Integer.parseInt(resSplit[0]);
            config.resolutionHeight = Integer.parseInt(resSplit[1]);

            config.bitDepth = Integer.parseInt((String) bitDepthBox.
                    getSelectedItem());
        }

        config.server = mpTypeServerBtn.isSelected();
        config.serverIP = serverIPTxt.getText();

        config.playerName = playerNameTxt.getText();
        config.teamModeOn = (config.server) ? gameTypeTeamBtn.isSelected() : true;

        switch ((String) teamBox.getSelectedItem()) {
            case "Blue":
                config.team = Team.BLUE;
                break;
            case "Green":
                config.team = Team.GREEN;
                break;
            case "Orange":
                config.team = Team.ORANGE;
                break;
            case "Red":
                config.team = Team.RED;
                break;
        }

        Main.startGame(config);
    }

    private void fullscreenBtnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_fullscreenBtnItemStateChanged
        screenResBox.setEnabled(fullscreenBtn.isSelected());
        refreshRateBox.setEnabled(fullscreenBtn.isSelected());
        bitDepthBox.setEnabled(fullscreenBtn.isSelected());
    }//GEN-LAST:event_fullscreenBtnItemStateChanged

    private void screenResBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_screenResBoxItemStateChanged
        GraphicsDevice graphicsDevice = GraphicsEnvironment.
                getLocalGraphicsEnvironment().
                getDefaultScreenDevice();

        DisplayMode[] modes = getModes();
        Set<Integer> rates = new TreeSet<>();
        Set<Integer> depths = new TreeSet<>();

        DefaultComboBoxModel<String> refreshRateModel = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> bitDepthModel = new DefaultComboBoxModel<>();

        for (DisplayMode mode : modes) {
            rates.add(mode.getRefreshRate());
            depths.add(mode.getBitDepth());
        }

        for (int rate : rates) {
            refreshRateModel.addElement("" + rate);
        }

        for (int depth : depths) {
            bitDepthModel.addElement("" + depth);
        }

        refreshRateBox.setModel(refreshRateModel);
        bitDepthBox.setModel(bitDepthModel);

        DisplayMode currentMode = graphicsDevice.getDisplayMode();
        if (rates.contains(currentMode.getRefreshRate()))
            refreshRateBox.setSelectedItem("" + currentMode.getRefreshRate());
        if (depths.contains(currentMode.getBitDepth()))
            bitDepthBox.setSelectedItem("" + currentMode.getBitDepth());
    }//GEN-LAST:event_screenResBoxItemStateChanged

    private DisplayMode[] getModes() {
        GraphicsDevice graphicsDevice = GraphicsEnvironment.
                getLocalGraphicsEnvironment().
                getDefaultScreenDevice();

        return graphicsDevice.getDisplayModes();
    }

    /**
     * @param args the command line arguments
     */
    public static void laf() {
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel.
         * For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LauncherGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LauncherGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LauncherGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LauncherGUI.class.getName()).log(
                    java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox bitDepthBox;
    private javax.swing.JCheckBox fullscreenBtn;
    private javax.swing.ButtonGroup gameTypeBtnGrp;
    private javax.swing.JRadioButton gameTypeFFABtn;
    private javax.swing.JRadioButton gameTypeTeamBtn;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.ButtonGroup mpTypeBtnGrp;
    private javax.swing.JRadioButton mpTypeClientBtn;
    private javax.swing.JRadioButton mpTypeServerBtn;
    private javax.swing.JTextField playerNameTxt;
    private javax.swing.JComboBox refreshRateBox;
    private javax.swing.JComboBox screenResBox;
    private javax.swing.JTextField serverIPTxt;
    private javax.swing.JList serversList;
    private javax.swing.JButton startBtn;
    private javax.swing.JComboBox teamBox;
    // End of variables declaration//GEN-END:variables

    private class PingReader implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            List<ServerData> servers = pingClient.getServers();
            Collections.sort(servers, new Comparator<ServerData>() {
                @Override
                public int compare(ServerData o1, ServerData o2) {
                    return o1.getFreshness() - o2.getFreshness();
                }
            });

            for (ServerData data : pingClient.getServers()) {
                if (!serversListModel.contains(data.ip)) {
                    serversListModel.addElement(data.ip);
                }
            }

            for (String ip : Collections.list(serversListModel.elements())) {
                boolean contains = false;
                for (ServerData data : pingClient.getServers()) {
                    if (data.ip.equals(ip)) {
                        contains = true;
                        break;
                    }
                }

                if (!contains) {
                    serversListModel.removeElement(ip);
                }
            }
        }
    }

    private static class TeamListCellRenderer extends JLabel implements
            ListCellRenderer<String> {

        private final ImageIcon BLUE = new ImageIcon(this.getClass().
                getResource(
                        "/minimap/head-blue.png"));
        private final ImageIcon GREEN = new ImageIcon(this.getClass().
                getResource(
                        "/minimap/head-green.png"));
        private final ImageIcon ORANGE = new ImageIcon(this.getClass().
                getResource(
                        "/minimap/head-orange.png"));
        private final ImageIcon RED = new ImageIcon(this.getClass().getResource(
                "/minimap/head-red.png"));

        @Override
        public Component getListCellRendererComponent(
                JList<? extends String> list,
                String value, int index, boolean isSelected,
                boolean cellHasFocus) {

            setText(value);
            switch (value) {
                case "Blue":
                    setIcon(BLUE);
                    break;
                case "Green":
                    setIcon(GREEN);
                    break;
                case "Orange":
                    setIcon(ORANGE);
                    break;
                case "Red":
                    setIcon(RED);
                    break;
            }

            setBackground(Color.GRAY);
            setOpaque(isSelected);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setOpaque(true);
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setOpaque(false);
                    repaint();
                }
            });

            return this;
        }
    }
}
