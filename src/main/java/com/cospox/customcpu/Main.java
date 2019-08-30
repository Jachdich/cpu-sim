//TODO
//MAR is updated at wrong time??? IDK?????????? 0x3 when it's indexing 0x2???????
//add display to show current indexed memory location
//make MAR increment twice per fetch
//Other stuff
//Regs can be updated by user by changing displayed values;

package com.cospox.customcpu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main extends JFrame {  
	private static final long serialVersionUID = 1L;
	
	private GridBagConstraints c = new GridBagConstraints();
	
	private RegisterDisplay left = new RegisterDisplay(new GridLayout(16, 2));
	private ControlDisplay right;
	
	public byte[] mem = new byte[64 * 1024];
	
	public Simulator sim;

	public static void main(String[] args) {  
		for (LookAndFeelInfo l: UIManager.getInstalledLookAndFeels()) {
			System.out.println(l);
		}
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
         
		new Main();
	}
	
	public Main() {
		super("Custom Processor Sim");
		this.initGUI();
		byte[] temp = {
				0x05,
				0x00,
				0x00,
				0x04,
				0x0c,
				0x20,
				0x0c,
				0x00,
				0x0c,
				0x11,};
		for (int i = 0; i < temp.length; i++) {
			this.mem[i] = temp[i];
		}
		this.sim = new Simulator(this.mem);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void initGUI() {
		this.setLayout(new GridBagLayout());
		this.right = new ControlDisplay(new GridLayout(2, 1), this.mem, this);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
        
        this.left.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Register Display"));
        
        this.right.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Control Display"));
        
		this.add(this.left, c);
		this.add(this.right, c);

		this.setSize(1300, 1080 / 2);
		this.setVisible(true); //making the frame visible  
	}
	
	public void clock() {
		this.sim.clock();
		this.updateDisplays();
	}
	
	private void updateDisplays() {
		this.left.update(this.sim.getRegs());
		this.right.update(this.sim.getBuses(), this.sim.getRegs());
	}
}