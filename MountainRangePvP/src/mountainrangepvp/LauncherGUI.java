/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mountainrangepvp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Timer;
import javax.swing.UIManager;
import mountainrangepvp.mp.lanping.PingClient;
import mountainrangepvp.mp.lanping.PingClient.ServerData;

/**
 *
 * @author lachlan
 */
public class LauncherGUI extends javax.swing.JFrame {

    private DefaultListModel<String> serversListModel;
    private PingClient pingClient;
    private Timer pingReadTimer;

    /**
     * Creates new form LauncherGUI
     */
    public LauncherGUI() {
        initComponents();

        serversListModel = new DefaultListModel<>();
        serversList.setModel(serversListModel);
        serverIPTxt.setText("");

        pingClient = new PingClient();

        try {
            pingClient.start();
        } catch (IOException ioe) {
            Log.warn("Could not start PingClient", ioe);
        }

        pingReadTimer = new Timer(1000, new PingReader());
        pingReadTimer.setRepeats(true);
        pingReadTimer.start();
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

        gameTypeBtnGrp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        gameTypeServerBtn = new javax.swing.JRadioButton();
        gameTypeClientBtn = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        serversList = new javax.swing.JList();
        serverIPTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        playerNameTxt = new javax.swing.JTextField();
        startBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Mountain Range PvP Launcher");
        setLocationByPlatform(true);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(1, 1, 1), 1, true), "Multiplayer"));

        gameTypeBtnGrp.add(gameTypeServerBtn);
        gameTypeServerBtn.setSelected(true);
        gameTypeServerBtn.setText("Server");

        gameTypeBtnGrp.add(gameTypeClientBtn);
        gameTypeClientBtn.setText("Client");
        gameTypeClientBtn.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                gameTypeClientBtnItemStateChanged(evt);
            }
        });

        jLabel1.setText("Game Type:");

        serversList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "IP Address" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
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

        jLabel3.setText("Recent/LAN Servers:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(gameTypeServerBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(gameTypeClientBtn))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(serverIPTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gameTypeServerBtn)
                    .addComponent(gameTypeClientBtn)
                    .addComponent(jLabel1))
                .addGap(0, 0, 0)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(serverIPTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(1, 1, 1), 1, true), "Player"));

        jLabel4.setText("Player Name:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(playerNameTxt)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(playerNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        startBtn.setFont(startBtn.getFont().deriveFont(startBtn.getFont().getSize()+5f));
        startBtn.setText("Start Server");
        startBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(startBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(startBtn)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void gameTypeClientBtnItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_gameTypeClientBtnItemStateChanged
        serverIPTxt.setEnabled(gameTypeClientBtn.isSelected());
        serversList.setEnabled(gameTypeClientBtn.isSelected());

        if (gameTypeClientBtn.isSelected()) {
            startBtn.setText("Start Client");
        } else {
            startBtn.setText("Start Server");
        }
    }//GEN-LAST:event_gameTypeClientBtnItemStateChanged

    private void serversListValueChanged(//GEN-FIRST:event_serversListValueChanged
            javax.swing.event.ListSelectionEvent evt) {//GEN-HEADEREND:event_serversListValueChanged
        if (serversList.getSelectedIndex() != -1) {
            serverIPTxt.setText(serversListModel.elementAt(serversList.
                    getSelectedIndex()));
        }
    }//GEN-LAST:event_serversListValueChanged

    private void startBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBtnActionPerformed
        dispose();

        pingClient.stop();

        if (gameTypeClientBtn.isSelected()) {
            Main.startClient(playerNameTxt.getText(), serverIPTxt.getText());
        } else {
            Main.startServer(playerNameTxt.getText());
        }
    }//GEN-LAST:event_startBtnActionPerformed

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
    private javax.swing.ButtonGroup gameTypeBtnGrp;
    private javax.swing.JRadioButton gameTypeClientBtn;
    private javax.swing.JRadioButton gameTypeServerBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField playerNameTxt;
    private javax.swing.JTextField serverIPTxt;
    private javax.swing.JList serversList;
    private javax.swing.JButton startBtn;
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
}
