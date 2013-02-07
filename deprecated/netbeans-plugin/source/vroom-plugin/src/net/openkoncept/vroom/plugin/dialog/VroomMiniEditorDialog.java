/*
 * VroomMiniEditorDialog.java
 *
 * Created on August 13, 2008, 11:08 AM
 */
package net.openkoncept.vroom.plugin.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import net.openkoncept.vroom.plugin.Constants;
import org.netbeans.modules.editor.indent.api.Reformat;
import org.openide.text.CloneableEditorSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author  fijaz
 */
public class VroomMiniEditorDialog extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    String oldContent;
    
    /** Creates new form VroomMiniEditorDialog */
    public VroomMiniEditorDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                String content = jEditorPane1.getText();
                if(oldContent.equals(content)) {
                    doClose(RET_CANCEL);
                } else {
                    if(JOptionPane.showConfirmDialog(null, org.openide.util.NbBundle.getMessage(VroomMiniEditorDialog.class, "VroomMiniEditorDialog.msgChangesLost"), "Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        doClose(RET_CANCEL);
                    }
                }
                
            }
        }, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jEditorPane1 = new net.openkoncept.vroom.plugin.dialog.VroomMiniEditorPane();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setTitle(org.openide.util.NbBundle.getMessage(VroomMiniEditorDialog.class, "VroomMiniEditorDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));

        jEditorPane1.setContentType(org.openide.util.NbBundle.getMessage(VroomMiniEditorDialog.class, "VroomMiniEditorDialog.jEditorPane1.contentType")); // NOI18N
        jScrollPane1.setViewportView(jEditorPane1);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        okButton.setText(org.openide.util.NbBundle.getMessage(VroomMiniEditorDialog.class, "VroomMiniEditorDialog.okButton.text_1")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        cancelButton.setText(org.openide.util.NbBundle.getMessage(VroomMiniEditorDialog.class, "VroomMiniEditorDialog.cancelButton.text_1")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_END);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-700)/2, (screenSize.height-500)/2, 700, 500);
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
    * @param args the command line arguments
    */
//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                VroomMiniEditorDialog dialog = new VroomMiniEditorDialog(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    public void setUri(String uri) {
        VroomMiniEditorPane ep = (VroomMiniEditorPane) jEditorPane1;
        ep.setUri(uri);
    }

    public void setJsFiles(List<String> jsFiles) {
        VroomMiniEditorPane ep = (VroomMiniEditorPane) jEditorPane1;
        ep.setJsFiles(jsFiles);
    }

    public void setCssFiles(List<String> cssFiles) {
        VroomMiniEditorPane ep = (VroomMiniEditorPane) jEditorPane1;
        ep.setCssFiles(cssFiles);
    }

    public void setContentType(String contentType) {
        EditorKit kit = CloneableEditorSupport.getEditorKit(contentType);
        jEditorPane1.setEditorKit(kit);
    }

    public void setContent(String text) {
        if(text == null || text.equals(Constants.EMPTY_STRING)) {
            return;
        }
        jEditorPane1.setText(text);
        jEditorPane1.setCaretPosition(0);
        Document doc = jEditorPane1.getDocument();
        Reformat rf = Reformat.get(doc);
        try {
            rf.lock();
            rf.reformat(0, doc.getLength());
            rf.unlock();
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
        oldContent = jEditorPane1.getText();
    }

    public String getContent() {
        return jEditorPane1.getText();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
