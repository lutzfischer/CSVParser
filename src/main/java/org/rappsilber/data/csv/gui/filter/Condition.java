/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.gui.filter;

import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionContainsField;
import org.rappsilber.data.csv.condition.CsvConditionDoubleEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleGreaterEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleGreaterThen;
import org.rappsilber.data.csv.condition.CsvConditionDoubleLessEqual;
import org.rappsilber.data.csv.condition.CsvConditionDoubleLessThen;
import org.rappsilber.data.csv.condition.CsvConditionEqualsField;
import org.rappsilber.data.csv.condition.CsvConditionGreaterField;
import org.rappsilber.data.csv.condition.CsvConditionGreaterOrEqualsField;
import org.rappsilber.data.csv.condition.CsvConditionLessField;
import org.rappsilber.data.csv.condition.CsvConditionLessOrEqualsField;
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
    HashSet<String> fieldCompares;
    String[] operants;
    String equals = "=";
    String gt =">"; 
    String ge = ">=";
    String lt = "<";
    String le = "<=";
    String regex ="regex";
    String contains ="contains";
    String matches ="regex";
    String equalsField = "= field";
    String geField = ">= field";
    String gtField = "> field";
    String ltField = "< field";
    String leField = "<= field";
    String containsField = "contains field";
    ArrayList<ChangeListener>  ChangeListeners = new ArrayList<>();
    /**
     * Creates new form AndCondition
     */
    public Condition() {
        initComponents();
        new JButton().addActionListener(cbColumn);
        cbColumnCompare.setVisible(false);
        operants=new String[] {
            equals,gt,ge,lt,le,contains,equalsField,gtField,geField,ltField,leField,containsField
        };
        fieldCompares = new HashSet<>();
        fieldCompares.add(equalsField);
        fieldCompares.add(gtField);
        fieldCompares.add(geField);
        fieldCompares.add(ltField);
        fieldCompares.add(leField);
        fieldCompares.add(containsField);
        cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(operants));
    }

    public Condition(String[] columns) {
        this();
        setColumns(columns);
        
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
    
    public int setColumns(String[] columns) {
        int ret =0;
        Integer selectedColumn = null;
        String selectedColumnName = null;
        Integer selectedColumnCompare = null;
        String selectedColumnCompareName = null;
        // if we had some previous selection remember these
        if (this.columns != null && this.columns.length >0) {
            selectedColumn = cbColumn.getSelectedIndex();
            selectedColumnName = (String) cbColumn.getSelectedItem();
            selectedColumnCompare = cbColumnCompare.getSelectedIndex();
            selectedColumnCompareName = (String) cbColumnCompare.getSelectedItem();
        }
        this.columns=columns;
        String[] colDropDown = new String[columns.length+3];
        colDropDown[0]="";
        colDropDown[1]="AND";
        colDropDown[2]="OR";
        System.arraycopy(columns, 0, colDropDown, 3, columns.length);
        cbColumn.setModel(new javax.swing.DefaultComboBoxModel<>(colDropDown));
        cbColumnCompare.setModel(new javax.swing.DefaultComboBoxModel<>(columns));

        // if we had some previous selection try to restore these
        if (selectedColumn != null) {
            Integer newColumnName = null;
            for (int  i = 0; i < colDropDown.length; i++) {
                if (colDropDown[i].contentEquals(selectedColumnName)) {
                    if (newColumnName == null || Math.abs(i - selectedColumn) < Math.abs(newColumnName - selectedColumn)) {
                        newColumnName = i;
                    }
                }
            }
            if (newColumnName != null) {
                cbColumn.setSelectedIndex(newColumnName);
            } else {
                ret++;
            }
        }
        // if we had some previous selection try to restore these
        if (selectedColumnCompare != null) {
            Integer newColumnName = null;
            for (int  i = 0; i < columns.length; i++) {
                if (columns[i].contentEquals(selectedColumnCompareName)) {
                    if (newColumnName == null || Math.abs(i - selectedColumnCompare) < Math.abs(newColumnName - selectedColumnCompare)) {
                        newColumnName = i;
                    }
                }
            }
            if (newColumnName != null) {
                cbColumnCompare.setSelectedIndex(newColumnName);
            } else {
                ret++;
            }
        }
        return ret;
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
        String op = (String) cbOperant.getSelectedItem();
        String vs = txtValue.getText();
        int col = cbColumn.getSelectedIndex()-3;
        CsvCondition con = null;
        if (col<0)
            return null;
        if (fieldCompares.contains(op)) {
            if (op == equalsField) {
                con = new CsvConditionEqualsField(col, cbColumnCompare.getSelectedIndex());
            } else if (op == gtField) {
                con = new CsvConditionGreaterField(col, cbColumnCompare.getSelectedIndex());
            } else if (op == geField) {
                con = new CsvConditionGreaterOrEqualsField(col, cbColumnCompare.getSelectedIndex());
            } else if (op == ltField) {
                con = new CsvConditionLessField(col, cbColumnCompare.getSelectedIndex());
            } else if (op == leField) {
                con = new CsvConditionLessOrEqualsField(col, cbColumnCompare.getSelectedIndex());
            } else if (op == containsField) {
                con = new CsvConditionContainsField(col, cbColumnCompare.getSelectedIndex());
            }
        } else if (op == regex) {
            con = new CsvConditionStringMatches(col, vs);
        } else {
        
            Double vd = null;
            if (!cbOperant.getSelectedItem().toString().contentEquals("contains")) {
                try {
                    vd = Double.parseDouble(vs);
                } catch (NumberFormatException ne) {
                }
            }

            if (vd != null) {
                // we assume a numeric value
                if (op == equals) {
                    con = new CsvConditionDoubleEqual(col, vd);
                } else if (op == ge) {
                    con = new CsvConditionDoubleGreaterEqual(col, vd);
                } else if (op == le) {
                    con = new CsvConditionDoubleLessEqual(col, vd);
                } if (op== gt) {
                    con = new CsvConditionDoubleGreaterThen(col, vd);
                } else if (op == lt) {
                    con = new CsvConditionDoubleLessThen(col, vd);
                }
            } else {
                if (vs.isEmpty()) {
                    con = new CsvConditionMissing(col);
                }
                if (op.contentEquals(equals)) {
                    con = new CsvConditionStringEqual(col, vs);
                } else if (op.contentEquals(ge)) {
                    con = new CsvConditionStringGreaterEqual(col, vs);
                } else if (op.contentEquals(le)) {
                    con = new CsvConditionStringLessEqual(col, vs);
                } else if (op.contentEquals(gt)) {
                    con = new CsvConditionStringGreaterThen(col, vs);
                } else if (op.contentEquals(lt)) {
                    con = new CsvConditionStringLessThen(col, vs);
                } else if (op.contentEquals(contains)) {
                    con = new CsvConditionStringContains(col, vs);
                } else if (op.contentEquals(matches)) {
                    con = new CsvConditionStringMatches(col, vs);
                }
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
        jComboBox1 = new javax.swing.JComboBox<>();
        cbColumn = new javax.swing.JComboBox<>();
        cbOperant = new javax.swing.JComboBox<>();
        txtValue = new javax.swing.JTextField();
        ckNot = new javax.swing.JCheckBox();
        cbColumnCompare = new javax.swing.JComboBox<>();

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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cbColumn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbColumnActionPerformed(evt);
            }
        });

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
                .addComponent(cbColumn, 0, 58, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(cbColumnCompare, 0, 54, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtValue, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(txtValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(cbColumnCompare, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(cbColumn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(ckNot))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbOperantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOperantActionPerformed
        if (fieldCompares.contains(cbOperant.getSelectedItem())) {
            cbColumnCompare.setVisible(true);
            this.txtValue.setVisible(false);
        } else {
            cbColumnCompare.setVisible(false);
            this.txtValue.setVisible(true);
        }
        
        
    }//GEN-LAST:event_cbOperantActionPerformed

    private void cbColumnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbColumnActionPerformed
        ChangeEvent e = new ChangeEvent(this);
        for (ChangeListener cl : listenerList.getListeners(ChangeListener.class)) {
            cl.stateChanged(e);
        }
    }//GEN-LAST:event_cbColumnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbColumn;
    private javax.swing.JComboBox<String> cbColumnCompare;
    private javax.swing.JComboBox<String> cbOperant;
    private javax.swing.JCheckBox ckNot;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtValue;
    // End of variables declaration//GEN-END:variables
}
