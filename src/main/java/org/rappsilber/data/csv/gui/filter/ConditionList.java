/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv.gui.filter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.rappsilber.data.csv.condition.CsvCondition;
import org.rappsilber.data.csv.condition.CsvConditionAnd;
import org.rappsilber.data.csv.condition.CsvConditionNot;
import org.rappsilber.data.csv.condition.CsvConditionOr;
import org.rappsilber.data.csv.gui.CSVPanel;

/**
 *
 * @author Lutz Fischer <lfischer@staffmail.ed.ac.uk>
 */
public class ConditionList extends javax.swing.JPanel  implements CSVConditionProvider{
    ArrayList<Condition> conditions = new ArrayList<>();
    ArrayList<ConditionList> subLists = new ArrayList<>();
    String[] columns;
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
    }
    
    public void setRemovable(boolean r) {
        if (r) {
            cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR", "remove" }));
        } else {
            cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        }
    }
    
    /**
     * Creates new form ConditionList
     */
    public ConditionList(String[] columns) {
        this();
        setColumns(columns);
    }
    
    public CsvCondition getCondition() {
        if (conditions.size() == 1)
            return null;
        ArrayList<CsvCondition> retl = new ArrayList<>(conditions.size()-1);
        CsvCondition ret;
        for (int c =0 ; c<conditions.size()-1;c++)
            retl.add(conditions.get(c).getCondition());
        if (cbOperant.getSelectedItem().equals("AND")) {
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
                ConditionList csv = new ConditionList();
                csv.setColumns(new String[]{"One","Two","Three"});
                window.setPreferredSize(csv.getPreferredSize());
                window.add(csv, BorderLayout.CENTER);
                window.pack();
                window.addWindowListener(new WindowAdapter() {
                  public void windowClosing(WindowEvent e) {
                    System.exit(0);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cbOperant = new javax.swing.JComboBox<>();
        ckNot = new javax.swing.JCheckBox();
        jScrollPane2 = new javax.swing.JScrollPane();
        pList = new javax.swing.JPanel();
        firstCondition = new org.rappsilber.data.csv.gui.filter.Condition();

        cbOperant.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 15, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbOperant, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ckNot))
                        .addGap(0, 14, Short.MAX_VALUE))
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void cbOperantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbOperantActionPerformed
        if (cbOperant.getSelectedItem().toString().contentEquals("remove")) {
            notifyChange(new ChangeEvent(this));
        }
    }//GEN-LAST:event_cbOperantActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cbOperant;
    private javax.swing.JCheckBox ckNot;
    private org.rappsilber.data.csv.gui.filter.Condition firstCondition;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pList;
    // End of variables declaration//GEN-END:variables

    private boolean doRemove() {
        return cbOperant.getSelectedItem().toString().contentEquals("remove");
    }
}
