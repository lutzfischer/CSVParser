/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.gui.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionDoubleEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleGreaterEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleGreaterThen;
import org.rappsilber.data.csv.condition.CsvConditionDoubleLessEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleLessThen;
import org.rappsilber.data.csv.condition.CsvConditionMissing;
import org.rappsilber.data.csv.condition.CsvConditionNot;
import org.rappsilber.data.csv.condition.CsvConditionStringContains;
import org.rappsilber.data.csv.condition.CsvConditionStringEqual;
import org.rappsilber.data.csv.condition.CsvConditionStringGreaterEqual;
import org.rappsilber.data.csv.condition.CsvConditionStringGreaterThen;
import org.rappsilber.data.csv.condition.CsvConditionStringLessEqual;
import org.rappsilber.data.csv.condition.CsvConditionStringLessThen;
import org.rappsilber.data.csv.condition.CsvConditionStringMatches;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class Condition extends javax.swing.JPanel implements CSVConditionProvider {

    String[] columns;
    boolean addedAndOR = false;
    ArrayList<ChangeListener>  ChangeListeners = new ArrayList<>();
    /**
     * Creates new form AndCondition
     */
    public Condition() {
        initComponents();
    }

    public Condition(String[] columns) {
        initComponents();
        setColumns(columns);
        new JButton().addActionListener(cbColumn);
    }    

    /**
     * Adds an <code>ActionListener</code> to the condition.
     * this will be triggered every time the column is changed
     * @param l the <code>ActionListener</code> to be added
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Adds an <code>ActionListener</code> to the condition.
     * this will be triggered every time the column is changed
     * @param l the <code>ActionListener</code> to be added
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    public void setColumns(String[] columns) {
        this.columns=columns;
        String[] colDropDown = new String[columns.length+3];
        colDropDown[0]="";
        colDropDown[1]="AND";
        colDropDown[2]="OR";
        System.arraycopy(columns, 0, colDropDown, 3, columns.length);
        cbColumn.setModel(new javax.swing.DefaultComboBoxModel<>(colDropDown));
    }
    
    public void setEmpty(){
        cbColumn.setSelectedIndex(0);
    }
    
    public boolean isAND() {
        return cbColumn.getSelectedIndex() == 1;
    }
    public boolean isOR() {
        return cbColumn.getSelectedIndex() == 2;
    }
    
    public CsvCondition getCondition() {
        String op = cbOperant.getSelectedItem().toString();
        String vs = cbValue.getText();
        int col = cbColumn.getSelectedIndex()-3;
        CsvCondition con = null;
        if (col<0)
            return null;
        
        Double vd = null;
        if (!cbOperant.getSelectedItem().toString().contentEquals("contains")) {
            try {
                vd = Double.parseDouble(vs);
            } catch (NumberFormatException ne) {
            }
        }
        
        if (vd != null) {
            // we assume a numeric value
            if (op.contentEquals("=")) {
                con = new CsvConditionDoubleEqual(col, vd);
            } else if (op.contentEquals(">=")) {
                con = new CsvConditionDoubleGreaterEqual(col, vd);
            } else if (op.contentEquals("<=")) {
                con = new CsvConditionDoubleLessEqual(col, vd);
            } if (op.contentEquals(">")) {
                con = new CsvConditionDoubleGreaterThen(col, vd);
            } else if (op.contentEquals("<")) {
                con = new CsvConditionDoubleLessThen(col, vd);
            }
        } else {
            if (vs.isEmpty()) {
                con = new CsvConditionMissing(col);
            }
            if (op.contentEquals("=")) {
                con = new CsvConditionStringEqual(col, vs);
            } else if (op.contentEquals(">=")) {
                con = new CsvConditionStringGreaterEqual(col, vs);
            } else if (op.contentEquals("<=")) {
                con = new CsvConditionStringLessEqual(col, vs);
            } else if (op.contentEquals(">")) {
                con = new CsvConditionStringGreaterThen(col, vs);
            } else if (op.contentEquals("<")) {
                con = new CsvConditionStringLessThen(col, vs);
            } else if (op.contentEquals("contains")) {
                con = new CsvConditionStringContains(col, vs);
            } else if (op.contentEquals("matches")) {
                con = new CsvConditionStringMatches(col, vs);
            }
        }
        if (ckNot.isSelected())
            return new CsvConditionNot(con);
        else
            return con;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cbColumn = new javax.swing.JComboBox<>();
        cbOperant = new javax.swing.JComboBox<>();
        cbValue = new javax.swing.JTextField();
        ckNot = new javax.swing.JCheckBox();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        cbColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbColumnActionPerformed(evt);
            }
        });

        cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "=", ">", ">=", "<", "<=", "contains", " " }));
        cbOperant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOperantActionPerformed(evt);
            }
        });

        ckNot.setText("Not");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(ckNot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbColumn, 0, 60, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbValue, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cbValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(ckNot))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbOperantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOperantActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbOperantActionPerformed

    private void cbColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbColumnActionPerformed
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener cl : listenerList.getListeners(ChangeListener.class)) {
            cl.stateChanged(e);
        }
    }//GEN-LAST:event_cbColumnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbColumn;
    private javax.swing.JComboBox<String> cbOperant;
    private javax.swing.JTextField cbValue;
    private javax.swing.JCheckBox ckNot;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
