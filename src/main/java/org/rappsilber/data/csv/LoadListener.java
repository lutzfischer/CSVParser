/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rappsilber.data.csv;

/**
 * The loading and processing can be done asynchronous - listeners can be used to get information on completions
 */
public interface LoadListener {

    /**
     * function to be called when the assigned event fires
     * @param row
     * @param column
     */
    void listen(int row, int column);
    
}
