/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.gui.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.rappsilber.data.csv.CSVRandomAccess;
import org.rappsilber.data.csv.CsvParser;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionFieldCompare;
import org.rappsilber.data.csv.condition.CsvConditionList;
import org.rappsilber.data.csv.condition.CsvConditionAnd;
import org.rappsilber.data.csv.condition.CsvConditionNot;
import org.rappsilber.data.csv.condition.CsvConditionOr;
import org.rappsilber.data.csv.condition.CsvConditionParser;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class ConditionList extends javax.swing.JPanel  implements CSVConditionProvider{
    ArrayList<Condition> conditions = new ArrayList<>();
    ArrayList<ConditionList> subLists = new ArrayList<>();
    String[] columns;
    CsvParser csv;
    
    ChangeListener elementChanged = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            boolean changed = false;
            CSVConditionProvider remove = null;
            ChangeEvent propagate = new ChangeEvent(ConditionList.this);
            if (e.getSource() instanceof Condition) {
                Condition c = (Condition) e.getSource();
                if (c.getCondition() == null) {
                    if (c != conditions.get(conditions.size()-1)) {
                        conditions.remove(c);
                        changed=true;
                    }
                    if (c.isAND() || c.isOR() ) {
                        ConditionList cl = new ConditionList(columns);
                        if (csv != null)
                            cl.setCsvParser(csv);
                        cl.addChangeListener(this);
                        subLists.add(cl);
                        if (c.isAND())
                            cl.setAnd();
                        else
                            cl.setOr();
                        cl.setRemovable(true);
                        changed=true;
                        c.setEmpty();
                    } 
                } else {
                    if (c == conditions.get(conditions.size()-1)) {
                        Condition nc = new Condition(columns);
                        if (csv != null)
                            nc.setCsvParser(csv);
                        nc.addChangeListener(this);
                        conditions.add(nc);
                        changed=true;
                    }
                }
            } else if (e.getSource() instanceof ConditionList) {
                ConditionList cl = (ConditionList) e.getSource();
                if (cl.doRemove()) {
                    changed=true;
                    subLists.remove(cl);
                }
                
                
            }
            
            if (changed) {
                pList.removeAll();
                for (Condition c : conditions) {
                    pList.add(c);
                }
                for (ConditionList cl : subLists) {
                    pList.add(cl);
                }
                pList.revalidate();
                pList.repaint();
                notifyChange(propagate);
            }
        }            


    };

    
    
    /**
     * Creates new form ConditionList
     */
    public ConditionList() {
        initComponents();
        conditions.add(firstCondition);
        firstCondition.addChangeListener(elementChanged);
        //new GridLayout( 2, false );
        txtFilterText.setVisible(false);
    }
    
    public void setRemovable(boolean r) {
        if (txtFilterText.isVisible()) {
            if (r) {
                cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GUI", "TEXT", "remove" }));
            } else {
                cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "GUI", "TEXT"}));
            }
        } else {
            if (r) {
                cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR", "remove" }));
            } else {
                cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR", "TEXT" }));
            }
        }
    }

    public void updateANDORTEXT() {
        setRemovable(isRemovable());
    }

    public boolean isRemovable() {
        for (int i = 0; i<cbOperant.getItemCount(); i++) {
            if (cbOperant.getItemAt(i).equals("remove")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates new form ConditionList
     */
    public ConditionList(String[] columns) {
        this();
        setColumns(columns);
    }
    
    /**
     * Creates new form ConditionList
     */
    public ConditionList(String[] columns, CsvCondition condition) {
        this();
        setColumns(columns);
        setCondition(condition);
    }
    
    public CsvCondition getCondition() throws ParseException{
        if (txtFilterText.isVisible()) {
            if (txtFilterText.getText().trim().length() == 0) 
                return null;
            return new CsvConditionParser(csv).parse(txtFilterText.getText());
        }
        if (conditions.size() == 1)
            return null;
        ArrayList<CsvCondition> retl = new ArrayList<>(conditions.size()-1);
        CsvCondition ret;
        for (int c =0 ; c<conditions.size()-1;c++)
            retl.add(conditions.get(c).getCondition());
        for (int c =0 ; c<subLists.size();c++) {
            CsvCondition con = subLists.get(c).getCondition();
            if (con!= null)
                retl.add(con);
        }
        String op = cbOperant.getSelectedItem().toString();
        if (op.contentEquals("TEXT"))
            op = cbOperantOld != null ? cbOperantOld : "AND";


        if (op.equals("AND")) {
            ret = new CsvConditionAnd(retl.toArray(new CsvCondition[retl.size()]));
        } else {
            ret = new CsvConditionOr(retl.toArray(new CsvCondition[retl.size()]));
        }
        if (ckNot.isSelected()) 
            return new CsvConditionNot(ret);
        return ret;
                
    }

    public void setOr(){
        if (!cbOperant.getSelectedItem().equals("OR")) {
            for (int i = 0; i<cbOperant.getItemCount(); i++) {
                if (cbOperant.getItemAt(i).equals("OR")) {
                    final int id = i;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            cbOperant.setSelectedIndex(id);
                            cbOperant.setSelectedItem(cbOperant.getItemAt(id));
                        }
                    });
                }
            }
        }
    }

    public void setNot(){
        if (!ckNot.isSelected()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ckNot.setSelected(true);
                }
            });
        }
    }


    public void setAnd(){
        if (!cbOperant.getSelectedItem().equals("AND")) {
            for (int i = 0; i<cbOperant.getItemCount(); i++) {
                if (cbOperant.getItemAt(i).equals("AND")) {
                    final int id = i;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            cbOperant.setSelectedIndex(id);
                            cbOperant.setSelectedItem(cbOperant.getItemAt(id));
                        }
                    });
                }
            }
        }
    }
    
    public int setColumns(String[] columns) {
        this.columns = columns;
        int ret = 0;
        for (CSVConditionProvider c: conditions) 
            ret = ret + c.setColumns(columns);
        if (conditions.size() == 0) {
            Condition c = new Condition(columns);
            conditions.add(c);
            pList.add(c);
        }
        return ret;
    }
    
    public int setCsvParser(CsvParser csv) {
        this.csv = csv;
        int ret = 0;
        if (csv.getHeader() != null)
             ret = setColumns(csv.getHeader());
        for (CSVConditionProvider c: conditions) 
            c.setCsvParser(csv);
        return ret;
    }
    
    /**
     * Sets the condition for this ConditionList by parsing the provided CsvCondition
     * and updating the UI components accordingly.
     * @param condition the condition to set
     */
    public void setCondition(CsvCondition condition) {
        // Clear the form
        clearConditions();
        if (condition == null)
            return;
        
        // Handle NOT condition
        if (condition instanceof CsvConditionNot) {
            ckNot.setSelected(true);
            condition = ((CsvConditionNot) condition).getInner();
            if (condition instanceof CsvConditionList && ((CsvConditionList) condition).getConditions().length == 1) {
                condition = ((CsvConditionList) condition).getConditions()[0];
            }
            if (condition == null)
                return;
        } else {
            ckNot.setSelected(false);
        }
        
        // Handle compound conditions (AND/OR)
        if (condition instanceof CsvConditionAnd) {
            setAnd();
            CsvCondition[] innerConditions = ((CsvConditionAnd) condition).getConditions();
            populateConditions(innerConditions);
        } else if (condition instanceof CsvConditionOr) {
            setOr();
            CsvCondition[] innerConditions = ((CsvConditionOr) condition).getConditions();
            populateConditions(innerConditions);
        } else {
            // Single condition
            setAnd(); // Default to AND for single conditions
            populateConditions(new CsvCondition[]{condition});
        }
        
        // Refresh the UI
        refreshUI();
    }
    
    /**
     * Clears all existing conditions from the form
     */
    private void clearConditions() {
        // Remove all conditions except the first one
        for (int i = conditions.size() - 1; i >= 1; i--) {
            conditions.remove(i);
        }
        
        // Clear sublists
        subLists.clear();
        
        // Reset the first condition
        if (!conditions.isEmpty()) {
            conditions.get(0).setEmpty();
        }
    }
    
    /**
     * Populates the form with the given array of conditions
     * Note: This implementation has limitations with complex conditions due to 
     * the incomplete setCondition method in the Condition class.
     */
    private void populateConditions(CsvCondition[] conditionsArray) {
        for (int i = 0; i < conditionsArray.length; i++) {
            CsvCondition cond = conditionsArray[i];
            boolean not = false;
            if (cond instanceof CsvConditionNot) {
                cond = ((CsvConditionNot) cond).getInner();
                not = true;
            }
            boolean isAnd = false;
            boolean isOr = false;
            if (cond instanceof CsvConditionAnd) {
                if (((CsvConditionAnd) cond).getConditions().length == 1) {
                    cond = ((CsvConditionAnd) cond).getConditions()[0];
                } else {
                    isAnd = true;
                }
            }

            if (cond instanceof CsvConditionOr) {
                if (((CsvConditionOr) cond).getConditions().length == 1) {
                    cond = ((CsvConditionOr) cond).getConditions()[0];
                } else {
                    isOr = true;
                }
            }

            
            // Handle compound conditions as sublists
            if (isAnd || isOr) {
                ConditionList subList = new ConditionList(columns);
                if (csv != null) {
                    subList.setCsvParser(csv);
                }
                subList.addChangeListener(elementChanged);
                subList.setCondition(cond);
                
                if (cond instanceof CsvConditionAnd) {
                    subList.setAnd();
                } else if (cond instanceof CsvConditionOr) {
                    subList.setOr();
                }
                
                subList.setRemovable(true);
                subLists.add(subList);
                if (not) {
                    subList.setNot();
                }

                // Note: setCondition() already calls populateConditions internally,
                // so we don't need to call it again here
            } else {
                // Handle simple conditions
                Condition conditionComponent;
                conditionComponent = conditions.get(conditions.size() - 1);
                // if (i == 0 && !conditions.isEmpty()) {
                //     // Use the existing first condition
                //     conditionComponent = conditions.get();
                // } else {
                //     // Create a new condition component
                //     conditionComponent = new Condition(columns);
                //     if (csv != null) {
                //         conditionComponent.setCsvParser(csv);
                //     }
                //     conditionComponent.addChangeListener(elementChanged);
                //     conditions.add(conditionComponent);
                // }
                conditionComponent.setColumn(cond.getField());
                if (cond instanceof CsvConditionFieldCompare) {
                    conditionComponent.setColumnCompare(((CsvConditionFieldCompare) cond).getCompareField());
                } else {
                    conditionComponent.setColumnCompare(null);
                    conditionComponent.setValue(cond.getValue());
                }
                conditionComponent.setComperatorString(cond.getComperatorString());
                if (not) {
                    conditionComponent.setNot(true);
                }   
                
            }
        }
        
        // Ensure we always have an empty condition at the end for adding new ones
        if (conditions.isEmpty() || conditions.get(conditions.size() - 1).getCondition() != null) {
            Condition emptyCondition = new Condition(columns);
            if (csv != null) {
                emptyCondition.setCsvParser(csv);
            }
            emptyCondition.addChangeListener(elementChanged);
            conditions.add(emptyCondition);
        }
    }
    
    /**
     * Refreshes the UI by removing and re-adding all components
     */
    private void refreshUI() {
        pList.removeAll();
        for (Condition c : conditions) {
            pList.add(c);
        }
        for (ConditionList cl : subLists) {
            pList.add(cl);
        }
        pList.revalidate();
        pList.repaint();
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
    
    
    public static void main(String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                JFrame window = new JFrame();
                window.setLayout(new BorderLayout());
                final ConditionList csv = new ConditionList();
                CSVRandomAccess parser = new CSVRandomAccess(',', '"');
                parser.addColumn("ONE");
                parser.addColumn("TWO");
                parser.addColumn("THREE");
                csv.setCsvParser(parser);

                
                window.setPreferredSize(csv.getPreferredSize());
                window.add(csv, BorderLayout.CENTER);
                JPanel pResult = new JPanel(new BorderLayout());
                final JTextField txtResult = new JTextField();
                pResult.add(txtResult, BorderLayout.CENTER);
                JButton btnResult = new JButton("get");
                pResult.add(btnResult, BorderLayout.EAST);
                window.add(csv, BorderLayout.CENTER);
                window.add(pResult, BorderLayout.SOUTH);
                window.pack();
                window.addWindowListener(new WindowAdapter() {
                  public void windowClosing(WindowEvent e) {
                    System.exit(0);
                  }
                });                
                btnResult.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            CsvCondition c = csv.getCondition();
                            txtResult.setText(c == null?"": c.toString());
                        } catch (ParseException ex) {
                            Logger.getLogger(ConditionList.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
                
                window.setVisible(true);
            }
        });

    }

    
    protected void notifyChange(ChangeEvent propagate) {
        for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
            listener.stateChanged(propagate);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbOperant = new javax.swing.JComboBox<>();
        ckNot = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        pList = new javax.swing.JPanel();
        firstCondition = new org.rappsilber.data.csv.gui.filter.Condition();
        txtFilterText = new javax.swing.JTextField();

        cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR", "TEXT" }));
        cbOperant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbOperantActionPerformed(evt);
            }
        });

        ckNot.setText("Not");

        pList.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pList.setLayout(new java.awt.GridLayout(0, 1));
        pList.add(firstCondition);

        jScrollPane2.setViewportView(pList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ckNot)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtFilterText)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtFilterText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ckNot))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbOperantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOperantActionPerformed
        javax.swing.SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                if (cbOperant.getSelectedItem().toString().contentEquals("TEXT")) {
                    try {
                        if (ConditionList.this.cbOperantOld != null && ConditionList.this.cbOperantOld.equals("TEXT"))
                            return;
                        
                        CsvCondition c = ConditionList.this.getCondition();
                        
                        ConditionList.this.txtFilterText.setVisible(true);
                        
                        if (c != null) {
                            ConditionList.this.txtFilterText.setText(c.toString());
                        } else {
                            ConditionList.this.txtFilterText.setText("");
                        }
                        
                        ConditionList.this.txtFilterText.setVisible(true);
                        ConditionList.this.jScrollPane2.setVisible(false);
                        
                        ConditionList.this.revalidate();
                        ConditionList.this.repaint();
                        updateANDORTEXT();
                        cbOperant.setSelectedItem("TEXT");
                    } catch (ParseException ex) {
                        Logger.getLogger(ConditionList.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (cbOperant.getSelectedItem().toString().contentEquals("remove")) {
                    notifyChange(new ChangeEvent(ConditionList.this));
                } else if (cbOperant.getSelectedItem().toString().contentEquals("GUI")) {
                    CsvCondition c = null;
                    try {
                        if (ConditionList.this.txtFilterText.getText().trim().length() > 0)
                            c = new CsvConditionParser(ConditionList.this.csv).parse(ConditionList.this.txtFilterText.getText());
                    } catch (ParseException e){
                        JOptionPane.showMessageDialog(ConditionList.this, "Can't parse condition \n" + e, "Parsing Error ", JOptionPane.ERROR_MESSAGE);
                        Logger.getLogger(this.getClass().getName()).log(Level.WARNING,"erro parsing condition",e);
                        return;
                    }
                    ConditionList.this.txtFilterText.setVisible(false);
                    ConditionList.this.clearConditions();
                    updateANDORTEXT();

                    if (ConditionList.this.txtFilterText.getText().length() > 0)
                        ConditionList.this.setCondition(c);

                    ConditionList.this.jScrollPane2.setVisible(true);

                    ConditionList.this.revalidate();
                    ConditionList.this.repaint();
                }
                ConditionList.this.cbOperantOld = cbOperant.getSelectedItem().toString();
            }
        });
    }//GEN-LAST:event_cbOperantActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbOperant;
    private String cbOperantOld = null;
    private javax.swing.JCheckBox ckNot;
    private org.rappsilber.data.csv.gui.filter.Condition firstCondition;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pList;
    private javax.swing.JTextField txtFilterText;
    // End of variables declaration//GEN-END:variables

    private boolean doRemove() {
        return cbOperant.getSelectedItem().toString().contentEquals("remove");
    }
}
