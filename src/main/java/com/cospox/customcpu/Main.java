//TODO
//Regs can be updated by user by changing displayed values;

package com.cospox.customcpu;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Main extends JFrame {  
	private static final long serialVersionUID = 1L;
	
	private GridBagConstraints c = new GridBagConstraints();
	
	private RegisterDisplay left = new RegisterDisplay(new GridBagLayout());
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
		this.right = new ControlDisplay(new GridBagLayout(), this.mem, this);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
        
        this.left.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Register Display"));
        
        this.right.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Control Display"));
        
		this.add(this.left, c);
		this.add(this.right, c);
		
		this.right.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				right.resizeEvent(e);
			}

			@Override public void componentHidden(ComponentEvent e) {}
			@Override public void componentMoved(ComponentEvent e) {}
			@Override public void componentShown(ComponentEvent e) {}
		});

		//this.setSize(0, 1080 / 2);
		this.pack();
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